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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.jp.baxomdistributor.Models.AddPurchaseOrderProductsPOJO;
import com.jp.baxomdistributor.Models.MatchShemePOJO;
import com.jp.baxomdistributor.Models.OrderProductListPOJO;
import com.jp.baxomdistributor.Models.SchemeListPOJO;
import com.jp.baxomdistributor.Models.SchemeProductPOJO;
import com.jp.baxomdistributor.Models.SchemesOrderPOJO;
import com.jp.baxomdistributor.Models.ViewSchemesOrderPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.ActivityAddPurchaseOrderBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;
import com.jp.baxomdistributor.databinding.EntityOrderProductAddPurchaseOrderBinding;
import com.jp.baxomdistributor.databinding.EntitySchemesListBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddPurchaseOrder extends AppCompatActivity {

    ActivityAddPurchaseOrderBinding binding;

    String TAG = getClass().getSimpleName();

    String delete_order_url = "", delete_order_response = "", add_purchase_order_url = "", add_purchase_order_response = "",
            update_purchase_orders_url = "", update_purchase_orders_response = "", issubmit = "", isverify = "", isauthentication = "",
            purchase_id = "", dist_list_url = "", dist_list_response = "", order_product_list_url = "", order_product_list_response = "";

    String dist_id = "", order_dist_id = "", distributor_id = "", salesman_id = "", transporter_name = "", transporter_contact1 = "", transporter_contact2 = "";

    int purchase_order_status = 0, prev_order_status = 0;

    ArrayList<AddPurchaseOrderProductsPOJO> arrayList_product_list, arrayList_addmore_prod_list;
    AddPurchaseOrderProductsPOJO addPurchaseOrderProductsPOJO;


    ArrayList<OrderProductListPOJO> arrayList_order_product_list, arrayList_order_addmore_product_list;
    double total_rs = 0, add_more_total_rs = 0, order_total_rs = 0;

    ArrayList<String> arrayList_order_status;

    ArrayList<String> arrayList_distributor_id, arrayList_distributor_name, arrayList_stock_point_name, arrayList_transporter_name,
            arrayList_transporter_contact1, arrayList_transporter_contact2;

    ArrayAdapter<String> arrayAdapter;

    ArrayList<Double> arrayList_minvalue;

    GDateTime gDateTime = new GDateTime();

    ProgressDialog pdialog;

    AlertDialog ad, ad_net_connection;
    AlertDialog.Builder builder;

    SharedPreferences sp, sp_distributor_detail, sp_update, sp_login, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanSuchnaList;

    ArrayList<SchemeListPOJO> arrayList_scheme_list;
    SchemeListPOJO schemeListPOJO;
    ArrayList<SchemeProductPOJO> arrayList_scheme_prod;
    SchemeProductPOJO schemeProductPOJO;
    ArrayList<MatchShemePOJO> arrayList_match_scheme_list, arrayList_total_scheme_order_list;
    MatchShemePOJO matchShemePOJO;
    ArrayList<Double> arrayList_scheme_order_discount;
    ArrayList<SchemesOrderPOJO> arrayList_schemes_order_list;
    SchemesOrderPOJO schemesOrderPOJO;

    ArrayList<ViewSchemesOrderPOJO> arrayList_view_scheme_order_list;
    ViewSchemesOrderPOJO viewSchemesOrderPOJO;
    boolean isSchemeCheck = false;

    double total_discount = 0.0, total_scheme_amount = 0.0;
    private IntentFilter minIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPurchaseOrderBinding.inflate(getLayoutInflater());
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
        sp = getSharedPreferences("salesman_detail", Context.MODE_PRIVATE);
        sp_distributor_detail = getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);
        sp_login = getSharedPreferences("login_detail", Context.MODE_PRIVATE);

        salesman_id = sp.getString("salesman_id", null);
        distributor_id = sp_distributor_detail.getString("distributor_id", "");

        issubmit = sp_login.getString("issubmit", "");
        isverify = sp_login.getString("isverify", "");
        isauthentication = sp_login.getString("isauthentication", "");

        db = new Database(getApplicationContext());

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);

        setLanguage(sp_multi_lang.getString("lang", ""));

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                AddPurchaseOrder.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language.getData();

        purchase_id = getIntent().getStringExtra("purchase_id");

        //Log.i(TAG, "issubmit=>" + issubmit + " isverify=>" + isverify + " isauthentication=>" + isauthentication);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (!distributor_id.equalsIgnoreCase("")) {

            binding.tvSalesmanName.setText("" + sp_distributor_detail.getString("name", " "));

            dist_list_url = getString(R.string.Base_URL) + getString(R.string.purchaseorder_distributor_list_by_distId_url) + distributor_id;
            new getDistListTask().execute(dist_list_url);

        } else {

            dist_list_url = getString(R.string.Base_URL) + getString(R.string.purchaseorder_distributor_list_url) + salesman_id;
            new getDistListTask().execute(dist_list_url);
            binding.tvSalesmanName.setText("" + sp.getString("salesman", " "));

        }


        binding.spnDistName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                binding.tvStockPointName.setText("" + arrayList_stock_point_name.get(position));
                dist_id = arrayList_distributor_id.get(position);
                transporter_name = arrayList_transporter_name.get(position);
                transporter_contact1 = arrayList_transporter_contact1.get(position);
                transporter_contact2 = arrayList_transporter_contact2.get(position);

                if (purchase_id.isEmpty()) {

                    arrayList_order_product_list = new ArrayList<>();
                    order_product_list_url = getString(R.string.Base_URL) + "purchaseOrderProdList.php?dist_id=" + dist_id;
                    new getProductListTask().execute(order_product_list_url);

                    binding.llPurchaseOrderDetail.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (purchase_id.isEmpty()) {

         /* arrayList_order_product_list = new ArrayList<>();
            order_product_list_url = getString(R.string.Base_URL) + "purchaseOrderProdList.php?dist_id=" + dist_id;
            new getProductListTask().execute(order_product_list_url);*/

            binding.llPurchaseOrderDetail.setVisibility(View.GONE);
            binding.btnAddMoreProd.setVisibility(View.GONE);

        } else {

            arrayList_order_product_list = new ArrayList<>();
            order_product_list_url = getString(R.string.Base_URL) + "viewPurchaseOrderDetail.php?order_id=" + purchase_id;
            new getProductListTask().execute(order_product_list_url);

            binding.llPurchaseOrderDetail.setVisibility(View.VISIBLE);


        }


        if (purchase_id.isEmpty()) {

            if (issubmit.equalsIgnoreCase("1")) {

                if (isverify.equalsIgnoreCase("1") && issubmit.equalsIgnoreCase("1") && isauthentication.equalsIgnoreCase("1")) {

                    binding.tvOrderStatus.setText("SUBMIT & VERIFY & AUTHORISE");
                    binding.btnSubmitAddPurchaseOrder.setText("Submit & Verify & AUTHORISE");
                    purchase_order_status = 3;

                } else if (issubmit.equalsIgnoreCase("1") && isverify.equalsIgnoreCase("1")) {

                    binding.tvOrderStatus.setText("SUBMIT & VEIRFY");
                    binding.btnSubmitAddPurchaseOrder.setText("Submit & Verify");
                    purchase_order_status = 2;

                } else if (issubmit.equalsIgnoreCase("1") && isauthentication.equalsIgnoreCase("1")) {

                    binding.tvOrderStatus.setText("SUBMIT & AUTHORISE");
                    binding.btnSubmitAddPurchaseOrder.setText("Submit & AUTHORISE");
                    purchase_order_status = 3;

                } else {

                    binding.tvOrderStatus.setText("SUBMIT");
                    binding.btnSubmitAddPurchaseOrder.setText("Submit");
                    purchase_order_status = 1;
                }


            } else if (isverify.equalsIgnoreCase("1")) {

                if (issubmit.equalsIgnoreCase("1") && isverify.equalsIgnoreCase("1") && isauthentication.equalsIgnoreCase("1")) {

                    binding.tvOrderStatus.setText("SUBMIT & VERIFY & AUTHORISE");
                    binding.btnSubmitAddPurchaseOrder.setText("Submit & Verify & AUTHORISE");
                    purchase_order_status = 3;

                } else if (issubmit.equalsIgnoreCase("1") && isverify.equalsIgnoreCase("1")) {

                    binding.tvOrderStatus.setText("SUBMIT & VEIRFY");
                    binding.btnSubmitAddPurchaseOrder.setText("Submit & Verify");
                    purchase_order_status = 2;

                } else if (isverify.equalsIgnoreCase("1") && isauthentication.equalsIgnoreCase("1")) {

                    binding.tvOrderStatus.setText("VEIRFY & AUTHORISE");
                    binding.btnSubmitAddPurchaseOrder.setText("VEIRFY & AUTHORISE");
                    purchase_order_status = 3;

                } else {

                    binding.tvOrderStatus.setText("VEIRFY");
                    binding.btnSubmitAddPurchaseOrder.setText("VEIRFY");
                    purchase_order_status = 2;
                }

            } else if (isauthentication.equalsIgnoreCase("1")) {

                if (issubmit.equalsIgnoreCase("1") && isverify.equalsIgnoreCase("1") && isauthentication.equalsIgnoreCase("1")) {

                    binding.tvOrderStatus.setText("SUBMIT & VERIFY & AUTHORISE");
                    binding.btnSubmitAddPurchaseOrder.setText("Submit & Verify & AUTHORISE");

                } else if (issubmit.equalsIgnoreCase("1") && isauthentication.equalsIgnoreCase("1")) {

                    binding.tvOrderStatus.setText("SUBMIT & AUTHORISE");
                    binding.btnSubmitAddPurchaseOrder.setText("SUBMIT & AUTHORISE");

                } else if (isverify.equalsIgnoreCase("1") && isauthentication.equalsIgnoreCase("1")) {

                    binding.tvOrderStatus.setText("VERIFY & AUTHORISE");
                    binding.btnSubmitAddPurchaseOrder.setText("Verify & AUTHORISE");

                } else {

                    binding.tvOrderStatus.setText("AUTHORISE");
                    binding.btnSubmitAddPurchaseOrder.setText("AUTHORISE");
                }

                purchase_order_status = 3;

            } else {

                Language language1 = new Language(sp_multi_lang.getString("lang", ""), AddPurchaseOrder.this,
                        setLang(sp_multi_lang.getString("lang", "")));
                Language.CommanList commanList = language1.getData();

                //binding.llOrderBtn.setVisibility(View.GONE);
                binding.btnSubmitAddPurchaseOrder.setVisibility(View.GONE);
                binding.tvOrderStatus.setText(commanList.getArrayList().get(19) + "");
            }

        }

        binding.imgBackPurchaseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnUpdateProdAddPurchaseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject order_product_list = new JSONObject();// main object
                JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

                for (int i = 0; i < arrayList_order_product_list.size(); i++) {
                    JSONObject jGroup = new JSONObject();// /sub Object

                    try {

                        if (Double.parseDouble(arrayList_order_product_list.get(i).getP_qty()) > 0) {

                            jGroup.put("p_id", arrayList_order_product_list.get(i).getP_id());
                            jGroup.put("p_qty", arrayList_order_product_list.get(i).getP_qty());
                            jGroup.put("p_price", arrayList_order_product_list.get(i).getP_price());
                            jGroup.put("p_title", arrayList_order_product_list.get(i).getP_title());
                            jGroup.put("p_title_hindi", arrayList_order_product_list.get(i).getP_title_hindi());
                            jGroup.put("p_short_desc", arrayList_order_product_list.get(i).getP_short_desc());
                            jGroup.put("p_long_desc", arrayList_order_product_list.get(i).getP_long_desc());
                            jGroup.put("p_prod_unit", arrayList_order_product_list.get(i).getP_prod_unit());
                            jGroup.put("p_gst", arrayList_order_product_list.get(i).getP_gst().replace("%", ""));
                            jGroup.put("p_dispatch_code", arrayList_order_product_list.get(i).getP_dispatch_code());

                            jArray.put(jGroup);
                        }

                        // order_product_list Name is JsonArray Name
                        order_product_list.put("prod_list", jArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.i(TAG, "prod_list" + jArray);

                JSONArray match_scheme_array = new JSONArray();

                for (int i = 0; i < arrayList_schemes_order_list.size(); i++) {

                    JSONObject jGroup = new JSONObject();// /sub Object

                    try {

                        if (Double.parseDouble(arrayList_schemes_order_list.get(i).getScheme_qty()) > 0) {

                            jGroup.put("Scheme_id", arrayList_schemes_order_list.get(i).getScheme_id());
                            jGroup.put("Scheme_name", arrayList_schemes_order_list.get(i).getScheme_name());
                            jGroup.put("Scheme_long_name", arrayList_schemes_order_list.get(i).getScheme_long_name());
                            jGroup.put("Scheme_type_id", arrayList_schemes_order_list.get(i).getScheme_type_id());
                            jGroup.put("Scheme_type_name", arrayList_schemes_order_list.get(i).getScheme_type_name());
                            jGroup.put("Scheme_image", arrayList_schemes_order_list.get(i).getScheme_image());
                            jGroup.put("Scheme_price", arrayList_schemes_order_list.get(i).getScheme_price());
                            //jGroup.put("Scheme_value", arrayList_schemes_order_list.get(i).getScheme_value());
                            jGroup.put("Scheme_qty", arrayList_schemes_order_list.get(i).getScheme_qty());
                            jGroup.put("Scheme_qty_del", arrayList_schemes_order_list.get(i).getScheme_qty_del());
                            jGroup.put("Result_product_id", arrayList_schemes_order_list.get(i).getResult_product_id());
                            jGroup.put("Result_product_qty", arrayList_schemes_order_list.get(i).getResult_product_qty());
                            jGroup.put("Result_product_price", arrayList_schemes_order_list.get(i).getResult_product_price());
                            jGroup.put("Result_product_total_price", arrayList_schemes_order_list.get(i).getResult_product_total_price());
                            jGroup.put("Result_product_image", arrayList_schemes_order_list.get(i).getResult_product_image());
                            jGroup.put("Is_scheme_half", arrayList_schemes_order_list.get(i).getIs_scheme_half());

                            match_scheme_array.put(jGroup);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //scheme_list
                Log.i(TAG, "Match_Scheme_list==>" + match_scheme_array);

                if ((Double.parseDouble(binding.tvTotalSchemeOrderValuePurchaseorder.getText().toString()) >
                        Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim()))) {

                    Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();

                } else {

                    if (jArray.length() > 0) {

                        update_purchase_orders_url = getString(R.string.Base_URL) + "updatePurchaseOrder_product.php?purchase_id=" + purchase_id +
                                "&order_amount=" + total_rs + "&prod_list=" + jArray + "&scheme_list=" + match_scheme_array;
                        Log.i(TAG, "update_orders_url=>" + update_purchase_orders_url + "");
                        new updatePurchaseOrderTask().execute(update_purchase_orders_url.replace("%20", " "));

                        SharedPreferences.Editor editor = sp_update.edit();
                        editor.putBoolean("isUpdate", true);
                        editor.apply();

                    } else {
                        Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(6) + "", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        binding.btnSubmitAddPurchaseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                JSONObject order_product_list = new JSONObject();// main object
                JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

                for (int i = 0; i < arrayList_order_product_list.size(); i++) {
                    JSONObject jGroup = new JSONObject();// /sub Object

                    try {

                        if (Double.parseDouble(arrayList_order_product_list.get(i).getP_qty()) > 0) {

                            jGroup.put("p_id", arrayList_order_product_list.get(i).getP_id());
                            jGroup.put("p_qty", arrayList_order_product_list.get(i).getP_qty());
                            jGroup.put("p_price", arrayList_order_product_list.get(i).getP_price());
                            jGroup.put("p_title", arrayList_order_product_list.get(i).getP_title());
                            jGroup.put("p_title_hindi", arrayList_order_product_list.get(i).getP_title_hindi());
                            jGroup.put("p_short_desc", arrayList_order_product_list.get(i).getP_short_desc());
                            jGroup.put("p_long_desc", arrayList_order_product_list.get(i).getP_long_desc());
                            jGroup.put("p_prod_unit", arrayList_order_product_list.get(i).getP_prod_unit());
                            jGroup.put("p_gst", arrayList_order_product_list.get(i).getP_gst().replace("%", ""));
                            jGroup.put("p_dispatch_code", arrayList_order_product_list.get(i).getP_dispatch_code());

                            jArray.put(jGroup);
                        }

                        // order_product_list Name is JsonArray Name
                        order_product_list.put("prod_list", jArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.i(TAG, "prod_list" + jArray);

                JSONArray match_scheme_array = new JSONArray();

                for (int i = 0; i < arrayList_schemes_order_list.size(); i++) {

                    JSONObject jGroup = new JSONObject();// /sub Object

                    try {

                        if (Double.parseDouble(arrayList_schemes_order_list.get(i).getScheme_qty()) > 0) {

                            jGroup.put("Scheme_id", arrayList_schemes_order_list.get(i).getScheme_id());
                            jGroup.put("Scheme_name", arrayList_schemes_order_list.get(i).getScheme_name());
                            jGroup.put("Scheme_long_name", arrayList_schemes_order_list.get(i).getScheme_long_name());
                            jGroup.put("Scheme_type_id", arrayList_schemes_order_list.get(i).getScheme_type_id());
                            jGroup.put("Scheme_type_name", arrayList_schemes_order_list.get(i).getScheme_type_name());
                            jGroup.put("Scheme_image", arrayList_schemes_order_list.get(i).getScheme_image());
                            jGroup.put("Scheme_price", arrayList_schemes_order_list.get(i).getScheme_price());
                            //jGroup.put("Scheme_value", arrayList_schemes_order_list.get(i).getScheme_value());
                            jGroup.put("Scheme_qty", arrayList_schemes_order_list.get(i).getScheme_qty());
                            jGroup.put("Scheme_qty_del", arrayList_schemes_order_list.get(i).getScheme_qty_del());
                            jGroup.put("Result_product_id", arrayList_schemes_order_list.get(i).getResult_product_id());
                            jGroup.put("Result_product_qty", arrayList_schemes_order_list.get(i).getResult_product_qty());
                            jGroup.put("Result_product_price", arrayList_schemes_order_list.get(i).getResult_product_price());
                            jGroup.put("Result_product_total_price", arrayList_schemes_order_list.get(i).getResult_product_total_price());
                            jGroup.put("Result_product_image", arrayList_schemes_order_list.get(i).getResult_product_image());
                            jGroup.put("Is_scheme_half", arrayList_schemes_order_list.get(i).getIs_scheme_half());

                            match_scheme_array.put(jGroup);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //scheme_list
                Log.i(TAG, "Match_Scheme_list==>" + match_scheme_array);

                if ((Double.parseDouble(binding.tvTotalSchemeOrderValuePurchaseorder.getText().toString()) >
                        Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim()))) {

                    Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();

                } else {

                    if (jArray.length() > 0) {

                        //Toast.makeText(AddPurchaseOrder.this, "" + jArray, Toast.LENGTH_SHORT).show();

                        Log.i(TAG, "prod_list=>" + jArray + "");

                        if (prev_order_status > 0) {

                            if (prev_order_status == 1) {

                                if (isverify.equalsIgnoreCase("0")) {

                                    Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(3) + "", Toast.LENGTH_SHORT).show();

                                } else {

                                    if (isverify.equalsIgnoreCase("1") && isauthentication.equalsIgnoreCase("1")) {

                                        update_purchase_orders_url = getString(R.string.Base_URL) + "updatePurchaseOrder.php?purchase_id=" + purchase_id +
                                                "&purchase_order_status=3&operation_status=verify&shipping_date=&order_amount=" + total_rs +
                                                "&LR_symbol=&no_of_article=&prod_list=" + jArray + "&scheme_list=" + match_scheme_array;
                                        //Log.i(TAG, "update_orders_url=>" + update_purchase_orders_url + "");
                                        new updatePurchaseOrderTask().execute(update_purchase_orders_url.replace("%20", " "));

                                    } else {

                                        update_purchase_orders_url = getString(R.string.Base_URL) + "updatePurchaseOrder.php?purchase_id=" + purchase_id +
                                                "&purchase_order_status=2&operation_status=verify&shipping_date=&order_amount=" + total_rs +
                                                "&LR_symbol=&no_of_article=&prod_list=" + jArray + "&scheme_list=" + match_scheme_array;
                                        //Log.i(TAG, "update_orders_url=>" + update_purchase_orders_url + "");
                                        new updatePurchaseOrderTask().execute(update_purchase_orders_url.replace("%20", " "));
                                    }


                                }

                            } else if (prev_order_status == 2) {

                                if (isauthentication.equalsIgnoreCase("0")) {

                                    Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();

                                } else {

                                    update_purchase_orders_url = getString(R.string.Base_URL) + "updatePurchaseOrder.php?purchase_id=" + purchase_id +
                                            "&purchase_order_status=3&operation_status=verify&shipping_date=&order_amount=" + total_rs +
                                            "&LR_symbol=&no_of_article=&prod_list=" + jArray + "&scheme_list=" + match_scheme_array;
                                    //Log.i(TAG, "update_orders_url=>" + update_purchase_orders_url + "");
                                    new updatePurchaseOrderTask().execute(update_purchase_orders_url.replace("%20", " "));
                                }
                            }

                        } else {

                            if (issubmit.equalsIgnoreCase("1")) {

                                //add_purchase_order_url = "http://192.168.43.151/Baxom/API/addPurchaseOrder.php?dist_id="+dist_id+"&purchase_order_status=1&order_date="+gDateTime.getDateymd()+"&shipping_date=&order_amount="+total_rs+"&LR_symbol=&no_of_article=&transporter_name=&transporter_contact1=&transporter_contact2=&prod_list="+jArray;
                                add_purchase_order_url = getString(R.string.Base_URL) + "addPurchaseOrder.php?dist_id=" + dist_id +
                                        "&purchase_order_status=" + purchase_order_status +
                                        "&shipping_date=&order_amount=" + total_rs + "&LR_symbol=&no_of_article=&transporter_name=" + transporter_name +
                                        "&transporter_contact1=" + transporter_contact1 + "&transporter_contact2=" + transporter_contact2 +
                                        "&prod_list=" + jArray + "&scheme_list=" + match_scheme_array;
                                //Log.i(TAG, "add_order_url=>" + add_purchase_order_url + "");
                                new AddPurchaseOrderTask().execute(add_purchase_order_url.replace("%20", " "));

                            } else {

                                Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(5) + "", Toast.LENGTH_SHORT).show();

                            }

                        }

                        SharedPreferences.Editor editor = sp_update.edit();
                        editor.putBoolean("isUpdate", true);
                        editor.apply();

                    } else {
                        Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(6) + "", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });


        binding.btnCancelPurchaseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showWarningDialog(commanSuchnaList.getArrayList().get(12) + "");

            }
        });

    }


    //==============================get product list task starts =========================

    public void getTotalOrderRs() {

        total_rs = 0;

        for (int i = 0; i < arrayList_order_product_list.size(); i++) {

            if (Double.parseDouble(arrayList_order_product_list.get(i).getP_qty()) > 0) {
                total_rs = total_rs + Double.parseDouble(arrayList_order_product_list.get(i).getP_qty()) * Double.parseDouble(arrayList_order_product_list.get(i).getP_price());
                binding.tvTotalOrderRsAddPurchaseOrder.setText("" + String.format("%.2f", total_rs));
            }
        }
        binding.tvTotalOrderRsAddPurchaseOrder.setText("" + String.format("%.2f", total_rs));
    }

    //==============================get product list task ends ===========================

    //=========================product list adapter code starts ==========================

    public double compareSchemes(String cmp_scheme_id) {

        boolean isMatched = false;

        double tot_SchemeValue = 0.0;
        int pos = 0;

        for (int i = 0; i < arrayList_scheme_list.size(); i++) {
            if (cmp_scheme_id.equalsIgnoreCase(arrayList_scheme_list.get(i).getScheme_id())) {
                pos = i;
            }
        }

        for (int i = 0; i < arrayList_scheme_list.size(); i++) {

            if (!cmp_scheme_id.equalsIgnoreCase(arrayList_scheme_list.get(i).getScheme_id())) {

                if (arrayList_scheme_list.get(pos).getArrayList().size() == arrayList_scheme_list.get(i).getArrayList().size()) {

                    //Log.i(TAG, "====>inside if match no of prod");
                    //Log.i(TAG, "=====Cmp_no of prod==>" + arrayList_scheme_list.get(pos).getArrayList().size());
                    //Log.i(TAG, "=====no of prod==>" + arrayList_scheme_list.get(i).getArrayList().size());

                    for (int j = 0; j < arrayList_scheme_list.get(i).getArrayList().size(); j++) {

                        if (arrayList_scheme_list.get(pos).getArrayList().get(j).getProduct_id()
                                .equalsIgnoreCase(arrayList_scheme_list.get(pos).getArrayList().get(j).getProduct_id())) {
                            isMatched = true;

                            //Log.i(TAG, "=======>inside if match prodId=" + arrayList_scheme_list.get(i).getArrayList().get(j).getProduct_id());
                            //Log.i(TAG, "========>Cmp_prodId==>" + arrayList_scheme_list.get(pos).getArrayList().get(j).getProduct_id());
                            //Log.i(TAG, "========>prodId==>" + arrayList_scheme_list.get(i).getArrayList().get(j).getProduct_id());

                        } else {
                            isMatched = false;
                        }
                    }

                    if (isMatched) {

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

    public void getTotalSchemeOrderAmount() {

        total_scheme_amount = 0.0;
        total_discount = 0.0;

        for (int i = 0; i < arrayList_total_scheme_order_list.size(); i++) {

            total_scheme_amount = total_scheme_amount + Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_qty()) *
                    Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_price());
            binding.tvTotalSchemeOrderValuePurchaseorder.setText("" + String.format("%.2f", total_scheme_amount));
            //Log.i(TAG, "<==============================>");
            //Log.i(TAG, "Scheme_id=>" + arrayList_total_scheme_order_list.get(i).getScheme_id());
            //Log.i(TAG, "Scheme_qty=>" + arrayList_total_scheme_order_list.get(i).getScheme_qty());
            //Log.i(TAG, "Scheme_price=>" + arrayList_total_scheme_order_list.get(i).getScheme_price());
            //Log.i(TAG, "total_scheme_value=>" + total_scheme_amount);

            total_discount = total_discount + arrayList_scheme_order_discount.get(i);
            //Log.i(TAG, "total_discount=>" + total_discount);
            binding.tvTotalDiscountValuePurchaseorder.setText("" + String.format("%.2f", total_discount));
        }


    }

    //=========================product list adapter code ends ============================

    //=========================Scheme list adapter code ends ============================

    public void checkScheme() {

        ArrayList<OrderProductListPOJO> arrayList_match_order_product_list = new ArrayList<>();

        JSONObject order_product_list = new JSONObject();// main object
        JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

        for (int i = 0; i < arrayList_order_product_list.size(); i++) {

            JSONObject jGroup = new JSONObject();// /sub Object

            try {

                if (Double.parseDouble(arrayList_order_product_list.get(i).getP_qty()) > 0) {

                    jGroup.put("p_id", arrayList_order_product_list.get(i).getP_id());
                    jGroup.put("p_qty", arrayList_order_product_list.get(i).getP_qty());
                    jGroup.put("p_price", arrayList_order_product_list.get(i).getP_price());

                    arrayList_match_order_product_list.add(new OrderProductListPOJO(arrayList_order_product_list.get(i).getP_id(),
                            arrayList_order_product_list.get(i).getP_qty(),
                            arrayList_order_product_list.get(i).getP_price(), "", "", "", "", "", "", "", ""));

                    jArray.put(jGroup);
                }

                // order_product_list Name is JsonArray Name
                order_product_list.put("prod_list", jArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, "inside check scheme jArray==>" + jArray);

        if (arrayList_match_order_product_list.size() > 0) {

            binding.btnSubmitAddPurchaseOrder.setEnabled(true);

            //isSchemeCheck = false;

            arrayList_match_scheme_list = new ArrayList<>();
            arrayList_total_scheme_order_list = new ArrayList<>();
            arrayList_scheme_order_discount = new ArrayList<>();

            //arrayList_schemes_order_list = new ArrayList<>();

            double scheme_value = 0.0;

            for (int j = 0; j < arrayList_scheme_list.size(); j++) {

                //Log.i(TAG, "<=========scheme id========>" + arrayList_scheme_list.get(j).getScheme_id());

                scheme_value = 0.0;

                if (arrayList_scheme_list.get(j).getArrayList().size() > 0) {

                    for (int i = 0; i < jArray.length(); i++) {

                        for (int n = 0; n < arrayList_scheme_list.get(j).getArrayList().size(); n++) {

                            //Log.i(TAG, "order_product_id=>" + arrayList_match_order_product_list.get(i).getP_id());
                            //Log.i(TAG, "scheme_product_id=>" + arrayList_scheme_list.get(j).getArrayList().get(n).getProduct_id());

                            if (arrayList_match_order_product_list.get(i).getP_id().equalsIgnoreCase(arrayList_scheme_list.get(j).getArrayList().get(n).getProduct_id())) {

                                //Log.i(TAG, "match product_id=>" + arrayList_match_order_product_list.get(i).getP_id());

                                scheme_value = scheme_value + Double.parseDouble(arrayList_match_order_product_list.get(i).getP_qty()) *
                                        Double.parseDouble(arrayList_match_order_product_list.get(i).getP_price());

                                //Log.i(TAG, "match product_order_value=>" + scheme_value);
                                //Log.i(TAG, "match getP_qty=>" + arrayList_match_order_product_list.get(i).getP_qty());
                                //Log.i(TAG, "match getP_price=>" + arrayList_match_order_product_list.get(i).getP_price());


                            } else {

                                //Log.i(TAG, "inside else doen't match");

                            }
                        }
                    }
                }

                arrayList_match_scheme_list.add(new MatchShemePOJO(arrayList_scheme_list.get(j).getScheme_id(),
                        0 + "",
                        scheme_value + ""));

                arrayList_total_scheme_order_list.add(new MatchShemePOJO(arrayList_scheme_list.get(j).getScheme_id(),
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

            Log.i(TAG, "inside check scheme Match_Scheme_list==>" + match_scheme_array);


            SchemListAdapter schemListAdapter = new SchemListAdapter(arrayList_scheme_list, AddPurchaseOrder.this);
            binding.rvSchemeListPurchaseorder.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
            binding.rvSchemeListPurchaseorder.setAdapter(schemListAdapter);

        } else {

            Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();
        }

    }

    public void showDialog(String msg) {

        builder = new AlertDialog.Builder(AddPurchaseOrder.this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(2) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    public void showWarningDialog(String msg) {

        builder = new AlertDialog.Builder(AddPurchaseOrder.this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(14) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                arrayList_order_addmore_product_list = new ArrayList<>();
                delete_order_url = getString(R.string.Base_URL) + "deletePurchaseOrder.php?purchase_id=" + purchase_id;
                new deleteOrderTask().execute(delete_order_url);

                SharedPreferences.Editor editor = sp_update.edit();
                editor.putBoolean("isUpdate", true);
                editor.apply();

            }
        });

        builder.setNegativeButton(commanSuchnaList.getArrayList().get(13) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
            }
        });

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        //ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);

    }

    public class getProductListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(AddPurchaseOrder.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(9) + "");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "order_product_url=>" + order_product_list_url + "");

            HttpHandler httpHandler = new HttpHandler();
            order_product_list_response = httpHandler.makeServiceCall(order_product_list_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "order_product_res=>" + order_product_list_response + "");
                getProductList();

                if (pdialog.isShowing())
                    pdialog.dismiss();

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }
    }

    private void getProductList() {

        arrayList_product_list = new ArrayList<>();
        double total_scheme_order_amount = 0.0;
        try {

            JSONObject jsonObject = new JSONObject(order_product_list_response + "");


            JSONArray jsonArray = jsonObject.getJSONArray("data");

            if (jsonArray.length() > 0) {


                for (int j = 0; j < jsonArray.length(); j++) {

                    JSONObject data_object = jsonArray.getJSONObject(j);

                    if (jsonObject.getString("order_status").equalsIgnoreCase("View")) {

                        binding.tvOrderDatetimePurchaseOrder.setText("" + gDateTime.ymdTodmy(data_object.getString("order_date")) + " "
                                + data_object.getString("order_date").substring(11));
                        if (!data_object.getString("shipping_date").equalsIgnoreCase("0000-00-00 00:00:00"))
                            binding.tvDeliveryDatetimePurchaseOrder.setText("" + gDateTime.ymdTodmy(data_object.getString("shipping_date")) + " "
                                    + data_object.getString("shipping_date").substring(11));
                        binding.tvOrderNoPurchaseOrder.setText("" + data_object.getString("purchase_id"));
                        binding.tvTotalOrderRsAddPurchaseOrder.setText("" + String.format("%.2f", Double.parseDouble(data_object.getString("order_amount"))));
                        order_total_rs = data_object.getDouble("order_amount");

                        binding.spnDistName.setVisibility(View.GONE);
                        binding.tvDistNameAddpurchaseorder.setVisibility(View.VISIBLE);
                        binding.tvDistNameAddpurchaseorder.setText("" + data_object.getString("dist_name"));
                        binding.tvStockPointName.setText("" + data_object.getString("stock_distributor_name"));

                        if (data_object.getString("purchase_order_status").equalsIgnoreCase("1"))
                            binding.tvOrderStatus.setText("Submit");
                        else if (data_object.getString("purchase_order_status").equalsIgnoreCase("2"))
                            binding.tvOrderStatus.setText("Verified");
                        else if (data_object.getString("purchase_order_status").equalsIgnoreCase("3"))
                            binding.tvOrderStatus.setText("Authorised");
                        else if (data_object.getString("purchase_order_status").equalsIgnoreCase("4"))
                            binding.tvOrderStatus.setText("Packing");
                        else if (data_object.getString("purchase_order_status").equalsIgnoreCase("5"))
                            binding.tvOrderStatus.setText("Shipping");
                        else if (data_object.getString("purchase_order_status").equalsIgnoreCase("6"))
                            binding.tvOrderStatus.setText("Shipped");

                        prev_order_status = Integer.parseInt(data_object.getString("purchase_order_status"));

                        Log.i(TAG, "inside task prev_order_status=>" + prev_order_status);

                        if (prev_order_status == 3)
                            binding.btnUpdateProdAddPurchaseOrder.setVisibility(View.VISIBLE);
                        else
                            binding.btnUpdateProdAddPurchaseOrder.setVisibility(View.GONE);


                        if (prev_order_status >= 3) {
                            //binding.llOrderBtn.setVisibility(View.GONE);
                            binding.btnSubmitAddPurchaseOrder.setVisibility(View.GONE);
                        }

                        if (prev_order_status == 1 && isverify.equalsIgnoreCase("0"))
                            binding.btnSubmitAddPurchaseOrder.setVisibility(View.GONE);
                        //binding.llOrderBtn.setVisibility(View.GONE);

                        if (prev_order_status == 2 && isauthentication.equalsIgnoreCase("0") && isverify.equalsIgnoreCase("0"))
                            binding.btnSubmitAddPurchaseOrder.setVisibility(View.GONE);
                        //binding.llOrderBtn.setVisibility(View.GONE);

                        /*if (prev_order_status == 2 && issubmit.equalsIgnoreCase("0") && isverify.equalsIgnoreCase("0")
                                && isauthentication.equalsIgnoreCase("0"))
                            binding.llOrderBtn.setVisibility(View.GONE);*/


                        if (data_object.getString("purchase_order_status").equalsIgnoreCase("1")) {
                            if (isverify.equalsIgnoreCase("1") && isauthentication.equalsIgnoreCase("1"))
                                binding.btnSubmitAddPurchaseOrder.setText("Verify & AUTHORISE");
                            else
                                binding.btnSubmitAddPurchaseOrder.setText("Verify");
                        } else if (data_object.getString("purchase_order_status").equalsIgnoreCase("2"))
                            binding.btnSubmitAddPurchaseOrder.setText("AUTHORISE");

                        order_dist_id = data_object.getString("dist_id");

                    }


                    JSONArray prod_detail_array = data_object.getJSONArray("prod_detail");

                    for (int i = 0; i < prod_detail_array.length(); i++) {

                        JSONObject object = prod_detail_array.getJSONObject(i);

                        JSONArray min_data_array = object.getJSONArray("min_data");
                        arrayList_minvalue = new ArrayList<>();
                        for (int k = 0; k < min_data_array.length(); k++) {
                            arrayList_minvalue.add(min_data_array.getDouble(k));
                        }

                        if (jsonObject.getString("order_status").equalsIgnoreCase("View")) {


                            addPurchaseOrderProductsPOJO = new AddPurchaseOrderProductsPOJO(object.getString("product_id"),
                                    object.getString("title"),
                                    object.getString("title_hindi"),
                                    object.getString("short_desc"),
                                    object.getString("long_desc"),
                                    object.getString("gst"),
                                    object.getString("dispatch_code"),
                                    "",
                                    object.getString("point"),
                                    object.getString("prod_unit"),
                                    object.getString("photo"),
                                    object.getString("product_qty"),
                                    object.getString("product_qty_del"),
                                    object.getString("product_price"),
                                    "",
                                    "",
                                    "",
                                    arrayList_minvalue);

                        } else if (jsonObject.getString("order_status").equalsIgnoreCase("Add")) {

                            addPurchaseOrderProductsPOJO = new AddPurchaseOrderProductsPOJO(object.getString("product_id"),
                                    object.getString("title"),
                                    object.getString("title_hindi"),
                                    object.getString("short_desc"),
                                    object.getString("long_desc"),
                                    object.getString("gst"),
                                    object.getString("dispatch_code"),
                                    object.getString("purchase_rate"),
                                    object.getString("point"),
                                    object.getString("prod_unit"),
                                    object.getString("photo"),
                                    "",
                                    "",
                                    "",
                                    object.getString("stock_qty"),
                                    object.getString("pending"),
                                    object.getString("stock_in_days"),
                                    arrayList_minvalue);
                        }

                        arrayList_product_list.add(addPurchaseOrderProductsPOJO);
                    }

                    ProductListAdapter productListAdapter = new ProductListAdapter(arrayList_product_list, getApplicationContext());
                    binding.rvPurchaseOrderProductList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                    binding.rvPurchaseOrderProductList.setAdapter(productListAdapter);


                    JSONArray scheme_list_array = data_object.getJSONArray("scheme_list");

                    //if (jsonObject.getString("order_status").equalsIgnoreCase("Add")) {

                    arrayList_total_scheme_order_list = new ArrayList<>();
                    arrayList_scheme_order_discount = new ArrayList<>();
                    arrayList_schemes_order_list = new ArrayList<>();

                    arrayList_scheme_list = new ArrayList<>();
                    arrayList_match_scheme_list = new ArrayList<>();
                    arrayList_view_scheme_order_list = new ArrayList<>();

                    if (scheme_list_array.length() > 0) {

                        binding.rvSchemeListPurchaseorder.setVisibility(View.VISIBLE);

                        for (int i = 0; i < scheme_list_array.length(); i++) {

                            JSONObject scheme_list_object = scheme_list_array.getJSONObject(i);

                            JSONArray scheme_prod_array = scheme_list_object.getJSONArray("scheme_prod");

                            arrayList_scheme_prod = new ArrayList<>();


                            for (int k = 0; k < scheme_prod_array.length(); k++) {

                                JSONObject scheme_prod_object = scheme_prod_array.getJSONObject(k);

                                schemeProductPOJO = new SchemeProductPOJO(scheme_prod_object.getString("product_id"),
                                        scheme_prod_object.getString("product_title"),
                                        scheme_prod_object.getString("product_qty"));

                                arrayList_scheme_prod.add(schemeProductPOJO);
                            }

                            if (purchase_id.isEmpty()) {

                                schemeListPOJO = new SchemeListPOJO(scheme_list_object.getString("scheme_id"),
                                        scheme_list_object.getString("scheme_name_sort"),
                                        scheme_list_object.getString("scheme_name_long"),
                                        scheme_list_object.getString("scheme_type_id"),
                                        scheme_list_object.getString("scheme_type_name"),
                                        scheme_list_object.getString("scheme_image"),
                                        scheme_list_object.getString("scheme_image_sort"),
                                        scheme_list_object.getString("scheme_price"),
                                        scheme_list_object.getString("result_product_id"),
                                        scheme_list_object.getString("result_product_qty"),
                                        scheme_list_object.getString("result_product_price"),
                                        scheme_list_object.getString("total_result_prod_value"),
                                        scheme_list_object.getString("pricelist_id"),
                                        scheme_list_object.getString("result_product_photo"),
                                        scheme_list_object.getString("result_product_photo_sort"),
                                        scheme_list_object.getString("pricelist_name"),
                                        scheme_list_object.getString("is_scheme_half"),
                                        scheme_list_object.getString("status"),
                                        arrayList_scheme_prod);


                                arrayList_scheme_list.add(schemeListPOJO);

                                arrayList_match_scheme_list.add(new MatchShemePOJO(scheme_list_object.getString("scheme_id"),
                                        0 + "", 0 + ""));
                            } else {

                                schemeListPOJO = new SchemeListPOJO(scheme_list_object.getString("scheme_id"),
                                        scheme_list_object.getString("scheme_name_sort"),
                                        scheme_list_object.getString("scheme_name_long"),
                                        scheme_list_object.getString("scheme_type_id"),
                                        scheme_list_object.getString("scheme_type_name"),
                                        scheme_list_object.getString("scheme_image"),
                                        scheme_list_object.getString("scheme_qty"),
                                        scheme_list_object.getString("scheme_qty_del"),
                                        (scheme_list_object.getDouble("scheme_qty") *
                                                scheme_list_object.getDouble("scheme_price")) + "",
                                        scheme_list_object.getString("scheme_image_sort"),
                                        scheme_list_object.getString("scheme_price"),
                                        scheme_list_object.getString("result_product_id"),
                                        scheme_list_object.getString("result_product_qty"),
                                        scheme_list_object.getString("result_product_price"),
                                        scheme_list_object.getString("total_result_prod_value"),
                                        scheme_list_object.getString("pricelist_id"),
                                        scheme_list_object.getString("result_product_photo"),
                                        scheme_list_object.getString("result_product_photo_sort"),
                                        scheme_list_object.getString("pricelist_name"),
                                        scheme_list_object.getString("is_scheme_half"),
                                        scheme_list_object.getString("status"),
                                        arrayList_scheme_prod);


                                arrayList_scheme_list.add(schemeListPOJO);

                                total_scheme_order_amount = total_scheme_order_amount + (scheme_list_object.getDouble("scheme_qty") *
                                        scheme_list_object.getDouble("scheme_price"));


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
                                        scheme_list_object.getString("is_scheme_half")));

                                if (scheme_list_object.getString("scheme_qty_del").equalsIgnoreCase("0")) {

                                    arrayList_total_scheme_order_list.add(new MatchShemePOJO(scheme_list_object.getString("scheme_id"),
                                            scheme_list_object.getString("scheme_qty"),
                                            scheme_list_object.getString("scheme_price")));

                                    arrayList_scheme_order_discount.add((Double.parseDouble(scheme_list_object.getString("total_result_prod_value"))
                                            * (Double.parseDouble(scheme_list_object.getString("scheme_qty")))));


                                } else {

                                    arrayList_total_scheme_order_list.add(new MatchShemePOJO(scheme_list_object.getString("scheme_id"),
                                            scheme_list_object.getString("scheme_qty_del"),
                                            scheme_list_object.getString("scheme_price")));

                                    arrayList_scheme_order_discount.add((Double.parseDouble(scheme_list_object.getString("total_result_prod_value")) *
                                            (Double.parseDouble(scheme_list_object.getString("scheme_qty_del")))));
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
                                        scheme_list_object.getString("is_scheme_half"),
                                        arrayList_scheme_prod);


                                arrayList_view_scheme_order_list.add(viewSchemesOrderPOJO);


                                binding.tvTotalSchemeOrderValuePurchaseorder.setText("" + total_scheme_order_amount);
                                binding.tvTotalDiscountValuePurchaseorder.setText("" + total_discount);


                            }


                        }

                        SchemListAdapter schemListAdapter = new SchemListAdapter(arrayList_scheme_list, AddPurchaseOrder.this);
                        binding.rvSchemeListPurchaseorder.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                        binding.rvSchemeListPurchaseorder.setAdapter(schemListAdapter);


                    } else
                        binding.rvSchemeListPurchaseorder.setVisibility(View.GONE);

                    /*} else {


                        if (scheme_list_array.length() > 0) {

                            binding.rvSchemeListPurchaseorder.setVisibility(View.VISIBLE);
                            binding.tvAvailableTitlePurchaseorder.setVisibility(View.VISIBLE);

                            arrayList_total_scheme_order_list = new ArrayList<>();
                            arrayList_scheme_order_discount = new ArrayList<>();
                            arrayList_schemes_order_list = new ArrayList<>();

                            arrayList_view_scheme_order_list = new ArrayList<>();

                            for (int k = 0; k < scheme_list_array.length(); k++) {

                                JSONObject scheme_list_object = scheme_list_array.getJSONObject(k);

                                JSONArray scheme_prod_array = scheme_list_object.getJSONArray("scheme_prod");

                                arrayList_scheme_prod = new ArrayList<>();

                                for (int l = 0; l < scheme_prod_array.length(); l++) {

                                    JSONObject scheme_prod_object = scheme_prod_array.getJSONObject(l);

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
                                        scheme_list_object.getString("is_scheme_half"),
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
                                        scheme_list_object.getString("is_scheme_half")));

                                if (scheme_list_object.getString("scheme_qty_del").equalsIgnoreCase("0")) {

                                    arrayList_total_scheme_order_list.add(new MatchShemePOJO(scheme_list_object.getString("scheme_id"),
                                            scheme_list_object.getString("scheme_qty"),
                                            scheme_list_object.getString("scheme_price")));

                                    arrayList_scheme_order_discount.add((Double.parseDouble(scheme_list_object.getString("total_result_prod_value"))
                                            * (Double.parseDouble(scheme_list_object.getString("scheme_qty")))));


                                } else {

                                    arrayList_total_scheme_order_list.add(new MatchShemePOJO(scheme_list_object.getString("scheme_id"),
                                            scheme_list_object.getString("scheme_qty_del"),
                                            scheme_list_object.getString("scheme_price")));

                                    arrayList_scheme_order_discount.add((Double.parseDouble(scheme_list_object.getString("total_result_prod_value")) * (Double.parseDouble(scheme_list_object.getString("scheme_qty_del")))));
                                }
                            }


                            OrderSchemListAdapter schemListAdapter = new OrderSchemListAdapter(arrayList_view_scheme_order_list, AddPurchaseOrder.this);
                            binding.rvSchemeListPurchaseorder.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                            binding.rvSchemeListPurchaseorder.setAdapter(schemListAdapter);

                            binding.tvTotalSchemeOrderValuePurchaseorder.setText("" + total_scheme_order_amount);
                            binding.tvTotalDiscountValuePurchaseorder.setText("" + total_discount);

                        } else {

                            binding.rvSchemeListPurchaseorder.setVisibility(View.GONE);
                            binding.tvAvailableTitlePurchaseorder.setVisibility(View.GONE);

                        }

                    }*/
                }


            } else {


            }


        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }

    public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyHolder> {

        ArrayList<AddPurchaseOrderProductsPOJO> arrayList_product_list;
        Context context;

        int start = 1;
        double product_price = 0, tot_order_price = 0;

        int min_pos = 0;


        public ProductListAdapter(ArrayList<AddPurchaseOrderProductsPOJO> arrayList_product_list, Context context) {
            this.arrayList_product_list = arrayList_product_list;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityOrderProductAddPurchaseOrderBinding.inflate(LayoutInflater.from(context)));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {

            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    AddPurchaseOrder.this, setLangEntity(sp_multi_lang.getString("lang", "")));
            Language.CommanList commanList = language.getData();
            if (commanList.getArrayList() != null && commanList.getArrayList().size() > 0) {
                holder.binding.tvRequiredStockTitle.setText("" + commanList.getArrayList().get(0));
                holder.binding.tvPtsRsTitle.setText("" + commanList.getArrayList().get(1));
                holder.binding.tvOrderRsTitle.setText("" + commanList.getArrayList().get(2));
            }
            holder.binding.tvProductNameAddPurchaseOrder.setText("" + arrayList_product_list.get(position).getTitle());
            holder.binding.tvProductUnitAddPurchaseOrder.setText("" + arrayList_product_list.get(position).getProd_unit());


            if (!arrayList_product_list.get(position).getStock_qty().isEmpty())
                holder.binding.tvProductStockQtyAddPurchadeOrder.setText("" + arrayList_product_list.get(position).getStock_qty());

            if (!arrayList_product_list.get(position).getStock_in_days().isEmpty())
                holder.binding.tvProductStockIndaysAddPurchadeOrder.setText("" + arrayList_product_list.get(position).getStock_in_days());

            if (!arrayList_product_list.get(position).getProduct_qty().isEmpty()) {

                holder.binding.edtProductQtyAddPurchaseOrder.setText("" + arrayList_product_list.get(position).getProduct_qty());
                holder.binding.tvProductPtsRsAddPurchaseOrder.setText("" + arrayList_product_list.get(position).getProduct_price());
                holder.binding.tvProductOrderRsAddPurchaseOrder.setText("" + String.format("%.2f", (Double.parseDouble(arrayList_product_list.get(position).getProduct_qty()) * Double.parseDouble(arrayList_product_list.get(position).getProduct_price()))));

                arrayList_order_product_list.add(new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                        arrayList_product_list.get(position).getProduct_qty(),
                        arrayList_product_list.get(position).getProduct_price(),
                        arrayList_product_list.get(position).getPurchase_rate(),
                        arrayList_product_list.get(position).getTitle(),
                        arrayList_product_list.get(position).getTitle_hindi(),
                        arrayList_product_list.get(position).getProd_unit(),
                        arrayList_product_list.get(position).getShort_desc(),
                        arrayList_product_list.get(position).getLong_desc(),
                        arrayList_product_list.get(position).getGst(),
                        arrayList_product_list.get(position).getDispatch_code()));

            } else {

                holder.binding.tvProductPtsRsAddPurchaseOrder.setText("" + arrayList_product_list.get(position).getPurchase_rate());

                arrayList_order_product_list.add(new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                        holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim(),
                        arrayList_product_list.get(position).getPurchase_rate(),
                        arrayList_product_list.get(position).getPurchase_rate(),
                        arrayList_product_list.get(position).getTitle(),
                        arrayList_product_list.get(position).getTitle_hindi(),
                        arrayList_product_list.get(position).getProd_unit(),
                        arrayList_product_list.get(position).getShort_desc(),
                        arrayList_product_list.get(position).getLong_desc(),
                        arrayList_product_list.get(position).getGst(),
                        arrayList_product_list.get(position).getDispatch_code()));

            }

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.notfoundimages);

            Glide.with(getApplicationContext()).load(arrayList_product_list.get(position).getPhoto() + "")
                    .apply(options)
                    .into(holder.binding.imgProductPhotoAddPurchaseOrder);

            //holder.binding.tvOrderProductOrderRs.setText("" + arrayList_product_order.get(position).getSales_rate());

            if (prev_order_status >= 4) {

                holder.binding.edtProductQtyAddPurchaseOrder.setEnabled(false);
                holder.binding.imgProductPlusQtyAddPurchaseOrder.setEnabled(false);
                holder.binding.imgProductMinusQtyAddPurchaseOrder.setEnabled(false);
            }

            getTotalOrderRs();

            holder.binding.edtProductQtyAddPurchaseOrder.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!arrayList_product_list.get(position).getProduct_qty().isEmpty())
                        product_price = Double.parseDouble(arrayList_product_list.get(position).getProduct_price());
                    else
                        product_price = Double.parseDouble(arrayList_product_list.get(position).getPurchase_rate());

                    if (holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().isEmpty()) {

                        holder.binding.edtProductQtyAddPurchaseOrder.setText("0");

                        tot_order_price = 0 * product_price;
                        holder.binding.tvProductOrderRsAddPurchaseOrder.setText("" + String.format("%.2f", tot_order_price));

                        if (arrayList_product_list.get(position).getPurchase_rate().isEmpty()) {

                            arrayList_order_product_list.set(position, new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                                    0 + "",
                                    arrayList_product_list.get(position).getProduct_price() + "",
                                    arrayList_product_list.get(position).getPurchase_rate(),
                                    arrayList_product_list.get(position).getTitle(),
                                    arrayList_product_list.get(position).getTitle_hindi(),
                                    arrayList_product_list.get(position).getProd_unit(),
                                    arrayList_product_list.get(position).getShort_desc(),
                                    arrayList_product_list.get(position).getLong_desc(),
                                    arrayList_product_list.get(position).getGst(),
                                    arrayList_product_list.get(position).getDispatch_code()));

                        } else {

                            arrayList_order_product_list.set(position, new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                                    0 + "",
                                    arrayList_product_list.get(position).getPurchase_rate() + "",
                                    arrayList_product_list.get(position).getPurchase_rate(),
                                    arrayList_product_list.get(position).getTitle(),
                                    arrayList_product_list.get(position).getTitle_hindi(),
                                    arrayList_product_list.get(position).getProd_unit(),
                                    arrayList_product_list.get(position).getShort_desc(),
                                    arrayList_product_list.get(position).getLong_desc(),
                                    arrayList_product_list.get(position).getGst(),
                                    arrayList_product_list.get(position).getDispatch_code()));
                        }
                    } else {

                        //tot_order_price = start * product_price;
                        tot_order_price = Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) * product_price;
                        holder.binding.tvProductOrderRsAddPurchaseOrder.setText("" + String.format("%.2f", tot_order_price));


                        if (arrayList_product_list.get(position).getPurchase_rate().isEmpty()) {

                            arrayList_order_product_list.set(position, new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                                    Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) + "",
                                    arrayList_product_list.get(position).getProduct_price() + "",
                                    arrayList_product_list.get(position).getPurchase_rate(),
                                    arrayList_product_list.get(position).getTitle(),
                                    arrayList_product_list.get(position).getTitle_hindi(),
                                    arrayList_product_list.get(position).getProd_unit(),
                                    arrayList_product_list.get(position).getShort_desc(),
                                    arrayList_product_list.get(position).getLong_desc(),
                                    arrayList_product_list.get(position).getGst(),
                                    arrayList_product_list.get(position).getDispatch_code()));

                        } else {

                            arrayList_order_product_list.set(position, new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                                    Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) + "",
                                    arrayList_product_list.get(position).getPurchase_rate() + "",
                                    arrayList_product_list.get(position).getPurchase_rate(),
                                    arrayList_product_list.get(position).getTitle(),
                                    arrayList_product_list.get(position).getTitle_hindi(),
                                    arrayList_product_list.get(position).getProd_unit(),
                                    arrayList_product_list.get(position).getShort_desc(),
                                    arrayList_product_list.get(position).getLong_desc(),
                                    arrayList_product_list.get(position).getGst(),
                                    arrayList_product_list.get(position).getDispatch_code()));
                        }
                    }

                    //Toast.makeText(context, "edit qty", Toast.LENGTH_SHORT).show();

                    getTotalOrderRs();

                    checkScheme();
                    /*if (purchase_id.isEmpty())
                        checkScheme();
                    else
                        checkOrderScheme();*/
                }

                @Override
                public void afterTextChanged(Editable s) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                if (!holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().isEmpty()) {

                                    boolean equal = false;

                                    double checkval = Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim());

                                    if (checkval > 0 && checkval < 1) {

                                        for (int i = 0; i < arrayList_product_list.get(position).getArrayList_minvalue().size(); i++) {

                                            //Log.i("checkval", arrayList_minvalue.get(i) + "");

                                            double v = arrayList_product_list.get(position).getArrayList_minvalue().get(i);

                                            //Log.i(checkval+"==",v+"");

                                            if (checkval == v)
                                                equal = true;

                                        }

                                        if (equal == true) {


                                        } else {

                                            Toast.makeText(context, commanSuchnaList.getArrayList().get(7) + "", Toast.LENGTH_SHORT).show();
                                            holder.binding.edtProductQtyAddPurchaseOrder.setText("0");
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

            holder.binding.imgProductPlusQtyAddPurchaseOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        isSchemeCheck = true;
                        holder.binding.edtProductQtyAddPurchaseOrder.requestFocus();

                        if (holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim().isEmpty()) {
                            start = 0;
                        } else {
                            start = (int) Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim());
                        }


                        start++;

                        if ((int) Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) < 1)
                            min_pos = 1 + arrayList_product_list.get(position).getArrayList_minvalue().indexOf(Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()));

                        if (Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) < 1 && min_pos < arrayList_product_list.get(position).getArrayList_minvalue().size()) {

                            // Log.i(TAG, "inside if min_pos=>" + min_pos);

                            holder.binding.edtProductQtyAddPurchaseOrder.setText("" + arrayList_product_list.get(position).getArrayList_minvalue().get(min_pos));
                            min_pos++;

                            // Log.i(TAG, "min_pos=>" + min_pos);

                        } else {

                            //  Log.i(TAG, "inside else");
                            //Log.i(TAG, "min_pos=>" + min_pos);

                            holder.binding.edtProductQtyAddPurchaseOrder.setText("" + start);
                        }

                        if (!arrayList_product_list.get(position).getProduct_qty().isEmpty())
                            product_price = Double.parseDouble(arrayList_product_list.get(position).getProduct_price());
                        else
                            product_price = Double.parseDouble(arrayList_product_list.get(position).getPurchase_rate());

                        //tot_order_price = start * product_price;
                        tot_order_price = Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) * product_price;
                        holder.binding.tvProductOrderRsAddPurchaseOrder.setText("" + String.format("%.2f", tot_order_price));


                        if (arrayList_product_list.get(position).getPurchase_rate().isEmpty()) {

                            arrayList_order_product_list.set(position, new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                                    Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) + "",
                                    arrayList_product_list.get(position).getProduct_price() + "",
                                    arrayList_product_list.get(position).getPurchase_rate(),
                                    arrayList_product_list.get(position).getTitle(),
                                    arrayList_product_list.get(position).getTitle_hindi(),
                                    arrayList_product_list.get(position).getProd_unit(),
                                    arrayList_product_list.get(position).getShort_desc(),
                                    arrayList_product_list.get(position).getLong_desc(),
                                    arrayList_product_list.get(position).getGst(),
                                    arrayList_product_list.get(position).getDispatch_code()));

                        } else {

                            arrayList_order_product_list.set(position, new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                                    Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) + "",
                                    arrayList_product_list.get(position).getPurchase_rate() + "",
                                    arrayList_product_list.get(position).getPurchase_rate(),
                                    arrayList_product_list.get(position).getTitle(),
                                    arrayList_product_list.get(position).getTitle_hindi(),
                                    arrayList_product_list.get(position).getProd_unit(),
                                    arrayList_product_list.get(position).getShort_desc(),
                                    arrayList_product_list.get(position).getLong_desc(),
                                    arrayList_product_list.get(position).getGst(),
                                    arrayList_product_list.get(position).getDispatch_code()));
                        }

                        getTotalOrderRs();

                    } catch (Exception e) {
                        e.getMessage();
                    }

                }
            });

            holder.binding.imgProductMinusQtyAddPurchaseOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        isSchemeCheck = true;

                        holder.binding.edtProductQtyAddPurchaseOrder.requestFocus();

                        if (holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim().isEmpty()) {
                            start = 0;
                        } else {
                            start = (int) Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim());
                        }

                        start--;

                        if ((int) Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) < 1)
                            min_pos = arrayList_product_list.get(position).getArrayList_minvalue().indexOf(Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()));


                        if (Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) == 1) {


                            if (arrayList_product_list.get(position).getArrayList_minvalue().size() > 1) {

                                //Log.i(TAG, "inside if min_pos=>" + min_pos);

                                min_pos = arrayList_product_list.get(position).getArrayList_minvalue().size() - 1;

                                holder.binding.edtProductQtyAddPurchaseOrder.setText(arrayList_product_list.get(position).getArrayList_minvalue().get(min_pos).toString());
                                //Log.i(TAG, "min_pos=>" + min_pos);

                            } else {

                                holder.binding.edtProductQtyAddPurchaseOrder.setText("" + start);

                                //Log.i(TAG, "inside if else min_pos=>" + min_pos);

                            }


                        } else if (Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) > 0 && Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) < 1) {

                            // Log.i(TAG, "inside else if min_pos=>" + min_pos);
                            min_pos--;
                            holder.binding.edtProductQtyAddPurchaseOrder.setText(arrayList_product_list.get(position).getArrayList_minvalue().get(min_pos).toString());
                            // Log.i(TAG, "min_pos=>" + min_pos);

                        } else {

                            // Log.i(TAG, "inside else");
                            // Log.i(TAG, "min_pos=>" + min_pos);

                            if (Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) > 0) {
                                holder.binding.edtProductQtyAddPurchaseOrder.setText("" + start);
                                // Log.i(TAG, "inside else -> if =>" + start);
                            }
                        }

                        if (!arrayList_product_list.get(position).getProduct_qty().isEmpty())
                            product_price = Double.parseDouble(arrayList_product_list.get(position).getProduct_price());
                        else
                            product_price = Double.parseDouble(arrayList_product_list.get(position).getPurchase_rate());

                        //tot_order_price = start * product_price;
                        tot_order_price = Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) * product_price;
                        holder.binding.tvProductOrderRsAddPurchaseOrder.setText("" + String.format("%.2f", tot_order_price));


                        if (arrayList_product_list.get(position).getPurchase_rate().isEmpty()) {

                            arrayList_order_product_list.set(position, new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                                    Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) + "",
                                    arrayList_product_list.get(position).getProduct_price() + "",
                                    arrayList_product_list.get(position).getPurchase_rate(),
                                    arrayList_product_list.get(position).getTitle(),
                                    arrayList_product_list.get(position).getTitle_hindi(),
                                    arrayList_product_list.get(position).getProd_unit(),
                                    arrayList_product_list.get(position).getShort_desc(),
                                    arrayList_product_list.get(position).getLong_desc(),
                                    arrayList_product_list.get(position).getGst(),
                                    arrayList_product_list.get(position).getDispatch_code()));

                        } else {

                            arrayList_order_product_list.set(position, new OrderProductListPOJO(arrayList_product_list.get(position).getProduct_id(),
                                    Double.parseDouble(holder.binding.edtProductQtyAddPurchaseOrder.getText().toString().trim()) + "",
                                    arrayList_product_list.get(position).getPurchase_rate() + "",
                                    arrayList_product_list.get(position).getPurchase_rate(),
                                    arrayList_product_list.get(position).getTitle(),
                                    arrayList_product_list.get(position).getTitle_hindi(),
                                    arrayList_product_list.get(position).getProd_unit(),
                                    arrayList_product_list.get(position).getShort_desc(),
                                    arrayList_product_list.get(position).getLong_desc(),
                                    arrayList_product_list.get(position).getGst(),
                                    arrayList_product_list.get(position).getDispatch_code()));
                        }
                        getTotalOrderRs();

                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList_product_list.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityOrderProductAddPurchaseOrderBinding binding;

            public MyHolder(EntityOrderProductAddPurchaseOrderBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    public class SchemListAdapter extends RecyclerView.Adapter<SchemListAdapter.MyHolder> {

        ArrayList<SchemeListPOJO> arrayList_scheme_list;
        Context context;

        double start = 0;

        public SchemListAdapter(ArrayList<SchemeListPOJO> arrayList_scheme_list, Context context) {
            this.arrayList_scheme_list = arrayList_scheme_list;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntitySchemesListBinding.inflate(LayoutInflater.from(context), null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {


            holder.binding.tvSchemeLongName.setText("" + arrayList_scheme_list.get(position).getScheme_long_name());
            holder.binding.tvConditionValue.setText("₹ " + arrayList_scheme_list.get(position).getScheme_price());
            holder.binding.tvResultProdValue.setText("₹ " + arrayList_scheme_list.get(position).getTotal_result_prod_value());

            //holder.binding.edtSchemeQty.setInputType(InputType.TYPE_NULL);

            holder.binding.edtSchemeQty.setEnabled(false);
            holder.binding.imgSchemePlusQty.setEnabled(false);
            holder.binding.imgSchemeMinusQty.setEnabled(false);

            getTotalSchemeOrderAmount();


            if (purchase_id.isEmpty()) {

                Log.i(TAG, "inside purchase_id empty");

                if (arrayList_scheme_list.get(position).getScheme_type_id().equalsIgnoreCase("1")) {

                    if (Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim()) >
                            (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.6)
                            && Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim()) <
                            Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())) {

                        holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#FF9800"));
                        holder.binding.llSchemeValue.setBackgroundColor(Color.parseColor("#FF9800"));
                        holder.binding.rlSchemeList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view6.setBackgroundColor(Color.parseColor("#FF9800"));

                        holder.binding.view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view5.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    } else if (Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim()) >=
                            Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())) {

                        holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#27ae60"));
                        //holder.binding.llSchemeValue.setBackgroundColor(Color.parseColor("#27ae60"));
                        holder.binding.llSchemeValue.setBackgroundColor(Color.GREEN);
                        holder.binding.rlSchemeList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view6.setBackgroundColor(Color.parseColor("#27ae60"));

                        holder.binding.view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view5.setBackgroundColor(Color.parseColor("#FFFFFF"));


                        holder.binding.imgSchemePlusQty.setEnabled(true);
                        holder.binding.imgSchemeMinusQty.setEnabled(true);
                    }

                    holder.binding.tvSchemeOrderValue.setText(" / " + String.format("%.2f", Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim())));

                } else {

                    if (Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price()) > (
                            Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.6)
                            && Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price()) <
                            Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())) {

                        holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#FF9800"));
                        holder.binding.llSchemeValue.setBackgroundColor(Color.parseColor("#FF9800"));
                        holder.binding.rlSchemeList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view6.setBackgroundColor(Color.parseColor("#FF9800"));


                        holder.binding.view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view5.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    } else if (Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price()) >=
                            Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())) {

                        holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#27ae60"));
                        holder.binding.llSchemeValue.setBackgroundColor(Color.GREEN);
                        holder.binding.rlSchemeList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view6.setBackgroundColor(Color.parseColor("#27ae60"));

                        holder.binding.view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        holder.binding.view5.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        holder.binding.imgSchemePlusQty.setEnabled(true);
                        holder.binding.imgSchemeMinusQty.setEnabled(true);
                    }

                    holder.binding.tvSchemeOrderValue.setText(" / " + String.format("%.2f", Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price())));
                }

            } else {

                //Log.i(TAG, "inside else purchase id");
                //Log.i(TAG, "inside else isSchemeCheck==" + isSchemeCheck);

                if (isSchemeCheck == true) {


                    holder.binding.edtSchemeQty.setText("0");
                    holder.binding.tvSchemeValue.setText("0.0");
                    holder.binding.tvSchemeOrderValue.setText(" / 0.0");

                    if (arrayList_scheme_list.get(position).getScheme_type_id().equalsIgnoreCase("1")) {

                        if (Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim()) >
                                (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.6)
                                && Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim()) <
                                Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())) {

                            holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#FF9800"));
                            holder.binding.llSchemeValue.setBackgroundColor(Color.parseColor("#FF9800"));
                            holder.binding.rlSchemeList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view6.setBackgroundColor(Color.parseColor("#FF9800"));

                            holder.binding.view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view5.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        } else if (Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim()) >=
                                Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())) {

                            holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#27ae60"));
                            //holder.binding.llSchemeValue.setBackgroundColor(Color.parseColor("#27ae60"));
                            holder.binding.llSchemeValue.setBackgroundColor(Color.GREEN);
                            holder.binding.rlSchemeList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view6.setBackgroundColor(Color.parseColor("#27ae60"));

                            holder.binding.view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view5.setBackgroundColor(Color.parseColor("#FFFFFF"));


                            holder.binding.imgSchemePlusQty.setEnabled(true);
                            holder.binding.imgSchemeMinusQty.setEnabled(true);
                        }

                        holder.binding.tvSchemeOrderValue.setText(" / " + binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim());

                    } else {

                        if (Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price()) >
                                (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.6)
                                && Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price()) <
                                Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())) {

                            holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#FF9800"));
                            holder.binding.llSchemeValue.setBackgroundColor(Color.parseColor("#FF9800"));
                            holder.binding.rlSchemeList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view6.setBackgroundColor(Color.parseColor("#FF9800"));


                            holder.binding.view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view5.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        } else if (Double.parseDouble(arrayList_match_scheme_list.get(position).getScheme_price()) >=
                                Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())) {

                            holder.binding.llSchemeList.setBackgroundColor(Color.parseColor("#27ae60"));
                            holder.binding.llSchemeValue.setBackgroundColor(Color.GREEN);
                            holder.binding.rlSchemeList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view6.setBackgroundColor(Color.parseColor("#27ae60"));

                            holder.binding.view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            holder.binding.view5.setBackgroundColor(Color.parseColor("#FFFFFF"));

                            holder.binding.imgSchemePlusQty.setEnabled(true);
                            holder.binding.imgSchemeMinusQty.setEnabled(true);
                        }

                        holder.binding.tvSchemeOrderValue.setText(" / " + arrayList_match_scheme_list.get(position).getScheme_price());
                    }

                } else {

                    holder.binding.edtSchemeQty.setEnabled(false);
                    holder.binding.imgSchemePlusQty.setEnabled(false);
                    holder.binding.imgSchemeMinusQty.setEnabled(false);

                    if (arrayList_scheme_list.get(position).getScheme_qty_del().equalsIgnoreCase("0")) {

                        //Log.i(TAG, "inside Undelivered");


                        holder.binding.edtSchemeQty.setText("" + arrayList_scheme_list.get(position).getScheme_qty());
                        holder.binding.tvSchemeValue.setText("" + (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_qty())
                                * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())));
                        holder.binding.tvSchemeOrderValue.setText(" / " + Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()));


                    } else {

                        //Log.i(TAG, "inside Delivered");

                        holder.binding.edtSchemeQty.setText("" + arrayList_scheme_list.get(position).getScheme_qty_del());
                        holder.binding.tvSchemeValue.setText("" + (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_qty_del())
                                * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())));
                        holder.binding.tvSchemeOrderValue.setText(" / " + Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()));

                    }


                }

            }


            if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0.5")) {

                if (Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) >
                        (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.5)) {

                    //Log.i(TAG, "inside half scheme");
                    if (prev_order_status >= 4) {
                        holder.binding.imgSchemePlusQty.setEnabled(false);
                        holder.binding.imgSchemeMinusQty.setEnabled(false);
                    } else {
                        holder.binding.imgSchemePlusQty.setEnabled(true);
                        holder.binding.imgSchemeMinusQty.setEnabled(true);
                    }

                }
            }


            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.notfoundimages);

            Glide.with(getApplicationContext()).load(arrayList_scheme_list.get(position).getScheme_image())
                    .apply(options)
                    .into(holder.binding.imgSchemePhoto);

            Glide.with(getApplicationContext()).load(arrayList_scheme_list.get(position).getResult_product_photo())
                    .apply(options)
                    .into(holder.binding.imgResultProdPhoto);

            if (purchase_id.isEmpty()) {


                if (arrayList_scheme_list.get(position).getScheme_type_id().equalsIgnoreCase("1")) {

                    arrayList_schemes_order_list.add(new SchemesOrderPOJO(arrayList_scheme_list.get(position).getScheme_id(),
                            arrayList_scheme_list.get(position).getScheme_name(),
                            arrayList_scheme_list.get(position).getScheme_long_name(),
                            arrayList_scheme_list.get(position).getScheme_type_id(),
                            arrayList_scheme_list.get(position).getScheme_type_name(),
                            arrayList_scheme_list.get(position).getScheme_image_sort(),
                            arrayList_scheme_list.get(position).getScheme_price(),
                            binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim() + "",
                            0 + "",
                            0 + "",
                            arrayList_scheme_list.get(position).getResult_product_id(),
                            arrayList_scheme_list.get(position).getResult_product_qty(),
                            arrayList_scheme_list.get(position).getResult_product_price(),
                            arrayList_scheme_list.get(position).getTotal_result_prod_value(),
                            arrayList_scheme_list.get(position).getResult_product_photo_sort(),
                            arrayList_scheme_list.get(position).getIs_scheme_half()));

                } else {

                    arrayList_schemes_order_list.add(new SchemesOrderPOJO(arrayList_scheme_list.get(position).getScheme_id(),
                            arrayList_scheme_list.get(position).getScheme_name(),
                            arrayList_scheme_list.get(position).getScheme_long_name(),
                            arrayList_scheme_list.get(position).getScheme_type_id(),
                            arrayList_scheme_list.get(position).getScheme_type_name(),
                            arrayList_scheme_list.get(position).getScheme_image_sort(),
                            arrayList_scheme_list.get(position).getScheme_price(),
                            arrayList_match_scheme_list.get(position).getScheme_price() + "",
                            0 + "",
                            0 + "",
                            arrayList_scheme_list.get(position).getResult_product_id(),
                            arrayList_scheme_list.get(position).getResult_product_qty(),
                            arrayList_scheme_list.get(position).getResult_product_price(),
                            arrayList_scheme_list.get(position).getTotal_result_prod_value(),
                            arrayList_scheme_list.get(position).getResult_product_photo_sort(),
                            arrayList_scheme_list.get(position).getIs_scheme_half()));

                }
            }

            holder.binding.imgSchemePlusQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.binding.edtSchemeQty.requestFocus();

                    //isSchemeCheck = true;

                    if (holder.binding.edtSchemeQty.getText().toString().trim().isEmpty()) {
                        start = 0;
                    } else {
                        start = Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim());
                    }

                    //Log.i(TAG, "TotalOrderRs=>" + binding.tvTotalOrderRs.getText().toString().trim());
                    //Log.i(TAG, "SchemeValue=>" + (start * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price())));
                    //Log.i(TAG, "SchemeOrderValue=>" + holder.binding.tvSchemeOrderValue.getText().toString().trim());


                    if ((Double.parseDouble(binding.tvTotalSchemeOrderValuePurchaseorder.getText().toString()) >
                            Double.parseDouble(binding.tvTotalOrderRsAddPurchaseOrder.getText().toString().trim()))) {

                        Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();

                        if (Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) >
                                (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.5) &&
                                Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) <
                                        (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))) {

                            Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();
                            //holder.binding.edtSchemeQty.setText("" + ((int) start - 0.5));

                        } else {

                            Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();
                            //holder.binding.edtSchemeQty.setText("" + ((int) start - 1));
                        }

                    } else {

                        if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0.5")) {

                            if ((Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) >
                                    Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")))) {

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();

                            } else {

                                if (Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim()) == 0) {

                                    holder.binding.edtSchemeQty.setText("0.5");

                                } else if (Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim()) >= 0.5) {

                                    start++;
                                    holder.binding.edtSchemeQty.setText("" + (int) start);
                                }
                            }

                        } else if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0")) {

                            if (((Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()) + 1) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) >
                                    Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")))) {

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();

                            } else {

                                if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0")) {

                                    if (start >= 0) {
                                        start++;
                                        holder.binding.edtSchemeQty.setText("" + (int) start);
                                    }
                                }

                            }

                        }


                        if ((Double.parseDouble(holder.binding.edtSchemeQty.getText().toString())
                                * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) >
                                Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")))) {

                            if (Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) >
                                    (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.5) &&
                                    Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) <
                                            (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))) {

                                //Log.i(TAG, "inside if starts=>" + start);

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();
                                holder.binding.edtSchemeQty.setText("" + ((int) start - 0.5));

                            } else {

                                //Log.i(TAG, "inside else starts=>" + start);

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();
                                holder.binding.edtSchemeQty.setText("" + ((int) start - 1));
                            }

                        } else {

                            holder.binding.tvSchemeValue.setText("" + String.format("%.2f", (Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()) *
                                    Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))));
                        }


                        double cmp_scheme_value = (Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()) *
                                Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()));

                        double schme_value = Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().replace("/", ""));

                        double tot_scheme_value = cmp_scheme_value + compareSchemes(arrayList_scheme_list.get(position).getScheme_id());

                        /*Log.i(TAG, "compareSchemes==>" + compareSchemes(arrayList_scheme_list.get(position).getScheme_id()));
                        Log.i(TAG, "tot_scheme_value==>" + tot_scheme_value);*/


                        if (tot_scheme_value > schme_value) {

                            //Toast.makeText(context, "...scheme value > scheme order...", Toast.LENGTH_SHORT).show();

                            if (Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) >
                                    (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()) * 0.5) &&
                                    Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) <
                                            (Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))) {

                                //Log.i(TAG, "inside if starts=>" + start);
                                //Log.i(TAG, "inside if starts=>" + holder.binding.edtSchemeQty.getText().toString());


                                Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();
                                if (holder.binding.edtSchemeQty.getText().toString().equalsIgnoreCase("0.5"))
                                    holder.binding.edtSchemeQty.setText("0");
                                else if (holder.binding.edtSchemeQty.getText().toString().equalsIgnoreCase("0") ||
                                        holder.binding.edtSchemeQty.getText().toString().equalsIgnoreCase("0.0"))
                                    holder.binding.edtSchemeQty.setText("0");
                                else
                                    holder.binding.edtSchemeQty.setText("" + ((int) start - 0.5));

                            } else {

                                //Log.i(TAG, "inside else starts=>" + holder.binding.edtSchemeQty.getText().toString());

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();

                                if (holder.binding.edtSchemeQty.getText().toString().equalsIgnoreCase("0.5"))
                                    holder.binding.edtSchemeQty.setText("0");
                                else if (holder.binding.edtSchemeQty.getText().toString().equalsIgnoreCase("0") ||
                                        holder.binding.edtSchemeQty.getText().toString().equalsIgnoreCase("0.0"))
                                    holder.binding.edtSchemeQty.setText("0");
                                else
                                    holder.binding.edtSchemeQty.setText("" + ((int) start - 1));
                            }

                            holder.binding.tvSchemeValue.setText("" + String.format("%.2f", (Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()) *
                                    Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))));


                        }

                    }
                    //holder.binding.tvSchemeValue.setText("" + String.format("%.2f", (Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()) * Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))));

                    arrayList_total_scheme_order_list.set(position, (new MatchShemePOJO(arrayList_scheme_list.get(position).getScheme_id(),
                            holder.binding.edtSchemeQty.getText().toString().trim(), arrayList_scheme_list.get(position).getScheme_price())));

                    arrayList_scheme_order_discount.set(position,
                            (Double.parseDouble(arrayList_scheme_list.get(position).getTotal_result_prod_value()) *
                                    Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim())));

                    if (purchase_id.isEmpty()) {

                        arrayList_schemes_order_list.set(position, (new SchemesOrderPOJO(arrayList_scheme_list.get(position).getScheme_id(),
                                arrayList_scheme_list.get(position).getScheme_name(),
                                arrayList_scheme_list.get(position).getScheme_long_name(),
                                arrayList_scheme_list.get(position).getScheme_type_id(),
                                arrayList_scheme_list.get(position).getScheme_type_name(),
                                arrayList_scheme_list.get(position).getScheme_image_sort(),
                                arrayList_scheme_list.get(position).getScheme_price(),
                                Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) + "",
                                Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim()) + "",
                                0 + "",
                                arrayList_scheme_list.get(position).getResult_product_id(),
                                arrayList_scheme_list.get(position).getResult_product_qty(),
                                arrayList_scheme_list.get(position).getResult_product_price(),
                                arrayList_scheme_list.get(position).getTotal_result_prod_value(),
                                arrayList_scheme_list.get(position).getResult_product_photo_sort(),
                                arrayList_scheme_list.get(position).getIs_scheme_half())));
                    } else {


                        arrayList_schemes_order_list.set(position, (new SchemesOrderPOJO(arrayList_scheme_list.get(position).getScheme_id(),
                                arrayList_scheme_list.get(position).getScheme_name(),
                                arrayList_scheme_list.get(position).getScheme_long_name(),
                                arrayList_scheme_list.get(position).getScheme_type_id(),
                                arrayList_scheme_list.get(position).getScheme_type_name(),
                                arrayList_scheme_list.get(position).getScheme_image(),
                                arrayList_scheme_list.get(position).getScheme_price(),
                                Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) + "",
                                Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim()) + "",
                                Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim()) + "",
                                arrayList_scheme_list.get(position).getResult_product_id(),
                                arrayList_scheme_list.get(position).getResult_product_qty(),
                                arrayList_scheme_list.get(position).getResult_product_price(),
                                arrayList_scheme_list.get(position).getTotal_result_prod_value(),
                                arrayList_scheme_list.get(position).getResult_product_photo(),
                                arrayList_scheme_list.get(position).getIs_scheme_half())));

                    }

                    getTotalSchemeOrderAmount();

                }


            });

            holder.binding.imgSchemeMinusQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //isSchemeCheck = true;

                    holder.binding.edtSchemeQty.requestFocus();

                    if (holder.binding.edtSchemeQty.getText().toString().trim().isEmpty()) {
                        start = 0;
                    } else {
                        start = Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim());
                    }

                    if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0.5")) {

                        if (start == 1) {

                            holder.binding.edtSchemeQty.setText("0.5");

                        } else if (start == 0.5) {

                            holder.binding.edtSchemeQty.setText("0");

                        } else {

                            start--;
                            if (start >= 0) {
                                holder.binding.edtSchemeQty.setText("" + (int) start);
                            }

                        }

                    } else if (arrayList_scheme_list.get(position).getIs_scheme_half().equalsIgnoreCase("0")) {

                        start--;

                        if (start > 0) {

                            holder.binding.edtSchemeQty.setText("" + (int) start);

                        } else {

                            holder.binding.edtSchemeQty.setText("0");

                        }

                    }

                    holder.binding.tvSchemeValue.setText("" + String.format("%.2f", (Double.parseDouble(holder.binding.edtSchemeQty.getText().toString()) *
                            Double.parseDouble(arrayList_scheme_list.get(position).getScheme_price()))));

                    arrayList_total_scheme_order_list.set(position, (new MatchShemePOJO(arrayList_scheme_list.get(position).getScheme_id(),
                            holder.binding.edtSchemeQty.getText().toString().trim(), arrayList_scheme_list.get(position).getScheme_price())));

                    arrayList_scheme_order_discount.set(position,
                            (Double.parseDouble(arrayList_scheme_list.get(position).getTotal_result_prod_value()) *
                                    Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim())));

                    if (purchase_id.isEmpty()) {

                        arrayList_schemes_order_list.set(position, (new SchemesOrderPOJO(arrayList_scheme_list.get(position).getScheme_id(),
                                arrayList_scheme_list.get(position).getScheme_name(),
                                arrayList_scheme_list.get(position).getScheme_long_name(),
                                arrayList_scheme_list.get(position).getScheme_type_id(),
                                arrayList_scheme_list.get(position).getScheme_type_name(),
                                arrayList_scheme_list.get(position).getScheme_image_sort(),
                                arrayList_scheme_list.get(position).getScheme_price(),
                                Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) + "",
                                Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim()) + "",
                                0 + "",
                                arrayList_scheme_list.get(position).getResult_product_id(),
                                arrayList_scheme_list.get(position).getResult_product_qty(),
                                arrayList_scheme_list.get(position).getResult_product_price(),
                                arrayList_scheme_list.get(position).getTotal_result_prod_value(),
                                arrayList_scheme_list.get(position).getResult_product_photo_sort(),
                                arrayList_scheme_list.get(position).getIs_scheme_half())));
                    } else {


                        arrayList_schemes_order_list.set(position, (new SchemesOrderPOJO(arrayList_scheme_list.get(position).getScheme_id(),
                                arrayList_scheme_list.get(position).getScheme_name(),
                                arrayList_scheme_list.get(position).getScheme_long_name(),
                                arrayList_scheme_list.get(position).getScheme_type_id(),
                                arrayList_scheme_list.get(position).getScheme_type_name(),
                                arrayList_scheme_list.get(position).getScheme_image(),
                                arrayList_scheme_list.get(position).getScheme_price(),
                                Double.parseDouble(holder.binding.tvSchemeOrderValue.getText().toString().trim().replace("/", "")) + "",
                                Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim()) + "",
                                Double.parseDouble(holder.binding.edtSchemeQty.getText().toString().trim()) + "",
                                arrayList_scheme_list.get(position).getResult_product_id(),
                                arrayList_scheme_list.get(position).getResult_product_qty(),
                                arrayList_scheme_list.get(position).getResult_product_price(),
                                arrayList_scheme_list.get(position).getTotal_result_prod_value(),
                                arrayList_scheme_list.get(position).getResult_product_photo(),
                                arrayList_scheme_list.get(position).getIs_scheme_half())));

                    }

                    getTotalSchemeOrderAmount();

                }
            });


        }

        @Override
        public int getItemCount() {
            return arrayList_scheme_list.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntitySchemesListBinding binding;

            public MyHolder(EntitySchemesListBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }

    /*=============================code for get more product starts=========================*/

    public class AddPurchaseOrderTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "add_order_url=>" + add_purchase_order_url + "");

            HttpHandler httpHandler = new HttpHandler();
            add_purchase_order_response = httpHandler.makeServiceCall(add_purchase_order_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "add_order_res=>" + add_purchase_order_response + "");

                if (add_purchase_order_response.contains("Added Successfully")) {
                    showDialog(commanSuchnaList.getArrayList().get(0) + "");
                } else {
                    Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    public class getDistListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "dist_list_url=>" + dist_list_url);

            HttpHandler httpHandler = new HttpHandler();
            dist_list_response = httpHandler.makeServiceCall(dist_list_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "dist_list_response=>" + dist_list_response);
                getDistList();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getDistList() {

        arrayList_distributor_id = new ArrayList<>();
        arrayList_distributor_name = new ArrayList<>();
        arrayList_stock_point_name = new ArrayList<>();
        arrayList_transporter_name = new ArrayList<>();
        arrayList_transporter_contact1 = new ArrayList<>();
        arrayList_transporter_contact2 = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(dist_list_response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                arrayList_distributor_id.add(object.getString("dist_id"));
                arrayList_distributor_name.add(object.getString("dist_name"));
                arrayList_stock_point_name.add(object.getString("stock_distributor_name"));

                arrayList_transporter_name.add(object.getString("transportername"));
                arrayList_transporter_contact1.add(object.getString("transportercontact1"));
                arrayList_transporter_contact2.add(object.getString("transportercontact2"));
            }

            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.mytextview_12sp, arrayList_distributor_name);
            binding.spnDistName.setAdapter(arrayAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*=============================code for get more product ends===========================*/


    public class updatePurchaseOrderTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(AddPurchaseOrder.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(9) + "");
            pdialog.setCancelable(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "update_orders_url=>" + strings[0] + "");

            HttpHandler httpHandler = new HttpHandler();
            update_purchase_orders_response = httpHandler.makeServiceCall(strings[0] + "");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

                Log.i(TAG, "update_orders_res=>" + update_purchase_orders_response + "");
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

                if (update_purchase_orders_response.contains("updated Successfully")) {

                    showDialog(commanSuchnaList.getArrayList().get(1) + "");

                } else {

                    Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    public class deleteOrderTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(AddPurchaseOrder.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(9) + "");
            pdialog.setCancelable(false);
            pdialog.show();


        }

        @Override
        protected Void doInBackground(String... strings) {


            Log.i(TAG, "order_product_url=>" + strings[0] + "");

            HttpHandler httpHandler = new HttpHandler();
            delete_order_response = httpHandler.makeServiceCall(strings[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "order_product_res=>" + delete_order_response + "");

                if (delete_order_response.contains("deleted Successfully")) {

                    showDialog(commanSuchnaList.getArrayList().get(10) + "");

                } else {

                    Toast.makeText(AddPurchaseOrder.this, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

                }
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    public void setLanguage(String key) {

        Language language = new Language(key, AddPurchaseOrder.this, setLang(key));
        Language.CommanList commanList = language.getData();
        if (setLang(key).size() > 0) {
            if (commanList.getArrayList() != null && commanList.getArrayList().size() > 0) {
                //Language.ShopLang lang = language.getAddShopData();
                binding.tvScreenHeadingApo.setText("" + commanList.getArrayList().get(0));
                binding.tvDistributorNameTitleApo.setText("" + commanList.getArrayList().get(1));
                binding.tvDefaultStockTitleApo.setText("" + commanList.getArrayList().get(2));
                binding.tvOrderdatetimeTitleApo.setText("" + commanList.getArrayList().get(3));
                binding.tvDeliverydatetimeTitleApo.setText("" + commanList.getArrayList().get(4));
                binding.tvOrdernoTitleApo.setText("" + commanList.getArrayList().get(5));
                binding.tvOpeningrsTitleApo.setText("" + commanList.getArrayList().get(6));
                binding.tvPrimaryrsTitleApo.setText("" + commanList.getArrayList().get(7));
                binding.tvLastmonthpendingprimaryTitleApo.setText("" + commanList.getArrayList().get(8));
                binding.tvSecondaryrsTitleApo.setText("" + commanList.getArrayList().get(9));
                binding.tvClosingrsTitleApo.setText("" + commanList.getArrayList().get(10));
                binding.tvPrimaryrsrequiredrsTitleApo.setText("" + commanList.getArrayList().get(11));
                binding.tvTotalpaymentpendingrsTitleApo.setText("" + commanList.getArrayList().get(12));
                binding.tvPaymentduersTitleApo.setText("" + commanList.getArrayList().get(13));
                binding.tvStatusTitleApo.setText("" + commanList.getArrayList().get(14));
                binding.btnSubmitAddPurchaseOrder.setText("" + commanList.getArrayList().get(15));
                binding.btnCancelPurchaseOrder.setText("" + commanList.getArrayList().get(16));
                binding.tvOrderrsTitleApo.setText("" + commanList.getArrayList().get(17));
                binding.tvThismonthtargetTitleApo.setText("" + commanList.getArrayList().get(18));
                binding.btnAddMoreProd.setText("" + commanList.getArrayList().get(20));
                binding.btnSubmitAddMoreProd.setText("" + commanList.getArrayList().get(21));

            }
        }
    }

    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "4");

        if (cur.getCount() > 0) {

            if (cur.moveToFirst()) {

                do {
                    if (key.equalsIgnoreCase("ENG"))
                        arrayList_lang_desc.add(cur.getString(3));
                    else if (key.equalsIgnoreCase("GUJ"))
                        arrayList_lang_desc.add(cur.getString(4));
                    else if (key.equalsIgnoreCase("HINDI"))
                        arrayList_lang_desc.add(cur.getString(5));

                } while (cur.moveToNext());

            }
        }
        cur.close();
        db.close();

        return arrayList_lang_desc;

    }

    public ArrayList<String> setLangEntity(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "21");

        if (cur.getCount() > 0) {

            if (cur.moveToFirst()) {

                do {
                    if (key.equalsIgnoreCase("ENG"))
                        arrayList_lang_desc.add(cur.getString(3));
                    else if (key.equalsIgnoreCase("GUJ"))
                        arrayList_lang_desc.add(cur.getString(4));
                    else if (key.equalsIgnoreCase("HINDI"))
                        arrayList_lang_desc.add(cur.getString(5));

                } while (cur.moveToNext());

            }
        }
        cur.close();
        db.close();

        return arrayList_lang_desc;

    }

    public ArrayList<String> setLangSuchna(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewMultiLangSuchna("4");

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

                builder = new AlertDialog.Builder(AddPurchaseOrder.this);
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