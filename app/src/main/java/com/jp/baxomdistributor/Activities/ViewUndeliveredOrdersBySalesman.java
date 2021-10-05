package com.jp.baxomdistributor.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.jp.baxomdistributor.Models.UndeliveredOrderbySalesmanPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.ActivityViewUndeliveredOrdersBySalesmanBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;
import com.jp.baxomdistributor.databinding.EntityUndeliveredOrdersBySalesmanBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewUndeliveredOrdersBySalesman extends AppCompatActivity {

    ActivityViewUndeliveredOrdersBySalesmanBinding binding;

    String TAG = getClass().getSimpleName();

    String entry_date, distributor_name, distributor_id, salesman_id = "", salesman_name = "", url = "", response = "", deliver_all_url = "", deliver_all_response = "", complete_order_url = "", complete_order_response = "";

    ArrayList<UndeliveredOrderbySalesmanPOJO> arrayList_orders_by_salesman;
    UndeliveredOrderbySalesmanPOJO orderbySalesmanPOJO;

    ProgressDialog pdialog;

    GDateTime gDateTime = new GDateTime();

    AlertDialog ad, ad_net_connection;
    AlertDialog.Builder builder;

    SharedPreferences sp_update, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanList, commanSuchnaList;

    boolean isUpdate = false;

    private IntentFilter minIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewUndeliveredOrdersBySalesmanBinding.inflate(getLayoutInflater());
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

        sp_update = getSharedPreferences("update_data", Context.MODE_PRIVATE);

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        SharedPreferences.Editor editor = sp_update.edit();
        editor.putBoolean("isUpdate", false);
        editor.apply();

        entry_date = getIntent().getStringExtra("entry_date");
        distributor_id = getIntent().getStringExtra("distributor_id");
        distributor_name = getIntent().getStringExtra("distributor_name");
        salesman_id = getIntent().getStringExtra("salesman_id");
        salesman_name = getIntent().getStringExtra("salesman_name");

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                ViewUndeliveredOrdersBySalesman.this, setLang(sp_multi_lang.getString("lang", "")));
        commanList = language.getData();
        if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
            binding.tvTitleUndeliveredOrderBySalesman.setText(commanList.getArrayList().get(0) + " : " + gDateTime.ymdTodmy(entry_date));
            binding.btnDeliverAll.setText("" + commanList.getArrayList().get(2));
        }

        Language language1 = new Language(sp_multi_lang.getString("lang", ""),
                ViewUndeliveredOrdersBySalesman.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language1.getData();

        binding.tvSalesmanNameUndeliveredOrderBySalesman.setText("" + salesman_name);

        if (distributor_name.equalsIgnoreCase("")) {
            binding.tvTitleDistributorNameUndeliveredOrderBySalesman.setText("" + salesman_name);
            binding.btnDeliverAll.setVisibility(View.GONE);
        } else {
            binding.tvTitleDistributorNameUndeliveredOrderBySalesman.setText("" + distributor_name);
        }


        arrayList_orders_by_salesman = new ArrayList<>();
        new getOrdersBySalesmanTask().execute();

        binding.imgBackUndeliveredOrderBySalesman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnDeliverAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeliverAllOrderDialog();

            }
        });

    }


    //==========================get shoplist by salesman code starts ==================

    public class getOrdersBySalesmanTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(ViewUndeliveredOrdersBySalesman.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(2) + "");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (distributor_name.equalsIgnoreCase("")) {
                url = getString(R.string.Base_URL) + getString(R.string.UndeliveredOrders_Bydistid_date_salmanid_url) + distributor_id + "&order_date=" + entry_date + "&salesman_id=" + salesman_id + "&daily_status=1,2,3,4,5";
            } else {
                url = getString(R.string.Base_URL) + getString(R.string.UndeliveredOrders_Bydistid_date_salmanid_url) + distributor_id + "&order_date=" + entry_date + "&salesman_id=" + salesman_id + "&daily_status=1";
            }
            Log.i("OrdersBySalesman url=>", url + "");

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("OrdersBySalesman res=>", response + "");

                getOrdersBySalesman();

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getOrdersBySalesman() {


        try {
            JSONObject jsonObject = new JSONObject(response + "");

            JSONObject data_object = jsonObject.getJSONObject("data");

            if (data_object.length() > 0) {

                binding.rvUndeliveredOrderBySalesmanList.setVisibility(View.VISIBLE);

                JSONObject total_datas_object = data_object.getJSONObject("total_datas");

                binding.tvTotalCountUndeliveredOrderBySalesman.setText(commanList.getArrayList().get(3) + " " + total_datas_object.getString("total_cnt"));
                binding.tvTotalAmountUndeliveredOrderBySalesman.setText(commanList.getArrayList().get(4) + " ₹ " + (int) Double.parseDouble(total_datas_object.getString("total_amt")));

                JSONArray group_datas_jsonArray = data_object.getJSONArray("group_datas");


                for (int i = 0; i < group_datas_jsonArray.length(); i++) {

                    JSONObject group_datas_object = group_datas_jsonArray.getJSONObject(i);
                    orderbySalesmanPOJO = new UndeliveredOrderbySalesmanPOJO(group_datas_object.getString("total_cnt"),
                            group_datas_object.getString("total_amt"),
                            group_datas_object.getString("shop_title"),
                            group_datas_object.getString("ID"),
                            group_datas_object.getString("photo"),
                            group_datas_object.getString("latitude"),
                            group_datas_object.getString("longitude"),
                            group_datas_object.getString("daily_status"),
                            group_datas_object.getString("shop_keeper_no"),
                            group_datas_object.getString("total_amt_del"));

                    arrayList_orders_by_salesman.add(orderbySalesmanPOJO);

                }

                OrderBySalesmanAdapter orderBySalesmanAdapter = new OrderBySalesmanAdapter(arrayList_orders_by_salesman, getApplicationContext());
                binding.rvUndeliveredOrderBySalesmanList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                binding.rvUndeliveredOrderBySalesmanList.setAdapter(orderBySalesmanAdapter);
            } else {

                binding.rvUndeliveredOrderBySalesmanList.setVisibility(View.GONE);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //==========================get shoplist by salesman code ends ====================


    //============================Recyclerview Adapter starts ========================

    public class OrderBySalesmanAdapter extends RecyclerView.Adapter<OrderBySalesmanAdapter.MyHolder> {

        ArrayList<UndeliveredOrderbySalesmanPOJO> arrayList_orders_by_salesman;
        Context context;

        public OrderBySalesmanAdapter(ArrayList<UndeliveredOrderbySalesmanPOJO> arrayList_orders_by_salesman, Context context) {
            this.arrayList_orders_by_salesman = arrayList_orders_by_salesman;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityUndeliveredOrdersBySalesmanBinding.inflate(LayoutInflater.from(context)));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") final int position) {


            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    ViewUndeliveredOrdersBySalesman.this, setLangEntity(sp_multi_lang.getString("lang", "")));
            Language.CommanList commanList = language.getData();
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                holder.binding.tvShopNameUndeliveredOrderBySalesman.setText("" + arrayList_orders_by_salesman.get(position).getShop_title());
                holder.binding.tvOrderAmountUndeliveredOrderBySalesman.setText(commanList.getArrayList().get(0) + " ₹ " + (int) Double.parseDouble(arrayList_orders_by_salesman.get(position).getTotal_amt()));
                holder.binding.tvDeliverAmountUndeliveredOrderBySalesman.setText(commanList.getArrayList().get(1) + " ₹ " + (int) Double.parseDouble(arrayList_orders_by_salesman.get(position).getTotal_amt_del()));
            }

            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.imagenotfoundicon);

            Glide.with(getApplicationContext()).load("" + arrayList_orders_by_salesman.get(position).getPhoto())
                    .apply(options)
                    .into(holder.binding.imgShopPhotoUndeliveredOrderBySalesman);

            if (distributor_name.equalsIgnoreCase("")) {

                holder.binding.tvCompleteDelivery.setEnabled(false);


                if (arrayList_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("1")) {

                    holder.binding.tvCompleteDelivery.setText("UN-DELIVER");
                    holder.binding.tvCompleteDelivery.setBackgroundColor(Color.parseColor("#E91E63"));


                } else if (arrayList_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("2")) {

                    holder.binding.tvCompleteDelivery.setText("FAILED ORDER");
                    holder.binding.tvCompleteDelivery.setBackgroundColor(Color.parseColor("#FB1504"));


                } else if (arrayList_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("3")) {

                    holder.binding.tvCompleteDelivery.setText("PARTIAL DELIVER");
                    holder.binding.tvCompleteDelivery.setBackgroundColor(Color.parseColor("#FFEB3B"));

                } else if (arrayList_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("4")) {

                    holder.binding.tvCompleteDelivery.setText("FAILED DELVER ORDER");
                    holder.binding.tvCompleteDelivery.setBackgroundColor(Color.parseColor("#FB1504"));

                } else if (arrayList_orders_by_salesman.get(position).getDaily_status().equalsIgnoreCase("5")) {

                    holder.binding.tvCompleteDelivery.setText("DELIVER ORDER");
                    holder.binding.tvCompleteDelivery.setBackgroundColor(Color.parseColor("#64D568"));
                }

            } else {

                holder.binding.tvCompleteDelivery.setText("100% \n Delivery");


            }


            holder.binding.imgBtnShopLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (arrayList_orders_by_salesman.get(position).getLatitude().equalsIgnoreCase("0.0") && arrayList_orders_by_salesman.get(position).getLongitude().equalsIgnoreCase("0.0")) {

                        Toast.makeText(context, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();

                    } else {

                       /* Intent intent1 = new Intent(context, MapsActivity.class);
                        intent1.putExtra("shop_latitude", arrayList_orders_by_salesman.get(position).getLatitude());
                        intent1.putExtra("shop_longitude", arrayList_orders_by_salesman.get(position).getLongitude());
                        intent1.putExtra("shop_title", arrayList_orders_by_salesman.get(position).getShop_title());
                        startActivity(intent1);*/

                        Uri navigation = Uri.parse("google.navigation:q=" + arrayList_orders_by_salesman.get(position).getLatitude() + "," + arrayList_orders_by_salesman.get(position).getLongitude() + "");
                        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                        navigationIntent.setPackage("com.google.android.apps.maps");
                        startActivity(navigationIntent);
                    }

                }
            });


            holder.binding.imgCallUndeliveredOrderBySalesman.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + arrayList_orders_by_salesman.get(position).getShop_keeper_no())));
                }
            });


            holder.binding.llShopListBySalesman.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ViewSalesOrderActivity.class);
                    intent.putExtra("order_id", arrayList_orders_by_salesman.get(position).getID());
                    intent.putExtra("distributor_name", distributor_name);
                    intent.putExtra("salesman_name", salesman_name);
                    intent.putExtra("setActivity", "Undelivered");
                    startActivity(intent);

                    SharedPreferences.Editor editor = sp_update.edit();
                    editor.putBoolean("isUpdate", false);
                    editor.apply();

                    Log.i(TAG, "isUpdate=>" + isUpdate);

                }
            });

            holder.binding.tvCompleteDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showupdateOrderDialog(arrayList_orders_by_salesman.get(position).getID());

                }
            });


        }

        @Override
        public int getItemCount() {
            return arrayList_orders_by_salesman.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityUndeliveredOrdersBySalesmanBinding binding;

            public MyHolder(EntityUndeliveredOrdersBySalesmanBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    //============================Recyclerview Adapter ends ==========================

    public void showupdateOrderDialog(final String o_id) {

        builder = new AlertDialog.Builder(ViewUndeliveredOrdersBySalesman.this);
        builder.setMessage(commanSuchnaList.getArrayList().get(0) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(8) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                complete_order_url = getString(R.string.Base_URL) + getString(R.string.fulldelivary_neworder_url) + o_id;
                new completeOrderTask().execute(complete_order_url);

                arrayList_orders_by_salesman = new ArrayList<>();
                new getOrdersBySalesmanTask().execute();

            }
        });

        builder.setNegativeButton(commanSuchnaList.getArrayList().get(9) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ad.dismiss();

            }
        });

        ad = builder.create();
        ad.show();

        //ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        //ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    //================================100 % complete order task starts ====================

    public class completeOrderTask extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i("complete_order_url=>", complete_order_url + "");

            HttpHandler httpHandler = new HttpHandler();
            complete_order_response = httpHandler.makeServiceCall(complete_order_url);

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("complete_order_res=>", complete_order_response + "");

                if (complete_order_response.contains("updated Successfully")) {

                    Toast.makeText(ViewUndeliveredOrdersBySalesman.this, commanSuchnaList.getArrayList().get(5) + "", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(ViewUndeliveredOrdersBySalesman.this, commanSuchnaList.getArrayList().get(6) + "", Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    //================================100 % complete order task ends ======================


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, minIntentFilter);

        isUpdate = sp_update.getBoolean("isUpdate", false);

        Log.i(TAG, "isUpdate onResume=>" + isUpdate);

        if (isUpdate) {
            arrayList_orders_by_salesman = new ArrayList<>();
            new getOrdersBySalesmanTask().execute();
        }

    }


    public void showDeliverAllOrderDialog() {

        builder = new AlertDialog.Builder(ViewUndeliveredOrdersBySalesman.this);
        builder.setMessage(commanSuchnaList.getArrayList().get(1) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(8) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new deliverAllOrderTask().execute();

            }
        });

        builder.setNegativeButton(commanSuchnaList.getArrayList().get(9) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ad.dismiss();

            }
        });

        ad = builder.create();
        ad.show();

        //ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        //ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }


    //=============================deliver all records task starts =======================

    public class deliverAllOrderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(ViewUndeliveredOrdersBySalesman.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(3) + "");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            deliver_all_url = getString(R.string.Base_URL) + getString(R.string.fulldelivar_all_neworder_url) + distributor_id +
                    "&order_date=" + entry_date + "&salesman_id=" + salesman_id;
            Log.i(TAG, "deliver_all_url=>" + deliver_all_url);

            HttpHandler httpHandler = new HttpHandler();
            deliver_all_response = httpHandler.makeServiceCall(deliver_all_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                Log.i(TAG, "deliver_all_response=>" + deliver_all_response);

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

                if (deliver_all_response.contains("all order deliver Successfully")) {

                    Toast.makeText(ViewUndeliveredOrdersBySalesman.this, commanSuchnaList.getArrayList().get(7) + "", Toast.LENGTH_SHORT).show();

                    //arrayList_orders_by_salesman = new ArrayList<>();
                    arrayList_orders_by_salesman.clear();
                    new getOrdersBySalesmanTask().execute();

                } else {

                    Toast.makeText(ViewUndeliveredOrdersBySalesman.this, commanSuchnaList.getArrayList().get(6) + "", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }
    }

    //=============================deliver all records task ends =========================


    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "16");

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
        Cursor cur = db.viewLanguage(key, "50");

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
        Cursor cur = db.viewMultiLangSuchna("16");

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

                builder = new AlertDialog.Builder(ViewUndeliveredOrdersBySalesman.this);
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

}