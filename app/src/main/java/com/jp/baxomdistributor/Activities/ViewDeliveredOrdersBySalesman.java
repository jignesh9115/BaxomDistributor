package com.jp.baxomdistributor.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jp.baxomdistributor.Models.DeliveredOrderbySalesmanPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.ActivityViewDeliveredOrdersBySalesmanBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;
import com.jp.baxomdistributor.databinding.EntityDeliveredOrdersBySalesmanBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewDeliveredOrdersBySalesman extends AppCompatActivity {

    ActivityViewDeliveredOrdersBySalesmanBinding binding;

    String entry_date, distributor_name, distributor_id, salesman_id = "", salesman_name = "", url = "", response = "", daily_status = "";

    ArrayList<DeliveredOrderbySalesmanPOJO> arrayList_delivered_orders_by_salesman;
    DeliveredOrderbySalesmanPOJO deliveredOrderbySalesmanPOJO;

    GDateTime gDateTime = new GDateTime();

    ProgressDialog pdialog;

    SharedPreferences sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanList, commanSuchnaList;
    private IntentFilter minIntentFilter;

    AlertDialog ad_net_connection;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewDeliveredOrdersBySalesmanBinding.inflate(getLayoutInflater());
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
        salesman_id = getIntent().getStringExtra("salesman_id");
        salesman_name = getIntent().getStringExtra("salesman_name");
        daily_status = getIntent().getStringExtra("daily_status");

        binding.tvSalesmanNameDeliveredOrderBySalesman.setText("" + salesman_name);

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                ViewDeliveredOrdersBySalesman.this, setLang(sp_multi_lang.getString("lang", "")));
        commanList = language.getData();
        if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
            binding.tvTitleDeliveredOrderByDate.setText("" + commanList.getArrayList().get(0) + " : " + gDateTime.ymdTodmy(entry_date));
            binding.tvTotalDeliveredOrderDeliveredOrderBySalesman.setText(commanList.getArrayList().get(2) + " ");
            binding.tvTotalBookingAmountDeliveredOrderBySalesman.setText(commanList.getArrayList().get(3) + "");
            binding.tvTotalDeliveredAmountDeliveredOrderBySalesman.setText(commanList.getArrayList().get(4) + "");
            binding.tvTotalPendingAmountDeliveredOrderBySalesman.setText(commanList.getArrayList().get(5) + "");
            binding.tvTotalFailAmountDeliveredOrderBySalesman.setText(commanList.getArrayList().get(6) + ":");
        }
        Language language1 = new Language(sp_multi_lang.getString("lang", ""),
                ViewDeliveredOrdersBySalesman.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language1.getData();


        binding.tvTitleDistributorNameDeliveredOrderByDate.setText("" + distributor_name);

        binding.imgBackDeliveredOrderByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        arrayList_delivered_orders_by_salesman = new ArrayList<>();
        new getDeliveredOrdersBysalesmanTask().execute();


    }


    //========================get delivered orders by salesman id task starts ======================

    public class getDeliveredOrdersBysalesmanTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(ViewDeliveredOrdersBySalesman.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setCancelable(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            url = getString(R.string.Base_URL) + getString(R.string.DeliveredOrders_ByDistid_date_salmanid_url) + distributor_id + "&order_date=" + entry_date + "&salesman_id=" + salesman_id + "&daily_status=" + daily_status;
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


        try {

            JSONObject jsonObject = new JSONObject(response + "");

            JSONArray data_array = jsonObject.getJSONArray("data");

            for (int i = 0; i < data_array.length(); i++) {

                //JSONObject data_object = jsonObject.getJSONObject("data");
                JSONObject data_object = data_array.getJSONObject(i);

                JSONObject total_datas_object = data_object.getJSONObject("total_datas");


                binding.tvTotalDeliveredOrderDeliveredOrderBySalesman.setText(commanList.getArrayList().get(2) + " " + total_datas_object.getString("total_cnt"));
                binding.tvTotalBookingAmountDeliveredOrderBySalesman.setText(commanList.getArrayList().get(3) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("total_amt")));
                binding.tvTotalDeliveredAmountDeliveredOrderBySalesman.setText(commanList.getArrayList().get(4) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("total_amt_del")));
                binding.tvTotalPendingAmountDeliveredOrderBySalesman.setText(commanList.getArrayList().get(5) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("pending_amount")));

                total_fail_percetage = (Double.parseDouble(total_datas_object.getString("total_amt_failed")) * 100) / Double.parseDouble(total_datas_object.getString("total_amt"));

                binding.tvTotalFailAmountDeliveredOrderBySalesman.setText(commanList.getArrayList().get(6) + "  (" + (int) total_fail_percetage + "%)   ₹ " + total_datas_object.getString("total_amt_failed"));


                JSONArray group_datas_jsonArray = data_object.getJSONArray("group_datas");

                for (int j = 0; j < group_datas_jsonArray.length(); j++) {

                    JSONObject group_datas_object = group_datas_jsonArray.getJSONObject(j);
                    deliveredOrderbySalesmanPOJO = new DeliveredOrderbySalesmanPOJO(group_datas_object.getString("total_cnt"),
                            group_datas_object.getString("total_amt"),
                            group_datas_object.getString("shop_title"),
                            group_datas_object.getString("ID"),
                            group_datas_object.getString("photo"),
                            group_datas_object.getString("latitude"),
                            group_datas_object.getString("longitude"),
                            group_datas_object.getString("total_amt_del"),
                            group_datas_object.getString("daily_status"),
                            group_datas_object.getString("shop_keeper_no"));

                    arrayList_delivered_orders_by_salesman.add(deliveredOrderbySalesmanPOJO);

                }

                DeliveredOrdersBySalesmanAdapter deliveredOrdersBySalesmanAdapter = new DeliveredOrdersBySalesmanAdapter(arrayList_delivered_orders_by_salesman, ViewDeliveredOrdersBySalesman.this);
                binding.rvDeliveredOrderListBySalesman.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                binding.rvDeliveredOrderListBySalesman.setAdapter(deliveredOrdersBySalesmanAdapter);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //========================get delivered orders by salesman id task ends ========================


    //===================Recyclerview Adapter code starts ===========================

    public class DeliveredOrdersBySalesmanAdapter extends RecyclerView.Adapter<DeliveredOrdersBySalesmanAdapter.MyHolder> {

        ArrayList<DeliveredOrderbySalesmanPOJO> arrayList_delivered_orders_by_salesman;
        Context context;

        public DeliveredOrdersBySalesmanAdapter(ArrayList<DeliveredOrderbySalesmanPOJO> arrayList_delivered_orders_by_salesman, Context context) {
            this.arrayList_delivered_orders_by_salesman = arrayList_delivered_orders_by_salesman;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityDeliveredOrdersBySalesmanBinding.inflate(LayoutInflater.from(context)));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") final int position) {


            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    ViewDeliveredOrdersBySalesman.this, setLangEntity(sp_multi_lang.getString("lang", "")));
            Language.CommanList commanList = language.getData();
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                holder.binding.tvShopNameDeliveredOrderBySalesman.setText("" + arrayList_delivered_orders_by_salesman.get(position).getShop_title());
                holder.binding.tvOrderAmountDeliveredOrderBySalesman.setText(commanList.getArrayList().get(0) + " : ₹ " + (int) Double.parseDouble(arrayList_delivered_orders_by_salesman.get(position).getTotal_amt()));
                holder.binding.tvDeliveryAmountUndeliveredOrderBySalesman.setText(commanList.getArrayList().get(1) + " : ₹ " + (int) Double.parseDouble(arrayList_delivered_orders_by_salesman.get(position).getTotal_amt_del()));
            }
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.imagenotfoundicon);

            Glide.with(getApplicationContext()).load("" + arrayList_delivered_orders_by_salesman.get(position).getPhoto())
                    .apply(options)
                    .into(holder.binding.imgShopPhotoDeliveredOrderBySalesman);

            if (arrayList_delivered_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("1")) {

                holder.binding.tvDeliveredOrderStatus.setText("UN-DELIVER");
                holder.binding.tvDeliveredOrderStatus.setBackgroundColor(Color.parseColor("#E91E63"));


            } else if (arrayList_delivered_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("2")) {

                holder.binding.tvDeliveredOrderStatus.setText("FAILED ORDER");
                holder.binding.tvDeliveredOrderStatus.setBackgroundColor(Color.parseColor("#FB1504"));


            } else if (arrayList_delivered_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("3")) {

                holder.binding.tvDeliveredOrderStatus.setText("PARTIAL DELIVER");
                holder.binding.tvDeliveredOrderStatus.setBackgroundColor(Color.parseColor("#FFEB3B"));

            } else if (arrayList_delivered_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("4")) {

                holder.binding.tvDeliveredOrderStatus.setText("FAILED DELVER ORDER");
                holder.binding.tvDeliveredOrderStatus.setBackgroundColor(Color.parseColor("#FB1504"));

            } else if (arrayList_delivered_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("5")) {

                holder.binding.tvDeliveredOrderStatus.setText("DELIVER ORDER");
                holder.binding.tvDeliveredOrderStatus.setBackgroundColor(Color.parseColor("#64D568"));
            }

            holder.binding.imgBtnShopLocationDeliveredOrderBySalesman.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (arrayList_delivered_orders_by_salesman.get(position).getLatitude().equalsIgnoreCase("0.0") && arrayList_delivered_orders_by_salesman.get(position).getLongitude().equalsIgnoreCase("0.0")) {

                        Toast.makeText(context, commanSuchnaList.getArrayList().get(1) + "", Toast.LENGTH_SHORT).show();

                    } else {

                     /*   Intent intent1 = new Intent(context, MapsActivity.class);
                        intent1.putExtra("shop_latitude", arrayList_delivered_orders_by_salesman.get(position).getLatitude());
                        intent1.putExtra("shop_longitude", arrayList_delivered_orders_by_salesman.get(position).getLongitude());
                        intent1.putExtra("shop_title", arrayList_delivered_orders_by_salesman.get(position).getShop_title());
                        startActivity(intent1);*/

                        Uri navigation = Uri.parse("google.navigation:q=" + arrayList_delivered_orders_by_salesman.get(position).getLatitude() + "," + arrayList_delivered_orders_by_salesman.get(position).getLongitude() + "");
                        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                        navigationIntent.setPackage("com.google.android.apps.maps");
                        startActivity(navigationIntent);
                    }
                }
            });

            holder.binding.imgCallDeliveredOrderBySalesman.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + arrayList_delivered_orders_by_salesman.get(position).getShop_keeper_no())));

                }
            });

            holder.binding.llShopListBySalesman.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ViewSalesOrderActivity.class);
                    intent.putExtra("order_id", arrayList_delivered_orders_by_salesman.get(position).getID());
                    intent.putExtra("distributor_name", distributor_name);
                    intent.putExtra("salesman_name", salesman_name);
                    intent.putExtra("setActivity", "Delivered");
                    startActivity(intent);


                }
            });


        }

        @Override
        public int getItemCount() {
            return arrayList_delivered_orders_by_salesman.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityDeliveredOrdersBySalesmanBinding binding;

            public MyHolder(EntityDeliveredOrdersBySalesmanBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    //===================Recyclerview Adapter code ends =============================


    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "13");

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
        Cursor cur = db.viewLanguage(key, "47");

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
        Cursor cur = db.viewMultiLangSuchna("13");

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

                builder = new AlertDialog.Builder(ViewDeliveredOrdersBySalesman.this);
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