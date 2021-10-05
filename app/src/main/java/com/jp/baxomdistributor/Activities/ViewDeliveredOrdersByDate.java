package com.jp.baxomdistributor.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Models.DeliveredOrderByDatePOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.ActivityViewDeliveredOrdersByDateBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;
import com.jp.baxomdistributor.databinding.EntityDeliveredOrdersByDateBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewDeliveredOrdersByDate extends AppCompatActivity {

    ActivityViewDeliveredOrdersByDateBinding binding;

    String entry_date, distributor_name, distributor_id, url = "", response = "", daily_status = "";

    ArrayList<DeliveredOrderByDatePOJO> arrayList_delivered_orders_by_date;
    DeliveredOrderByDatePOJO deliveredOrderByDatePOJO;

    GDateTime gDateTime = new GDateTime();

    ProgressDialog pdialog;

    SharedPreferences sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanSuchnaList;

    AlertDialog ad_net_connection;
    AlertDialog.Builder builder;
    private IntentFilter minIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewDeliveredOrdersByDateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //==============code for check quick response starts==========
        minIntentFilter = new IntentFilter();
        minIntentFilter.addAction(ConstantVariables.BroadcastStringForAction);
        Intent serviceIntent = new Intent(this, NetConnetionService.class);
        startService(serviceIntent);

        if (!isOnline(getApplicationContext()))
            connctionDialog();
        else {

            if (ad_net_connection != null && ad_net_connection.isShowing()) {
                ad_net_connection.dismiss();
                ad_net_connection = null;
            }
        }
        //==============code for check quick response ends============

        entry_date = getIntent().getStringExtra("entry_date");
        distributor_id = getIntent().getStringExtra("distributor_id");
        distributor_name = getIntent().getStringExtra("distributor_name");
        daily_status = getIntent().getStringExtra("daily_status");

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                ViewDeliveredOrdersByDate.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language.getData();


        binding.tvTitleDistributorNameDeliveredOrderByDate.setText("" + distributor_name);

        arrayList_delivered_orders_by_date = new ArrayList<>();
        new getDeliveredOrdersByDateTask().execute();


        binding.imgBackDeliveredOrderByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //========================get delivered orders by date id task starts ======================

    public class getDeliveredOrdersByDateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(ViewDeliveredOrdersByDate.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setCancelable(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_date_url) + distributor_id + "&order_date=" + entry_date + "&daily_status=" + daily_status;
            Log.i("DeliveredOrders url=>", url + "");

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("DeliveredOrders res=>", response + "");

                getDeliveredOrders();

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } catch (Exception e) {
                Log.i("TAG", e.getMessage());
            }
        }
    }

    private void getDeliveredOrders() {

        double total_fail_percetage = 0.0;
        int total_order = 0, total_del_order = 0;

        try {


            JSONObject jsonObject = new JSONObject(response + "");

            JSONArray data_array = jsonObject.getJSONArray("data");

            for (int i = 0; i < data_array.length(); i++) {

                //JSONObject data_object = jsonObject.getJSONObject("data");
                JSONObject data_object = data_array.getJSONObject(i);

                JSONObject total_datas_object = data_object.getJSONObject("total_datas");

                // binding.tvTotalCountDeliveredOrdersByDate.setText("Total Orders : " + total_datas_object.getString("total_cnt"));


                Language language = new Language(sp_multi_lang.getString("lang", ""),
                        ViewDeliveredOrdersByDate.this, setLang(sp_multi_lang.getString("lang", "")));
                Language.CommanList commanList = language.getData();
                if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                    binding.tvTitleDeliveredOrderByDate.setText("" + commanList.getArrayList().get(0) + " : " + gDateTime.ymdTodmy(entry_date));
                    binding.tvTotalTitleDeliveredorders.setText("" + commanList.getArrayList().get(1));

                    binding.tvTotalBookingAmountByDate.setText(commanList.getArrayList().get(3) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("total_amt")));
                    binding.tvTotalDeliveredAmountByDate.setText(commanList.getArrayList().get(4) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("total_amt_del")));
                    binding.tvTotalPendingAmountByDate.setText(commanList.getArrayList().get(5) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("pending_amount")));
                }
                total_fail_percetage = (Double.parseDouble(total_datas_object.getString("total_amt_failed")) * 100) / Double.parseDouble(total_datas_object.getString("total_amt"));

                binding.tvTotalFailAmountByDate.setText(commanList.getArrayList().get(5) + " (" + (int) total_fail_percetage + "%) ₹ " + total_datas_object.getString("total_amt_failed"));


                JSONArray group_datas_jsonArray = data_object.getJSONArray("group_datas");

                for (int j = 0; j < group_datas_jsonArray.length(); j++) {

                    JSONObject group_datas_object = group_datas_jsonArray.getJSONObject(j);
                    deliveredOrderByDatePOJO = new DeliveredOrderByDatePOJO(group_datas_object.getString("total_cnt"),
                            group_datas_object.getString("total_amt"),
                            group_datas_object.getString("total_amt_del"),
                            group_datas_object.getString("total_amt_failed"),
                            group_datas_object.getString("pending_amount"),
                            group_datas_object.getString("salesman_id"),
                            group_datas_object.getString("salesman"),
                            group_datas_object.getString("total_order"),
                            group_datas_object.getString("total_deliver_order"));

                    arrayList_delivered_orders_by_date.add(deliveredOrderByDatePOJO);

                    total_del_order = total_del_order + group_datas_object.getInt("total_deliver_order");
                    total_order = total_order + group_datas_object.getInt("total_order");

                }
                if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null)
                    binding.tvTotalCountDeliveredOrdersByDate.setText(commanList.getArrayList().get(2) + "              " + total_del_order + " / " + total_order);


                DeliveredOrdersByDateAdapter deliveredOrdersByDateAdapter = new DeliveredOrdersByDateAdapter(arrayList_delivered_orders_by_date, ViewDeliveredOrdersByDate.this);
                binding.rvDeliveredOrderListByDate.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                binding.rvDeliveredOrderListByDate.setAdapter(deliveredOrdersByDateAdapter);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //========================get delivered orders by date id task ends ========================


    //======================Recylcerview Adapter starts ===========================

    public class DeliveredOrdersByDateAdapter extends RecyclerView.Adapter<DeliveredOrdersByDateAdapter.MyHolder> {

        ArrayList<DeliveredOrderByDatePOJO> arrayList_delivered_orders_by_date;
        Context context;

        double fail_percetage = 0.0;

        public DeliveredOrdersByDateAdapter(ArrayList<DeliveredOrderByDatePOJO> arrayList_delivered_orders_by_date, Context context) {
            this.arrayList_delivered_orders_by_date = arrayList_delivered_orders_by_date;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityDeliveredOrdersByDateBinding.inflate(LayoutInflater.from(context)));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") final int position) {

            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    ViewDeliveredOrdersByDate.this, setLangEntity(sp_multi_lang.getString("lang", "")));
            Language.CommanList commanList = language.getData();
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                holder.binding.tvSalesmanNameDeliveredOrderByDate.setText("" + arrayList_delivered_orders_by_date.get(position).getSalesman());
                holder.binding.tvTotalDeliveredOrderDeliveredOrderByDate.setText(commanList.getArrayList().get(0) + "          " + arrayList_delivered_orders_by_date.get(position).getTotal_deliver_order() + " / " + arrayList_delivered_orders_by_date.get(position).getTotal_order());
                holder.binding.tvTotalBookingAmountDeliveredOrderByDate.setText(commanList.getArrayList().get(1) + " ₹ " + (int) Double.parseDouble(arrayList_delivered_orders_by_date.get(position).getTotal_amt()));
                holder.binding.tvTotalDeliveredAmountDeliveredOrderByDate.setText(commanList.getArrayList().get(2) + " ₹ " + (int) Double.parseDouble(arrayList_delivered_orders_by_date.get(position).getTotal_amt_del()));
                holder.binding.tvTotalPendingAmountDeliveredOrderByDate.setText(commanList.getArrayList().get(3) + " ₹ " + (int) Double.parseDouble(arrayList_delivered_orders_by_date.get(position).getPending_amount()));
            }
            fail_percetage = (Double.parseDouble(arrayList_delivered_orders_by_date.get(position).getTotal_amt_failed()) * 100) / Double.parseDouble(arrayList_delivered_orders_by_date.get(position).getTotal_amt());

            holder.binding.tvTotalFailAmountDeliveredOrderByDate.setText(commanList.getArrayList().get(4) + " (" + (int) fail_percetage + "%) ₹ " + arrayList_delivered_orders_by_date.get(position).getTotal_amt_failed());


            holder.binding.llDateList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ViewDeliveredOrdersBySalesman.class);
                    intent.putExtra("entry_date", entry_date);
                    intent.putExtra("distributor_id", distributor_id);
                    intent.putExtra("distributor_name", distributor_name);
                    intent.putExtra("salesman_id", arrayList_delivered_orders_by_date.get(position).getSalesman_id());
                    intent.putExtra("salesman_name", arrayList_delivered_orders_by_date.get(position).getSalesman());
                    intent.putExtra("daily_status", daily_status);
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList_delivered_orders_by_date.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityDeliveredOrdersByDateBinding binding;

            public MyHolder(EntityDeliveredOrdersByDateBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    //======================Recylcerview Adapter ends =============================


    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "12");

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
        Cursor cur = db.viewLanguage(key, "46");

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
        Cursor cur = db.viewMultiLangSuchna("12");

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

    public void connctionDialog() {

        try {
            if (ad_net_connection == null) {

                DialogNetworkErrorBinding neterrorBinding;

                builder = new AlertDialog.Builder(ViewDeliveredOrdersByDate.this);
                builder.setCancelable(false);
                neterrorBinding = DialogNetworkErrorBinding.inflate(getLayoutInflater());
                View view = neterrorBinding.getRoot();

                neterrorBinding.btnRetry.setOnClickListener(view1 -> {
                    ad_net_connection.dismiss();
                    ad_net_connection = null;
                });


                builder.setView(view);
                ad_net_connection = builder.create();
                ad_net_connection.show();

            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null && ni.isConnectedOrConnecting())
            return true;
        else
            return false;

    }

    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConstantVariables.BroadcastStringForAction)) {

                if (intent.getStringExtra("online_status").equals("false")) {
                    connctionDialog();
                } else {
                    if (ad_net_connection != null && ad_net_connection.isShowing()) {
                        ad_net_connection.dismiss();
                        ad_net_connection = null;
                    }
                }
            }
        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(myReceiver, minIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, minIntentFilter);
    }
}