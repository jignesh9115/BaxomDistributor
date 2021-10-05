package com.jp.baxomdistributor.ui.delivered_sales_orders;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Activities.ViewDeliveredOrdersByDate;
import com.jp.baxomdistributor.Models.DeliveredOrderByDistIdPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.EntityDeliveredOrdersByDistBinding;
import com.jp.baxomdistributor.databinding.FragmentDeliveredSalesOrdersBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class DeliveredSalesOrdersFragment extends Fragment {

    private DeliveredSalesOrdersViewModel homeViewModel;
    String TAG = getClass().getSimpleName();
    FragmentDeliveredSalesOrdersBinding binding;

    ArrayList<DeliveredOrderByDistIdPOJO> arrayList_delivered_orders;
    DeliveredOrderByDistIdPOJO deliveredOrderByDistIdPOJO;

    String url = "", response = "", distributor_id = "", distributor_name = "", from_date = "", to_date = "", daily_status = "", combine_daily_status = "";

    ArrayList<String> daily_status_list;

    SharedPreferences sp_distributor_detail, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanList, commanSuchnaList;

    DatePickerDialog dp;
    GDateTime gDateTime = new GDateTime();
    Calendar cal;
    int m, y, d, totaldayofmonth = 0;

    ProgressDialog pdialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(DeliveredSalesOrdersViewModel.class);
        binding = FragmentDeliveredSalesOrdersBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        sp_distributor_detail = getActivity().getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);
        distributor_id = sp_distributor_detail.getString("distributor_id", "");
        distributor_name = sp_distributor_detail.getString("name", "");

        binding.tvTitleDistributorNameDeliveredOrders.setText("" + distributor_name);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(gDateTime.getYear()));
        calendar.set(Calendar.MONTH, Integer.parseInt(gDateTime.getMonth()) - 1);
        totaldayofmonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        sp_multi_lang = getActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getActivity());

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                getActivity(), setLang(sp_multi_lang.getString("lang", "")));
        commanList = language.getData();

        Language language1 = new Language(sp_multi_lang.getString("lang", ""),
                getActivity(), setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language1.getData();

        if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
            binding.tvFromDateTitleDso.setText("" + commanList.getArrayList().get(0));
            binding.tvToDateTitleDso.setText("" + commanList.getArrayList().get(1));
            binding.cbThisMonthDeliveredOrder.setText("" + commanList.getArrayList().get(2));
            binding.cbUndeliveredOrdersDeliveredOrder.setText("" + commanList.getArrayList().get(3));
            binding.cbFulldeliveredOrdersDeliveredOrder.setText("" + commanList.getArrayList().get(4));
            binding.cbPartialdeliveredOrdersDeliveredOrder.setText("" + commanList.getArrayList().get(5));
            binding.cbFaildeliveredOrdersDeliveredOrder.setText("" + commanList.getArrayList().get(6));
            binding.tvTotalTitleDso.setText("" + commanList.getArrayList().get(7));
        }
        from_date = getPrevDate();
        to_date = gDateTime.getDateymd();

        Log.i(TAG, "from_date====>" + from_date);

        daily_status_list = new ArrayList<>();
        arrayList_delivered_orders = new ArrayList<>();
        url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
        new getDeliveredOrdersByDistTask().execute(url);


        binding.tvFromDateDeliveredOrders.setText("" + gDateTime.ymdTodmy(from_date));
        binding.tvFromDateDeliveredOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                y = Integer.parseInt(gDateTime.getYear());
                m = Integer.parseInt(gDateTime.getMonth()) - 1;
                d = Integer.parseInt(gDateTime.getDay());

                dp = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cal = Calendar.getInstance();
                        cal.set(year, month, dayOfMonth);
                        DateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        from_date = df.format(cal.getTime());


                        binding.tvFromDateDeliveredOrders.setText("" + dff.format(cal.getTime()));


                        combine_daily_status = "";
                        daily_status = "";


                        for (int i = 0; i < daily_status_list.size(); i++) {
                            combine_daily_status = combine_daily_status + daily_status_list.get(i);
                        }

                        try {

                            DecimalFormat formatter = new DecimalFormat("#,#");
                            daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                        } catch (Exception e) {
                        }

                        to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());

                        arrayList_delivered_orders = new ArrayList<>();
                        url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                        new getDeliveredOrdersByDistTask().execute(url);

                        binding.cbThisMonthDeliveredOrder.setChecked(false);

                    }
                }, y, m, d);
                dp.show();

            }
        });


        binding.tvToDateDeliveredOrders.setText("" + gDateTime.getDatedmy());
        binding.tvToDateDeliveredOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                y = Integer.parseInt(gDateTime.getYear());
                m = Integer.parseInt(gDateTime.getMonth()) - 1;
                d = Integer.parseInt(gDateTime.getDay());

                dp = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cal = Calendar.getInstance();
                        cal.set(year, month, dayOfMonth);
                        DateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        to_date = df.format(cal.getTime());
                        binding.tvToDateDeliveredOrders.setText("" + dff.format(cal.getTime()));

                        combine_daily_status = "";
                        daily_status = "";


                        for (int i = 0; i < daily_status_list.size(); i++) {
                            combine_daily_status = combine_daily_status + daily_status_list.get(i);
                        }

                        try {

                            DecimalFormat formatter = new DecimalFormat("#,#");
                            daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                        } catch (Exception e) {
                        }

                        from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());

                        arrayList_delivered_orders = new ArrayList<>();
                        url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                        new getDeliveredOrdersByDistTask().execute(url);

                        binding.cbThisMonthDeliveredOrder.setChecked(false);

                    }
                }, y, m, d);
                dp.show();

            }
        });


        binding.cbThisMonthDeliveredOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (binding.cbThisMonthDeliveredOrder.isChecked()) {


                    from_date = getPrevDate();
                    to_date = getLastDateOfMonth();

                    binding.tvToDateDeliveredOrders.setText("" + gDateTime.ymdTodmy(to_date));
                    binding.tvFromDateDeliveredOrders.setText("" + gDateTime.ymdTodmy(from_date));

                    combine_daily_status = "";
                    daily_status = "";


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }

                    arrayList_delivered_orders = new ArrayList<>();
                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    new getDeliveredOrdersByDistTask().execute(url);

                } /*else {

                    combine_daily_status = "";
                    daily_status = "";

                    binding.tvToDateDeliveredOrders.setText(""+gDateTime.getDatedmy());
                    binding.tvFromDateDeliveredOrders.setText(""+gDateTime.getDatedmy());


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }


                    from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());
                    to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());

                    arrayList_delivered_orders = new ArrayList<>();
                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    new getDeliveredOrdersByDistTask().execute(url);
                }*/

            }
        });

        binding.cbUndeliveredOrdersDeliveredOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (binding.cbUndeliveredOrdersDeliveredOrder.isChecked()) {

                    //daily_status_list.add("0");
                    daily_status_list.add("1");

                    combine_daily_status = "";
                    daily_status = "";


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }


                    if (binding.cbThisMonthDeliveredOrder.isChecked()) {

                        from_date = getPrevDate();
                        to_date = getLastDateOfMonth();

                    } else {

                        from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());
                        to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());
                    }

                    arrayList_delivered_orders = new ArrayList<>();
                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    new getDeliveredOrdersByDistTask().execute(url);

                } else {

                    //daily_status_list.remove("0");
                    daily_status_list.remove("1");

                    combine_daily_status = "";
                    daily_status = "";


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }


                    if (binding.cbThisMonthDeliveredOrder.isChecked()) {

                        from_date = getPrevDate();
                        to_date = getLastDateOfMonth();
                    } else {

                        from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());
                        to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());
                    }

                    arrayList_delivered_orders = new ArrayList<>();
                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    new getDeliveredOrdersByDistTask().execute(url);

                }
            }
        });

        binding.cbFulldeliveredOrdersDeliveredOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (binding.cbFulldeliveredOrdersDeliveredOrder.isChecked()) {

                    //daily_status_list.add("1");
                    daily_status_list.add("5");

                    combine_daily_status = "";
                    daily_status = "";


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }


                    if (binding.cbThisMonthDeliveredOrder.isChecked()) {

                        from_date = getPrevDate();
                        to_date = getLastDateOfMonth();

                    } else {

                        from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());
                        to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());
                    }

                    arrayList_delivered_orders = new ArrayList<>();
                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    new getDeliveredOrdersByDistTask().execute(url);

                } else {

                    //daily_status_list.remove("1");
                    daily_status_list.remove("5");

                    combine_daily_status = "";
                    daily_status = "";


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }


                    if (binding.cbThisMonthDeliveredOrder.isChecked()) {

                        from_date = getPrevDate();
                        to_date = getLastDateOfMonth();

                    } else {

                        from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());
                        to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());
                    }

                    arrayList_delivered_orders = new ArrayList<>();
                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    new getDeliveredOrdersByDistTask().execute(url);

                }

            }
        });

        binding.cbPartialdeliveredOrdersDeliveredOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (binding.cbPartialdeliveredOrdersDeliveredOrder.isChecked()) {

                    //daily_status_list.add("2");
                    daily_status_list.add("3");

                    combine_daily_status = "";
                    daily_status = "";


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }


                    if (binding.cbThisMonthDeliveredOrder.isChecked()) {

                        from_date = getPrevDate();
                        to_date = getLastDateOfMonth();
                    } else {

                        from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());
                        to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());
                    }

                    arrayList_delivered_orders = new ArrayList<>();
                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    new getDeliveredOrdersByDistTask().execute(url);

                } else {

                    //daily_status_list.remove("2");
                    daily_status_list.remove("3");

                    combine_daily_status = "";
                    daily_status = "";


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }


                    if (binding.cbThisMonthDeliveredOrder.isChecked()) {

                        from_date = getPrevDate();
                        to_date = getLastDateOfMonth();

                    } else {

                        from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());
                        to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());
                    }


                    arrayList_delivered_orders = new ArrayList<>();
                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    new getDeliveredOrdersByDistTask().execute(url);

                }
            }
        });

        binding.cbFaildeliveredOrdersDeliveredOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (binding.cbFaildeliveredOrdersDeliveredOrder.isChecked()) {

                    //daily_status_list.add("3");
                    daily_status_list.add("4");

                    combine_daily_status = "";
                    daily_status = "";


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }


                    if (binding.cbThisMonthDeliveredOrder.isChecked()) {

                        from_date = getPrevDate();
                        to_date = getLastDateOfMonth();

                    } else {

                        from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());
                        to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());
                    }


                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    arrayList_delivered_orders = new ArrayList<>();
                    new getDeliveredOrdersByDistTask().execute(url);

                } else {

                    //daily_status_list.remove("3");
                    daily_status_list.remove("4");

                    combine_daily_status = "";
                    daily_status = "";


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }


                    if (binding.cbThisMonthDeliveredOrder.isChecked()) {

                        from_date = getPrevDate();
                        to_date = getLastDateOfMonth();

                    } else {

                        from_date = gDateTime.dmyToymd(binding.tvFromDateDeliveredOrders.getText().toString());
                        to_date = gDateTime.dmyToymd(binding.tvToDateDeliveredOrders.getText().toString());
                    }

                    arrayList_delivered_orders = new ArrayList<>();
                    url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_url) + distributor_id + "&daily_status=" + daily_status + "&from_date=" + from_date + "&to_date=" + to_date;
                    new getDeliveredOrdersByDistTask().execute(url);

                }

            }
        });


        return root;
    }


    //========================get delivered orders by dist id task starts ======================

    public class getDeliveredOrdersByDistTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(getActivity());
            pdialog.setMessage(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setCancelable(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

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
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getDeliveredOrders() {

        DeliveredOrdersAdapter deliveredOrdersAdapter = new DeliveredOrdersAdapter();

        double total_fail_percetage = 0.0, total_amt = 0.0, total_amt_failed = 0.0;
        int total_order = 0, total_del_order = 0, del_order = 0, order = 0;

        binding.imgEmptyListDeliveredOrders.setVisibility(View.GONE);
        binding.rvDeliveredOrderListByDist.setVisibility(View.GONE);

       /* binding.tvTotalBookingAmount.setText("Total Booking : ₹ ");
        binding.tvTotalDeliveredAmount.setText("Total Delivered : ₹ ");
        binding.tvTotalPendingAmount.setText("Total Pending : ₹ ");
        binding.tvTotalFailAmount.setText("Total Fail (%) : ₹ ");
        binding.tvTotalCountDeliveredOrders.setText("Total Orders :");*/


        binding.llTotal.setVisibility(View.GONE);


        try {

            JSONObject jsonObject = new JSONObject(response);

            JSONArray data_array = jsonObject.getJSONArray("data");

            if (data_array.length() > 0) {

                binding.imgEmptyListDeliveredOrders.setVisibility(View.GONE);
                binding.rvDeliveredOrderListByDist.setVisibility(View.VISIBLE);
                binding.llTotal.setVisibility(View.VISIBLE);

                for (int i = 0; i < data_array.length(); i++) {

                    JSONObject data_object = data_array.getJSONObject(i);
                    JSONObject total_datas_object = data_object.getJSONObject("total_datas");

                    binding.tvTotalBookingAmount.setText(commanList.getArrayList().get(9) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("total_amt")));
                    binding.tvTotalDeliveredAmount.setText(commanList.getArrayList().get(10) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("total_amt_del")));
                    binding.tvTotalPendingAmount.setText(commanList.getArrayList().get(11) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("pending_amount")));

                    //total_fail_percetage = (Double.parseDouble(total_datas_object.getString("total_amt")) * Double.parseDouble(total_datas_object.getString("total_amt_failed")) / 100);

                    total_amt = Double.parseDouble(total_datas_object.getString("total_amt"));
                    total_amt_failed = Double.parseDouble(total_datas_object.getString("total_amt_failed"));

                    total_fail_percetage = ((total_amt_failed * 100) / total_amt);

                    Log.i("total_fail_percetage=>", total_fail_percetage + "");

                    binding.tvTotalFailAmount.setText(commanList.getArrayList().get(12) + "  (" + (int) total_fail_percetage + "%)   ₹ " + total_datas_object.getString("total_amt_failed"));

                    JSONArray group_datas_jsonArray = data_object.getJSONArray("group_datas");

                    for (int j = 0; j < group_datas_jsonArray.length(); j++) {

                        JSONObject group_datas_object = group_datas_jsonArray.getJSONObject(j);
                        deliveredOrderByDistIdPOJO = new DeliveredOrderByDistIdPOJO(group_datas_object.getString("total_cnt"),
                                group_datas_object.getString("total_amt"),
                                group_datas_object.getString("total_amt_del"),
                                group_datas_object.getString("total_amt_failed"),
                                group_datas_object.getString("pending_amount"),
                                group_datas_object.getString("entry_date"),
                                group_datas_object.getString("entry_date_pass"),
                                group_datas_object.getString("total_order"),
                                group_datas_object.getString("total_deliver_order"));

                        arrayList_delivered_orders.add(deliveredOrderByDistIdPOJO);

                        total_del_order = total_del_order + Integer.parseInt(group_datas_object.getString("total_deliver_order"));
                        total_order = total_order + Integer.parseInt(group_datas_object.getString("total_order"));

                        Log.i("total_del_order=>", total_del_order + "");
                        Log.i("total_order=>", total_order + "");

                    }

                    binding.tvTotalCountDeliveredOrders.setText(commanList.getArrayList().get(8) + "              " + total_del_order + " / " + total_order);


                    deliveredOrdersAdapter = new DeliveredOrdersAdapter(arrayList_delivered_orders, getActivity());
                    binding.rvDeliveredOrderListByDist.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    binding.rvDeliveredOrderListByDist.setAdapter(deliveredOrdersAdapter);
                }

            } else {

                binding.imgEmptyListDeliveredOrders.setVisibility(View.VISIBLE);
                binding.rvDeliveredOrderListByDist.setVisibility(View.GONE);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //========================get delivered orders by dist id task ends ========================


    //========================Recyclerview Adapter code starts ==========================

    public class DeliveredOrdersAdapter extends RecyclerView.Adapter<DeliveredOrdersAdapter.MyHolder> {

        ArrayList<DeliveredOrderByDistIdPOJO> arrayList_delivered_orders;
        Context context;

        double fail_percetage = 0.0;

        public DeliveredOrdersAdapter() {
        }

        public DeliveredOrdersAdapter(ArrayList<DeliveredOrderByDistIdPOJO> arrayList_delivered_orders, Context context) {
            this.arrayList_delivered_orders = arrayList_delivered_orders;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityDeliveredOrdersByDistBinding.inflate(LayoutInflater.from(context)));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") final int position) {

            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    getActivity(), setLangEntity(sp_multi_lang.getString("lang", "")));
            Language.CommanList commanList = language.getData();
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                holder.binding.tvUndeliveredOrderEntryDate.setText("" + arrayList_delivered_orders.get(position).getEntry_date());
                holder.binding.tvTotalDeliveredOrderDeliveredOrder.setText(commanList.getArrayList().get(0) + "                     " + arrayList_delivered_orders.get(position).getTotal_deliver_order() + " / " + arrayList_delivered_orders.get(position).getTotal_order());
                holder.binding.tvTotalBookingAmountDeliveredOrder.setText(commanList.getArrayList().get(1) + " ₹ " + (int) Double.parseDouble(arrayList_delivered_orders.get(position).getTotal_amt()));
                holder.binding.tvTotalDeliveredAmountDeliveredOrder.setText(commanList.getArrayList().get(2) + " ₹ " + (int) Double.parseDouble(arrayList_delivered_orders.get(position).getTotal_amt_del()));
                holder.binding.tvTotalPendingAmountDeliveredOrder.setText(commanList.getArrayList().get(3) + " ₹ " + (int) Double.parseDouble(arrayList_delivered_orders.get(position).getPending_amount()));
            }
            //============may be crash app when pending_amount=null total_amt_failed=null
            fail_percetage = (Double.parseDouble(arrayList_delivered_orders.get(position).getTotal_amt_failed()) * 100) / Double.parseDouble(arrayList_delivered_orders.get(position).getTotal_amt());

            holder.binding.tvTotalFailAmountDeliveredOrder.setText(commanList.getArrayList().get(4) + " (" + (int) fail_percetage + "%)    ₹ " + arrayList_delivered_orders.get(position).getTotal_amt_failed());

            holder.binding.llDateList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    combine_daily_status = "";
                    daily_status = "";


                    Collections.sort(daily_status_list, new Comparator<String>() {
                        @Override
                        public int compare(String lhs, String rhs) {
                            Log.i("fileter_data=>", "sort by area");
                            return lhs.compareTo(rhs);
                        }
                    });


                    for (int i = 0; i < daily_status_list.size(); i++) {
                        combine_daily_status = combine_daily_status + daily_status_list.get(i);
                    }

                    try {

                        DecimalFormat formatter = new DecimalFormat("#,#");
                        daily_status = formatter.format(Integer.parseInt(combine_daily_status));

                    } catch (Exception e) {
                    }

                    Intent intent = new Intent(context, ViewDeliveredOrdersByDate.class);
                    intent.putExtra("entry_date", arrayList_delivered_orders.get(position).getEntry_date_pass());
                    intent.putExtra("distributor_id", distributor_id);
                    intent.putExtra("distributor_name", distributor_name);
                    intent.putExtra("daily_status", daily_status);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList_delivered_orders.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityDeliveredOrdersByDistBinding binding;

            public MyHolder(EntityDeliveredOrdersByDistBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    //========================Recyclerview Adapter code ends ============================

    public String getPrevDate() {

        if (gDateTime.getMonth().length() == 1)
            return gDateTime.getYear() + "-0" + gDateTime.getMonth() + "-01";
        else
            return gDateTime.getYear() + "-" + gDateTime.getMonth() + "-01";
    }


    public String getLastDateOfMonth() {

        if (gDateTime.getMonth().length() == 1)
            return gDateTime.getYear() + "-0" + gDateTime.getMonth() + "-" + totaldayofmonth;
        else
            return gDateTime.getYear() + "-" + gDateTime.getMonth() + "-" + totaldayofmonth;

    }


    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "30");

        if (cur.getCount() > 0) {

            Log.i(TAG, "Count==>" + cur.getCount());

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
        Cursor cur = db.viewLanguage(key, "48");

        if (cur.getCount() > 0) {

            Log.i(TAG, "Count==>" + cur.getCount());

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
        Cursor cur = db.viewMultiLangSuchna("30");

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