package com.jp.baxomdistributor.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.BuildConfig;
import com.jp.baxomdistributor.Models.DeliverySummeryDetailPOJO;
import com.jp.baxomdistributor.Models.DeliverySummeryPOJO;
import com.jp.baxomdistributor.Models.DitributorTablePOJO;
import com.jp.baxomdistributor.Models.GoodsSummeryDetailsPOJO;
import com.jp.baxomdistributor.Models.GoodsSummeryPOJO;
import com.jp.baxomdistributor.Models.GroupDatesOfSalesmanPOJO;
import com.jp.baxomdistributor.Models.SalesProdDetailPOJO;
import com.jp.baxomdistributor.Models.SchemeListPDFPOJO;
import com.jp.baxomdistributor.Models.UndeliveredOrderByDatePOJO;
import com.jp.baxomdistributor.Models.UndeliveredOrderByGroupDatePOJO;
import com.jp.baxomdistributor.Models.ViewSalesOrderByOrderIdPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.FileUtils;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.Utils.PdfUtils;
import com.jp.baxomdistributor.databinding.ActivityViewUndeliveredOrdersByDateBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;
import com.jp.baxomdistributor.databinding.EntityUndeliveredOrdersByDateBinding;
import com.jp.baxomdistributor.databinding.EntityUndeliveredOrdersByGroupDateBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewUndeliveredOrdersByDate extends AppCompatActivity {

    ActivityViewUndeliveredOrdersByDateBinding binding;
    String TAG = getClass().getSimpleName();

    String entry_date, distributor_name, distributor_id, url = "", response = "", view_sales_order_url = "", view_sales_order_response = "",
            action = "";

    double total_amount = 0;

    ArrayList<UndeliveredOrderByDatePOJO> arrayList_Order_by_date;
    UndeliveredOrderByDatePOJO salesOrderByDistIdandDatePOJO;

    ProgressDialog pdialog;

    String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    GDateTime gDateTime = new GDateTime();

    ArrayList<DitributorTablePOJO> arrayList_dist_table;
    DitributorTablePOJO ditributorTablePOJO;

    ArrayList<ViewSalesOrderByOrderIdPOJO> arrayList_view_sales_orders;
    ViewSalesOrderByOrderIdPOJO viewSalesOrderByOrderIdPOJO;

    ArrayList<UndeliveredOrderByGroupDatePOJO> arrayList_salesorders_group_date;
    UndeliveredOrderByGroupDatePOJO undeliveredOrderByGroupDatePOJO;

    ArrayList<SalesProdDetailPOJO> arrayList_Prod_Detail;
    SalesProdDetailPOJO salesProdDetailPOJO;

    ArrayList<DeliverySummeryPOJO> arrayList_delivery_summery;
    DeliverySummeryPOJO deliverySummeryPOJO;

    ArrayList<DeliverySummeryDetailPOJO> arrayList_delivery_summery_detail;
    DeliverySummeryDetailPOJO deliverySummeryDetailPOJO;

    ArrayList<GoodsSummeryPOJO> arrayList_good_summery;
    GoodsSummeryPOJO goodsSummeryPOJO;

    ArrayList<GoodsSummeryDetailsPOJO> arrayList_good_summery_detail;
    GoodsSummeryDetailsPOJO goodsSummeryDetailsPOJO;

    ArrayList<String> arrayList_order_id;
    ArrayList<GroupDatesOfSalesmanPOJO> arrayList_salesman_dates;

    ArrayList<SchemeListPDFPOJO> arrayList_scheme_name, arrayList_all_order_schemes;
    SchemeListPDFPOJO schemeListPDFPOJO;

    SharedPreferences sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanList, commanSuchnaList;

    ActivityManager.MemoryInfo memoryInfo;
    private IntentFilter minIntentFilter;

    AlertDialog ad_net_connection;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewUndeliveredOrdersByDateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Thread.currentThread().setDefaultUncaughtExceptionHandler(new HandleException());

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

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        //================code for allow strictMode starts ================

        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        //================code for allow strictMode ends ===================


        if (!AskPermissions(ViewUndeliveredOrdersByDate.this, permissionsRequired)) {
            ActivityCompat.requestPermissions(ViewUndeliveredOrdersByDate.this, permissionsRequired, 1);
        }


        Bundle bundle = getIntent().getExtras();
        action = bundle.getString("action", "");
        entry_date = getIntent().getStringExtra("entry_date");
        distributor_id = getIntent().getStringExtra("distributor_id");
        distributor_name = getIntent().getStringExtra("distributor_name");

        if (action.equalsIgnoreCase("multiple"))
            binding.llTotal.setVisibility(View.GONE);

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                ViewUndeliveredOrdersByDate.this, setLang(sp_multi_lang.getString("lang", "")));
        commanList = language.getData();
        if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
            binding.tvTitleUndeliveredOrderByDate.setText(commanList.getArrayList().get(0) + ":" + gDateTime.ymdTodmy(entry_date));
            binding.tvTotalTitleUso.setText("" + commanList.getArrayList().get(1));
            binding.btnOrderSummery.setText("" + commanList.getArrayList().get(2));
            binding.btnShopLocation.setText("" + commanList.getArrayList().get(3));

        }
        Language language1 = new Language(sp_multi_lang.getString("lang", ""),
                ViewUndeliveredOrdersByDate.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language1.getData();

        binding.tvTitleDistributorNameUndeliveredOrderByDate.setText("" + distributor_name);

        arrayList_order_id = new ArrayList<>();

        arrayList_Order_by_date = new ArrayList<>();
        new getSalesOrderByDateTask().execute();

        binding.imgBackUndeliveredOrderByDate.setOnClickListener(v -> finish());


        binding.btnOrderSummery.setOnClickListener(v -> {

            if (total_amount == 0.00 && !action.equalsIgnoreCase("multiple")) {

                Toast.makeText(ViewUndeliveredOrdersByDate.this, commanSuchnaList.getArrayList().get(2) + "", Toast.LENGTH_SHORT).show();

            } else {
                if (action.equalsIgnoreCase("multiple")) {

                    if (arrayList_salesman_dates.size() > 0) {

                        JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

                        for (int i = 0; i < arrayList_salesman_dates.size(); i++) {

                            JSONObject jGroup = new JSONObject();// /sub Object
                            try {

                                jGroup.put("salesman_id", arrayList_salesman_dates.get(i).getSalesman_id());
                                jGroup.put("date", arrayList_salesman_dates.get(i).getDate());

                                jArray.put(jGroup);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.i(TAG, "jArray==>" + jArray);

                        view_sales_order_url = getString(R.string.Base_URL) + "viewsalesorderpdf_by_group_dates.php?dist_id=" + distributor_id
                                + "&salesdate_arr=" + jArray;
                        new viewSalesOrderByIdTask().execute(view_sales_order_url);

                    } else {
                        Toast.makeText(ViewUndeliveredOrdersByDate.this, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    if (arrayList_order_id.size() > 0) {


                        binding.chkSelectAllSalesman.setChecked(false);


                        String combine_order_id = "";
                        for (int i = 0; i < arrayList_order_id.size(); i++) {

                            if (arrayList_order_id.size() > 1) {
                                combine_order_id = combine_order_id + arrayList_order_id.get(i) + ",";
                            } else {
                                combine_order_id = combine_order_id + arrayList_order_id.get(i);
                            }
                        }

                        view_sales_order_url = getString(R.string.Base_URL) + "viewsalesorderpdf.php?dist_id=" + distributor_id
                                + "&order_date=" + entry_date + "&salesman_id=" + combine_order_id.replaceAll(",$", "");

                        new viewSalesOrderByIdTask().execute(view_sales_order_url);

                    } else {
                        Toast.makeText(ViewUndeliveredOrdersByDate.this, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();
                    }
                }


            }

        });


        binding.btnGeneratePdfMergedshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (total_amount == 0.00 && !action.equalsIgnoreCase("multiple")) {

                    Toast.makeText(ViewUndeliveredOrdersByDate.this, commanSuchnaList.getArrayList().get(2) + "", Toast.LENGTH_SHORT).show();

                } else {

                    if (action.equalsIgnoreCase("multiple")) {

                        if (arrayList_salesman_dates.size() > 0) {

                            JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

                            for (int i = 0; i < arrayList_salesman_dates.size(); i++) {

                                JSONObject jGroup = new JSONObject();// /sub Object
                                try {

                                    jGroup.put("salesman_id", arrayList_salesman_dates.get(i).getSalesman_id());
                                    jGroup.put("date", arrayList_salesman_dates.get(i).getDate());

                                    jArray.put(jGroup);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            Log.i(TAG, "jArray==>" + jArray);

                            view_sales_order_url = getString(R.string.Base_URL) + "merge_shop_salesorderpdf_by_group_dates.php?dist_id=" + distributor_id
                                    + "&salesdate_arr=" + jArray;
                            new viewSalesOrderByIdTask().execute(view_sales_order_url);

                        } else {
                            Toast.makeText(ViewUndeliveredOrdersByDate.this, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        if (arrayList_order_id.size() > 0) {

                            String combine_order_id = "";

                            for (int i = 0; i < arrayList_order_id.size(); i++) {

                                if (arrayList_order_id.size() > 1) {
                                    combine_order_id = combine_order_id + arrayList_order_id.get(i) + ",";
                                } else {
                                    combine_order_id = combine_order_id + arrayList_order_id.get(i);
                                }
                            }

                            binding.chkSelectAllSalesman.setChecked(false);

                            view_sales_order_url = getString(R.string.Base_URL) + "merge_shop_salesorderpdf.php?dist_id=" + distributor_id
                                    + "&order_date=" + entry_date + "&salesman_id=" + combine_order_id.replaceAll(",$", "");
                            new viewSalesOrderByIdTask().execute(view_sales_order_url);

                        } else {
                            Toast.makeText(ViewUndeliveredOrdersByDate.this, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });


        binding.btnShopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (action.equalsIgnoreCase("multiple")) {

                    if (arrayList_salesman_dates.size() > 0) {

                        JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

                        for (int i = 0; i < arrayList_salesman_dates.size(); i++) {

                            JSONObject jGroup = new JSONObject();// /sub Object
                            try {

                                jGroup.put("salesman_id", arrayList_salesman_dates.get(i).getSalesman_id());
                                jGroup.put("date", arrayList_salesman_dates.get(i).getDate());

                                jArray.put(jGroup);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.i(TAG, "jArray==>" + jArray);

                        Intent intent = new Intent(ViewUndeliveredOrdersByDate.this, MapsActivity.class);
                        intent.putExtra("jArray", "" + jArray.toString());
                        intent.putExtra("map_action", "multiple_date");
                        startActivity(intent);

                    } else {
                        Toast.makeText(ViewUndeliveredOrdersByDate.this, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    if (arrayList_order_id.size() > 0) {

                        String combine_order_id = "";

                        for (int i = 0; i < arrayList_order_id.size(); i++) {

                            if (arrayList_order_id.size() > 1) {
                                combine_order_id = combine_order_id + arrayList_order_id.get(i) + ",";
                            } else {
                                combine_order_id = combine_order_id + arrayList_order_id.get(i);
                            }
                        }

                        Log.i(TAG, "combine_order_id==>" + combine_order_id.replaceAll(",$", ""));


                        Intent intent = new Intent(ViewUndeliveredOrdersByDate.this, MapsActivity.class);
                        intent.putExtra("salesman_Ids", "" + combine_order_id.replaceAll(",$", ""));
                        intent.putExtra("order_date", "" + entry_date);
                        intent.putExtra("map_action", "ORDERSHOP");
                        startActivity(intent);

                    } else {
                        Toast.makeText(ViewUndeliveredOrdersByDate.this, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


    }


//=======================get salesman list task starts ===================================

    public class getSalesOrderByDateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(ViewUndeliveredOrdersByDate.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (action.equalsIgnoreCase("multiple"))
                url = getString(R.string.Base_URL) + "salesorderbysalesman_bygroupdate.php?dist_id=" + distributor_id + "&order_date=" + entry_date;
            else
                url = getString(R.string.Base_URL) + getString(R.string.UndeliveredOrder_ByDistid_date_url) + distributor_id + "&order_date=" + entry_date;

            Log.i("SalesOrderByDate url=>", url + "");

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("SalesOrderByDate res=>", response + "");

                if (action.equalsIgnoreCase("multiple"))
                    getSalesOrderByGroupDate();
                else
                    getSalesOrderByDate();

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getSalesOrderByGroupDate() {

        try {

            JSONObject jsonObject = new JSONObject(response + "");
            JSONArray data_array = jsonObject.getJSONArray("data");

            if (data_array.length() > 0) {

                arrayList_order_id = new ArrayList<>();
                arrayList_salesman_dates = new ArrayList<>();
                arrayList_salesorders_group_date = new ArrayList<>();

                for (int i = 0; i < data_array.length(); i++) {

                    JSONObject data_object = data_array.getJSONObject(i);

                    JSONObject total_datas_object = data_object.getJSONObject("total_datas");

                    JSONArray group_datas_array = data_object.getJSONArray("group_datas");
                    arrayList_Order_by_date = new ArrayList<>();
                    for (int j = 0; j < group_datas_array.length(); j++) {

                        JSONObject group_datas_object = group_datas_array.getJSONObject(j);
                        salesOrderByDistIdandDatePOJO = new UndeliveredOrderByDatePOJO(group_datas_object.getString("entry_date"),
                                group_datas_object.getString("total_cnt"),
                                group_datas_object.getString("total_amt"),
                                group_datas_object.getString("salesman_id"),
                                group_datas_object.getString("salesman"),
                                group_datas_object.getString("total_amt_del"),
                                false);

                        arrayList_Order_by_date.add(salesOrderByDistIdandDatePOJO);

                    }

                    undeliveredOrderByGroupDatePOJO = new UndeliveredOrderByGroupDatePOJO(
                            data_object.getString("dates"),
                            total_datas_object.getString("total_cnt"),
                            total_datas_object.getString("total_amt"),
                            total_datas_object.getString("total_amt_del"),
                            arrayList_Order_by_date);

                    arrayList_salesorders_group_date.add(undeliveredOrderByGroupDatePOJO);
                }

                OrderByGroupDateAdapter orderByGroupDateAdapter = new OrderByGroupDateAdapter(arrayList_salesorders_group_date, ViewUndeliveredOrdersByDate.this);
                binding.rvUndeliveredOrderByDateList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                binding.rvUndeliveredOrderByDateList.setAdapter(orderByGroupDateAdapter);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getSalesOrderByDate() {

        try {
            JSONObject jsonObject = new JSONObject(response + "");
            JSONObject data_object = jsonObject.getJSONObject("data");

            JSONObject total_datas_object = data_object.getJSONObject("total_datas");

            binding.tvTotalCountUndeliveredOrderByDate.setText(commanList.getArrayList().get(4) + " " + total_datas_object.getString("total_cnt"));
            binding.tvTotalAmountUndeliveredOrderByDate.setText(commanList.getArrayList().get(5) + " ₹ " + String.format("%.2f", Double.parseDouble(total_datas_object.getString("total_amt"))));
            //binding.tvTotalAmountUndeliveredOrderByDate.setText("Total Amount : ₹-" + total_datas_object.getString("total_amt_del"));
            total_amount = Double.parseDouble(String.format("%.2f", Double.parseDouble(total_datas_object.getString("total_amt"))));
            arrayList_salesman_dates = new ArrayList<>();
            JSONArray group_datas_jsonArray = data_object.getJSONArray("group_datas");
            for (int i = 0; i < group_datas_jsonArray.length(); i++) {

                JSONObject group_datas_object = group_datas_jsonArray.getJSONObject(i);
                salesOrderByDistIdandDatePOJO = new UndeliveredOrderByDatePOJO(group_datas_object.getString("entry_date"),
                        group_datas_object.getString("total_cnt"),
                        group_datas_object.getString("total_amt"),
                        group_datas_object.getString("salesman_id"),
                        group_datas_object.getString("salesman"),
                        group_datas_object.getString("total_amt_del"),
                        false);

                arrayList_Order_by_date.add(salesOrderByDistIdandDatePOJO);

            }

            OrderByDateAdapter orderByDateAdapter = new OrderByDateAdapter(arrayList_Order_by_date, getApplicationContext());
            binding.rvUndeliveredOrderByDateList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
            binding.rvUndeliveredOrderByDateList.setAdapter(orderByDateAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

//=======================get salesman list task ends =====================================


//===================Recyclerview Adapter code starts =====================================

    public class OrderByDateAdapter extends RecyclerView.Adapter<OrderByDateAdapter.MyHolder> {

        ArrayList<UndeliveredOrderByDatePOJO> arrayList_Order_by_date;
        Context context;

        boolean isSelectedAll;

        public OrderByDateAdapter(ArrayList<UndeliveredOrderByDatePOJO> arrayList_Order_by_date, Context context) {
            this.arrayList_Order_by_date = arrayList_Order_by_date;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityUndeliveredOrdersByDateBinding.inflate(LayoutInflater.from(context)));
        }

        public void selectAll() {
            isSelectedAll = true;
            notifyDataSetChanged();
        }

        public void UnselectAll() {
            isSelectedAll = false;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {


            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    ViewUndeliveredOrdersByDate.this, setLangEntity(sp_multi_lang.getString("lang", "")));
            Language.CommanList commanList = language.getData();


            if (action.equalsIgnoreCase("multiple")) {

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 2, 0, 0);
                holder.binding.llSalesmanNameList.setLayoutParams(params);


                /*params.width = 100;
                holder.binding.llCenter.setLayoutParams(params);*/


            }


            holder.binding.tvUndeliveredOrderSalesmanName.setText("" + arrayList_Order_by_date.get(position).getSalesman());
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                holder.binding.tvUndeliveredOrderTotalAmountSalesmanList.setText(commanList.getArrayList().get(0) + " ₹ " + String.format("%.2f", Double.parseDouble(arrayList_Order_by_date.get(position).getTotal_amt())));
                holder.binding.tvUndeliveredOrderTotalCountSalesmanList.setText(commanList.getArrayList().get(1) + " " + arrayList_Order_by_date.get(position).getTotal_cnt());
            }
            holder.binding.llSalesmanNameList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(context, ViewUndeliveredOrdersBySalesman.class);
                    intent.putExtra("entry_date", arrayList_Order_by_date.get(position).getEntry_date());
                    intent.putExtra("distributor_id", distributor_id);
                    intent.putExtra("distributor_name", distributor_name);
                    intent.putExtra("salesman_id", arrayList_Order_by_date.get(position).getSalesman_id());
                    intent.putExtra("salesman_name", arrayList_Order_by_date.get(position).getSalesman());
                    intent.putExtra("action", "all_orders");
                    startActivity(intent);
                }
            });

            holder.binding.chkSelectSalesman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    arrayList_Order_by_date.get(position).setSelect(isChecked);

                    Log.i(TAG, "arrayList_salesman_dates=>" + arrayList_salesman_dates.size());

                    if (holder.binding.chkSelectSalesman.isChecked()) {

                        if (action.equalsIgnoreCase("multiple")) {

                            if (!isSalesmanAvailable(arrayList_Order_by_date.get(position).getSalesman_id(),
                                    arrayList_Order_by_date.get(position).getEntry_date())) {

                                arrayList_salesman_dates.add(new GroupDatesOfSalesmanPOJO(arrayList_Order_by_date.get(position).getSalesman_id(),
                                        arrayList_Order_by_date.get(position).getEntry_date()));
                            }

                        } else
                            arrayList_order_id.add(arrayList_Order_by_date.get(position).getSalesman_id());


                    } else {

                        if (action.equalsIgnoreCase("multiple")) {

                            if (arrayList_salesman_dates.size() > 0)
                                arrayList_salesman_dates.remove(getPosition(arrayList_Order_by_date.get(position).getSalesman_id(),
                                        arrayList_Order_by_date.get(position).getEntry_date()));
                        } else {
                            if (arrayList_order_id.size() > 0)
                                arrayList_order_id.remove(arrayList_Order_by_date.get(position).getSalesman_id());
                        }
                    }

                }
            });

            if (!isSelectedAll) holder.binding.chkSelectSalesman.setChecked(false);
            else holder.binding.chkSelectSalesman.setChecked(true);


            binding.chkSelectAllSalesman.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (binding.chkSelectAllSalesman.isChecked()) {

                    arrayList_order_id.clear();
                    selectAll();

                } else {
                    UnselectAll();
                }

            });


        }

        private boolean getcheckedvisible() {

            if (arrayList_Order_by_date.size() > 0) {
                for (int i = 0; i < arrayList_Order_by_date.size(); i++) {
                    if (arrayList_Order_by_date.get(i).isSelect())
                        return true;
                }
            }

            return false;
        }

        @Override
        public int getItemCount() {
            return arrayList_Order_by_date.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityUndeliveredOrdersByDateBinding binding;

            public MyHolder(EntityUndeliveredOrdersByDateBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    public class OrderByGroupDateAdapter extends RecyclerView.Adapter<OrderByGroupDateAdapter.MyHolder> {

        ArrayList<UndeliveredOrderByGroupDatePOJO> arrayList_salesorders_group_date;
        Context context;

        public OrderByGroupDateAdapter(ArrayList<UndeliveredOrderByGroupDatePOJO> arrayList_salesorders_group_date, Context context) {
            this.arrayList_salesorders_group_date = arrayList_salesorders_group_date;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityUndeliveredOrdersByGroupDateBinding.inflate(LayoutInflater.from(context), parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {

            holder.binding.tvOrderDate.setText("" + gDateTime.ymdTodmy(arrayList_salesorders_group_date.get(position).getDates()));

            holder.binding.tvTotalCountGroupdate.setText(commanList.getArrayList().get(4) + " " + arrayList_salesorders_group_date.get(position).getTotal_cnt());
            holder.binding.tvTotalAmountGroupdate.setText(commanList.getArrayList().get(5) + " ₹ " + arrayList_salesorders_group_date.get(position).getTotal_amt());

            OrderByDateAdapter orderByDateAdapter = new OrderByDateAdapter(arrayList_salesorders_group_date.get(position).getArrayList_Order_by_date(), getApplicationContext());
            holder.binding.rvGroupDateList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
            holder.binding.rvGroupDateList.setAdapter(orderByDateAdapter);

        }

        @Override
        public int getItemCount() {
            return arrayList_salesorders_group_date.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            EntityUndeliveredOrdersByGroupDateBinding binding;

            public MyHolder(EntityUndeliveredOrdersByGroupDateBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }

    //===================Recyclerview Adapter code ends =======================================

    public static boolean AskPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //===========================view sales order task starts ===============================

    public class viewSalesOrderByIdTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(ViewUndeliveredOrdersByDate.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(1) + "");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected Void doInBackground(String... strings) {


            Log.i("view_sales_order_url=>", view_sales_order_url + "");

            HttpHandler httpHandler = new HttpHandler();
            view_sales_order_response = httpHandler.makeServiceCall(view_sales_order_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("view_sales_order_res=>", view_sales_order_response + "");

                viewSalesOrderById();

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }

    }

    private void viewSalesOrderById() {

        ditributorTablePOJO = new DitributorTablePOJO();
        arrayList_dist_table = new ArrayList<>();

        String order_time = "";

        try {

            JSONObject jsonObject = new JSONObject(view_sales_order_response + "");
            final JSONArray data_array = jsonObject.getJSONArray("data");

            for (int i = 0; i < data_array.length(); i++) {

                viewSalesOrderByOrderIdPOJO = new ViewSalesOrderByOrderIdPOJO();
                arrayList_view_sales_orders = new ArrayList<>();

                JSONObject data_object = data_array.getJSONObject(i);

                JSONObject saleman_detail_object = data_object.getJSONObject("saleman_detail");
                JSONObject dist_order_detail_object = data_object.getJSONObject("dist_detail");

                viewSalesOrderByOrderIdPOJO.setSaleman_name(saleman_detail_object.getString("name"));

                JSONObject dist_detail_object = data_object.getJSONObject("distributor_detail");

                viewSalesOrderByOrderIdPOJO.setDist_name(dist_detail_object.getString("name"));
                viewSalesOrderByOrderIdPOJO.setDist_number(dist_detail_object.getString("number"));

                JSONObject order_detail_object = data_object.getJSONObject("order_detail");

                viewSalesOrderByOrderIdPOJO.setOrder_id(order_detail_object.getString("order_id"));
                viewSalesOrderByOrderIdPOJO.setOrder_date(order_detail_object.getString("order_date"));
                viewSalesOrderByOrderIdPOJO.setOrder_time(order_detail_object.getString("order_time"));
                order_time = order_detail_object.getString("order_time");

                JSONArray scheme_list_array = data_object.getJSONArray("scheme_list");

                arrayList_scheme_name = new ArrayList<>();

                for (int j = 0; j < scheme_list_array.length(); j++) {

                    JSONObject scheme_list_object = scheme_list_array.getJSONObject(j);

                    schemeListPDFPOJO = new SchemeListPDFPOJO(scheme_list_object.getString("scheme_id"),
                            scheme_list_object.getString("scheme_name"),
                            scheme_list_object.getString("scheme_qty"),
                            scheme_list_object.getString("result_product_qty"),
                            scheme_list_object.getString("result_product_price"));

                    arrayList_scheme_name.add(schemeListPDFPOJO);

                }


                JSONArray prod_detail_array = data_object.getJSONArray("prod_detail");

                arrayList_Prod_Detail = new ArrayList<>();

                for (int j = 0; j < prod_detail_array.length(); j++) {

                    JSONObject prod_detail_object = prod_detail_array.getJSONObject(j);

                    salesProdDetailPOJO = new SalesProdDetailPOJO(prod_detail_object.getString("product_id"),
                            prod_detail_object.getString("title"),
                            prod_detail_object.getString("product_qty"),
                            prod_detail_object.getString("product_price"),
                            prod_detail_object.getString("product_qty_del"),
                            prod_detail_object.getString("prod_unit"),
                            prod_detail_object.getString("photo"));

                    arrayList_Prod_Detail.add(salesProdDetailPOJO);

                }

                JSONObject shop_detail_object = data_object.getJSONObject("shop_detail");

                if (shop_detail_object.length() != 0) {

                    viewSalesOrderByOrderIdPOJO.setTitle(shop_detail_object.getString("title"));
                    viewSalesOrderByOrderIdPOJO.setShop_keeper_name(shop_detail_object.getString("shop_keeper_name"));
                    viewSalesOrderByOrderIdPOJO.setLatitude(shop_detail_object.getString("latitude"));
                    viewSalesOrderByOrderIdPOJO.setLongitude(shop_detail_object.getString("longitude"));
                    viewSalesOrderByOrderIdPOJO.setShop_image(shop_detail_object.getString("shop_image"));
                    viewSalesOrderByOrderIdPOJO.setLast_order_rs(shop_detail_object.getString("last_order_rs"));
                    viewSalesOrderByOrderIdPOJO.setLast_visit_ave(shop_detail_object.getString("last_visit_ave"));
                    viewSalesOrderByOrderIdPOJO.setLast_10visit_ave(shop_detail_object.getString("last_10visit_ave"));
                    viewSalesOrderByOrderIdPOJO.setContact_no(shop_detail_object.getString("contact_no"));
                    viewSalesOrderByOrderIdPOJO.setShop_keeper_no(shop_detail_object.getString("shop_keeper_no"));
                    viewSalesOrderByOrderIdPOJO.setTin_no(shop_detail_object.getString("tin_no"));
                    viewSalesOrderByOrderIdPOJO.setAddress(shop_detail_object.getString("address"));
                    viewSalesOrderByOrderIdPOJO.setShop_no(shop_detail_object.getString("shop_no"));

                }

                viewSalesOrderByOrderIdPOJO.setTotal_discount(dist_order_detail_object.getString("total_discount"));

                //Log.i("total_discount=>", dist_order_detail_object.getString("total_discount"));

                viewSalesOrderByOrderIdPOJO.setArrayList_sales_prod_detail(arrayList_Prod_Detail);

                viewSalesOrderByOrderIdPOJO.setArrayList_scheme_name(arrayList_scheme_name);

                arrayList_view_sales_orders.add(viewSalesOrderByOrderIdPOJO);

                //ditributorTablePOJO.setArrayList(arrayList_view_sales_orders);

                arrayList_dist_table.add(new DitributorTablePOJO(arrayList_view_sales_orders));
            }

            arrayList_delivery_summery = new ArrayList<>();

            JSONObject delivery_summary_object = jsonObject.getJSONObject("delivery_summary");

            JSONObject dist_detail_object = delivery_summary_object.getJSONObject("dist_detail");

            viewSalesOrderByOrderIdPOJO.setDist_name(dist_detail_object.getString("name"));
            viewSalesOrderByOrderIdPOJO.setDist_number(dist_detail_object.getString("number"));

            JSONObject dis_order_detail_object = delivery_summary_object.getJSONObject("dis_order_detail");

            JSONArray details_array = delivery_summary_object.getJSONArray("details");

            arrayList_delivery_summery_detail = new ArrayList<>();

            for (int i = 0; i < details_array.length(); i++) {

                JSONObject details_object = details_array.getJSONObject(i);

                deliverySummeryDetailPOJO = new DeliverySummeryDetailPOJO(details_object.getString("shop_no"),
                        details_object.getString("shop_name"),
                        details_object.getString("salesman"),
                        details_object.getString("order_amt"),
                        details_object.getString("del_order_amt"),
                        details_object.getString("coll_order_amt"),
                        details_object.getString("order_date"));

                arrayList_delivery_summery_detail.add(deliverySummeryDetailPOJO);

            }

            deliverySummeryPOJO = new DeliverySummeryPOJO(dist_detail_object.getString("name"),
                    dist_detail_object.getString("number"),
                    dis_order_detail_object.getString("order_date"),
                    dis_order_detail_object.getString("total_orders"),
                    dis_order_detail_object.getString("order_amt"),
                    dis_order_detail_object.getString("delivery_order_amt"),
                    dis_order_detail_object.getString("collection_amt"),
                    order_time,
                    arrayList_delivery_summery_detail);

            arrayList_delivery_summery.add(deliverySummeryPOJO);

            arrayList_good_summery = new ArrayList<>();

            JSONObject goods_summary_object = jsonObject.getJSONObject("goods_summary");

            JSONObject gs_dis_order_detail_object = goods_summary_object.getJSONObject("dis_order_detail");

            //JSONArray gs_details_array = goods_summary_object.getJSONArray("details");
            JSONArray gs_details_array = goods_summary_object.getJSONArray("details");

            arrayList_good_summery_detail = new ArrayList<>();

            for (int i = 0; i < gs_details_array.length(); i++) {

                JSONObject gs_details_object = gs_details_array.getJSONObject(i);

                goodsSummeryDetailsPOJO = new GoodsSummeryDetailsPOJO(gs_details_object.getString("prod_name"),
                        gs_details_object.getString("qty_out"),
                        gs_details_object.getString("amt_out"),
                        gs_details_object.getString("rate"),
                        gs_details_object.getString("qty_in"),
                        gs_details_object.getString("amt_in"));

                arrayList_good_summery_detail.add(goodsSummeryDetailsPOJO);

            }

            goodsSummeryPOJO = new GoodsSummeryPOJO(gs_dis_order_detail_object.getString("order_date"),
                    gs_dis_order_detail_object.getString("total_orders"),
                    gs_dis_order_detail_object.getString("order_amt"),
                    gs_dis_order_detail_object.getString("delivery_order_amt"),
                    gs_dis_order_detail_object.getString("collection_amt"),
                    order_time,
                    arrayList_good_summery_detail);

            arrayList_good_summery.add(goodsSummeryPOJO);


            JSONArray scheme_list_all_order_array = goods_summary_object.getJSONArray("scheme_list_all_order_arr");

            arrayList_all_order_schemes = new ArrayList<>();

            for (int j = 0; j < scheme_list_all_order_array.length(); j++) {

                JSONObject scheme_list_all_order_object = scheme_list_all_order_array.getJSONObject(j);

                schemeListPDFPOJO = new SchemeListPDFPOJO(scheme_list_all_order_object.getString("scheme_id"),
                        scheme_list_all_order_object.getString("scheme_name"),
                        scheme_list_all_order_object.getString("scheme_qty"),
                        scheme_list_all_order_object.getString("result_product_qty"),
                        scheme_list_all_order_object.getString("result_product_price"));

                arrayList_all_order_schemes.add(schemeListPDFPOJO);

            }


            //createPDF(data_array.length());
            //if (!memoryInfo.lowMemory)
            createPDFUtils(data_array.length());
            /*else
                Toast.makeText(this, "low memory", Toast.LENGTH_SHORT).show();*/

        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }

    //===========================view sales order task ends =================================


    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "15");

        if (cur.getCount() > 0) {

            //Log.i(TAG, "Count==>" + cur.getCount());

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
        Cursor cur = db.viewLanguage(key, "49");

        if (cur.getCount() > 0) {

            //Log.i(TAG, "Count==>" + cur.getCount());

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
        Cursor cur = db.viewMultiLangSuchna("15");

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


    //=====================code start for create pdf file =====================================


    File file;

    private void createPDFUtils(int tot_dist) {

        try {


            PdfUtils.initializeDoc();

            //===========code for Distributor table starts==
            int start_dist = 0, end_dist = 4;
            double tot_dist_pages = (Double.valueOf(tot_dist) / 4);

            Log.i(TAG, "tot_dist_pages==>" + tot_dist_pages);
            Log.i(TAG, "tot_dist_pages==>" + String.valueOf(tot_dist_pages).split("\\.")[1]);

            if (!String.valueOf(tot_dist_pages).split("\\.")[1].equals("0")) {
                tot_dist_pages = tot_dist_pages + 1;
            }

            for (int i = 0; i < (int) tot_dist_pages; i++) {

                createDistributorTable(tot_dist, start_dist, end_dist);
                if (tot_dist > end_dist) {
                    start_dist += 4;
                    end_dist += 4;
                }
            }

            //===========code for Delivery Summery table starts==
            int tot_orders = Integer.parseInt(arrayList_delivery_summery.get(0).getTotal_orders());
            int tot_del_pages = 0;

            if (tot_orders < 40)
                tot_del_pages = 1;
            else if (tot_orders > 40 && tot_orders <= 80)
                tot_del_pages = 2;
            else if (tot_orders > 80 && tot_orders <= 120)
                tot_del_pages = 3;
            else if (tot_orders > 120 && tot_orders <= 160)
                tot_del_pages = 4;
            else if (tot_orders > 160 && tot_orders <= 200)
                tot_del_pages = 5;
            else if (tot_orders > 200 && tot_orders <= 240)
                tot_del_pages = 6;
            else if (tot_orders > 240 && tot_orders <= 280)
                tot_del_pages = 7;
            else if (tot_orders > 280 && tot_orders <= 320)
                tot_del_pages = 8;
            else if (tot_orders > 320 && tot_orders <= 360)
                tot_del_pages = 9;
            else if (tot_orders > 360 && tot_orders <= 400)
                tot_del_pages = 10;

            Log.i(TAG, "tot_del_pages=>" + tot_del_pages);

            int start_order = 0, end_order = 40;

            for (int i = 0; i < tot_del_pages; i++) {

                Log.i(TAG, "start_order==>" + start_order);
                Log.i(TAG, "end_order==>" + end_order);

                createDelSummeryTable(start_order, end_order, tot_orders);

                if (tot_orders >= end_order) {
                    start_order += 40;
                    end_order += 40;
                }
            }

            //===========code for Good Summery table starts==
            createPDFUtiltGoodSummeryTable();

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmm");
                //file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Order List/" + "OrderList-" + dateFormat.format(Calendar.getInstance().getTime()) + ".pdf");
                //file = new File(FileUtils.ORDER_PDF_FOLDER_PATH + "OrderList-" + dateFormat.format(Calendar.getInstance().getTime()) + ".pdf");
                file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/OrderList-" + dateFormat.format(Calendar.getInstance().getTime()) + ".pdf");
                //file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Order List/Invoice.pdf");
                file.getParentFile().mkdirs();
                file.createNewFile();

                PdfUtils.getDocument().writeTo(new FileOutputStream(file));
                openGeneratedPDF();

            } catch (IOException e) {
                e.printStackTrace();
            }
            PdfUtils.closePage();

        } catch (Exception e) {

            Log.i(TAG, "Error Occured: " + e.getMessage());
            Toast.makeText(this, "Error Occured: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void createDistributorTable(int tot_dist, int start_dist, int end_dist) {


        //create page
        PdfUtils.createPdfPage(0, 660, 975);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);


        for (int i = start_dist; i < end_dist; i++) {

            if (i < tot_dist) {

                if (i == (start_dist + 0)) {

                    //===========code for distributor left top table starts==
                    createdisttableLeftTop(i);

                } else if (i == (start_dist + 1)) {

                    //===========code for distributor right top table starts==
                    createdisttableRightTop(i);

                } else if (i == (start_dist + 2)) {

                    //===========code for distributor left bottom table starts==
                    createdisttableLeftBottom(i);

                } else if (i == (start_dist + 3)) {

                    //===========code for distributor right bottom table starts==
                    createdisttableRightBottom(i);
                }
            }


        }


        PdfUtils.finishPage();

    }

    private void createdisttableLeftTop(int pos) {

        PdfUtils.drawImage(getDrawable(R.drawable.dist_table), 20, 20, 325, 457);
        PdfUtils.drawText("NAME : " + arrayList_dist_table.get(pos).getArrayList().get(0).getDist_name() + "("
                + arrayList_dist_table.get(pos).getArrayList().get(0).getDist_number() + ")" + "    No.: "
                + arrayList_dist_table.get(pos).getArrayList().get(0).getShop_no(), 25, 35);

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id().length() < 9)
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id(), 90, 50);
        else
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id().substring(0, 8) + "...", 90, 50);

        PdfUtils.drawText(gDateTime.ymdTodmy(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_date()) +
                " " + arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_time(), 220, 50);
        PdfUtils.drawText("ORDER/ESTIMATE", 60, 67);

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name().length() < 28)
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name(), 175, 67);
        else
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name().substring(0, 25) + "...", 175, 67);

        PdfUtils.drawText("SHOP NAME : " + arrayList_dist_table.get(pos).getArrayList().get(0).getTitle(), 25, 85);


       /* String s="opp. Naklank dham, Navagam Main road,\n" +
                "Navagam Rajkot, Gujarat 360002, India opp. Naklank dham, Navagam Main road,\n" +
                "Navagam Rajkot, Gujarat 360002, India";
        Log.i(TAG, "getAddress.length()==>" +s.length());*/

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 36) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress(), 25, 103);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 36 && arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 92) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 36) + "-", 25, 103);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(36), 25, 118);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 92 && arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 145) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 36) + "-", 25, 103);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(36, 92) + "-", 25, 118);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(92), 25, 133);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 145) {
            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 36) + "-", 25, 103);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(36, 92) + "-", 25, 118);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(92, 145) + "...", 25, 133);
        }

        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getTin_no(), 80, 150);
        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getContact_no(), 80, 167);
        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getShop_keeper_no(), 80, 184);
        double total_amount = 0;


        if (arrayList_dist_table.get(pos).getArrayList().get(0).getShop_image().isEmpty()) {
            PdfUtils.drawImage(getDrawable(R.drawable.imagenotfoundicon), 238, 140, 321, 188);
        } else {
            Drawable drawable = new BitmapDrawable(getResources(), getBitmapFromURL(arrayList_dist_table.get(pos).getArrayList().get(0).getShop_image() + ""));
            PdfUtils.drawImage(drawable, 238, 140, 321, 188);
        }


        int prod_top = 217, qty_top = 217, price_top = 217, tot_price_top = 217;
        for (int i = 0; i < arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().size(); i++) {

            if (i == 0) {

                PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getTitle(), 25, prod_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())), 177, qty_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price())), 220, price_top);
                PdfUtils.drawText(String.format("%.2f", (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                        * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()))), 262, tot_price_top);

            } else {

                PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getTitle(), 25, prod_top += 17);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())), 177, qty_top += 17);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price())), 220, price_top += 17);
                PdfUtils.drawText(String.format("%.2f", (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                        * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()))), 262, tot_price_top += 17);
            }
            total_amount = total_amount + (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                    * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()));
        }

        PdfUtils.drawText("₹ " + String.format("%.2f", total_amount), 225, 383);
        PdfUtils.drawText("-₹ " + arrayList_dist_table.get(pos).getArrayList().get(0).getTotal_discount(), 225, 400);
        PdfUtils.drawText("₹ " + String.format("%.2f", (total_amount -
                Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getTotal_discount()))), 225, 417);

        String combine_scheme_name = "";

        for (int i = 0; i < arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().size(); i++) {

            if (arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().size() > 1) {
                combine_scheme_name = combine_scheme_name + arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().get(i).getScheme_name() + ",";
            } else {
                combine_scheme_name = combine_scheme_name + arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().get(i).getScheme_name();
            }
        }

        if (combine_scheme_name.replaceAll(",$", "").length() < 48) {

            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", ""), 80, 434);

        } else if (combine_scheme_name.replaceAll(",$", "").length() > 48 && combine_scheme_name.replaceAll(",$", "").length() < 105) {

            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", "").substring(0, 48) + "-", 80, 434);
            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", "").substring(48), 25, 450);

        } else if (combine_scheme_name.replaceAll(",$", "").length() > 105) {

            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", "").substring(0, 48) + "-", 80, 434);
            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", "").substring(48, 105) + "...", 25, 450);
        }
    }

    private void createdisttableRightTop(int pos) {

        PdfUtils.drawImage(getDrawable(R.drawable.dist_table), 340, 20, 640, 457);
        PdfUtils.drawText("NAME : " + arrayList_dist_table.get(pos).getArrayList().get(0).getDist_name() + "("
                + arrayList_dist_table.get(pos).getArrayList().get(0).getDist_number() + ")" + "    No.: "
                + arrayList_dist_table.get(pos).getArrayList().get(0).getShop_no(), 345, 35);

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id().length() < 9)
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id(), 410, 50);
        else
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id().substring(0, 8) + "...", 410, 50);

        PdfUtils.drawText(gDateTime.ymdTodmy(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_date()) +
                " " + arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_time(), 535, 50);
        PdfUtils.drawText("ORDER/ESTIMATE", 380, 67);

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name().length() < 28)
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name(), 492, 67);
        else
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name().substring(0, 25) + "...", 492, 67);

        PdfUtils.drawText("SHOP NAME : " + arrayList_dist_table.get(pos).getArrayList().get(0).getTitle(), 345, 85);

        Log.i(TAG, "getAddress.length()==>" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length());

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 35) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress(), 345, 103);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 36 && arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 90) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 35) + "-", 345, 103);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(36), 345, 118);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 90 && arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 143) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 35) + "-", 345, 103);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(35, 90) + "-", 345, 118);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(90), 345, 133);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 143) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 35) + "-", 345, 103);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(35, 90) + "-", 345, 118);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(90, 143) + "...", 345, 133);
        }

        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getTin_no(), 400, 150);
        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getContact_no(), 400, 167);
        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getShop_keeper_no(), 400, 184);
        double right_top_total_amount = 0;


        if (arrayList_dist_table.get(pos).getArrayList().get(0).getShop_image().isEmpty()) {
            PdfUtils.drawImage(getDrawable(R.drawable.imagenotfoundicon), 554, 140, 636, 188);
        } else {
            Drawable drawable = new BitmapDrawable(getResources(), getBitmapFromURL(arrayList_dist_table.get(pos).getArrayList().get(0).getShop_image() + ""));
            PdfUtils.drawImage(drawable, 554, 140, 636, 188);
        }


        int right_top_prod_top = 217, right_top_qty_top = 217, right_top_price_top = 217, right_top_tot_price_top = 217;
        for (int i = 0; i < arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().size(); i++) {

            if (i == 0) {

                PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getTitle(), 345, right_top_prod_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())), 493, right_top_qty_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price())), 535, right_top_price_top);
                PdfUtils.drawText(String.format("%.2f", (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                        * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()))), 578, right_top_tot_price_top);

            } else {

                PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getTitle(), 345, right_top_prod_top += 17);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())), 493, right_top_qty_top += 17);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price())), 535, right_top_price_top += 17);
                PdfUtils.drawText(String.format("%.2f", (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                        * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()))), 578, right_top_tot_price_top += 17);
            }
            right_top_total_amount = right_top_total_amount + (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                    * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()));
        }

        PdfUtils.drawText("₹ " + String.format("%.2f", right_top_total_amount), 538, 383);
        PdfUtils.drawText("-₹ " + arrayList_dist_table.get(pos).getArrayList().get(0).getTotal_discount(), 538, 400);
        PdfUtils.drawText("₹ " + String.format("%.2f", (right_top_total_amount -
                Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getTotal_discount()))), 538, 417);

        String right_top_combine_scheme_name = "";

        for (int i = 0; i < arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().size(); i++) {

            if (arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().size() > 1) {
                right_top_combine_scheme_name = right_top_combine_scheme_name + arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().get(i).getScheme_name() + ",";
            } else {
                right_top_combine_scheme_name = right_top_combine_scheme_name + arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().get(i).getScheme_name();
            }
        }

        if (right_top_combine_scheme_name.replaceAll(",$", "").length() < 41) {

            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", ""), 400, 434);

        } else if (right_top_combine_scheme_name.replaceAll(",$", "").length() > 41 && right_top_combine_scheme_name.replaceAll(",$", "").length() < 95) {

            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", "").substring(0, 41) + "-", 400, 434);
            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", "").substring(41), 345, 450);

        } else if (right_top_combine_scheme_name.replaceAll(",$", "").length() > 95) {

            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", "").substring(0, 41) + "-", 400, 434);
            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", "").substring(41, 95) + "...", 345, 450);
        }
    }

    private void createdisttableLeftBottom(int pos) {

        PdfUtils.drawImage(getDrawable(R.drawable.dist_table), 20, 480, 325, 950);
        PdfUtils.drawText("NAME : " + arrayList_dist_table.get(pos).getArrayList().get(0).getDist_name() + "("
                + arrayList_dist_table.get(pos).getArrayList().get(0).getDist_number() + ")" + "    No.: "
                + arrayList_dist_table.get(pos).getArrayList().get(0).getShop_no(), 25, 495);

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id().length() < 9)
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id(), 90, 513);
        else
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id().substring(0, 8) + "...", 90, 513);

        PdfUtils.drawText(gDateTime.ymdTodmy(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_date()) +
                " " + arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_time(), 220, 513);
        PdfUtils.drawText("ORDER/ESTIMATE", 60, 531);

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name().length() < 28)
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name(), 175, 531);
        else
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name().substring(0, 25) + "...", 175, 531);

        PdfUtils.drawText("SHOP NAME : " + arrayList_dist_table.get(pos).getArrayList().get(0).getTitle(), 25, 548);

        Log.i(TAG, "getAddress.length()==>" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length());

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 36) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress(), 25, 566);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 36 && arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 92) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 36) + "-", 25, 566);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(36), 25, 584);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 100 && arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 145) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 36) + "-", 25, 566);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(36, 92) + "-", 25, 584);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(92), 25, 602);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 145) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 36) + "-", 25, 566);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(36, 92) + "-", 25, 584);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(92, 145) + "...", 25, 602);
        }

        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getTin_no(), 80, 620);
        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getContact_no(), 80, 638);
        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getShop_keeper_no(), 80, 656);
        double total_amount = 0;


        if (arrayList_dist_table.get(pos).getArrayList().get(0).getShop_image().isEmpty()) {
            PdfUtils.drawImage(getDrawable(R.drawable.imagenotfoundicon), 238, 609, 321, 660);
        } else {
            Drawable drawable = new BitmapDrawable(getResources(), getBitmapFromURL(arrayList_dist_table.get(pos).getArrayList().get(0).getShop_image() + ""));
            PdfUtils.drawImage(drawable, 238, 609, 321, 660);
        }


        int prod_top = 692, qty_top = 692, price_top = 692, tot_price_top = 692;
        for (int i = 0; i < arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().size(); i++) {

            if (i == 0) {

                PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getTitle(), 25, prod_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())), 177, qty_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price())), 220, price_top);
                PdfUtils.drawText(String.format("%.2f", (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                        * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()))), 262, tot_price_top);

            } else {

                PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getTitle(), 25, prod_top += 17);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())), 177, qty_top += 17);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price())), 220, price_top += 17);
                PdfUtils.drawText(String.format("%.2f", (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                        * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()))), 262, tot_price_top += 17);
            }
            total_amount = total_amount + (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                    * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()));
        }

        PdfUtils.drawText("₹ " + String.format("%.2f", total_amount), 225, 872);
        PdfUtils.drawText("-₹ " + arrayList_dist_table.get(pos).getArrayList().get(0).getTotal_discount(), 225, 889);
        PdfUtils.drawText("₹ " + String.format("%.2f", (total_amount -
                Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getTotal_discount()))), 225, 907);

        String combine_scheme_name = "";

        for (int i = 0; i < arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().size(); i++) {

            if (arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().size() > 1) {
                combine_scheme_name = combine_scheme_name + arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().get(i).getScheme_name() + ",";
            } else {
                combine_scheme_name = combine_scheme_name + arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().get(i).getScheme_name();
            }
        }

        if (combine_scheme_name.replaceAll(",$", "").length() < 48) {

            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", ""), 80, 925);

        } else if (combine_scheme_name.replaceAll(",$", "").length() > 48 && combine_scheme_name.replaceAll(",$", "").length() < 105) {

            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", "").substring(0, 48) + "-", 80, 925);
            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", "").substring(48), 25, 943);

        } else if (combine_scheme_name.replaceAll(",$", "").length() > 105) {

            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", "").substring(0, 48) + "-", 80, 925);
            PdfUtils.drawText(combine_scheme_name.replaceAll(",$", "").substring(48, 105) + "...", 25, 943);
        }
    }

    private void createdisttableRightBottom(int pos) {

        PdfUtils.drawImage(getDrawable(R.drawable.dist_table), 340, 480, 640, 950);
        PdfUtils.drawText("NAME : " + arrayList_dist_table.get(pos).getArrayList().get(0).getDist_name() + "("
                + arrayList_dist_table.get(pos).getArrayList().get(0).getDist_number() + ")" + "    No.: "
                + arrayList_dist_table.get(pos).getArrayList().get(0).getShop_no(), 345, 495);
        if (arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id().length() < 9)
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id(), 410, 513);
        else
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_id().substring(0, 8) + "...", 410, 513);

        PdfUtils.drawText(gDateTime.ymdTodmy(arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_date()) +
                " " + arrayList_dist_table.get(pos).getArrayList().get(0).getOrder_time(), 535, 513);
        PdfUtils.drawText("ORDER/ESTIMATE", 380, 531);

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name().length() < 28)
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name(), 492, 531);
        else
            PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getSaleman_name().substring(0, 25) + "...", 492, 531);

        PdfUtils.drawText("SHOP NAME : " + arrayList_dist_table.get(pos).getArrayList().get(0).getTitle(), 345, 548);

        Log.i(TAG, "getAddress.length()==>" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length());

        if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 35) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress(), 345, 566);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 35 && arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 90) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 35) + "-", 345, 566);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(36), 345, 584);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 90 && arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() < 143) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 35) + "-", 345, 566);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(35, 90) + "-", 345, 584);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(90), 345, 602);

        } else if (arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().length() > 143) {

            PdfUtils.drawText("SHOP ADDRESS : " + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(0, 35) + "-", 566, 103);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(35, 90) + "-", 345, 584);
            PdfUtils.drawText("" + arrayList_dist_table.get(pos).getArrayList().get(0).getAddress().substring(90, 143) + "...", 345, 602);
        }

        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getTin_no(), 400, 620);
        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getContact_no(), 400, 638);
        PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getShop_keeper_no(), 400, 656);
        double right_top_total_amount = 0;


        if (arrayList_dist_table.get(pos).getArrayList().get(0).getShop_image().isEmpty()) {
            PdfUtils.drawImage(getDrawable(R.drawable.imagenotfoundicon), 554, 609, 636, 660);
        } else {
            Drawable drawable = new BitmapDrawable(getResources(), getBitmapFromURL(arrayList_dist_table.get(pos).getArrayList().get(0).getShop_image() + ""));
            PdfUtils.drawImage(drawable, 554, 609, 636, 660);
        }


        int right_top_prod_top = 692, right_top_qty_top = 692, right_top_price_top = 692, right_top_tot_price_top = 692;
        for (int i = 0; i < arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().size(); i++) {

            if (i == 0) {

                PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getTitle(), 345, right_top_prod_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())), 493, right_top_qty_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price())), 535, right_top_price_top);
                PdfUtils.drawText(String.format("%.2f", (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                        * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()))), 578, right_top_tot_price_top);

            } else {

                PdfUtils.drawText(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getTitle(), 345, right_top_prod_top += 17);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())), 493, right_top_qty_top += 17);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price())), 535, right_top_price_top += 17);
                PdfUtils.drawText(String.format("%.2f", (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                        * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()))), 578, right_top_tot_price_top += 17);
            }
            right_top_total_amount = right_top_total_amount + (Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_qty())
                    * (int) Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_sales_prod_detail().get(i).getProduct_price()));
        }

        PdfUtils.drawText("₹ " + String.format("%.2f", right_top_total_amount), 538, 872);
        PdfUtils.drawText("-₹ " + arrayList_dist_table.get(pos).getArrayList().get(0).getTotal_discount(), 538, 889);
        PdfUtils.drawText("₹ " + String.format("%.2f", (right_top_total_amount -
                Double.parseDouble(arrayList_dist_table.get(pos).getArrayList().get(0).getTotal_discount()))), 538, 907);

        String right_top_combine_scheme_name = "";

        for (int i = 0; i < arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().size(); i++) {

            if (arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().size() > 1) {
                right_top_combine_scheme_name = right_top_combine_scheme_name + arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().get(i).getScheme_name() + ",";
            } else {
                right_top_combine_scheme_name = right_top_combine_scheme_name + arrayList_dist_table.get(pos).getArrayList().get(0).getArrayList_scheme_name().get(i).getScheme_name();
            }
        }

        if (right_top_combine_scheme_name.replaceAll(",$", "").length() < 41) {

            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", ""), 400, 924);

        } else if (right_top_combine_scheme_name.replaceAll(",$", "").length() > 41 && right_top_combine_scheme_name.replaceAll(",$", "").length() < 95) {

            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", "").substring(0, 41) + "-", 400, 924);
            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", "").substring(41), 345, 939);

        } else if (right_top_combine_scheme_name.replaceAll(",$", "").length() > 95) {

            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", "").substring(0, 41) + "-", 400, 924);
            PdfUtils.drawText(right_top_combine_scheme_name.replaceAll(",$", "").substring(41, 95) + "...", 345, 939);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createDelSummeryTable(int start, int end, int tot_order) {

        //create page
        PdfUtils.createPdfPage(0, 660, 975);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);

        PdfUtils.drawImage(getDrawable(R.drawable.squre_pdf), 40, 40, 620, 940);

        PdfUtils.drawText("DISTRIBUTOR NAME : " + arrayList_delivery_summery.get(0).getDist_name(), 200, 55);
        PdfUtils.drawLine(40, 60, 620, 60);
        PdfUtils.drawText("DELIVERY SUMMERY", 50, 75);


        /*code for vertical line 2nd row*/
        PdfUtils.drawLine(175, 60, 175, 80);
        PdfUtils.drawLine(330, 60, 330, 80);
        PdfUtils.drawLine(440, 60, 440, 80);

        /*code for vertical line 3rd row*/
        PdfUtils.drawLine(145, 80, 145, 112);
        PdfUtils.drawLine(205, 80, 205, 112);
        PdfUtils.drawLine(370, 80, 370, 112);
        PdfUtils.drawLine(440, 80, 440, 112);
        PdfUtils.drawLine(544, 80, 544, 112);


        PdfUtils.drawLine(90, 80, 90, 940);
        PdfUtils.drawLine(260, 80, 250, 940);
        PdfUtils.drawLine(315, 112, 315, 940);
        PdfUtils.drawLine(400, 112, 400, 940);
        PdfUtils.drawLine(475, 112, 475, 940);
        PdfUtils.drawLine(544, 112, 544, 940);

        PdfUtils.drawText("NAME OF DELIVERY MAN", 180, 75);
        PdfUtils.drawLine(40, 80, 620, 80);
        PdfUtils.drawText("DATE : ", 450, 75);
        PdfUtils.drawText("TOTAL", 45, 93);
        PdfUtils.drawText("ORDER", 45, 108);
        PdfUtils.drawText(" ORDER", 153, 93);
        PdfUtils.drawText("AMOUNT", 150, 108);
        PdfUtils.drawText("DELIVERY", 302, 93);
        PdfUtils.drawText("AMOUNT", 303, 108);
        PdfUtils.drawText("COLLECTION", 450, 93);
        PdfUtils.drawText("AMOUNT", 460, 108);
        PdfUtils.drawLine(40, 112, 620, 112);

        //PdfUtils.drawText("ORDER", 45, 125);
        PdfUtils.drawText("NO.", 53, 130);
        PdfUtils.drawText("NAME OF SHOP", 100, 130);
        PdfUtils.drawText("ORDER", 270, 125);
        PdfUtils.drawText("DATE", 275, 137);
        PdfUtils.drawText("SALESMAN", 330, 125);
        PdfUtils.drawText("NAME", 340, 137);
        PdfUtils.drawText("ORDER", 415, 125);
        PdfUtils.drawText("AMOUNT", 410, 137);
        PdfUtils.drawText("DELIVERY", 480, 125);
        PdfUtils.drawText("AMOUNT", 480, 137);
        PdfUtils.drawText("COLLECTION", 545, 125);
        PdfUtils.drawText("AMOUNT", 550, 137);

        PdfUtils.drawLine(40, 140, 620, 140);


        PdfUtils.drawText(gDateTime.ymdTodmy(arrayList_delivery_summery.get(0).getOrder_date()) + " " + arrayList_delivery_summery.get(0).getOrder_time(), 500, 75);
            /*PdfUtils.drawText(arrayList_delivery_summery.get(0).getTotal_orders(), 105, 100);
            PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_delivery_summery.get(0).getOrder_amt())), 203, 100);*/

        int orderno_top = 155, shopname_top = 155, salesman_top = 155, order_date_top = 155, orderamt_top = 155, total_orders = 0;
        double tot_order_amount = 0.0;

        int draw_line_top = 160, draw_line_top1 = 160;
        PdfUtils.drawLine(40, draw_line_top, 620, draw_line_top1);
        for (int i = 1; i < 39; i++) {
            PdfUtils.drawLine(40, draw_line_top += 20, 620, draw_line_top1 += 20);
        }

        for (int i = start; i < end; i++) {

            if (i < tot_order) {

                if (i == start) {

                    PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getOrder_no(), 45, orderno_top);

                    if (arrayList_delivery_summery_detail.get(i).getShop_name().length() > 22)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name().substring(0, 22) + "...", 92, shopname_top);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name(), 92, shopname_top);

                    PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getOrder_date(), 260, order_date_top);

                    if (arrayList_delivery_summery_detail.get(i).getSalesman().length() > 12)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman().substring(0, 9) + "...", 318, salesman_top);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman(), 318, salesman_top);

                    PdfUtils.drawText(String.format("%.0f", Double.parseDouble(arrayList_delivery_summery_detail.get(i).getOrder_amt())), 405, orderamt_top);

                } /*else if (i > (start + 9) && i < (start + 19)) {

                    PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getOrder_no(), 45, orderno_top += 19);

                    if (arrayList_delivery_summery_detail.get(i).getShop_name().length() > 28)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name().substring(0, 28) + "...", 102, shopname_top += 19);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name(), 102, shopname_top += 19);

                    if (arrayList_delivery_summery_detail.get(i).getSalesman().length() > 15)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman().substring(0, 14) + "...", 283, salesman_top += 19);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman(), 283, salesman_top += 19);

                    PdfUtils.drawText(String.format("%.0f", Double.parseDouble(arrayList_delivery_summery_detail.get(i).getOrder_amt())), 405, orderamt_top += 19);
                } */ else {

                    PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getOrder_no(), 45, orderno_top += 20);

                    if (arrayList_delivery_summery_detail.get(i).getShop_name().length() > 22)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name().substring(0, 22) + "...", 92, shopname_top += 20);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name(), 92, shopname_top += 20);

                    PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getOrder_date(), 260, order_date_top += 20);

                    if (arrayList_delivery_summery_detail.get(i).getSalesman().length() > 12)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman().substring(0, 9) + "...", 318, salesman_top += 20);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman(), 318, salesman_top += 20);

                    PdfUtils.drawText(String.format("%.0f", Double.parseDouble(arrayList_delivery_summery_detail.get(i).getOrder_amt())), 405, orderamt_top += 20);
                }


                tot_order_amount = tot_order_amount + Double.parseDouble(arrayList_delivery_summery_detail.get(i).getOrder_amt());
                total_orders++;

            }
        }

        PdfUtils.drawText(total_orders + "", 105, 100);
        PdfUtils.drawText(String.format("%.2f", tot_order_amount), 210, 100);

        PdfUtils.finishPage();
    }

    private void createPDFUtiltGoodSummeryTable() {

        //create page
        PdfUtils.createPdfPage(0, 660, 975);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);

        //PdfUtils.drawImage(getDrawable(R.drawable.good_summery_table), 20, 20, 640, 950);
        PdfUtils.drawImage(getDrawable(R.drawable.squre_pdf), 40, 40, 620, 960);
        PdfUtils.drawText("DISTRIBUTOR NAME : " + arrayList_delivery_summery.get(0).getDist_name(), 200, 55);
        PdfUtils.drawLine(40, 60, 620, 60);
        PdfUtils.drawText("GOODS SUMMERY", 50, 75);
        PdfUtils.drawText("NAME OF DELIVERY MAN", 179, 75);
        PdfUtils.drawText("DATE : ", 450, 75);
        PdfUtils.drawText(gDateTime.ymdTodmy(arrayList_delivery_summery.get(0).getOrder_date()) + " " + arrayList_delivery_summery.get(0).getOrder_time(), 500, 75);
        PdfUtils.drawLine(40, 80, 620, 80);

        PdfUtils.drawText("NAME OF PRODUCT", 100, 95);
        PdfUtils.drawLine(40, 100, 620, 100);
        PdfUtils.drawText("QTY OUT", 260, 95);
        PdfUtils.drawText("AMOUNT OUT", 320, 95);
        PdfUtils.drawText("RATE", 420, 95);
        PdfUtils.drawText("QTY IN", 500, 95);
        PdfUtils.drawText("AMOUNT IN", 550, 95);

        /*code for vertical line 2nd row*/
        PdfUtils.drawLine(175, 60, 175, 80);
        PdfUtils.drawLine(330, 60, 330, 80);
        PdfUtils.drawLine(440, 60, 440, 80);


        PdfUtils.drawLine(259, 80, 259, 900);
        PdfUtils.drawLine(318, 80, 318, 960);
        PdfUtils.drawLine(418, 80, 418, 960);
        PdfUtils.drawLine(498, 80, 498, 900);
        PdfUtils.drawLine(548, 80, 548, 960);

        int draw_line_top = 120, draw_line_top1 = 120;
        int prodnm_top = 115, qty_top = 115, rate_top = 115, orderamtout_top = 115;

        PdfUtils.drawLine(40, draw_line_top, 620, draw_line_top1);
        for (int i = 0; i < 39; i++) {
            PdfUtils.drawLine(40, draw_line_top += 20, 620, draw_line_top1 += 20);
        }

        double tot_good_amount_by_delman = 0, tot_scheme_amt_out = 0;
        for (int i = 0; i < arrayList_good_summery_detail.size(); i++) {

            if (i == 0) {

                PdfUtils.drawText(arrayList_good_summery_detail.get(i).getProd_name(), 43, prodnm_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_good_summery_detail.get(i).getQty_out())), 263, qty_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_good_summery_detail.get(i).getAmt_out())), 330, rate_top);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_good_summery_detail.get(i).getRate())), 420, orderamtout_top);

            }/* else if (i > 6 && i < 17) {

                PdfUtils.drawText(arrayList_good_summery_detail.get(i).getProd_name(), 43, prodnm_top += 18);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_good_summery_detail.get(i).getQty_out())), 263, qty_top += 18);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_good_summery_detail.get(i).getAmt_out())), 345, rate_top += 18);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_good_summery_detail.get(i).getRate())), 420, orderamtout_top += 18);

            }*/ else {

                PdfUtils.drawText(arrayList_good_summery_detail.get(i).getProd_name(), 43, prodnm_top += 20);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_good_summery_detail.get(i).getQty_out())), 263, qty_top += 20);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_good_summery_detail.get(i).getAmt_out())), 330, rate_top += 20);
                PdfUtils.drawText(String.format("%.2f", Double.parseDouble(arrayList_good_summery_detail.get(i).getRate())), 420, orderamtout_top += 20);
            }

            tot_good_amount_by_delman = tot_good_amount_by_delman + Double.parseDouble(arrayList_good_summery_detail.get(i).getAmt_out());

        }

        for (int i = 0; i < arrayList_all_order_schemes.size(); i++) {


            PdfUtils.drawText("(scheme) " + arrayList_all_order_schemes.get(i).getScheme_name(), 43, prodnm_top += 20);
            PdfUtils.drawText("" + (Double.parseDouble(arrayList_all_order_schemes.get(i).getScheme_qty())), 263, rate_top += 20);
            PdfUtils.drawText("-" + String.format("%.2f", ((Double.parseDouble(arrayList_all_order_schemes.get(i).getScheme_qty())
                    * Double.parseDouble(arrayList_all_order_schemes.get(i).getResult_product_qty())
                    * Double.parseDouble(arrayList_all_order_schemes.get(i).getResult_product_price())))), 330, qty_top += 20);
            PdfUtils.drawText("-" + String.format("%.2f", (Double.parseDouble(arrayList_all_order_schemes.get(i).getResult_product_qty())
                    * Double.parseDouble(arrayList_all_order_schemes.get(i).getResult_product_price()))), 420, orderamtout_top += 20);

            tot_scheme_amt_out = tot_scheme_amt_out + ((Double.parseDouble(arrayList_all_order_schemes.get(i).getScheme_qty())
                    * Double.parseDouble(arrayList_all_order_schemes.get(i).getResult_product_qty())
                    * Double.parseDouble(arrayList_all_order_schemes.get(i).getResult_product_price())));
        }

        PdfUtils.drawText("₹ " + String.format("%.2f", (tot_good_amount_by_delman - tot_scheme_amt_out)), 322, 920);

        PdfUtils.drawText("TOTAL AMOUNT OF GOODS GIVEN TO", 70, 915);
        PdfUtils.drawText("DELIVERY MAN", 70, 928);
        PdfUtils.drawText("TOTAL DELIVERED GOODS AMOUNT", 70, 950);
        PdfUtils.drawText("RETURN GOODS", 420, 915);
        PdfUtils.drawText(" AMOUNT", 422, 928);
        PdfUtils.drawText("SIGN OF DELIVERY", 420, 945);
        PdfUtils.drawText("MAN", 422, 957);

        PdfUtils.drawLine(40, 930, 620, 930);


        PdfUtils.finishPage();
    }

    private void openGeneratedPDF() {

        //file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/PurchaseOrder.pdf");
        if (file.exists()) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            //====================fileProvider ===========

            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }

            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "No Application Available For PDF View", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public void clearMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    //=====================code end for create pdf file =======================================


    public boolean isSalesmanAvailable(String salesman_id, String date) {

        for (int i = 0; i < arrayList_salesman_dates.size(); i++) {
            if (salesman_id.equalsIgnoreCase(arrayList_salesman_dates.get(i).getSalesman_id())
                    && date.equalsIgnoreCase(arrayList_salesman_dates.get(i).getDate()))
                return true;
        }
        return false;
    }

    public int getPosition(String sId, String date) {

        for (int i = 0; i < arrayList_salesman_dates.size(); i++) {

            if (sId.equalsIgnoreCase(arrayList_salesman_dates.get(i).getSalesman_id())
                    && date.equalsIgnoreCase(arrayList_salesman_dates.get(i).getDate()))
                return i;
        }
        return 0;
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

                builder = new AlertDialog.Builder(ViewUndeliveredOrdersByDate.this);
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