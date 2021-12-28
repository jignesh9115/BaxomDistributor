package com.jp.baxomdistributor.ui.undeliveredorders_3_1_0;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jp.baxomdistributor.Activities.SalesOrderDeliveryScreen;
import com.jp.baxomdistributor.Activities.UndeliveredOrdersNewActivity;
import com.jp.baxomdistributor.Activities.UndeliveredOrdersv3_1_0Activity;
import com.jp.baxomdistributor.Models.UndeliveredOrderByDistIdPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.EntityUndeliveredOrdersByDistBinding;
import com.jp.baxomdistributor.databinding.FragmentUndeliveredSalesOrdersBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UndeliveredSalesOrders_3_1_0Fragment extends Fragment {

    private UndeliveredSalesOrders_3_1_0ViewModel homeViewModel;

    FragmentUndeliveredSalesOrdersBinding binding;

    String url = "", response = "", distributor_id = "", distributor_name = "";

    ArrayList<UndeliveredOrderByDistIdPOJO> arrayList_group_dates_list;
    UndeliveredOrderByDistIdPOJO groupDatesPOJO;

    SharedPreferences sp_distributor_detail, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanList, commanSuchnaList;

    ProgressDialog pdialog;

    boolean isSelection = false;
    ArrayList<String> arrayList_order_date;
    private final String TAG = getClass().getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(UndeliveredSalesOrders_3_1_0ViewModel.class);

        binding = FragmentUndeliveredSalesOrdersBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        sp_distributor_detail = getActivity().getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);
        distributor_id = sp_distributor_detail.getString("distributor_id", "");
        distributor_name = sp_distributor_detail.getString("name", "");

        binding.tvTitleDistributorName.setText("" + distributor_name);

        sp_multi_lang = getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getActivity());

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                getActivity(), setLang(sp_multi_lang.getString("lang", "")));
        commanList = language.getData();

        Language language1 = new Language(sp_multi_lang.getString("lang", ""),
                getActivity(), setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language1.getData();
        if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
            binding.tvTotalTitleUsof.setText("" + commanList.getArrayList().get(0));
            binding.tvTotalCount.setText(commanList.getArrayList().get(1) + "");
            binding.tvTotalAmount.setText(commanList.getArrayList().get(2) + "");
        }

        //Toast.makeText(getActivity(), "distributor_id=" + distributor_id + "\ndistributor_name=" + distributor_name, Toast.LENGTH_SHORT).show();

        arrayList_group_dates_list = new ArrayList<>();
        new getUndeliveredOrderListTask().execute();

        binding.imgSelectionDate.setOnClickListener(v -> {

            if (arrayList_order_date.size() > 0) {

                Intent intent = new Intent(getActivity(), UndeliveredOrdersv3_1_0Activity.class);
                intent.putExtra("date_list", new Gson().toJson(arrayList_order_date));
                intent.putExtra("distributor_id", distributor_id);
                intent.putExtra("distributor_name", distributor_name);
                intent.putExtra("action", "multiple");
                startActivity(intent);
            } else
                Toast.makeText(getActivity(), "please select date", Toast.LENGTH_SHORT).show();

            /*Intent intent = new Intent(getActivity(), SalesOrderDeliveryScreen.class);
            startActivity(intent);*/

        });

        binding.imgCancel.setOnClickListener(v -> {
            binding.imgCancel.setVisibility(View.GONE);
            isSelection = false;
            arrayList_group_dates_list = new ArrayList<>();
            binding.rvUndeliveredOrderDateList.setVisibility(View.GONE);
            new getUndeliveredOrderListTask().execute();
        });

        return root;
    }


    //================get undelivered sales order list task starts ===============================

    public class getUndeliveredOrderListTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(getActivity());
            pdialog.setMessage(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            url = getString(R.string.Base_URL) + getString(R.string.UndeliveredOrders_ByDistid_v310_url) + distributor_id;
            Log.i("UndeliveredOrder url=>", url + "");

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("UndeliveredOrder res=>", response + "");

                getUndeliveredOrder();

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getUndeliveredOrder() {

        binding.imgEmptyListUndeliveredOrders.setVisibility(View.GONE);
        binding.rvUndeliveredOrderDateList.setVisibility(View.GONE);

        try {

            JSONObject jsonObject = new JSONObject(response + "");

            JSONArray data_array = jsonObject.getJSONArray("data");


            if (data_array.length() > 0) {

                binding.imgEmptyListUndeliveredOrders.setVisibility(View.GONE);
                binding.rvUndeliveredOrderDateList.setVisibility(View.VISIBLE);
                binding.llTotalUndelivered.setVisibility(View.VISIBLE);


                arrayList_order_date = new ArrayList<>();
                for (int i = 0; i < data_array.length(); i++) {

                    JSONObject data_object = data_array.getJSONObject(i);

                    //JSONObject data_object = jsonObject.getJSONObject("data");

                    JSONObject total_datas_object = data_object.getJSONObject("total_datas");


                    binding.tvTotalCount.setText(commanList.getArrayList().get(1) + " " + total_datas_object.getString("total_cnt"));
                    binding.tvTotalAmount.setText(commanList.getArrayList().get(2) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("total_amt")));
                    //binding.tvTotalAmount.setText("Total Amount : ₹-" + total_datas_object.getString("total_amt_del"));

                    JSONArray group_datas_jsonArray = data_object.getJSONArray("group_datas");

                    for (int j = 0; j < group_datas_jsonArray.length(); j++) {

                        JSONObject group_datas_object = group_datas_jsonArray.getJSONObject(j);

                        groupDatesPOJO = new UndeliveredOrderByDistIdPOJO(group_datas_object.getString("total_cnt"),
                                group_datas_object.getString("total_amt"),
                                group_datas_object.getString("entry_date"),
                                group_datas_object.getString("entry_date_pass"),
                                group_datas_object.getString("total_amt_del"));

                        arrayList_group_dates_list.add(groupDatesPOJO);

                    }

                    GroupDatesRVAdapter groupDatesRVAdapter = new GroupDatesRVAdapter(arrayList_group_dates_list, getActivity());
                    binding.rvUndeliveredOrderDateList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    binding.rvUndeliveredOrderDateList.setAdapter(groupDatesRVAdapter);
                }

            } else {

                binding.imgEmptyListUndeliveredOrders.setVisibility(View.VISIBLE);
                binding.rvUndeliveredOrderDateList.setVisibility(View.GONE);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //================get undelivered sales order list task ends =================================


    //====================Recycler view Adapter code starts ======================================

    public class GroupDatesRVAdapter extends RecyclerView.Adapter<GroupDatesRVAdapter.MyHolder> {

        ArrayList<UndeliveredOrderByDistIdPOJO> arrayList_group_dates_list;
        Context context;

        public GroupDatesRVAdapter(ArrayList<UndeliveredOrderByDistIdPOJO> arrayList_group_dates_list, Context context) {
            this.arrayList_group_dates_list = arrayList_group_dates_list;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityUndeliveredOrdersByDistBinding.inflate(LayoutInflater.from(context)));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {


            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    getActivity(), setLangEntity(sp_multi_lang.getString("lang", "")));
            Language.CommanList commanList = language.getData();

            holder.binding.tvUndeliveredOrderEntryDate.setText("" + arrayList_group_dates_list.get(position).getEntry_date());
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                holder.binding.tvUndeliveredOrderTotalAmount.setText(commanList.getArrayList().get(0) + " : ₹ " + (int) Double.parseDouble(arrayList_group_dates_list.get(position).getTotal_amt()));
                holder.binding.tvUndeliveredOrderTotalCount.setText(commanList.getArrayList().get(1) + " : " + arrayList_group_dates_list.get(position).getTotal_cnt());
            }


            /*holder.binding.llDateList.setOnClickListener(v -> {

                Intent intent = new Intent(context, ViewUndeliveredOrdersByDate.class);
                intent.putExtra("entry_date", arrayList_group_dates_list.get(position).getEntry_date_pass());
                intent.putExtra("distributor_id", distributor_id);
                intent.putExtra("distributor_name", distributor_name);
                startActivity(intent);

            });*/

            /*if (isSelection)
                holder.binding.llSelection.setVisibility(View.VISIBLE);
            else
                holder.binding.llSelection.setVisibility(View.GONE);*/

            holder.binding.chkSelectDate.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (holder.binding.chkSelectDate.isChecked())
                    if (arrayList_order_date.size() < 5) {
                        arrayList_order_date.add(arrayList_group_dates_list.get(position).getEntry_date_pass());
                    } else {
                        holder.binding.chkSelectDate.setChecked(false);
                        Toast.makeText(context, "you can only 5 dates !", Toast.LENGTH_SHORT).show();
                    }
                else if (!holder.binding.chkSelectDate.isChecked()) {

                    if (arrayList_order_date.size() > 0)
                        arrayList_order_date.remove(arrayList_group_dates_list.get(position).getEntry_date_pass());
                }

            });

        }

        @Override
        public int getItemCount() {
            return arrayList_group_dates_list.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityUndeliveredOrdersByDistBinding binding;

            public MyHolder(EntityUndeliveredOrdersByDistBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    //====================Recycler view Adapter code ends ========================================

    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "39");

        if (cur.getCount() > 0) {

            if (cur.moveToFirst()) {

                do {

                    if (key.equalsIgnoreCase("ENG"))
                        arrayList_lang_desc.add(cur.getString(3));
                    else if (key.equalsIgnoreCase("GUJ"))
                        arrayList_lang_desc.add(cur.getString(4));
                    else if (key.equalsIgnoreCase("HINDI"))
                        arrayList_lang_desc.add(cur.getString(5));

                }
                while (cur.moveToNext());

            }
        }
        cur.close();
        db.close();

        return arrayList_lang_desc;

    }


    public ArrayList<String> setLangEntity(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "44");

        if (cur.getCount() > 0) {

            if (cur.moveToFirst()) {

                do {

                    if (key.equalsIgnoreCase("ENG"))
                        arrayList_lang_desc.add(cur.getString(3));
                    else if (key.equalsIgnoreCase("GUJ"))
                        arrayList_lang_desc.add(cur.getString(4));
                    else if (key.equalsIgnoreCase("HINDI"))
                        arrayList_lang_desc.add(cur.getString(5));

                }
                while (cur.moveToNext());

            }
        }
        cur.close();
        db.close();

        return arrayList_lang_desc;

    }


    public ArrayList<String> setLangSuchna(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewMultiLangSuchna("39");

        if (cur.getCount() > 0) {

            if (cur.moveToFirst()) {

                do {

                    if (key.equalsIgnoreCase("ENG"))
                        arrayList_lang_desc.add(cur.getString(4));
                    else if (key.equalsIgnoreCase("GUJ"))
                        arrayList_lang_desc.add(cur.getString(5));
                    else if (key.equalsIgnoreCase("HINDI"))
                        arrayList_lang_desc.add(cur.getString(6));

                }
                while (cur.moveToNext());

            }
        }
        cur.close();
        db.close();

        return arrayList_lang_desc;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pdialog != null && pdialog.isShowing())
            pdialog.dismiss();
    }

}