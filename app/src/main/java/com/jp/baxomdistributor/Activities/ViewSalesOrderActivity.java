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
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jp.baxomdistributor.Models.MatchShemePOJO;
import com.jp.baxomdistributor.Models.OrderProductListPOJO;
import com.jp.baxomdistributor.Models.SalesOrderProductPOJO;
import com.jp.baxomdistributor.Models.SchemeListPOJO;
import com.jp.baxomdistributor.Models.SchemeProductPOJO;
import com.jp.baxomdistributor.Models.SchemesOrderPOJO;
import com.jp.baxomdistributor.Models.UpdateSalesOrderProductPOJO;
import com.jp.baxomdistributor.Models.ViewSchemesOrderPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.ActivityViewSalesOrderBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;
import com.jp.baxomdistributor.databinding.DialogShowShopPhotoBinding;
import com.jp.baxomdistributor.databinding.EntitySalesOrderProductBinding;
import com.jp.baxomdistributor.databinding.EntityViewOrderSchemesListBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewSalesOrderActivity extends AppCompatActivity {

    ActivityViewSalesOrderBinding binding;

    String TAG = getClass().getSimpleName();

    String order_id, url = "", response = "", update_sales_order_url = "", update_sales_order_response = "", update_fail_confirmation_url = "",
            update_fail_confirmation_response = "", send_to_redeliver_url = "", send_to_redeliver_response = "";
    String shop_latitude, shop_longitude, shop_photo = "", distributor_name = "", del_fail_reason = "", del_fail_reason_id = "", salesman_name = "", setActivity = "", next_shop_id = "", salesman_id = "", bit_id = "", shop_id = "", dist_id = "";

    double purchase_rate_total_order_rs = 0, total_order_rs = 0, total_discount = 0.0, total_scheme_amount = 0.0;

    ArrayList<SalesOrderProductPOJO> arrayList_sales_order_product;
    SalesOrderProductPOJO salesOrderProductPOJO;

    ArrayList<UpdateSalesOrderProductPOJO> arrayList_update_order_product;
    UpdateSalesOrderProductPOJO updateSalesOrderProductPOJO;

    ArrayList<String> arrayList_delivery_fail_reasons_id, arrayList_delivery_fail_reasons;
    ArrayAdapter<String> arrayAdapter;

    ArrayList<SchemeListPOJO> arrayList_scheme_list;
    SchemeListPOJO schemeListPOJO;

    ArrayList<SchemeProductPOJO> arrayList_scheme_prod;
    SchemeProductPOJO schemeProductPOJO;

    ArrayList<ViewSchemesOrderPOJO> arrayList_view_scheme_order_list;
    ViewSchemesOrderPOJO viewSchemesOrderPOJO;

    ArrayList<MatchShemePOJO> arrayList_match_scheme_list, arrayList_total_scheme_order_list;
    MatchShemePOJO matchShemePOJO;

    ArrayList<Double> arrayList_scheme_order_discount;

    ArrayList<SchemesOrderPOJO> arrayList_schemes_order_list;
    SchemesOrderPOJO schemesOrderPOJO;

    boolean isSchemeCheck = false;

    ProgressDialog pdialog;

    GDateTime gDateTime = new GDateTime();

    AlertDialog ad, ad_finish, ad_shop_photo, ad_net_connection;
    AlertDialog.Builder builder;

    ArrayList<OrderProductListPOJO> arrayList_order_product_list;

    ArrayList<Double> arrayList_minvalue;

    SharedPreferences sp_update, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanList, commanSuchnaList;

    boolean isUpdate = false;
    //ArrayList<String> arrayList_scheme_list;
    int reordercount = 0;
    private IntentFilter minIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewSalesOrderBinding.inflate(getLayoutInflater());
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

        Animation animBlink = AnimationUtils.loadAnimation(this,
                R.anim.blink);

        animBlink.setDuration(400);
        binding.tvAvailableTitleViewOrder.startAnimation(animBlink);

        sp_update = getSharedPreferences("update_data", Context.MODE_PRIVATE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        setLanguage(sp_multi_lang.getString("lang", ""));


        Language language = new Language(sp_multi_lang.getString("lang", ""),
                ViewSalesOrderActivity.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language.getData();

        order_id = getIntent().getStringExtra("order_id");
        distributor_name = getIntent().getStringExtra("distributor_name");
        salesman_name = getIntent().getStringExtra("salesman_name");
        setActivity = getIntent().getStringExtra("setActivity");

        //Toast.makeText(this, ""+salesman_name, Toast.LENGTH_SHORT).show();

        if (setActivity.equalsIgnoreCase("Undelivered")) {

            if (distributor_name.equalsIgnoreCase("")) {

                binding.llFailReason.setVisibility(View.GONE);
                binding.llConfirmButtons.setVisibility(View.GONE);
                binding.btnSubmitUpdateOrder.setVisibility(View.GONE);

            } else {

                binding.llFailReason.setVisibility(View.VISIBLE);
                binding.llConfirmButtons.setVisibility(View.GONE);
                binding.btnSubmitUpdateOrder.setVisibility(View.VISIBLE);
            }

        } else if (setActivity.equalsIgnoreCase("Delivered")) {

            binding.llFailReason.setVisibility(View.GONE);
            binding.llConfirmButtons.setVisibility(View.GONE);
            binding.btnSubmitUpdateOrder.setVisibility(View.GONE);

        } else if (setActivity.equalsIgnoreCase("DelFailConfirm")) {

            binding.llFailReason.setVisibility(View.GONE);
            binding.llDeliveryFailReason.setVisibility(View.VISIBLE);
            binding.llConfirmButtons.setVisibility(View.VISIBLE);
            binding.btnSubmitUpdateOrder.setVisibility(View.GONE);

        }

        //Toast.makeText(this, "" + order_id, Toast.LENGTH_SHORT).show();

        if (distributor_name.equalsIgnoreCase("")) {

            binding.tvDistributorNameSalesorder.setText("" + salesman_name);
            binding.tvTitleSalesOrder.setText("" + commanList.getArrayList().get(0));

            //binding.tvTitleSalesOrder.setText("SHOP DELIEVERY-ORDER");

        } else {

            binding.tvDistributorNameSalesorder.setText("" + distributor_name);
            binding.tvTitleSalesOrder.setText(commanList.getArrayList().get(0) + " " + salesman_name);
        }

        arrayList_update_order_product = new ArrayList<>();

        url = getString(R.string.Base_URL) + getString(R.string.viewsales_orderdetail_url) + order_id;
        new getSalesOrderByOrderidTask().execute(url);

        binding.imgBackSalesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.spnDeliveryFailReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                del_fail_reason = arrayList_delivery_fail_reasons.get(position);
                del_fail_reason_id = arrayList_delivery_fail_reasons_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.imgShopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (shop_latitude.equalsIgnoreCase(" ") && shop_longitude.equalsIgnoreCase(" ")) {

                    Toast.makeText(ViewSalesOrderActivity.this, commanSuchnaList.getArrayList().get(6) + "", Toast.LENGTH_SHORT).show();

                } else {

                   /* Intent intent1 = new Intent(ViewSalesOrderActivity.this, MapsActivity.class);
                    intent1.putExtra("shop_latitude", shop_latitude);
                    intent1.putExtra("shop_longitude", shop_longitude);
                    intent1.putExtra("shop_title", binding.tvShopNameSalesOrder.getText().toString().trim());
                    startActivity(intent1);*/

                    Uri navigation = Uri.parse("google.navigation:q=" + shop_latitude + "," + shop_longitude + "");
                    Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                    navigationIntent.setPackage("com.google.android.apps.maps");
                    startActivity(navigationIntent);
                }

            }
        });

        binding.btnCheckSchemeViewOrder.setOnClickListener(v -> {

            ArrayList<OrderProductListPOJO> arrayList_match_order_product_list = new ArrayList<>();

            JSONObject order_product_list = new JSONObject();// main object
            JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

            for (int i = 0; i < arrayList_update_order_product.size(); i++) {

                JSONObject jGroup = new JSONObject();// /sub Object

                try {

                    if (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) > 0) {

                        jGroup.put("p_id", arrayList_update_order_product.get(i).getP_id());
                        jGroup.put("p_qty", arrayList_update_order_product.get(i).getP_qty_del());
                        jGroup.put("p_price", arrayList_update_order_product.get(i).getP_price());

                        arrayList_match_order_product_list.add(new OrderProductListPOJO(arrayList_update_order_product.get(i).getP_id(),
                                arrayList_update_order_product.get(i).getP_qty_del(),
                                arrayList_update_order_product.get(i).getP_price(),
                                "", "", "", "", "", "", "", ""));

                        jArray.put(jGroup);
                    }

                    // order_product_list Name is JsonArray Name
                    order_product_list.put("prod_list", jArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.i(TAG, "order prod list==>" + jArray);

            if (arrayList_match_order_product_list.size() > 0) {
                //binding.btnSubmitShopOrder.setEnabled(true);

                isSchemeCheck = true;

                arrayList_match_scheme_list = new ArrayList<>();
                arrayList_total_scheme_order_list = new ArrayList<>();
                arrayList_scheme_order_discount = new ArrayList<>();

                arrayList_schemes_order_list = new ArrayList<>();

                double scheme_value = 0.0;

                for (int j = 0; j < arrayList_view_scheme_order_list.size(); j++) {

                    Log.i(TAG, "<=========scheme id========>" + arrayList_view_scheme_order_list.get(j).getScheme_id());

                    scheme_value = 0.0;

                    if (arrayList_view_scheme_order_list.get(j).getArrayList().size() > 0) {

                        for (int i = 0; i < jArray.length(); i++) {

                            for (int n = 0; n < arrayList_view_scheme_order_list.get(j).getArrayList().size(); n++) {

                                //Log.i(TAG, "order_product_id=>" + arrayList_match_order_product_list.get(i).getP_id());
                                //Log.i(TAG, "scheme_product_id=>" + arrayList_scheme_list.get(j).getArrayList().get(n).getProduct_id());

                                if (arrayList_match_order_product_list.get(i).getP_id().equalsIgnoreCase(arrayList_view_scheme_order_list.get(j).getArrayList().get(n).getProduct_id())) {

                                    //Log.i(TAG, "match product_id=>" + arrayList_match_order_product_list.get(i).getP_id());
                                    //Log.i(TAG, "match product_qty=>" + arrayList_match_order_product_list.get(i).getP_qty());

                                    scheme_value = scheme_value + Double.parseDouble(arrayList_match_order_product_list.get(i).getP_qty()) * Double.parseDouble(arrayList_match_order_product_list.get(i).getP_price());

                                    // Log.i(TAG, "match product_order_value=>" + scheme_value);

                                } else {

                                    // Log.i(TAG, "inside else doen't match");

                                }
                            }
                        }
                    }

                    arrayList_match_scheme_list.add(new MatchShemePOJO(arrayList_view_scheme_order_list.get(j).getScheme_id(),
                            0 + "",
                            scheme_value + ""));

                    arrayList_total_scheme_order_list.add(new MatchShemePOJO(arrayList_view_scheme_order_list.get(j).getScheme_id(),
                            "0.0",
                            scheme_value + ""));

                    arrayList_scheme_order_discount.add(0.0);

                }

                JSONArray match_scheme_array = new JSONArray();

                for (int i = 0; i < arrayList_match_scheme_list.size(); i++) {

                    JSONObject jGroup = new JSONObject();// /sub Object

                    try {

                        jGroup.put("Scheme_id", arrayList_match_scheme_list.get(i).getScheme_id());
                        jGroup.put("Scheme_qty", arrayList_match_scheme_list.get(i).getScheme_qty());
                        jGroup.put("Scheme_price", arrayList_match_scheme_list.get(i).getScheme_price());

                        match_scheme_array.put(jGroup);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.i(TAG, "Match_Scheme_list==>" + match_scheme_array);


                SchemListAdapter schemListAdapter = new SchemListAdapter(arrayList_view_scheme_order_list, ViewSalesOrderActivity.this);
                binding.rvSchemeListViewOrder.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                binding.rvSchemeListViewOrder.setAdapter(schemListAdapter);

            } else {

                Toast.makeText(ViewSalesOrderActivity.this, commanSuchnaList.getArrayList().get(7) + "", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSubmitUpdateOrder.setOnClickListener(v -> {

            boolean check_qty = false;

            purchase_rate_total_order_rs = 0.0;

            JSONObject order_product_list = new JSONObject();// main object
            JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

            for (int i = 0; i < arrayList_update_order_product.size(); i++) {
                JSONObject jGroup = new JSONObject();// /sub Object

                try {

                    //if (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) > 0) {

                    jGroup.put("p_id", arrayList_update_order_product.get(i).getP_id());
                    jGroup.put("p_qty", arrayList_update_order_product.get(i).getP_qty());
                    jGroup.put("p_qty_del", arrayList_update_order_product.get(i).getP_qty_del());
                    jGroup.put("p_price", arrayList_update_order_product.get(i).getP_price());
                    jGroup.put("p_purchase_price", arrayList_sales_order_product.get(i).getPurchase_rate());
                    jArray.put(jGroup);
                    //}


                    purchase_rate_total_order_rs = purchase_rate_total_order_rs +
                            (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) *
                                    Double.parseDouble(arrayList_sales_order_product.get(i).getPurchase_rate()));

                    if (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) <
                            Double.parseDouble(arrayList_update_order_product.get(i).getP_qty())) {
                        check_qty = true;
                    }

                    // order_product_list Name is JsonArray Name
                    order_product_list.put("prod_list", jArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.i(TAG, "deliver_prod_list==>" + jArray);

            JSONArray match_scheme_array = new JSONArray();

            for (int i = 0; i < arrayList_schemes_order_list.size(); i++) {

                JSONObject jGroup = new JSONObject();// /sub Object

                try {

                    if (isSchemeCheck == true) {

                        if (Double.parseDouble(arrayList_schemes_order_list.get(i).getScheme_qty()) > 0) {

                            jGroup.put("scheme_id", arrayList_schemes_order_list.get(i).getScheme_id());
                            //jGroup.put("Scheme_name", arrayList_schemes_order_list.get(i).getScheme_name());
                            //jGroup.put("Scheme_long_name", arrayList_schemes_order_list.get(i).getScheme_long_name());
                            //jGroup.put("Scheme_type_id", arrayList_schemes_order_list.get(i).getScheme_type_id());
                            //jGroup.put("Scheme_type_name", arrayList_schemes_order_list.get(i).getScheme_type_name());
                            //jGroup.put("Scheme_image", arrayList_schemes_order_list.get(i).getScheme_image());
                            jGroup.put("scheme_price", arrayList_schemes_order_list.get(i).getScheme_price());
                            //jGroup.put("Scheme_value", arrayList_schemes_order_list.get(i).getScheme_value());
                            jGroup.put("scheme_qty", arrayList_schemes_order_list.get(i).getScheme_qty());
                            jGroup.put("scheme_qty_del", arrayList_schemes_order_list.get(i).getScheme_qty_del());
                            //jGroup.put("Result_product_id", arrayList_schemes_order_list.get(i).getResult_product_id());
                            //jGroup.put("Result_product_qty", arrayList_schemes_order_list.get(i).getResult_product_qty());
                            //jGroup.put("Result_product_price", arrayList_schemes_order_list.get(i).getResult_product_price());
                            //jGroup.put("Result_product_total_price", arrayList_schemes_order_list.get(i).getResult_product_total_price());
                            //jGroup.put("Result_product_image", arrayList_schemes_order_list.get(i).getResult_product_image());
                            //jGroup.put("Is_scheme_half", arrayList_schemes_order_list.get(i).getIs_scheme_half());

                            match_scheme_array.put(jGroup);
                        }

                    } else {

                        if (Double.parseDouble(arrayList_schemes_order_list.get(i).getScheme_value()) > 0) {

                            jGroup.put("scheme_id", arrayList_schemes_order_list.get(i).getScheme_id());
                            //jGroup.put("Scheme_name", arrayList_schemes_order_list.get(i).getScheme_name());
                            //jGroup.put("Scheme_long_name", arrayList_schemes_order_list.get(i).getScheme_long_name());
                            //jGroup.put("Scheme_type_id", arrayList_schemes_order_list.get(i).getScheme_type_id());
                            //jGroup.put("Scheme_type_name", arrayList_schemes_order_list.get(i).getScheme_type_name());
                            //jGroup.put("Scheme_image", arrayList_schemes_order_list.get(i).getScheme_image());
                            jGroup.put("scheme_price", arrayList_schemes_order_list.get(i).getScheme_price());
                            //jGroup.put("Scheme_value", arrayList_schemes_order_list.get(i).getScheme_value());
                            jGroup.put("scheme_qty", arrayList_schemes_order_list.get(i).getScheme_qty());
                            jGroup.put("scheme_qty_del", arrayList_schemes_order_list.get(i).getScheme_qty_del());
                            //jGroup.put("Result_product_id", arrayList_schemes_order_list.get(i).getResult_product_id());
                            //jGroup.put("Result_product_qty", arrayList_schemes_order_list.get(i).getResult_product_qty());
                            //jGroup.put("Result_product_price", arrayList_schemes_order_list.get(i).getResult_product_price());
                            //jGroup.put("Result_product_total_price", arrayList_schemes_order_list.get(i).getResult_product_total_price());
                            //jGroup.put("Result_product_image", arrayList_schemes_order_list.get(i).getResult_product_image());
                            //jGroup.put("Is_scheme_half", arrayList_schemes_order_list.get(i).getIs_scheme_half());

                            match_scheme_array.put(jGroup);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.i(TAG, "Match_Scheme_list==>" + match_scheme_array);
            //Log.i(TAG, "Match_Scheme_list_size==>" + arrayList_schemes_order_list.size());


            if ((Double.parseDouble(binding.tvTotalSchemeOrderValueViewOrder.getText().toString()) >
                    Double.parseDouble(binding.tvDeliveryRsSalesOrder.getText().toString().trim()))) {

                Toast.makeText(ViewSalesOrderActivity.this, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

            } else {

                if ((jArray.length() > 0) || Integer.parseInt(del_fail_reason_id) > 1) {

                    Log.i(TAG, "check_qty===>" + check_qty);

                    if (check_qty == true && binding.spnDeliveryFailReason.getSelectedItem().toString().equalsIgnoreCase("-")) {

                        Toast.makeText(getApplicationContext(), commanSuchnaList.getArrayList().get(9) + "", Toast.LENGTH_SHORT).show();

                    } else {

                        getTotalOrderRs();
                        update_sales_order_url = getString(R.string.Base_URL) + getString(R.string.updateneworder_url) + jArray +
                                "&order_id=" + order_id + "&del_fail_reason=" + del_fail_reason + "&scheme_list=" + match_scheme_array + "&total_discount=" + total_discount;
                        //Log.i(TAG, "update_order_url=>" + update_sales_order_url + "");
                        new updateSalesOrderTask().execute(update_sales_order_url);

                        //Toast.makeText(ViewSalesOrderActivity.this, "complete orders", Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(ShopOrderActivity.this, "" + jArray, Toast.LENGTH_LONG).show();
                    //Toast.makeText(ShopOrderActivity.this, "total_rs=" + total_rs + "\ntotal_line=" + total_line + "\nlatitude=" + latitude + "\nlongitude=" + longitude, Toast.LENGTH_LONG).show();


                } else {

                    Toast.makeText(ViewSalesOrderActivity.this, commanSuchnaList.getArrayList().get(10) + "", Toast.LENGTH_LONG).show();

                }

            }
            //Toast.makeText(ViewSalesOrderActivity.this, "" + jArray + "\n" + order_id + "\n" + del_fail_reason, Toast.LENGTH_SHORT).show();


            SharedPreferences.Editor editor = sp_update.edit();
            editor.putBoolean("isUpdate", true);
            editor.apply();

            isUpdate = sp_update.getBoolean("isUpdate", false);
            Log.i(TAG, "isUpdate=>" + isUpdate);

        });

        binding.imgShopPhotoSalesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (shop_photo == null) {


                } else {

                    DialogShowShopPhotoBinding binding1;
                    builder = new AlertDialog.Builder(ViewSalesOrderActivity.this);
                    binding1 = DialogShowShopPhotoBinding.inflate(getLayoutInflater());
                    builder.setView(binding1.getRoot());

                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.imagenotfoundicon);

                    Glide.with(getApplicationContext()).load(shop_photo + "")
                            .apply(options)
                            .into(binding1.imgShowShopPhoto);

                    ad_shop_photo = builder.create();
                    ad_shop_photo.show();

                }
            }
        });


        binding.imgShopData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ViewSalesOrderActivity.this, UpdateShopActivity.class);
                intent.putExtra("shop_id", shop_id);
                startActivity(intent);

            }
        });

        binding.btnConfirmFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if (Integer.parseInt(del_fail_reason_id) > 1) {

                //update_fail_confirmation_url = getString(R.string.Base_URL) + "updateconfirmationfailorder.php?order_id=" + order_id + "&sales_del_fail_reason=" + binding.spnDeliveryFailReason.getSelectedItem().toString().trim();
                update_fail_confirmation_url = getString(R.string.Base_URL) + "updateconfirmationfailorder.php?order_id=" + order_id + "&sales_del_fail_reason=" + binding.tvDelFailReason.getText().toString().trim();
                //Log.i("updatefailconfirm_url=>", update_fail_confirmation_url + "");
                new updateFailConfirmationTask().execute(update_fail_confirmation_url.replace(" ", "%20"));

                SharedPreferences.Editor editor = sp_update.edit();
                editor.putBoolean("isUpdate", true);
                editor.apply();

                isUpdate = sp_update.getBoolean("isUpdate", false);
                Log.i(TAG, "isUpdate=>" + isUpdate);

                /*} else {

                    Toast.makeText(ViewSalesOrderActivity.this, "please select reason for fail order", Toast.LENGTH_SHORT).show();

                }*/

            }
        });

        binding.btnRedelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* double total_rs = 0.0;
                int total_line = 0;

                JSONObject order_product_list = new JSONObject();// main object
                JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

                for (int i = 0; i < arrayList_order_product_list.size(); i++) {
                    JSONObject jGroup = new JSONObject();// /sub Object

                    try {

                        if (Double.parseDouble(arrayList_order_product_list.get(i).getP_qty()) > 0) {

                            jGroup.put("p_id", arrayList_order_product_list.get(i).getP_id());
                            jGroup.put("p_qty", arrayList_order_product_list.get(i).getP_qty());
                            jGroup.put("p_price", arrayList_order_product_list.get(i).getP_price());

                            jArray.put(jGroup);

                            total_rs = total_rs + Double.parseDouble(arrayList_order_product_list.get(i).getP_qty()) * Double.parseDouble(arrayList_order_product_list.get(i).getP_price());

                            total_line++;
                        }

                        // order_product_list Name is JsonArray Name
                        order_product_list.put("prod_list", jArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                //Toast.makeText(ViewSalesOrderActivity.this, ""+jArray, Toast.LENGTH_SHORT).show();
                Log.i("jArray==>", jArray + "");
                Log.i("total_rs==>", total_rs + "");
                Log.i("total_line==>", total_line + "");*/

                //send_to_redeliver_url = getString(R.string.Base_URL) + "addneworder.php?salesman_id=" + salesman_id + "&bit_id=" + bit_id + "&shop_id=" + shop_id + "&total_rs=" + total_rs + "&total_line=" + total_line + "&def_dist=" + dist_id + "&latitude=" + shop_latitude + "&longitude=" + shop_longitude + "&prod_list=" + jArray + "&non_order_reason=&isredelivery=1";
                send_to_redeliver_url = getString(R.string.Base_URL) + "sendingforredeliver.php?order_id=" + order_id;
                new sendtoRedeliverTask().execute(send_to_redeliver_url.replace("%20", " "));

                //http://projects.drbbagpks.org/Baxom/API/sendingforredeliver.php?order_id=1

                SharedPreferences.Editor editor = sp_update.edit();
                editor.putBoolean("isUpdate", true);
                editor.apply();

                isUpdate = sp_update.getBoolean("isUpdate", false);
                Log.i(TAG, "isUpdate=>" + isUpdate);

            }
        });

    }


    //======================get sales order task starts ==========================

    public class getSalesOrderByOrderidTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(ViewSalesOrderActivity.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(5) + "");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i("SalesOrder url=>", url + "");

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("SalesOrder res=>", response + "");

                getSalesOrder();

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getSalesOrder() {

        double total_rs, total_line, delivery_rs, deliery_line, success, total, total_scheme_order_amount = 0.0;

        try {

            JSONObject jsonObject = new JSONObject(response + "");
            JSONArray data_array = jsonObject.getJSONArray("data");

            for (int i = 0; i < data_array.length(); i++) {

                JSONObject data_object = data_array.getJSONObject(i);

                JSONObject saleman_detail_object = data_object.getJSONObject("saleman_detail");
                salesman_id = saleman_detail_object.getString("salesman_id");

                JSONObject dist_detail_object = data_object.getJSONObject("dist_detail");
                dist_id = dist_detail_object.getString("dist_id");

                JSONObject shop_business_details_object = data_object.getJSONObject("shop_business_details");

                total_rs = Double.parseDouble(shop_business_details_object.getString("total_rs"));
                total_line = Double.parseDouble(shop_business_details_object.getString("total_line"));
                delivery_rs = Double.parseDouble(shop_business_details_object.getString("delivery_rs"));
                deliery_line = Double.parseDouble(shop_business_details_object.getString("deliery_line"));
                success = Double.parseDouble(shop_business_details_object.getString("success"));
                total = Double.parseDouble(shop_business_details_object.getString("total"));

                binding.tvDelAttempt.setText("" + (int) success + " / " + (int) total);
                binding.tvDelBussiness.setText("" + (int) delivery_rs + " / " + (int) total_rs);
                binding.tvDelLine.setText("" + (int) deliery_line + " / " + (int) total_line);

                /*binding.tvTotalOrderAmount.setText("Total Order Rs. : " + shop_business_details_object.getString("total_order_rs"));
                binding.tvTotalDiscount.setText("Total Discount : " + shop_business_details_object.getString("total_discount"));
                binding.tvGrandTotal.setText("Grantd Total : " + (Double.parseDouble(shop_business_details_object.getString("total_order_rs")) - Double.parseDouble(shop_business_details_object.getString("total_discount"))));*/

                JSONArray prod_detail_array = data_object.getJSONArray("prod_detail");

                arrayList_sales_order_product = new ArrayList<>();
                //arrayList_order_product_list = new ArrayList<>();

                for (int j = 0; j < prod_detail_array.length(); j++) {

                    JSONObject prod_detail_object = prod_detail_array.getJSONObject(j);

                    JSONArray min_data_array = prod_detail_object.getJSONArray("min_data");
                    arrayList_minvalue = new ArrayList<>();
                    for (int k = 0; k < min_data_array.length(); k++) {
                        arrayList_minvalue.add(min_data_array.getDouble(k));
                    }

                    salesOrderProductPOJO = new SalesOrderProductPOJO(prod_detail_object.getString("product_id"),
                            prod_detail_object.getString("title"),
                            prod_detail_object.getString("point"),
                            prod_detail_object.getString("product_qty"),
                            prod_detail_object.getString("product_price"),
                            prod_detail_object.getString("purchase_rate"),
                            prod_detail_object.getString("product_qty_del"),
                            prod_detail_object.getString("prod_unit"),
                            prod_detail_object.getString("photo"),
                            arrayList_minvalue);

                    arrayList_sales_order_product.add(salesOrderProductPOJO);

                    /*   arrayList_order_product_list.add(new OrderProductListPOJO(prod_detail_object.getString("product_id"),
                            prod_detail_object.getString("product_qty"),
                            prod_detail_object.getString("product_price"),"" ,""));*/

                    total_order_rs = total_order_rs + Double.parseDouble(prod_detail_object.getString("product_qty")) * Double.parseDouble(prod_detail_object.getString("product_price"));
                }


                SalesOrderRVAdapter salesOrderRVAdapter = new SalesOrderRVAdapter(arrayList_sales_order_product, getApplicationContext());
                binding.rvSalesOrderProductList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                binding.rvSalesOrderProductList.setAdapter(salesOrderRVAdapter);

                JSONObject order_detail_object = data_object.getJSONObject("order_detail");


                if (setActivity.equalsIgnoreCase("DelFailConfirm")) {

                    if (order_detail_object.getString("del_fail_confirm").equalsIgnoreCase("1")) {

                        binding.llConfirmButtons.setVisibility(View.GONE);

                    } else if (order_detail_object.getString("del_fail_confirm").equalsIgnoreCase("0")) {

                        binding.llConfirmButtons.setVisibility(View.VISIBLE);

                    }
                }

                binding.tvOrderDatetimeSalesOrder.setText("" + order_detail_object.getString("order_date"));

                if (!order_detail_object.getString("delivery_date").equalsIgnoreCase(""))
                    binding.tvDeliveryDatetimeSalesOrder.setText("" + order_detail_object.getString("delivery_date"));

                binding.tvOrderNoSalesOrder.setText("" + order_detail_object.getString("order_id"));
                reordercount = order_detail_object.getInt("reordercount");

                total_discount = order_detail_object.getDouble("total_discount");

                //binding.tvDeliveryRsSalesOrder.setText("" + order_detail_object.getDouble("delivery_rs"));

                binding.tvTotalOrderRsSalesOrder.setText("" + order_detail_object.getDouble("total_order_rs"));
                binding.tvDelFailReason.setText("" + order_detail_object.getString("del_fail_reason"));

                JSONArray fail_remarks_array = data_object.getJSONArray("fail_remarks");

                arrayList_delivery_fail_reasons_id = new ArrayList<>();
                arrayList_delivery_fail_reasons = new ArrayList<>();

                for (int k = 0; k < fail_remarks_array.length(); k++) {

                    JSONObject fail_remarks_object = fail_remarks_array.getJSONObject(k);

                    arrayList_delivery_fail_reasons_id.add("" + fail_remarks_object.getString("ID"));
                    arrayList_delivery_fail_reasons.add("" + fail_remarks_object.getString("rem_title"));
                }

                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.mytextview1, arrayList_delivery_fail_reasons);
                binding.spnDeliveryFailReason.setAdapter(arrayAdapter);

                JSONObject shop_detail_object = data_object.getJSONObject("shop_detail");

                shop_id = shop_detail_object.getString("shop_id");
                bit_id = shop_detail_object.getString("bit_id");

                binding.tvShopNameSalesOrder.setText("" + shop_detail_object.getString("title"));
                binding.tvShopContactPersonNameSalesOrder.setText("" + shop_detail_object.getString("shop_keeper_name"));

                binding.tvShopLastOrderRsSalesOrder.setText("" + (int) Double.parseDouble(data_object.getString("last_order_rs")));
                binding.tvShopLast3VisitAvgSalesOrder.setText("" + (int) Double.parseDouble(data_object.getString("last_visit_ave")));
                binding.tvShopLast10VisitAvgSalesOrder.setText("" + (int) Double.parseDouble(data_object.getString("last_10visit_ave")));

                shop_latitude = shop_detail_object.getString("latitude");
                shop_longitude = shop_detail_object.getString("longitude");

                shop_photo = shop_detail_object.getString("shop_image");

                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.imagenotfoundicon);

                Glide.with(getApplicationContext()).load(shop_detail_object.getString("shop_image") + "")
                        .apply(options)
                        .into(binding.imgShopPhotoSalesOrder);


                JSONArray scheme_list_array = data_object.getJSONArray("scheme_list");

                arrayList_schemes_order_list = new ArrayList<>();

                if (scheme_list_array.length() > 0) {

                    binding.rvSchemeListViewOrder.setVisibility(View.VISIBLE);
                    binding.tvAvailableTitleViewOrder.setVisibility(View.VISIBLE);

                    arrayList_total_scheme_order_list = new ArrayList<>();
                    arrayList_scheme_order_discount = new ArrayList<>();
                    arrayList_schemes_order_list = new ArrayList<>();

                    arrayList_view_scheme_order_list = new ArrayList<>();

                    for (int k = 0; k < scheme_list_array.length(); k++) {

                        JSONObject scheme_list_object = scheme_list_array.getJSONObject(k);

                        JSONArray scheme_prod_array = scheme_list_object.getJSONArray("scheme_prod");

                        arrayList_scheme_prod = new ArrayList<>();

                        for (int j = 0; j < scheme_prod_array.length(); j++) {

                            JSONObject scheme_prod_object = scheme_prod_array.getJSONObject(j);

                            schemeProductPOJO = new SchemeProductPOJO(scheme_prod_object.getString("product_id"),
                                    scheme_prod_object.getString("product_title"),
                                    scheme_prod_object.getString("product_qty"));

                            arrayList_scheme_prod.add(schemeProductPOJO);
                        }


                        viewSchemesOrderPOJO = new ViewSchemesOrderPOJO(scheme_list_object.getString("scheme_id"),
                                scheme_list_object.getString("scheme_name_sort"),
                                scheme_list_object.getString("scheme_name_long"),
                                scheme_list_object.getString("scheme_type_id"),
                                scheme_list_object.getString("scheme_type_name"),
                                scheme_list_object.getString("scheme_image"),
                                scheme_list_object.getString("scheme_qty"),
                                scheme_list_object.getString("scheme_qty_del"),
                                scheme_list_object.getString("scheme_price"),
                                scheme_list_object.getString("result_product_id"),
                                scheme_list_object.getString("result_product_qty"),
                                scheme_list_object.getString("result_product_price"),
                                scheme_list_object.getString("total_result_prod_value"),
                                scheme_list_object.getString("result_product_photo"),
                                scheme_list_object.getString("is_half_scheme"),
                                arrayList_scheme_prod);

                        total_scheme_order_amount = total_scheme_order_amount + (scheme_list_object.getDouble("scheme_qty") * scheme_list_object.getDouble("scheme_price"));

                        arrayList_view_scheme_order_list.add(viewSchemesOrderPOJO);


                        arrayList_schemes_order_list.add(new SchemesOrderPOJO(scheme_list_object.getString("scheme_id"),
                                scheme_list_object.getString("scheme_name_sort"),
                                scheme_list_object.getString("scheme_name_long"),
                                scheme_list_object.getString("scheme_type_id"),
                                scheme_list_object.getString("scheme_type_name"),
                                scheme_list_object.getString("scheme_image"),
                                scheme_list_object.getString("scheme_price"),
                                (scheme_list_object.getDouble("scheme_qty") * scheme_list_object.getDouble("scheme_price")) + "",
                                scheme_list_object.getString("scheme_qty"),
                                scheme_list_object.getString("scheme_qty"),
                                scheme_list_object.getString("result_product_id"),
                                scheme_list_object.getString("result_product_qty"),
                                scheme_list_object.getString("result_product_price"),
                                scheme_list_object.getString("total_result_prod_value"),
                                scheme_list_object.getString("result_product_photo"),
                                scheme_list_object.getString("is_half_scheme")));

                      /*  arrayList_match_scheme_list.add(new MatchShemePOJO(scheme_list_object.getString("scheme_id"),
                                0 + "", 0 + ""));*/


                        if (scheme_list_object.getString("scheme_qty_del").equalsIgnoreCase("0")) {

                            arrayList_total_scheme_order_list.add(new MatchShemePOJO(scheme_list_object.getString("scheme_id"),
                                    scheme_list_object.getString("scheme_qty"),
                                    scheme_list_object.getString("scheme_price")));

                            arrayList_scheme_order_discount.add((Double.parseDouble(scheme_list_object.getString("total_result_prod_value")) * (Double.parseDouble(scheme_list_object.getString("scheme_qty")))));


                        } else {

                            arrayList_total_scheme_order_list.add(new MatchShemePOJO(scheme_list_object.getString("scheme_id"),
                                    scheme_list_object.getString("scheme_qty_del"),
                                    scheme_list_object.getString("scheme_price")));

                            arrayList_scheme_order_discount.add((Double.parseDouble(scheme_list_object.getString("total_result_prod_value")) * (Double.parseDouble(scheme_list_object.getString("scheme_qty_del")))));
                        }
                    }


                    SchemListAdapter schemListAdapter = new SchemListAdapter(arrayList_view_scheme_order_list, ViewSalesOrderActivity.this);
                    binding.rvSchemeListViewOrder.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                    binding.rvSchemeListViewOrder.setAdapter(schemListAdapter);

                    binding.tvTotalSchemeOrderValueViewOrder.setText("" + total_scheme_order_amount);
                    binding.tvTotalDiscountValueViewOrder.setText("" + total_discount);

                } else {

                    binding.rvSchemeListViewOrder.setVisibility(View.GONE);
                    binding.tvAvailableTitleViewOrder.setVisibility(View.GONE);

                }

                //binding.tvDeliveryRsSalesOrder.setText("" + ((int) total_order_rs));



               /* JSONArray scheme_list_array = data_object.getJSONArray("scheme_list");

                if (scheme_list_array.length() > 0) {

                    binding.tvNoschemeAvailable.setVisibility(View.GONE);
                    binding.rvSchemeList.setVisibility(View.VISIBLE);

                    arrayList_scheme_list = new ArrayList<>();

                    for (int k = 0; k < scheme_list_array.length(); k++) {

                        JSONObject scheme_list_object = scheme_list_array.getJSONObject(k);

                        arrayList_scheme_list.add("" + scheme_list_object.getString("scheme_name"));
                    }

                    SchemeListAdapter schemeListAdapter = new SchemeListAdapter(arrayList_scheme_list, getApplicationContext());
                    binding.rvSchemeList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                    binding.rvSchemeList.setAdapter(schemeListAdapter);

                } else {
                    binding.tvNoschemeAvailable.setVisibility(View.VISIBLE);
                    binding.rvSchemeList.setVisibility(View.GONE);
                }*/


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //======================get sales order task ends ============================


    //===================== Sales Order RV Adapter starts ===========================

    public class SalesOrderRVAdapter extends RecyclerView.Adapter<SalesOrderRVAdapter.MyHolder> {

        ArrayList<SalesOrderProductPOJO> arrayList_sales_order_product;
        Context context;

        double start = 0;
        double product_price = 0, tot_order_price = 0;

        int min_pos = 0;

        public SalesOrderRVAdapter(ArrayList<SalesOrderProductPOJO> arrayList_sales_order_product, Context context) {
            this.arrayList_sales_order_product = arrayList_sales_order_product;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntitySalesOrderProductBinding.inflate(LayoutInflater.from(context)));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {

            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    ViewSalesOrderActivity.this, setLangEntity(sp_multi_lang.getString("lang", "")));
            commanList = language.getData();

            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                holder.binding.tvOrderqtyTitleSales.setText("" + commanList.getArrayList().get(0));
                holder.binding.tvOrderrsTitleSales.setText("" + commanList.getArrayList().get(1));
                holder.binding.tvDeliveryrsTitleSales.setText("" + commanList.getArrayList().get(2));
            }
            holder.binding.tvSalesOrderProductName.setText("" + arrayList_sales_order_product.get(position).getTitle());
            holder.binding.tvSalesOrderProductUnit.setText("" + arrayList_sales_order_product.get(position).getProd_unit());
            holder.binding.tvSalesOrderProductMrpRs.setText("" + (int) (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price())));
            holder.binding.tvSalesOrderProductPoint.setText("" + arrayList_sales_order_product.get(position).getPoint());

            holder.binding.tvSalesOrderProductQty.setText("" + arrayList_sales_order_product.get(position).getProduct_qty());
            holder.binding.tvSalesOrderProductOrderRs.setText("" + (int) (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty()) * Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price())));

            //holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty_del()) * Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price())));

            //holder.binding.edtDeliveryProductQtySalesOrder.setText("" + arrayList_sales_order_product.get(position).getProduct_qty_del());
            //holder.binding.edtDeliveryProductQtySalesOrder.setText("" + arrayList_sales_order_product.get(position).getProduct_qty_del());

            if (setActivity.equalsIgnoreCase("Undelivered")) {

                if (distributor_name.equalsIgnoreCase("")) {

                    holder.binding.edtDeliveryProductQtySalesOrder.setEnabled(false);
                    holder.binding.imgSalesProductMinusQty.setEnabled(false);
                    holder.binding.imgSalesProductPlusQty.setEnabled(false);

                } else {

                    holder.binding.edtDeliveryProductQtySalesOrder.setEnabled(true);
                    holder.binding.imgSalesProductMinusQty.setEnabled(true);
                    holder.binding.imgSalesProductPlusQty.setEnabled(true);
                }

                if (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty_del()) > 0) {

                    holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty_del()) * Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price())));
                    holder.binding.edtDeliveryProductQtySalesOrder.setText("" + arrayList_sales_order_product.get(position).getProduct_qty_del());

                    Log.i(TAG, "inside if");
                    Log.i(TAG, "Product_id=>" + arrayList_sales_order_product.get(position).getProduct_id());
                    Log.i(TAG, "Product_qty_del=>" + arrayList_sales_order_product.get(position).getProduct_qty_del());

                } else {

                    holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty()) * Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price())));
                    holder.binding.edtDeliveryProductQtySalesOrder.setText("" + arrayList_sales_order_product.get(position).getProduct_qty());

                    Log.i(TAG, "inside else");
                    Log.i(TAG, "Product_id=>" + arrayList_sales_order_product.get(position).getProduct_id());
                    Log.i(TAG, "Product_qty_del=>" + arrayList_sales_order_product.get(position).getProduct_qty_del());

                }

            } else if (setActivity.equalsIgnoreCase("Delivered")) {

                holder.binding.edtDeliveryProductQtySalesOrder.setEnabled(false);
                holder.binding.imgSalesProductMinusQty.setEnabled(false);
                holder.binding.imgSalesProductPlusQty.setEnabled(false);

                holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty_del()) * Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price())));
                holder.binding.edtDeliveryProductQtySalesOrder.setText("" + arrayList_sales_order_product.get(position).getProduct_qty_del());

            } else if (setActivity.equalsIgnoreCase("DelFailConfirm")) {

                holder.binding.edtDeliveryProductQtySalesOrder.setEnabled(false);
                holder.binding.imgSalesProductMinusQty.setEnabled(false);
                holder.binding.imgSalesProductPlusQty.setEnabled(false);

                holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty_del()) * Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price())));
                holder.binding.edtDeliveryProductQtySalesOrder.setText("" + arrayList_sales_order_product.get(position).getProduct_qty_del());
            }

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.notfoundimages);

            Glide.with(getApplicationContext()).load(arrayList_sales_order_product.get(position).getPhoto() + "")
                    .apply(options)
                    .into(holder.binding.imgSalesOrderProductPhoto);


            if (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty_del()) > 0) {

                arrayList_update_order_product.add(new UpdateSalesOrderProductPOJO(arrayList_sales_order_product.get(position).getProduct_id(),
                        arrayList_sales_order_product.get(position).getProduct_qty(),
                        arrayList_sales_order_product.get(position).getProduct_qty_del(),
                        arrayList_sales_order_product.get(position).getProduct_price()));

            } else {

                arrayList_update_order_product.add(new UpdateSalesOrderProductPOJO(arrayList_sales_order_product.get(position).getProduct_id(),
                        arrayList_sales_order_product.get(position).getProduct_qty(),
                        arrayList_sales_order_product.get(position).getProduct_qty(),
                        arrayList_sales_order_product.get(position).getProduct_price()));
            }
            Log.i("arrayList_order_product", arrayList_update_order_product.size() + "");

            getTotalOrderRs();

            holder.binding.edtDeliveryProductQtySalesOrder.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                    try {


                        product_price = Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price());

                        Double del_qty;

                        if (holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().isEmpty()) {
                            del_qty = 0.0;
                        } else {
                            del_qty = Double.valueOf(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim());
                        }

                        if (reordercount > 0) {

                            if (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty_del()) > 0) {

                                if (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty_del()) > del_qty) {
                                    Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();
                                    holder.binding.edtDeliveryProductQtySalesOrder.setText("" + arrayList_sales_order_product.get(position).getProduct_qty_del());
                                }
                            }
                        }


                  /*  if (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty()) < del_qty) {

                        Toast.makeText(context, "Delivered qty should be less than order qty", Toast.LENGTH_SHORT).show();

                    } else {*/

                        if (holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().isEmpty()) {

                            tot_order_price = 0 * product_price;
                            holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) tot_order_price);

                            arrayList_update_order_product.set(position, new UpdateSalesOrderProductPOJO(arrayList_sales_order_product.get(position).getProduct_id(),
                                    arrayList_sales_order_product.get(position).getProduct_qty(),
                                    0 + "",
                                    arrayList_sales_order_product.get(position).getProduct_price()));

                        } else {

                            tot_order_price = Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) * product_price;
                            holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) tot_order_price);

                            arrayList_update_order_product.set(position, new UpdateSalesOrderProductPOJO(arrayList_sales_order_product.get(position).getProduct_id(),
                                    arrayList_sales_order_product.get(position).getProduct_qty(),
                                    Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) + "",
                                    arrayList_sales_order_product.get(position).getProduct_price()));

                        }

                        //Toast.makeText(context, "edit qty", Toast.LENGTH_SHORT).show();
                        getTotalOrderRs();

                        binding.btnCheckSchemeViewOrder.performClick();


                        //}

                        //Log.i("arrayList_order_product", arrayList_update_order_product.size() + "");
                    } catch (Exception e) {

                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                if (!holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().isEmpty()) {

                                    boolean equal = false;

                                    double checkval = Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim());

                                    if (checkval > 0 && checkval < 1) {

                                        for (int i = 0; i < arrayList_sales_order_product.get(position).getArrayList_minvalue().size(); i++) {

                                            //Log.i("checkval", arrayList_minvalue.get(i) + "");

                                            double v = arrayList_sales_order_product.get(position).getArrayList_minvalue().get(i);

                                            //Log.i(checkval+"==",v+"");

                                            if (checkval == v)
                                                equal = true;

                                        }

                                        if (equal == true) {


                                        } else {

                                            Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();
                                            holder.binding.edtDeliveryProductQtySalesOrder.setText("0");
                                            min_pos = 0;
                                        }
                                    }
                                }

                            } catch (Exception e) {
                            }

                        }
                    }, 1000);


                }
            });


            holder.binding.imgSalesProductPlusQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double qty = 0.0;

                    holder.binding.edtDeliveryProductQtySalesOrder.requestFocus();

                    try {

                   /* if (Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_qty()) <= start) {

                        Toast.makeText(context, "Delivered qty should be less than order qty", Toast.LENGTH_SHORT).show();

                    } else {*/

                        if (holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim().isEmpty()) {
                            start = 0;
                        } else {
                            start = Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim());
                        }

                  /*  if (start < 0) {
                        holder.binding.edtDeliveryProductQtySalesOrder.setText("" + start);
                    } else {
                        if (start >= 0) {

                            start++;
                            holder.binding.edtDeliveryProductQtySalesOrder.setText("" + start);

                            product_price = Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price());

                            //tot_order_price = start * product_price;
                            tot_order_price = Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) * product_price;
                            holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) tot_order_price);

                            arrayList_update_order_product.set(position, new UpdateSalesOrderProductPOJO(arrayList_sales_order_product.get(position).getProduct_id(),
                                    arrayList_sales_order_product.get(position).getProduct_qty(),
                                    Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) + "",
                                    arrayList_sales_order_product.get(position).getProduct_price()));

                        }
                    }*/


                        start++;

                        if ((int) Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) < 1)
                            min_pos = 1 + arrayList_sales_order_product.get(position).getArrayList_minvalue().indexOf(Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()));

                        if (Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) < 1 && min_pos < arrayList_sales_order_product.get(position).getArrayList_minvalue().size()) {

                            //Log.i(TAG, "inside if min_pos=>" + min_pos);

                            holder.binding.edtDeliveryProductQtySalesOrder.setText("" + arrayList_sales_order_product.get(position).getArrayList_minvalue().get(min_pos));
                            qty = arrayList_sales_order_product.get(position).getArrayList_minvalue().get(min_pos);
                            min_pos++;

                            //Log.i(TAG, "min_pos=>" + min_pos);

                        } else {

                            //Log.i(TAG, "inside else");
                            //Log.i(TAG, "min_pos=>" + min_pos);

                            holder.binding.edtDeliveryProductQtySalesOrder.setText("" + (int) start);
                            qty = (int) start;
                        }

                        product_price = Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price());

                        Log.i(TAG, "qty_plus==>" + qty);

                        //tot_order_price = start * product_price;
                        //tot_order_price = Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) * product_price;
                        tot_order_price = qty * product_price;
                        holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) tot_order_price);

                        arrayList_update_order_product.set(position, new UpdateSalesOrderProductPOJO(arrayList_sales_order_product.get(position).getProduct_id(),
                                arrayList_sales_order_product.get(position).getProduct_qty(),
                                //Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) + "",
                                qty + "",
                                arrayList_sales_order_product.get(position).getProduct_price()));


                        getTotalOrderRs();
                        binding.btnCheckSchemeViewOrder.performClick();


                    } catch (Exception e) {

                    }

                    // }

                    //Log.i("arrayList_order_product", arrayList_update_order_product.size() + "");

                }


            });

            holder.binding.imgSalesProductMinusQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double qty = 0.0;

                    holder.binding.edtDeliveryProductQtySalesOrder.requestFocus();

                    try {


                        if (holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim().isEmpty()) {
                            start = 0;
                        } else {
                            start = Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim());
                        }


                  /*  if (start > 0 && start != 0 && start != -1) {
                        start--;
                        holder.binding.edtDeliveryProductQtySalesOrder.setText("" + start);

                        product_price = Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price());

                        //tot_order_price = start * product_price;
                        tot_order_price = Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) * product_price;
                        holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) tot_order_price);

                        arrayList_update_order_product.set(position, new UpdateSalesOrderProductPOJO(arrayList_sales_order_product.get(position).getProduct_id(),
                                arrayList_sales_order_product.get(position).getProduct_qty(),
                                Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) + "",
                                arrayList_sales_order_product.get(position).getProduct_price()));


                    } else {
                        holder.binding.edtDeliveryProductQtySalesOrder.setText("0");
                        //arrayList_order_product_list.remove(arrayList_order_product_list.get(position));
                    }*/

                        start--;

                        if ((int) Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) < 1)
                            min_pos = arrayList_sales_order_product.get(position).getArrayList_minvalue().indexOf(Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()));


                        if (Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) == 1) {


                            if (arrayList_sales_order_product.get(position).getArrayList_minvalue().size() > 1) {

                                //Log.i(TAG, "inside if min_pos=>" + min_pos);

                                min_pos = arrayList_sales_order_product.get(position).getArrayList_minvalue().size() - 1;

                                holder.binding.edtDeliveryProductQtySalesOrder.setText(arrayList_sales_order_product.get(position).getArrayList_minvalue().get(min_pos).toString());
                                qty = Double.parseDouble(arrayList_sales_order_product.get(position).getArrayList_minvalue().get(min_pos).toString());
                                //Log.i(TAG, "min_pos=>" + min_pos);

                            } else {

                                holder.binding.edtDeliveryProductQtySalesOrder.setText("" + start);
                                qty = start;
                                //Log.i(TAG, "inside if else min_pos=>" + min_pos);

                            }


                        } else if (Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) > 0 && Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) < 1) {

                            //Log.i(TAG, "inside else if min_pos=>" + min_pos);
                            min_pos--;
                            holder.binding.edtDeliveryProductQtySalesOrder.setText(arrayList_sales_order_product.get(position).getArrayList_minvalue().get(min_pos).toString());
                            qty = Double.parseDouble(arrayList_sales_order_product.get(position).getArrayList_minvalue().get(min_pos).toString());
                            //Log.i(TAG, "min_pos=>" + min_pos);

                        } else {

                            //Log.i(TAG, "inside else");
                            //Log.i(TAG, "min_pos=>" + min_pos);

                            if (Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) > 0) {
                                holder.binding.edtDeliveryProductQtySalesOrder.setText("" + (int) start);
                                qty = (int) start;

                                //Log.i(TAG, "inside else -> if =>" + start);
                            }
                        }

                        Log.i(TAG, "qty_minus==>" + qty);

                        product_price = Double.parseDouble(arrayList_sales_order_product.get(position).getProduct_price());

                        //tot_order_price = start * product_price;
                        //tot_order_price = Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) * product_price;
                        tot_order_price = qty * product_price;
                        holder.binding.tvSalesOrderProductDeliveryRs.setText("" + (int) tot_order_price);

                        arrayList_update_order_product.set(position, new UpdateSalesOrderProductPOJO(arrayList_sales_order_product.get(position).getProduct_id(),
                                arrayList_sales_order_product.get(position).getProduct_qty(),
                                //Double.parseDouble(holder.binding.edtDeliveryProductQtySalesOrder.getText().toString().trim()) + "",
                                qty + "",
                                arrayList_sales_order_product.get(position).getProduct_price()));


                        getTotalOrderRs();

                        binding.btnCheckSchemeViewOrder.performClick();


                    } catch (Exception e) {

                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList_sales_order_product.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntitySalesOrderProductBinding binding;

            public MyHolder(EntitySalesOrderProductBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    //===================== Sales Order RV Adapter ends =============================

    public void getTotalOrderRs() {

        double total_rs = 0.0;

        //Log.i(TAG, "arrayList_update_order_product=>" + arrayList_update_order_product.size());

        for (int i = 0; i < arrayList_update_order_product.size(); i++) {

            if (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) > 0) {

                total_rs = total_rs + (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) * Double.parseDouble(arrayList_update_order_product.get(i).getP_price()));
                binding.tvDeliveryRsSalesOrder.setText("" + total_rs);
                //Log.i(TAG, "total_rs=>" + total_rs);
            }
        }
        binding.tvDeliveryRsSalesOrder.setText("" + total_rs);

    }


    //updateneworder.php?
    // prod_list=[{%22p_id%22:%20%2212%22,%22p_qty_del%22:%20%221%22}]
    // &order_id=1
    //&del_fail_reason=no prodcts

    //==============================update order task starts ========================

    public class updateSalesOrderTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "update_order_url=>" + update_sales_order_url + "");

            HttpHandler httpHandler = new HttpHandler();
            update_sales_order_response = httpHandler.makeServiceCall(update_sales_order_url.replace("%22", "\"").replace("%20", " "));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "update_order_res" + update_sales_order_response + "");
                if (update_sales_order_response.contains("updated Successfully")) {
               /* if (next_shop_id.equalsIgnoreCase("0")) {

                    showDialogExit();

                } else {

                    showDialog();
                }*/
                    showDialog();
                } else {
                    Toast.makeText(ViewSalesOrderActivity.this, commanSuchnaList.getArrayList().get(12) + "", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    //==============================update order task ends ==========================


    public void showDialog() {

        builder = new AlertDialog.Builder(ViewSalesOrderActivity.this);
        builder.setMessage(commanSuchnaList.getArrayList().get(0) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(4) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

                /*arrayList_sales_order_product.clear();
                arrayList_update_order_product.clear();
                url = getString(R.string.Base_URL) + getString(R.string.viewsales_orderdetail_url) + order_id;
                new getSalesOrderByOrderidTask().execute(url);*/

            }
        });

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    public void showDialogExit() {

        builder = new AlertDialog.Builder(ViewSalesOrderActivity.this);
        builder.setMessage(commanSuchnaList.getArrayList().get(1) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(4) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        ad_finish = builder.create();
        ad_finish.show();

        ad_finish.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad_finish.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }


    //===============================update delivery fail confirmation task starts ======================

    public class updateFailConfirmationTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i("updatefailconfirm_url=>", update_fail_confirmation_url + "");

            HttpHandler httpHandler = new HttpHandler();
            update_fail_confirmation_response = httpHandler.makeServiceCall(update_fail_confirmation_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("updatefailconfirm_res=>", update_fail_confirmation_response + "");

                if (update_fail_confirmation_response.contains("")) {

                    showDynamicDialog(commanSuchnaList.getArrayList().get(2) + "");

                } else {

                    Toast.makeText(ViewSalesOrderActivity.this, commanSuchnaList.getArrayList().get(12) + "", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    //===============================update delivery fail confirmation task ends ========================


    //===============================send to redelivery task starts ================================

    public class sendtoRedeliverTask extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i("send_to_redeliver_url=>", send_to_redeliver_url + "");

            HttpHandler httpHandler = new HttpHandler();
            send_to_redeliver_response = httpHandler.makeServiceCall(send_to_redeliver_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("send_to_redeliver_res=>", send_to_redeliver_response + "");

                if (send_to_redeliver_response.contains("redeliver Successfully")) {

                    showDynamicDialog(commanSuchnaList.getArrayList().get(3) + "");

                } else {

                    Toast.makeText(ViewSalesOrderActivity.this, commanSuchnaList.getArrayList().get(12) + "", Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    //===============================send to redelivery task ends ==================================


    public void showDynamicDialog(String msg) {

        builder = new AlertDialog.Builder(ViewSalesOrderActivity.this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(4) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        ad_finish = builder.create();
        ad_finish.show();

        ad_finish.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad_finish.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }


    //========================== scheme list Adapter code starts ======================

    public class SchemListAdapter extends RecyclerView.Adapter<SchemListAdapter.MyHolder> {

        ArrayList<ViewSchemesOrderPOJO> arrayList_scheme_list;
        Context context;

        double start = 0;

        public SchemListAdapter(ArrayList<ViewSchemesOrderPOJO> arrayList_scheme_list, Context context) {
            this.arrayList_scheme_list = arrayList_scheme_list;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityViewOrderSchemesListBinding.inflate(LayoutInflater.from(context), null, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.binding.edtSchemeQtyViewOrder.setEnabled(false);
            holder.binding.imgSchemePlusQtyViewOrder.setEnabled(false);
            holder.binding.imgSchemeMinusQtyViewOrder.setEnabled(false);

            holder.binding.tvSchemeLongNameViewOrder.setText("" + arrayList_scheme_list.get(position).getScheme_name_long());
            holder.binding.tvConditionValueViewOrder.setText("₹ " + arrayList_scheme_list.get(position).getScheme_price());
            holder.binding.tvResultProdValueViewOrder.setText("₹ " + arrayList_scheme_list.get(position).getTotal_result_prod_value());

            holder.binding.tvSchemeOrderQty.setText("" + arrayList_scheme_list.get(position).getScheme_qty());

            if (isSchemeCheck) {

                holder.binding.edtSchemeQtyViewOrder.setText("0");
                holder.binding.tvSchemeValueViewOrder.setText("0.0");
                holder.binding.tvSchemeOrderValueViewOrder.setText(" / 0.0");

                if (arrayList_view_scheme_order_list.get(position).getScheme_type_id().equalsIgnoreCase("1")) {

                    if (Double.parseDouble(binding.tvDeliveryRsSalesOrder.getText().toString().trim()) > (Double.parseDouble(arrayList_view_scheme_order_list.get(position).getScheme_price()) * 0.6)
                            && Double.parseDouble(binding.tvDeliveryRsSalesOrder.getText().toString().trim()) < Double.parseDouble(arrayList_view_scheme_order_list.get(position).getScheme_price())) {

                        holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#FF9800"));
                        holder.binding.llSchemeValueViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                        holder.binding.rlSchemeListViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view6ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                        holder.binding.view7ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));

                        holder.binding.view1ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view2ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view3ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view4ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view5ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    } else if (Double.parseDouble(binding.tvDeliveryRsSalesOrder.getText().toString().trim()) >= Double.parseDouble(arrayList_view_scheme_order_list.get(position).getScheme_price())) {

                        holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#27ae60"));
                        //holder.binding.llSchemeValue.setBackgroundColor(Color.parseColor("#27ae60"));
                        holder.binding.llSchemeValueViewOrder.setBackgroundColor(Color.GREEN);
                        holder.binding.rlSchemeListViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view6ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));
                        holder.binding.view7ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));

                        holder.binding.view1ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view2ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view3ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view4ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view5ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));


                        holder.binding.imgSchemePlusQtyViewOrder.setEnabled(true);
                        holder.binding.imgSchemeMinusQtyViewOrder.setEnabled(true);
                    }

                    holder.binding.tvSchemeOrderValueViewOrder.setText(" / " + binding.tvDeliveryRsSalesOrder.getText().toString().trim());

                } else {

                    if (Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price()) > (Double.parseDouble(arrayList_view_scheme_order_list.get(position).getScheme_price()) * 0.6)
                            && Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price()) < Double.parseDouble(arrayList_view_scheme_order_list.get(position).getScheme_price())) {

                        holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#FF9800"));
                        holder.binding.llSchemeValueViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));
                        holder.binding.rlSchemeListViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view6ViewOrder.setBackgroundColor(Color.parseColor("#FF9800"));


                        holder.binding.view1ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view2ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view3ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view4ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view5ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    } else if (Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price()) >= Double.parseDouble(arrayList_view_scheme_order_list.get(position).getScheme_price())) {

                        holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#27ae60"));
                        holder.binding.llSchemeValueViewOrder.setBackgroundColor(Color.GREEN);
                        holder.binding.rlSchemeListViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view6ViewOrder.setBackgroundColor(Color.parseColor("#27ae60"));

                        holder.binding.view1ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view2ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view3ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view4ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view5ViewOrder.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        holder.binding.imgSchemePlusQtyViewOrder.setEnabled(true);
                        holder.binding.imgSchemeMinusQtyViewOrder.setEnabled(true);
                    }

                    holder.binding.tvSchemeOrderValueViewOrder.setText(" / " + arrayList_match_scheme_list.get(position).getScheme_price());
                }

                if (arrayList_view_scheme_order_list.get(position).getIs_scheme_half().equalsIgnoreCase("0.5")) {

                    if (Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) >
                            (Double.parseDouble(arrayList_view_scheme_order_list.get(position).getScheme_price()) * 0.5)) {

                        Log.i(TAG, "inside half scheme");

                        holder.binding.imgSchemePlusQtyViewOrder.setEnabled(true);
                        holder.binding.imgSchemeMinusQtyViewOrder.setEnabled(true);

                    }

                }

                arrayList_schemes_order_list.add(new SchemesOrderPOJO(arrayList_scheme_list.get(position).getScheme_id(),
                        arrayList_scheme_list.get(position).getScheme_name_sort(),
                        arrayList_scheme_list.get(position).getScheme_name_long(),
                        arrayList_scheme_list.get(position).getScheme_type_id(),
                        arrayList_scheme_list.get(position).getScheme_type_name(),
                        arrayList_scheme_list.get(position).getScheme_name_sort(),
                        arrayList_scheme_list.get(position).getScheme_price(),
                        Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) + "",
                        0 + "",
                        0 + "",
                        arrayList_scheme_list.get(position).getResult_product_id(),
                        arrayList_scheme_list.get(position).getResult_product_qty(),
                        arrayList_scheme_list.get(position).getResult_product_price(),
                        arrayList_scheme_list.get(position).getTotal_result_prod_value(),
                        arrayList_scheme_list.get(position).getResult_product_image(),
                        arrayList_scheme_list.get(position).getIs_scheme_half()));

            } else {


                if (setActivity.equalsIgnoreCase("Undelivered")) {

                    Log.i(TAG, "inside Undelivered");

                    if (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_qty_del()) > 0) {

                        holder.binding.edtSchemeQtyViewOrder.setText("" + arrayList_scheme_list.get(position).getScheme_qty_del());
                        holder.binding.tvSchemeValueViewOrder.setText("" + (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_qty_del()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())));
                        holder.binding.tvSchemeOrderValueViewOrder.setText(" / " + Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()));


                    } else {

                        holder.binding.edtSchemeQtyViewOrder.setText("" + arrayList_scheme_list.get(position).getScheme_qty());
                        holder.binding.tvSchemeValueViewOrder.setText("" + (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_qty()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())));
                        holder.binding.tvSchemeOrderValueViewOrder.setText(" / " + Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()));

                    }


                } else if (setActivity.equalsIgnoreCase("Delivered")) {

                    Log.i(TAG, "inside Delivered");

                    holder.binding.edtSchemeQtyViewOrder.setText("" + arrayList_scheme_list.get(position).getScheme_qty_del());
                    holder.binding.tvSchemeValueViewOrder.setText("" + (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_qty_del()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())));
                    holder.binding.tvSchemeOrderValueViewOrder.setText(" / " + Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()));

                } else if (setActivity.equalsIgnoreCase("DelFailConfirm")) {

                    Log.i(TAG, "inside DelFailConfirm");

                    holder.binding.edtSchemeQtyViewOrder.setText("" + arrayList_scheme_list.get(position).getScheme_qty_del());
                    holder.binding.tvSchemeValueViewOrder.setText("" + (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_qty_del()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())));
                    holder.binding.tvSchemeOrderValueViewOrder.setText(" / " + Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()));

                }


            }

            getTotalSchemeOrderAmount();


            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.notfoundimages);

            Glide.with(getApplicationContext()).load(arrayList_view_scheme_order_list.get(position).getScheme_image())
                    .apply(options)
                    .into(holder.binding.imgSchemePhotoViewOrder);

            Glide.with(getApplicationContext()).load(arrayList_view_scheme_order_list.get(position).getResult_product_image())
                    .apply(options)
                    .into(holder.binding.imgResultProdPhotoViewOrder);


            holder.binding.imgSchemePlusQtyViewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.binding.edtSchemeQtyViewOrder.requestFocus();

                    if (holder.binding.edtSchemeQtyViewOrder.getText().toString().trim().isEmpty()) {
                        start = 0;
                    } else {
                        start = Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim());
                    }

                    //Log.i(TAG, "TotalOrderRs=>" + binding.tvDeliveryRsSalesOrder.getText().toString().trim());
                    //Log.i(TAG, "SchemeValue=>" + (start * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())));
                    //Log.i(TAG, "SchemeOrderValue=>" + holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim());

                    if ((Double.parseDouble(binding.tvTotalSchemeOrderValueViewOrder.getText().toString()) >
                            Double.parseDouble(binding.tvDeliveryRsSalesOrder.getText().toString().trim()))) {

                        Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

                        if (Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) >
                                (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.5) &&
                                Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) <
                                        (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))) {

                            Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();
                            //holder.binding.edtSchemeQty.setText("" + ((int) start - 0.5));

                        } else {

                            Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();
                            //holder.binding.edtSchemeQty.setText("" + ((int) start - 1));
                        }


                    } else {

                        if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0.5")) {

                            if ((Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) >
                                    Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")))) {

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

                            } else {

                                if (Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim()) == 0) {

                                    holder.binding.edtSchemeQtyViewOrder.setText("0.5");

                                } else if (Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim()) >= 0.5) {

                                    start++;
                                    holder.binding.edtSchemeQtyViewOrder.setText("" + (int) start);
                                }
                            }

                        } else if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0")) {

                            if (((Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString()) + 1) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) >
                                    Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")))) {

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

                            } else {

                                if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0")) {

                                    if (start >= 0) {
                                        start++;
                                        holder.binding.edtSchemeQtyViewOrder.setText("" + (int) start);
                                    }
                                }

                            }

                        }

                        if (((Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString())) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) >
                                Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")))) {

                            if (Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) >
                                    (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.5) &&
                                    Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) <
                                            (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))) {

                                Log.i(TAG, "inside if starts=>" + start);

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();
                                holder.binding.edtSchemeQtyViewOrder.setText("" + ((int) start - 0.5));

                            } else {

                                Log.i(TAG, "inside else starts=>" + start);

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();
                                holder.binding.edtSchemeQtyViewOrder.setText("" + ((int) start - 1));
                            }

                        } else {

                            holder.binding.tvSchemeValueViewOrder.setText("" + String.format("%.2f", (Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))));
                        }


                        double cmp_scheme_value = (Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString()) *
                                Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()));

                        double schme_value = Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().replace("/", ""));

                        double tot_scheme_value = cmp_scheme_value + compareSchemes(arrayList_scheme_list.get(position).getScheme_id());


                       /* Log.i(TAG, "compareSchemes==>" + compareSchemes(arrayList_scheme_list.get(position).getScheme_id()));
                        Log.i(TAG, "tot_scheme_value==>" + tot_scheme_value);*/


                        if (tot_scheme_value > schme_value) {

                            //Toast.makeText(context, "...scheme value > scheme order...", Toast.LENGTH_SHORT).show();

                            if (Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) >
                                    (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.5) &&
                                    Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) <
                                            (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))) {

                                //Log.i(TAG, "inside if starts=>" + holder.binding.edtSchemeQtyViewOrder.getText().toString());

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

                                if (holder.binding.edtSchemeQtyViewOrder.getText().toString().equalsIgnoreCase("0.5"))
                                    holder.binding.edtSchemeQtyViewOrder.setText("0");
                                else if (holder.binding.edtSchemeQtyViewOrder.getText().toString().equalsIgnoreCase("0") ||
                                        holder.binding.edtSchemeQtyViewOrder.getText().toString().equalsIgnoreCase("0.0"))
                                    holder.binding.edtSchemeQtyViewOrder.setText("0");
                                else
                                    holder.binding.edtSchemeQtyViewOrder.setText("" + ((int) start - 0.5));


                            } else {

                                //Log.i(TAG, "inside else starts=>" + holder.binding.edtSchemeQtyViewOrder.getText().toString());
                                Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

                                if (holder.binding.edtSchemeQtyViewOrder.getText().toString().equalsIgnoreCase("0.5"))
                                    holder.binding.edtSchemeQtyViewOrder.setText("0");
                                else if (holder.binding.edtSchemeQtyViewOrder.getText().toString().equalsIgnoreCase("0") ||
                                        holder.binding.edtSchemeQtyViewOrder.getText().toString().equalsIgnoreCase("0.0"))
                                    holder.binding.edtSchemeQtyViewOrder.setText("0");
                                else
                                    holder.binding.edtSchemeQtyViewOrder.setText("" + ((int) start - 1));
                            }

                            holder.binding.tvSchemeValueViewOrder.setText("" + String.format("%.2f",
                                    (Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString()) *
                                            Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))));


                        }


                    }
                    //holder.binding.tvSchemeValue.setText("" + String.format("%.2f", (Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))));

                    arrayList_total_scheme_order_list.set(position, (new MatchShemePOJO(arrayList_scheme_list.get(position).getScheme_id(),
                            holder.binding.edtSchemeQtyViewOrder.getText().toString().trim(), arrayList_scheme_list.get(position).getScheme_price())));

                    arrayList_scheme_order_discount.set(position,
                            (Double.parseDouble(arrayList_scheme_list.get(position).getTotal_result_prod_value()) *
                                    Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim())));

                    arrayList_schemes_order_list.set(position, (new SchemesOrderPOJO(arrayList_scheme_list.get(position).getScheme_id(),
                            arrayList_scheme_list.get(position).getScheme_name_sort(),
                            arrayList_scheme_list.get(position).getScheme_name_long(),
                            arrayList_scheme_list.get(position).getScheme_type_id(),
                            arrayList_scheme_list.get(position).getScheme_type_name(),
                            arrayList_scheme_list.get(position).getScheme_image(),
                            arrayList_scheme_list.get(position).getScheme_price(),
                            Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) + "",
                            Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim()) + "",
                            Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim()) + "",
                            arrayList_scheme_list.get(position).getResult_product_id(),
                            arrayList_scheme_list.get(position).getResult_product_qty(),
                            arrayList_scheme_list.get(position).getResult_product_price(),
                            arrayList_scheme_list.get(position).getTotal_result_prod_value(),
                            arrayList_scheme_list.get(position).getResult_product_image(),
                            arrayList_scheme_list.get(position).getIs_scheme_half())));

                    getTotalSchemeOrderAmount();

                }

            });

            holder.binding.imgSchemeMinusQtyViewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.binding.edtSchemeQtyViewOrder.requestFocus();

                    if (holder.binding.edtSchemeQtyViewOrder.getText().toString().trim().isEmpty()) {
                        start = 0;
                    } else {
                        start = Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim());
                    }

                    if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0.5")) {

                        if (start == 1) {

                            holder.binding.edtSchemeQtyViewOrder.setText("0.5");

                        } else if (start == 0.5) {

                            holder.binding.edtSchemeQtyViewOrder.setText("0");

                        } else {

                            start--;
                            if (start >= 0) {
                                holder.binding.edtSchemeQtyViewOrder.setText("" + (int) start);
                            }

                        }

                    } else if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0")) {

                        start--;

                        if (start > 0) {

                            holder.binding.edtSchemeQtyViewOrder.setText("" + (int) start);

                        } else {

                            holder.binding.edtSchemeQtyViewOrder.setText("0");

                        }

                    }

                    holder.binding.tvSchemeValueViewOrder.setText("" + String.format("%.2f", (Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))));

                    arrayList_total_scheme_order_list.set(position, (new MatchShemePOJO(arrayList_scheme_list.get(position).getScheme_id(),
                            holder.binding.edtSchemeQtyViewOrder.getText().toString().trim(), arrayList_scheme_list.get(position).getScheme_price())));

                    arrayList_scheme_order_discount.set(position,
                            (Double.parseDouble(arrayList_scheme_list.get(position).getTotal_result_prod_value()) *
                                    Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim())));

                    arrayList_schemes_order_list.set(position, (new SchemesOrderPOJO(arrayList_scheme_list.get(position).getScheme_id(),
                            arrayList_scheme_list.get(position).getScheme_name_sort(),
                            arrayList_scheme_list.get(position).getScheme_name_long(),
                            arrayList_scheme_list.get(position).getScheme_type_id(),
                            arrayList_scheme_list.get(position).getScheme_type_name(),
                            arrayList_scheme_list.get(position).getScheme_image(),
                            arrayList_scheme_list.get(position).getScheme_price(),
                            Double.parseDouble(holder.binding.tvSchemeOrderValueViewOrder.getText().toString().trim().replace("/", "")) + "",
                            Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim()) + "",
                            Double.parseDouble(holder.binding.edtSchemeQtyViewOrder.getText().toString().trim()) + "",
                            arrayList_scheme_list.get(position).getResult_product_id(),
                            arrayList_scheme_list.get(position).getResult_product_qty(),
                            arrayList_scheme_list.get(position).getResult_product_price(),
                            arrayList_scheme_list.get(position).getTotal_result_prod_value(),
                            arrayList_scheme_list.get(position).getResult_product_image(),
                            arrayList_scheme_list.get(position).getIs_scheme_half())));

                    getTotalSchemeOrderAmount();

                }
            });


        }

        @Override
        public int getItemCount() {
            return arrayList_scheme_list.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityViewOrderSchemesListBinding binding;

            public MyHolder(EntityViewOrderSchemesListBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }

    //========================== scheme list Adapter code ends ========================


    public void getTotalSchemeOrderAmount() {

        total_scheme_amount = 0.0;
        total_discount = 0.0;


        for (int i = 0; i < arrayList_total_scheme_order_list.size(); i++) {

            total_scheme_amount = total_scheme_amount + Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_qty()) * Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_price());
            binding.tvTotalSchemeOrderValueViewOrder.setText("" + String.format("%.2f", total_scheme_amount));

            total_discount = total_discount + arrayList_scheme_order_discount.get(i);
            binding.tvTotalDiscountValueViewOrder.setText("" + String.format("%.2f", total_discount));
        }
    }

    public double compareSchemes(String cmp_scheme_id) {

        boolean isMatched = false;

        double tot_SchemeValue = 0.0;
        int pos = 0;

        for (int i = 0; i < arrayList_view_scheme_order_list.size(); i++) {
            if (cmp_scheme_id.equalsIgnoreCase(arrayList_view_scheme_order_list.get(i).getScheme_id())) {
                pos = i;
            }
        }

        for (int i = 0; i < arrayList_view_scheme_order_list.size(); i++) {

            if (!cmp_scheme_id.equalsIgnoreCase(arrayList_view_scheme_order_list.get(i).getScheme_id())) {

                if (arrayList_view_scheme_order_list.get(pos).getArrayList().size() == arrayList_view_scheme_order_list.get(i).getArrayList().size()) {

                    //Log.i(TAG, "====>inside if match no of prod");
                    //Log.i(TAG, "=====Cmp_no of prod==>" + arrayList_scheme_list.get(pos).getArrayList().size());
                    //Log.i(TAG, "=====no of prod==>" + arrayList_scheme_list.get(i).getArrayList().size());

                    for (int j = 0; j < arrayList_view_scheme_order_list.get(i).getArrayList().size(); j++) {

                        if (arrayList_view_scheme_order_list.get(pos).getArrayList().get(j).getProduct_id()
                                .equalsIgnoreCase(arrayList_view_scheme_order_list.get(pos).getArrayList().get(j).getProduct_id())) {
                            isMatched = true;

                            //Log.i(TAG, "=======>inside if match prodId=" + arrayList_scheme_list.get(i).getArrayList().get(j).getProduct_id());
                            //Log.i(TAG, "========>Cmp_prodId==>" + arrayList_scheme_list.get(pos).getArrayList().get(j).getProduct_id());
                            //Log.i(TAG, "========>prodId==>" + arrayList_scheme_list.get(i).getArrayList().get(j).getProduct_id());

                        } else {
                            isMatched = false;
                        }
                    }

                    if (isMatched) {

                        /*Log.i(TAG, "=============>Scheme  name=>" + arrayList_scheme_list.get(i).getScheme_name());
                        Log.i(TAG, "=========>Scheme   qty=>" + arrayList_total_scheme_order_list.get(i).getScheme_qty());
                        Log.i(TAG, "=========>Scheme price=>" + arrayList_total_scheme_order_list.get(i).getScheme_price());
                        Log.i(TAG, "=========>Scheme Value=>" + (Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_price()))
                                * Double.parseDouble((arrayList_total_scheme_order_list.get(i).getScheme_qty())));*/

                        tot_SchemeValue = tot_SchemeValue + (Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_price()))
                                * Double.parseDouble((arrayList_total_scheme_order_list.get(i).getScheme_qty()));

                    }

                } else {
                    //Log.i(TAG, "==>inside else not match no of prod");
                    isMatched = false;
                }
            }
        }

        return tot_SchemeValue;
    }

    public void setLanguage(String key) {

        Language language = new Language(key, ViewSalesOrderActivity.this, setLang(key));
        commanList = language.getData();
        if (setLang(key).size() > 0) {
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                binding.tvThisOrderrsTitleViewsales.setText("" + commanList.getArrayList().get(1));
                binding.tvThisedeliveryrsTitleViewsales.setText("" + commanList.getArrayList().get(2));
                binding.tvOrderdatetimeTitleViewsales.setText("" + commanList.getArrayList().get(3));
                binding.tvDeliverydatetimeTitleViewsales.setText("" + commanList.getArrayList().get(4));
                binding.tvOrdernoTitleViewsales.setText("" + commanList.getArrayList().get(5));

                binding.tvShopNameTitleViewsales.setText("" + commanList.getArrayList().get(6));
                binding.tvContactpersonTitleViewsales.setText("" + commanList.getArrayList().get(7));

                binding.tvLastorderrsTitleViewsales.setText("" + commanList.getArrayList().get(8));
                binding.tvLast3visitTitleViewsales.setText("" + commanList.getArrayList().get(9));
                binding.tvLast10visitTitleViewsales.setText("" + commanList.getArrayList().get(10));
                binding.btnAvailableTitleViewsales.setText("" + commanList.getArrayList().get(11));

                binding.tvDeliveryTitleViewsales.setText("" + commanList.getArrayList().get(12));
                binding.tvAttemptTitleViewsales.setText("" + commanList.getArrayList().get(13));
                binding.tvBusinessTitleViewsales.setText("" + commanList.getArrayList().get(14));
                binding.tvLineTitleViewsales.setText("" + commanList.getArrayList().get(15));
                binding.tvAvailableTitleViewOrder.setText("" + commanList.getArrayList().get(16));

                binding.btnCheckSchemeViewOrder.setText("" + commanList.getArrayList().get(17));

                binding.tvTotalSchemeOrderValueTitleViewsales.setText(Html.fromHtml("" + commanList.getArrayList().get(18)));
                binding.tvTotalDiscountValueTitleViewsales.setText("" + commanList.getArrayList().get(19));

                binding.tvReasonfordeliveryfailTitleViewsales.setText(Html.fromHtml("" + commanList.getArrayList().get(20)));
                binding.tvDeliveryfailreasonVieworder.setText(Html.fromHtml("" + commanList.getArrayList().get(21)));

                binding.btnConfirmFail.setText("" + commanList.getArrayList().get(23));
                binding.btnRedelivery.setText(Html.fromHtml("" + commanList.getArrayList().get(24)));
                binding.btnSubmitUpdateOrder.setText("" + commanList.getArrayList().get(25));

        /*binding.tvAvailableTitleViewOrder.setText("" + commanList.getArrayList().get(21));
        binding.tvAvailableTitleViewOrder.setText("" + commanList.getArrayList().get(25));*/

            }
        }
    }

    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "14");

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
        Cursor cur = db.viewLanguage(key, "24");

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
        Cursor cur = db.viewMultiLangSuchna("14");

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

                builder = new AlertDialog.Builder(ViewSalesOrderActivity.this);
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