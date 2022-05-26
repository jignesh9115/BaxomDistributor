package com.jp.baxomdistributor.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.jp.baxomdistributor.Adapters.DeliveryOrderFailReasonAdapter;
import com.jp.baxomdistributor.Adapters.DeliveryOrderProdAdapter;
import com.jp.baxomdistributor.Adapters.DeliveryOrderSchemeAdapter;
import com.jp.baxomdistributor.Interfaces.DelFailSelectionListener;
import com.jp.baxomdistributor.Interfaces.DeliveryProdQtyListener;
import com.jp.baxomdistributor.Interfaces.DeliverySchemeQtyListener;
import com.jp.baxomdistributor.Models.DelFailReasonModel;
import com.jp.baxomdistributor.Models.DeliveryOrderProdModel;
import com.jp.baxomdistributor.Models.MatchShemePOJO;
import com.jp.baxomdistributor.Models.OrderProductListPOJO;
import com.jp.baxomdistributor.Models.SchemeProductPOJO;
import com.jp.baxomdistributor.Models.SchemesOrderPOJO;
import com.jp.baxomdistributor.Models.UpdateSalesOrderProductPOJO;
import com.jp.baxomdistributor.Models.ViewSchemesOrderPOJO;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.Api;
import com.jp.baxomdistributor.Utils.AppUtils;
import com.jp.baxomdistributor.Utils.FileUriUtils;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.ActivitySalesOrderDeliveryScreenBinding;
import com.jp.baxomdistributor.databinding.DialogViewLrSymbolBinding;
import com.thekhaeng.pushdownanim.PushDownAnim;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SalesOrderDeliveryScreen extends AppCompatActivity implements DeliveryProdQtyListener,
        DeliverySchemeQtyListener, DelFailSelectionListener {

    ActivitySalesOrderDeliveryScreenBinding binding;
    Retrofit retrofit;
    Api api;
    private final String TAG = getClass().getSimpleName();

    String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    ArrayList<UpdateSalesOrderProductPOJO> arrayList_update_order_product;
    ArrayList<DeliveryOrderProdModel> arrayList_order_prods;
    DeliveryOrderProdModel deliveryOrderProdModel;

    ArrayList<ViewSchemesOrderPOJO> arrayList_order_schemes;
    ViewSchemesOrderPOJO deliveryOrderSchemeModel;
    ArrayList<Double> arrayList_minvalue;

    DeliveryOrderProdAdapter deliveryOrderProdAdapter;
    DeliveryOrderSchemeAdapter deliveryOrderSchemeAdapter;

    ArrayList<MatchShemePOJO> arrayList_match_scheme_list, arrayList_total_scheme_order_list;
    MatchShemePOJO matchShemePOJO;
    ArrayList<Double> arrayList_scheme_order_discount;
    ArrayList<SchemesOrderPOJO> arrayList_schemes_order_list;

    ArrayList<SchemeProductPOJO> arrayList_scheme_prod;
    SchemeProductPOJO schemeProductPOJO;

    DatePickerDialog dp;
    Calendar cal;
    GDateTime gDateTime = new GDateTime();
    int m, y, d;

    double purchase_rate_total_order_rs = 0, total_biz_rs = 0, total_discount = 0.0, total_scheme_amount = 0.0,
            delivery_order_rs = 0, latitude, longitude;

    boolean isSchemeCheck = false, GpsStatus;

    String path = "", image_name = "", del_fail_reason = "", shop_latitude, shop_longitude, shop_id, salesman_id,
            order_date, is_npc = "no";
    File imgfile;

    AlertDialog ad;
    AlertDialog.Builder builder;

    double cash_collection_rs, debit_value_rs, cheque_collection_rs, kasar_rs;

    SharedPreferences sp_distributor_detail, sp_update;
    String upload_url, update_sales_order_url = "", update_sales_order_response = "";

    DeliveryOrderFailReasonAdapter deliveryOrderFailReasonAdapter;
    ArrayList<DelFailReasonModel> arrayList_del_fail_reasons;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySalesOrderDeliveryScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sp_distributor_detail = getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);
        sp_update = getSharedPreferences("update_data", Context.MODE_PRIVATE);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.Base_URL))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        api = retrofit.create(Api.class);

        if (CheckGpsStatus()) {
            //Toast.makeText(AddShopActivity.this, "ON", Toast.LENGTH_SHORT).show();
            if (!AskPermissions(SalesOrderDeliveryScreen.this, permissionsRequired)) {
                ActivityCompat.requestPermissions(SalesOrderDeliveryScreen.this, permissionsRequired, 1);
            } else {
                getCurrentLocation();
            }
        } else {
            displayLocationSettingsRequest(SalesOrderDeliveryScreen.this);
        }

        view_sales_delivery_order(getIntent().getStringExtra("order_id"));

        binding.imgBack.setOnClickListener(view -> finish());

        binding.tvDebitDueDate.setText("" + gDateTime.getDatedmy());

        binding.imgDebitDueDateCalender.setOnClickListener(v -> {

            y = Integer.parseInt(gDateTime.getYear());
            m = Integer.parseInt(gDateTime.getMonth()) - 1;
            d = Integer.parseInt(gDateTime.getDay());

            dp = new DatePickerDialog(SalesOrderDeliveryScreen.this, (view, year, month, dayOfMonth) -> {

                cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                DateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                binding.tvDebitDueDate.setText("" + dff.format(cal.getTime()));

            }, y, m, d);
            dp.show();

        });

        binding.imgMobileNo.setOnClickListener(view -> {
            if (!binding.edtShopMobileNo.getText().toString().isEmpty()
                    && binding.edtShopMobileNo.getText().toString().length() == 10) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + binding.edtShopMobileNo.getText().toString()));
                startActivity(intent);
            } else
                Toast.makeText(SalesOrderDeliveryScreen.this, "Invalid Mobile No", Toast.LENGTH_SHORT).show();
        });

        binding.imgWhatsappNo.setOnClickListener(view -> {
            if (!binding.edtShopWhatsappNo.getText().toString().isEmpty()
                    && binding.edtShopWhatsappNo.getText().toString().length() == 10) {

                Intent In_Whats = new Intent(Intent.ACTION_VIEW);
                In_Whats.setData(Uri.parse("http://api.whatsapp.com/send?phone=+91"
                        + binding.edtShopWhatsappNo.getText().toString() + "&&text=Hi"));
                startActivity(In_Whats);

            } else
                Toast.makeText(SalesOrderDeliveryScreen.this, "Invalid WhatsApp No", Toast.LENGTH_SHORT).show();

        });

        binding.imgLocationShopAddress.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + 22.3039 + ">,<" + 70.8022 + ">?q=<" + 22.3039 + ">,<" + 70.8022 + ">(" + "rajkot" + ")"));
            startActivity(intent);
        });

        //delivery_fail_selection();

        cal_payment_receipt();

        binding.btnDelOrderSubmit.setOnClickListener(view -> {

            int fail_line = 0, fail_nline = 0;
            double fail_order_rs = 0.0, fail_biz_rs = 0.0, fail_purchase_rs = 0.0;

            JSONObject order_product_list = new JSONObject();// main object
            JSONArray prod_jArray = new JSONArray();// /ItemDetail jsonArray

            for (int i = 0; i < arrayList_update_order_product.size(); i++) {
                JSONObject jGroup = new JSONObject();// /sub Object

                try {

                    if (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) > 0) {

                        jGroup.put("p_id", arrayList_update_order_product.get(i).getP_id());
                        jGroup.put("p_qty", arrayList_update_order_product.get(i).getP_qty());
                        jGroup.put("p_qty_del", arrayList_update_order_product.get(i).getP_qty_del());
                        jGroup.put("p_price", arrayList_order_prods.get(i).getMrp_rs());
                        jGroup.put("p_purchase_price", arrayList_order_prods.get(i).getPtr_rs());
                        jGroup.put("p_biz_price", arrayList_order_prods.get(i).getProd_price());
                        prod_jArray.put(jGroup);

                        purchase_rate_total_order_rs = purchase_rate_total_order_rs +
                                (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) *
                                        Double.parseDouble(arrayList_order_prods.get(i).getPtr_rs()));

                        /*delivery_order_rs = delivery_order_rs +
                                (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) *
                                        Double.parseDouble(arrayList_order_prods.get(i).getProd_price()));*/
                    } else {
                        if (arrayList_order_prods.get(i).getIs_nline().equalsIgnoreCase("yes"))
                            fail_nline++;
                    }

                    if (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del())
                            < Double.parseDouble(arrayList_update_order_product.get(i).getP_qty())) {
                        fail_purchase_rs = fail_purchase_rs +
                                ((Double.parseDouble(arrayList_update_order_product.get(i).getP_qty())
                                        - Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del())) *
                                        Double.parseDouble(arrayList_order_prods.get(i).getPtr_rs()));
                    }

                    order_product_list.put("prod_list", prod_jArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            fail_line = arrayList_update_order_product.size() - prod_jArray.length();
            fail_biz_rs = total_biz_rs - Double.parseDouble(binding.edtDeliveryTpRs.getText().toString().trim());

            Log.i(TAG, "delivery_order_rs==>" + delivery_order_rs);
            Log.i(TAG, "delivery_value==>" + binding.edtDeliveryValueRs.getText().toString().trim());

            fail_order_rs = delivery_order_rs - Double.parseDouble(binding.edtDeliveryValueRs.getText().toString().trim());

            Log.i(TAG, "Deliver_prod_list==>" + prod_jArray);

            JSONArray match_scheme_array = new JSONArray();

            if (arrayList_schemes_order_list != null)
                for (int i = 0; i < arrayList_schemes_order_list.size(); i++) {

                    JSONObject jGroup = new JSONObject();// /sub Object

                    try {
                        if (isSchemeCheck == true) {

                            if (Double.parseDouble(arrayList_schemes_order_list.get(i).getScheme_qty()) > 0) {

                                jGroup.put("scheme_id", arrayList_schemes_order_list.get(i).getScheme_id());
                                jGroup.put("scheme_name", arrayList_schemes_order_list.get(i).getScheme_name());
                                jGroup.put("scheme_long_name", arrayList_schemes_order_list.get(i).getScheme_long_name());
                                jGroup.put("scheme_type_id", arrayList_schemes_order_list.get(i).getScheme_type_id());
                                jGroup.put("scheme_type_name", arrayList_schemes_order_list.get(i).getScheme_type_name());
                                jGroup.put("scheme_image", arrayList_schemes_order_list.get(i).getScheme_image().replace("http://projects.drbbagpks.org/Baxom//images/scheme_images/", ""));
                                jGroup.put("scheme_price", arrayList_schemes_order_list.get(i).getScheme_price());
                                jGroup.put("scheme_value", arrayList_schemes_order_list.get(i).getScheme_value());
                                jGroup.put("scheme_qty", arrayList_schemes_order_list.get(i).getScheme_qty());
                                jGroup.put("scheme_qty_del", arrayList_schemes_order_list.get(i).getScheme_qty_del());
                                jGroup.put("result_product_id", arrayList_schemes_order_list.get(i).getResult_product_id());
                                jGroup.put("result_product_qty", arrayList_schemes_order_list.get(i).getResult_product_qty());
                                jGroup.put("result_product_price", arrayList_schemes_order_list.get(i).getResult_product_price());
                                jGroup.put("result_product_total_price", arrayList_schemes_order_list.get(i).getResult_product_total_price());
                                jGroup.put("result_product_image", arrayList_schemes_order_list.get(i).getResult_product_image().replace("http://projects.drbbagpks.org/Baxom//images/product/", ""));
                                jGroup.put("is_scheme_half", arrayList_schemes_order_list.get(i).getIs_scheme_half());

                                match_scheme_array.put(jGroup);
                            }

                        } else {

                            if (Double.parseDouble(arrayList_schemes_order_list.get(i).getScheme_value()) > 0) {

                                jGroup.put("scheme_id", arrayList_schemes_order_list.get(i).getScheme_id());
                                jGroup.put("scheme_name", arrayList_schemes_order_list.get(i).getScheme_name());
                                jGroup.put("scheme_long_name", arrayList_schemes_order_list.get(i).getScheme_long_name());
                                jGroup.put("scheme_type_id", arrayList_schemes_order_list.get(i).getScheme_type_id());
                                jGroup.put("scheme_type_name", arrayList_schemes_order_list.get(i).getScheme_type_name());
                                jGroup.put("scheme_image", arrayList_schemes_order_list.get(i).getScheme_image().replace("http://projects.drbbagpks.org/Baxom//images/scheme_images/", ""));
                                jGroup.put("scheme_price", arrayList_schemes_order_list.get(i).getScheme_price());
                                jGroup.put("scheme_value", arrayList_schemes_order_list.get(i).getScheme_value());
                                jGroup.put("scheme_qty", arrayList_schemes_order_list.get(i).getScheme_qty());
                                jGroup.put("scheme_qty_del", arrayList_schemes_order_list.get(i).getScheme_qty_del());
                                jGroup.put("result_product_id", arrayList_schemes_order_list.get(i).getResult_product_id());
                                jGroup.put("result_product_qty", arrayList_schemes_order_list.get(i).getResult_product_qty());
                                jGroup.put("result_product_price", arrayList_schemes_order_list.get(i).getResult_product_price());
                                jGroup.put("result_product_total_price", arrayList_schemes_order_list.get(i).getResult_product_total_price());
                                jGroup.put("result_product_image", arrayList_schemes_order_list.get(i).getResult_product_image().replace("http://projects.drbbagpks.org/Baxom//images/product/", ""));
                                jGroup.put("is_scheme_half", arrayList_schemes_order_list.get(i).getIs_scheme_half());

                                match_scheme_array.put(jGroup);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            Log.i(TAG, "Match_Scheme_list==>" + match_scheme_array);

            Location startPoint = new Location("locationA");
            startPoint.setLatitude(latitude);
            startPoint.setLongitude(longitude);

            Location endPoint = new Location("locationA");
            endPoint.setLatitude(Double.parseDouble(shop_latitude));
            endPoint.setLongitude(Double.parseDouble(shop_longitude));

            String order_distance;

            if (latitude != 0.0 && latitude != 0.0)
                order_distance = String.valueOf(startPoint.distanceTo(endPoint));
            else
                order_distance = "";

            Log.i(TAG, "fail_biz_rs..>" + fail_biz_rs);

            if (fail_biz_rs > 0.0
                    && del_fail_reason.equalsIgnoreCase("")) {

                Toast.makeText(SalesOrderDeliveryScreen.this, "please select Delivery Fail Reason", Toast.LENGTH_SHORT).show();

            } else if (binding.edtCashCollectionRs.getText().toString().isEmpty()
                    && binding.edtDebiltValueRs.getText().toString().isEmpty()
                    && binding.edtChequeCollectionRs.getText().toString().isEmpty()) {

                Toast.makeText(SalesOrderDeliveryScreen.this, "please enter Payment Receipt Detail", Toast.LENGTH_SHORT).show();

            } else if (Double.parseDouble(binding.edtKasarRs.getText().toString().trim()) > 0) {

                Toast.makeText(SalesOrderDeliveryScreen.this, "Payment is less than delivery Value", Toast.LENGTH_SHORT).show();

            } else {

                JSONArray order_jArray = new JSONArray();// /ItemDetail jsonArray
                JSONObject jGroup = new JSONObject();// /sub Object

                try {

                    jGroup.put("order_id", binding.edtOrderNo.getText().toString().trim());
                    jGroup.put("shop_id", shop_id);
                    jGroup.put("salesman_id", salesman_id);
                    jGroup.put("order_date", order_date);
                    jGroup.put("submited_id", sp_distributor_detail.getString("distributor_id", ""));
                    jGroup.put("submited_by", "distributor");
                    jGroup.put("deliveryman", sp_distributor_detail.getString("name", ""));

                    jGroup.put("tot_biz_rs", total_biz_rs);
                    jGroup.put("tot_delivery_biz_rs", binding.edtDeliveryTpRs.getText().toString().trim());
                    jGroup.put("tot_order_rs", delivery_order_rs);
                    jGroup.put("tot_delivery_rs", binding.edtDeliveryValueRs.getText().toString().trim());
                    jGroup.put("tot_del_purchase_rs", purchase_rate_total_order_rs);

                    jGroup.put("tot_scheme_order_value", binding.tvTotalSchemeValue.getText().toString().trim().replace("Rs. ", "").replace(" /-", ""));
                    jGroup.put("tot_discount", binding.tvTotalDiscountValue.getText().toString().trim().replace("Rs. ", "").replace(" /-", ""));
                    jGroup.put("del_fail_reason", del_fail_reason);
                    jGroup.put("shop_close_img", path);

                    jGroup.put("cash_collection_rs", binding.edtCashCollectionRs.getText().toString().trim());
                    jGroup.put("debit_value_rs", binding.edtDebiltValueRs.getText().toString().trim());
                    jGroup.put("cheque_collection_rs", binding.edtChequeCollectionRs.getText().toString().trim());

                    if (binding.edtDebiltValueRs.getText().toString().trim().isEmpty())
                        jGroup.put("debit_due_date", "");
                    else
                        jGroup.put("debit_due_date", binding.tvDebitDueDate.getText().toString().trim());

                    jGroup.put("kasar_rs", binding.edtKasarRs.getText().toString().trim());
                    jGroup.put("order_distance", order_distance);
                    jGroup.put("fail_line", fail_line);
                    jGroup.put("fail_nline", fail_nline);
                    jGroup.put("fail_biz_rs", fail_biz_rs);
                    jGroup.put("fail_order_rs", fail_order_rs);
                    jGroup.put("fail_purchase_rs", fail_purchase_rs);
                    jGroup.put("prod_list", prod_jArray);
                    jGroup.put("scheme_list", match_scheme_array);

                    order_jArray.put(jGroup);

                } catch (Exception e) {
                    e.getMessage();
                }
                Log.i(TAG, "order_jArray==>" + order_jArray);

                salesOrderDelivery(order_jArray + "");

                if (!path.isEmpty())
                    new salesOrderDeliveryTask().execute(binding.edtOrderNo.getText().toString().trim());

                SharedPreferences.Editor editor = sp_update.edit();
                editor.putBoolean("isUpdate", true);
                editor.apply();

                Log.i(TAG, "isUpdate=>" + sp_update.getBoolean("isUpdate", false));
            }

        });

    }

    private void cal_payment_receipt() {

        binding.edtCashCollectionRs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cal_kasar_rs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.edtDebiltValueRs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cal_kasar_rs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.edtChequeCollectionRs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cal_kasar_rs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void cal_kasar_rs() {

        if (binding.edtCashCollectionRs.getText().toString().isEmpty())
            cash_collection_rs = 0;
        else
            cash_collection_rs = Double.parseDouble(binding.edtCashCollectionRs.getText().toString().trim());

        if (binding.edtDebiltValueRs.getText().toString().isEmpty())
            debit_value_rs = 0;
        else
            debit_value_rs = Double.parseDouble(binding.edtDebiltValueRs.getText().toString().trim());

        if (binding.edtChequeCollectionRs.getText().toString().isEmpty())
            cheque_collection_rs = 0;
        else
            cheque_collection_rs = Double.parseDouble(binding.edtChequeCollectionRs.getText().toString().trim());

        kasar_rs = Double.parseDouble(binding.edtDeliveryValueRs.getText().toString().trim()) - cash_collection_rs - debit_value_rs - cheque_collection_rs;
        binding.edtKasarRs.setText("" + kasar_rs);
    }

    /*private void delivery_fail_selection() {

        PushDownAnim.setPushDownAnimTo(binding.cardShopCloseSelection)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> {

                    binding.llSelectionShopClose.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_selected));
                    binding.llSelectionWantDebit.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionForcefullyOrder.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionShopClosePhoto.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNoorderGiven.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNotRequiredNow.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));

                    binding.tvShopClose.setTextColor(Color.WHITE);
                    binding.tvWantDebit.setTextColor(Color.BLACK);
                    binding.tvForcefullyOrder.setTextColor(Color.BLACK);
                    binding.tvPhotoOfClosedShop.setTextColor(getResources().getColor(R.color.colorDivider));
                    binding.tvNoOrderGiven.setTextColor(Color.BLACK);
                    binding.tvNotRequiredNow.setTextColor(Color.BLACK);

                    binding.llPhotoOfShopclose.setVisibility(View.GONE);
                    binding.imgPhotoofCloseshop.setImageURI(null);

                    del_fail_reason = "Shop Close";
                    path = "";

                    binding.tvPhotoOfClosedShop.setText("Photo of Closed Shop +");
                    binding.tvPhotoOfClosedShop.setTextSize(14);
                });

        PushDownAnim.setPushDownAnimTo(binding.cardWantDebitSelection)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> {

                    binding.llSelectionShopClose.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionWantDebit.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_selected));
                    binding.llSelectionForcefullyOrder.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionShopClosePhoto.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNoorderGiven.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNotRequiredNow.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));

                    binding.tvShopClose.setTextColor(Color.BLACK);
                    binding.tvWantDebit.setTextColor(Color.WHITE);
                    binding.tvForcefullyOrder.setTextColor(Color.BLACK);
                    binding.tvPhotoOfClosedShop.setTextColor(getResources().getColor(R.color.colorDivider));
                    binding.tvNoOrderGiven.setTextColor(Color.BLACK);
                    binding.tvNotRequiredNow.setTextColor(Color.BLACK);

                    binding.llPhotoOfShopclose.setVisibility(View.GONE);
                    binding.imgPhotoofCloseshop.setImageURI(null);

                    del_fail_reason = "Want Debit";
                    path = "";
                    binding.tvPhotoOfClosedShop.setText("Photo of Closed Shop +");
                    binding.tvPhotoOfClosedShop.setTextSize(14);
                });


        PushDownAnim.setPushDownAnimTo(binding.cardForcefullyOrderSelection)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> {

                    binding.llSelectionShopClose.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionWantDebit.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionForcefullyOrder.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_selected));
                    binding.llSelectionShopClosePhoto.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNoorderGiven.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNotRequiredNow.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));

                    binding.tvShopClose.setTextColor(Color.BLACK);
                    binding.tvWantDebit.setTextColor(Color.BLACK);
                    binding.tvForcefullyOrder.setTextColor(Color.WHITE);
                    binding.tvPhotoOfClosedShop.setTextColor(getResources().getColor(R.color.colorDivider));
                    binding.tvNoOrderGiven.setTextColor(Color.BLACK);
                    binding.tvNotRequiredNow.setTextColor(Color.BLACK);

                    binding.llPhotoOfShopclose.setVisibility(View.GONE);
                    binding.imgPhotoofCloseshop.setImageURI(null);

                    del_fail_reason = "Forcefully Order";
                    path = "";
                    binding.tvPhotoOfClosedShop.setText("Photo of Closed Shop +");
                    binding.tvPhotoOfClosedShop.setTextSize(14);

                });

        PushDownAnim.setPushDownAnimTo(binding.cardPhotoOfShopCloseSelection)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> {

                    binding.llSelectionShopClose.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionWantDebit.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionForcefullyOrder.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionShopClosePhoto.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_selected));
                    binding.llSelectionNoorderGiven.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNotRequiredNow.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));

                    binding.tvShopClose.setTextColor(Color.BLACK);
                    binding.tvWantDebit.setTextColor(Color.BLACK);
                    binding.tvForcefullyOrder.setTextColor(Color.BLACK);
                    binding.tvPhotoOfClosedShop.setTextColor(Color.WHITE);
                    binding.tvNoOrderGiven.setTextColor(Color.BLACK);
                    binding.tvNotRequiredNow.setTextColor(Color.BLACK);

                    del_fail_reason = "Shop Close";
                    path = "";
                    AppUtils.openCameraIntent(SalesOrderDeliveryScreen.this);

                    binding.tvPhotoOfClosedShop.setText("Photo of Closed Shop");
                    binding.tvPhotoOfClosedShop.setTextSize(10);

                });

        PushDownAnim.setPushDownAnimTo(binding.cardNoorderGivenSelection)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> {

                    binding.llSelectionShopClose.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionWantDebit.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionForcefullyOrder.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionShopClosePhoto.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNoorderGiven.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_selected));
                    binding.llSelectionNotRequiredNow.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));

                    binding.tvShopClose.setTextColor(Color.BLACK);
                    binding.tvWantDebit.setTextColor(Color.BLACK);
                    binding.tvForcefullyOrder.setTextColor(Color.BLACK);
                    binding.tvPhotoOfClosedShop.setTextColor(getResources().getColor(R.color.colorDivider));
                    binding.tvNoOrderGiven.setTextColor(Color.WHITE);
                    binding.tvNotRequiredNow.setTextColor(Color.BLACK);

                    binding.llPhotoOfShopclose.setVisibility(View.GONE);
                    binding.imgPhotoofCloseshop.setImageURI(null);

                    del_fail_reason = "No Order Given";
                    path = "";
                    binding.tvPhotoOfClosedShop.setText("Photo of Closed Shop +");
                    binding.tvPhotoOfClosedShop.setTextSize(14);

                });

        PushDownAnim.setPushDownAnimTo(binding.cardNotRequiredNowSelection)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> {

                    binding.llSelectionShopClose.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionWantDebit.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionForcefullyOrder.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionShopClosePhoto.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNoorderGiven.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_unselected));
                    binding.llSelectionNotRequiredNow.setBackground(getResources().getDrawable(R.drawable.bg_del_fail_selected));

                    binding.tvShopClose.setTextColor(Color.BLACK);
                    binding.tvWantDebit.setTextColor(Color.BLACK);
                    binding.tvForcefullyOrder.setTextColor(Color.BLACK);
                    binding.tvPhotoOfClosedShop.setTextColor(getResources().getColor(R.color.colorDivider));
                    binding.tvNoOrderGiven.setTextColor(Color.BLACK);
                    binding.tvNotRequiredNow.setTextColor(Color.WHITE);

                    binding.llPhotoOfShopclose.setVisibility(View.GONE);
                    binding.imgPhotoofCloseshop.setImageURI(null);

                    del_fail_reason = "Not Required Now";
                    path = "";
                    binding.tvPhotoOfClosedShop.setText("Photo of Closed Shop +");
                    binding.tvPhotoOfClosedShop.setTextSize(14);
                });


        binding.imgPhotoofCloseshop.setOnClickListener(v -> {
            DialogViewLrSymbolBinding binding1;
            builder = new AlertDialog.Builder(SalesOrderDeliveryScreen.this, R.style.AlertDialogTheme);
            binding1 = DialogViewLrSymbolBinding.inflate(getLayoutInflater());
            builder.setView(binding1.getRoot());

            Uri uri = Uri.fromFile(imgfile);
            binding1.imgShowShopPhoto.setImageURI(uri);

            binding1.imgCancelLrsymbol.setOnClickListener(v1 -> ad.dismiss());
            ad = builder.create();
            ad.show();

            Window window = ad.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.height = 1500;
            window.setAttributes(layoutParams);

        });

    }*/

    public void view_sales_delivery_order(String order_id) {

        Call<String> call = api.view_sales_delivery_order(order_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "delivery_order_req...>" + call.request());
                Log.i(TAG, "delivery_order_res...>" + response.body());

                if (response.body() != null && !response.body().isEmpty()) {

                    try {

                        double total_scheme_order_amount = 0.0;
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.length() > 0) {

                            binding.rvOrderProds.setVisibility(View.VISIBLE);
                            binding.imgEmptyOrderProds.setVisibility(View.GONE);

                            JSONObject object = jsonArray.getJSONObject(0);

                            if (object.getString("order_npc").equalsIgnoreCase("1"))
                                is_npc = "yes";
                            else
                                is_npc = "no";

                            shop_id = object.getString("shop_id");
                            salesman_id = object.getString("salesman_id");
                            order_date = object.getString("order_dmy");
                            salesman_id = object.getString("salesman_id");
                            binding.edtShopName.setText("" + object.getString("shop_name"));
                            binding.edtShopMobileNo.setText("" + object.getString("shop_mobile_no"));
                            binding.edtShopWhatsappNo.setText("" + object.getString("shop_whatsapp_no"));
                            binding.edtShopAddress.setText("" + object.getString("shop_address"));
                            shop_latitude = object.getString("shop_latitude");
                            shop_longitude = object.getString("shop_longitude");

                            binding.edtOrderNo.setText("" + object.getString("order_no"));
                            binding.edtSalesman.setText("" + object.getString("salesman"));
                            binding.edtOrderDateAndTime.setText("" + object.getString("order_date"));
                            binding.edtDeliveryDateAndTime.setText("" + object.getString("delivery_date"));

                            binding.edtOderValueRs.setText("" + object.getString("total_order_tp_rs"));
                            binding.edtDeliveryValueRs.setText("" + object.getString("total_order_tp_rs"));
                            binding.edtDeliveryTpRs.setText("" + object.getString("total_order_rs"));

                            total_discount = object.getDouble("total_discount");
                            total_biz_rs = object.getDouble("total_order_rs");
                            //delivery_order_rs = object.getDouble("total_order_rs");
                            delivery_order_rs = object.getDouble("total_order_tp_rs");

                            JSONArray order_detail_arr = object.getJSONArray("order_detail_arr");
                            if (order_detail_arr.length() > 0) {

                                binding.rvOrderProds.setVisibility(View.VISIBLE);
                                binding.imgEmptyOrderProds.setVisibility(View.GONE);

                                arrayList_order_prods = new ArrayList<>();
                                arrayList_update_order_product = new ArrayList<>();

                                for (int i = 0; i < order_detail_arr.length(); i++) {

                                    JSONObject order_detail_obj = order_detail_arr.getJSONObject(i);

                                    JSONArray min_data_array = order_detail_obj.getJSONArray("min_data");
                                    arrayList_minvalue = new ArrayList<>();
                                    for (int k = 0; k < min_data_array.length(); k++) {
                                        arrayList_minvalue.add(min_data_array.getDouble(k));
                                    }

                                    deliveryOrderProdModel = new DeliveryOrderProdModel(
                                            order_detail_obj.getString("prod_id"),
                                            order_detail_obj.getString("prod_name"),
                                            order_detail_obj.getString("photo"),
                                            order_detail_obj.getString("prod_unit"),
                                            order_detail_obj.getString("prod_qty"),
                                            order_detail_obj.getString("prod_qty_del"),
                                            order_detail_obj.getString("prod_price"),
                                            order_detail_obj.getString("prod_purchase_rate"),
                                            order_detail_obj.getString("prod_target_price"),
                                            order_detail_obj.getString("prod_mrp"),
                                            (Double.parseDouble(order_detail_obj.getString("prod_qty")) *
                                                    Double.parseDouble(order_detail_obj.getString("prod_price"))) + "",
                                            (Double.parseDouble(order_detail_obj.getString("prod_qty_del")) *
                                                    Double.parseDouble(order_detail_obj.getString("prod_price"))) + "",
                                            order_detail_obj.getString("is_nline"),
                                            arrayList_minvalue);

                                    arrayList_order_prods.add(deliveryOrderProdModel);

                                    arrayList_update_order_product.add(new UpdateSalesOrderProductPOJO(
                                            order_detail_obj.getString("prod_id"),
                                            order_detail_obj.getString("prod_qty"),
                                            order_detail_obj.getString("prod_qty_del"),
                                            order_detail_obj.getString("prod_mrp")));


                                }

                                deliveryOrderProdAdapter = new DeliveryOrderProdAdapter(arrayList_order_prods,
                                        SalesOrderDeliveryScreen.this,
                                        SalesOrderDeliveryScreen.this);
                                binding.rvOrderProds.setLayoutManager(new LinearLayoutManager(SalesOrderDeliveryScreen.this,
                                        LinearLayoutManager.VERTICAL,
                                        false));
                                binding.rvOrderProds.setAdapter(deliveryOrderProdAdapter);

                            } else {
                                binding.rvOrderProds.setVisibility(View.GONE);
                                binding.imgEmptyOrderProds.setVisibility(View.VISIBLE);
                            }

                            JSONArray scheme_detail_arr = object.getJSONArray("scheme_detail_arr");
                            if (scheme_detail_arr.length() > 0) {

                                binding.rvOrderSchemes.setVisibility(View.VISIBLE);
                                binding.imgEmptyOrderSchemes.setVisibility(View.GONE);

                                arrayList_order_schemes = new ArrayList<>();

                                arrayList_total_scheme_order_list = new ArrayList<>();
                                arrayList_scheme_order_discount = new ArrayList<>();
                                arrayList_schemes_order_list = new ArrayList<>();


                                for (int i = 0; i < scheme_detail_arr.length(); i++) {
                                    JSONObject scheme_detail_obj = scheme_detail_arr.getJSONObject(i);

                                    JSONArray scheme_prod_array = scheme_detail_obj.getJSONArray("scheme_prod");

                                    arrayList_scheme_prod = new ArrayList<>();

                                    for (int j = 0; j < scheme_prod_array.length(); j++) {

                                        JSONObject scheme_prod_object = scheme_prod_array.getJSONObject(j);

                                        schemeProductPOJO = new SchemeProductPOJO(scheme_prod_object.getString("product_id"),
                                                scheme_prod_object.getString("product_title"),
                                                scheme_prod_object.getString("product_qty"));
                                        arrayList_scheme_prod.add(schemeProductPOJO);
                                    }

                                    deliveryOrderSchemeModel = new ViewSchemesOrderPOJO(
                                            scheme_detail_obj.getString("scheme_id"),
                                            scheme_detail_obj.getString("scheme_name_sort"),
                                            scheme_detail_obj.getString("scheme_name_long"),
                                            scheme_detail_obj.getString("scheme_type_id"),
                                            scheme_detail_obj.getString("scheme_type_name"),
                                            scheme_detail_obj.getString("scheme_image"),
                                            scheme_detail_obj.getString("scheme_qty"),
                                            scheme_detail_obj.getString("scheme_qty_del"),
                                            scheme_detail_obj.getString("scheme_price"),
                                            scheme_detail_obj.getString("result_product_id"),
                                            scheme_detail_obj.getString("result_product_qty"),
                                            scheme_detail_obj.getString("result_product_price"),
                                            scheme_detail_obj.getString("total_result_prod_value"),
                                            scheme_detail_obj.getString("result_product_photo"),
                                            scheme_detail_obj.getString("is_half_scheme"),
                                            arrayList_scheme_prod);

                                    total_scheme_order_amount = total_scheme_order_amount +
                                            (scheme_detail_obj.getDouble("scheme_qty") *
                                                    scheme_detail_obj.getDouble("scheme_price"));

                                    arrayList_order_schemes.add(deliveryOrderSchemeModel);

                                    arrayList_schemes_order_list.add(new SchemesOrderPOJO(scheme_detail_obj.getString("scheme_id"),
                                            scheme_detail_obj.getString("scheme_name_sort"),
                                            scheme_detail_obj.getString("scheme_name_long"),
                                            scheme_detail_obj.getString("scheme_type_id"),
                                            scheme_detail_obj.getString("scheme_type_name"),
                                            scheme_detail_obj.getString("scheme_image"),
                                            scheme_detail_obj.getString("scheme_price"),
                                            (scheme_detail_obj.getDouble("scheme_qty")
                                                    * scheme_detail_obj.getDouble("scheme_price")) + "",
                                            scheme_detail_obj.getString("scheme_qty"),
                                            scheme_detail_obj.getString("scheme_qty"),
                                            scheme_detail_obj.getString("result_product_id"),
                                            scheme_detail_obj.getString("result_product_qty"),
                                            scheme_detail_obj.getString("result_product_price"),
                                            scheme_detail_obj.getString("total_result_prod_value"),
                                            scheme_detail_obj.getString("result_product_photo"),
                                            scheme_detail_obj.getString("is_half_scheme")));

                                    if (scheme_detail_obj.getString("scheme_qty_del").equalsIgnoreCase("0")) {

                                        arrayList_total_scheme_order_list.add(new MatchShemePOJO(
                                                scheme_detail_obj.getString("scheme_id"),
                                                scheme_detail_obj.getString("scheme_qty"),
                                                scheme_detail_obj.getString("scheme_price")));

                                        arrayList_scheme_order_discount.add(
                                                (Double.parseDouble(scheme_detail_obj.getString("total_result_prod_value"))
                                                        * (Double.parseDouble(scheme_detail_obj.getString("scheme_qty")))));
                                    } else {

                                        arrayList_total_scheme_order_list.add(new MatchShemePOJO(
                                                scheme_detail_obj.getString("scheme_id"),
                                                scheme_detail_obj.getString("scheme_qty_del"),
                                                scheme_detail_obj.getString("scheme_price")));

                                        arrayList_scheme_order_discount.add(
                                                (Double.parseDouble(scheme_detail_obj.getString("total_result_prod_value"))
                                                        * (Double.parseDouble(scheme_detail_obj.getString("scheme_qty_del")))));
                                    }
                                }

                                deliveryOrderSchemeAdapter = new DeliveryOrderSchemeAdapter(
                                        arrayList_order_schemes,
                                        arrayList_match_scheme_list,
                                        arrayList_total_scheme_order_list,
                                        SalesOrderDeliveryScreen.this,
                                        isSchemeCheck,
                                        Double.parseDouble(binding.edtDeliveryValueRs.getText().toString().trim()),
                                        total_biz_rs,
                                        Double.parseDouble(binding.tvTotalSchemeValue.getText().toString().trim().replace("Rs.", "").replace("/-", "")),
                                        SalesOrderDeliveryScreen.this);

                                binding.rvOrderSchemes.setLayoutManager(new LinearLayoutManager(SalesOrderDeliveryScreen.this,
                                        RecyclerView.VERTICAL, false));
                                binding.rvOrderSchemes.setAdapter(deliveryOrderSchemeAdapter);

                                binding.tvTotalSchemeValue.setText("Rs. " + total_scheme_order_amount + " /-");
                                binding.tvTotalDiscountValue.setText("Rs. " + total_discount + " /-");

                            } else {
                                binding.rvOrderSchemes.setVisibility(View.GONE);
                                binding.imgEmptyOrderSchemes.setVisibility(View.VISIBLE);
                            }

                            JSONArray del_fail_reason_arr = object.getJSONArray("del_fail_reason");
                            if (del_fail_reason_arr.length() > 0) {
                                arrayList_del_fail_reasons = new ArrayList<>();
                                arrayList_del_fail_reasons.add(
                                        new DelFailReasonModel(
                                                "Shop Close Photo",
                                                false, null));
                                for (int i = 0; i < del_fail_reason_arr.length(); i++) {
                                    arrayList_del_fail_reasons.add(
                                            new DelFailReasonModel(
                                                    del_fail_reason_arr.getJSONObject(i).getString("fail_resion"),
                                                    false, null));
                                }

                                deliveryOrderFailReasonAdapter = new DeliveryOrderFailReasonAdapter(
                                        arrayList_del_fail_reasons,
                                        SalesOrderDeliveryScreen.this,
                                        SalesOrderDeliveryScreen.this);
                                binding.rvFailReasons.setLayoutManager(new GridLayoutManager(
                                        SalesOrderDeliveryScreen.this, 2
                                ));
                                binding.rvFailReasons.setAdapter(deliveryOrderFailReasonAdapter);

                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.i(TAG, "delivery_order_error...>" + t);
            }
        });

    }

    public void checkScheme() {

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

                    arrayList_match_order_product_list.add(new OrderProductListPOJO(
                            arrayList_update_order_product.get(i).getP_id(),
                            arrayList_update_order_product.get(i).getP_qty_del(),
                            arrayList_update_order_product.get(i).getP_price(),
                            "", "", "", "",
                            "", "", "", ""));

                    jArray.put(jGroup);
                }

                order_product_list.put("prod_list", jArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (arrayList_match_order_product_list.size() > 0) {
            //binding.btnSubmitShopOrder.setEnabled(true);

            isSchemeCheck = true;

            arrayList_match_scheme_list = new ArrayList<>();
            arrayList_total_scheme_order_list = new ArrayList<>();
            arrayList_scheme_order_discount = new ArrayList<>();

            //arrayList_schemes_order_list = new ArrayList<>();

            double scheme_value = 0.0;

            for (int j = 0; j < arrayList_order_schemes.size(); j++) {

                //Log.i(TAG, "<=========scheme id========>" + arrayList_order_schemes.get(j).getScheme_id());

                scheme_value = 0.0;

                if (arrayList_order_schemes.get(j).getArrayList().size() > 0) {

                    for (int i = 0; i < jArray.length(); i++) {

                        for (int n = 0; n < arrayList_order_schemes.get(j).getArrayList().size(); n++) {

                            //Log.i(TAG, "order_product_id=>" + arrayList_match_order_product_list.get(i).getP_id());
                            //Log.i(TAG, "scheme_product_id=>" + arrayList_scheme_list.get(j).getArrayList().get(n).getProduct_id());

                            if (arrayList_match_order_product_list.get(i).getP_id()
                                    .equalsIgnoreCase(arrayList_order_schemes.get(j).getArrayList().get(n).getProduct_id())) {

                                //Log.i(TAG, "match product_id=>" + arrayList_match_order_product_list.get(i).getP_id());
                                //Log.i(TAG, "match product_qty=>" + arrayList_match_order_product_list.get(i).getP_qty());

                                scheme_value = scheme_value + Double.parseDouble(arrayList_match_order_product_list.get(i).getP_qty())
                                        * Double.parseDouble(arrayList_match_order_product_list.get(i).getP_price());

                                // Log.i(TAG, "match product_order_value=>" + scheme_value);

                            } else {

                                // Log.i(TAG, "inside else doen't match");

                            }
                        }
                    }
                }

                arrayList_match_scheme_list.add(new MatchShemePOJO(arrayList_order_schemes.get(j).getScheme_id(),
                        0 + "",
                        scheme_value + ""));

                arrayList_total_scheme_order_list.add(new MatchShemePOJO(arrayList_order_schemes.get(j).getScheme_id(),
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

            //Log.i(TAG, "Match_Scheme_list==>" + match_scheme_array);

            getTotalSchemeOrderAmount();

            deliveryOrderSchemeAdapter = new DeliveryOrderSchemeAdapter(
                    arrayList_order_schemes,
                    arrayList_match_scheme_list,
                    arrayList_total_scheme_order_list,
                    SalesOrderDeliveryScreen.this,
                    isSchemeCheck,
                    Double.parseDouble(binding.edtDeliveryValueRs.getText().toString().trim()),
                    total_biz_rs,
                    Double.parseDouble(binding.tvTotalSchemeValue.getText().toString().trim().replace("Rs.", "").replace("/-", "")),
                    SalesOrderDeliveryScreen.this);

            binding.rvOrderSchemes.setLayoutManager(new LinearLayoutManager(SalesOrderDeliveryScreen.this,
                    RecyclerView.VERTICAL, false));
            binding.rvOrderSchemes.setAdapter(deliveryOrderSchemeAdapter);

        } else {

            //Toast.makeText(SalesOrderDeliveryScreen.this, commanSuchnaList.getArrayList().get(7) + "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void qtyChange(int pos, double qty) {

        arrayList_update_order_product.set(pos, new UpdateSalesOrderProductPOJO(
                arrayList_order_prods.get(pos).getProd_id(),
                arrayList_order_prods.get(pos).getProd_qty(),
                qty + "",
                arrayList_order_prods.get(pos).getMrp_rs()));

        getTotalOrderRs();
        cal_kasar_rs();

        if (arrayList_order_schemes != null)
            checkScheme();
    }

    @Override
    public void schemeQtyScheme(int pos, double schemeValue, double schemeQty) {

        arrayList_total_scheme_order_list.set(pos, (new MatchShemePOJO(arrayList_order_schemes.get(pos).getScheme_id(),
                schemeQty + "",
                arrayList_order_schemes.get(pos).getScheme_price())));

        arrayList_scheme_order_discount.set(pos,
                (Double.parseDouble(arrayList_order_schemes.get(pos).getTotal_result_prod_value()) *
                        schemeQty));

        arrayList_schemes_order_list.set(pos, (new SchemesOrderPOJO(
                arrayList_order_schemes.get(pos).getScheme_id(),
                arrayList_order_schemes.get(pos).getScheme_name_sort(),
                arrayList_order_schemes.get(pos).getScheme_name_long(),
                arrayList_order_schemes.get(pos).getScheme_type_id(),
                arrayList_order_schemes.get(pos).getScheme_type_name(),
                arrayList_order_schemes.get(pos).getScheme_image(),
                arrayList_order_schemes.get(pos).getScheme_price(),
                schemeValue + "",
                schemeQty + "",
                schemeQty + "",
                arrayList_order_schemes.get(pos).getResult_product_id(),
                arrayList_order_schemes.get(pos).getResult_product_qty(),
                arrayList_order_schemes.get(pos).getResult_product_price(),
                arrayList_order_schemes.get(pos).getTotal_result_prod_value(),
                arrayList_order_schemes.get(pos).getResult_product_image(),
                arrayList_order_schemes.get(pos).getIs_scheme_half())));

        getTotalSchemeOrderAmount();
    }

    public void getTotalSchemeOrderAmount() {

        total_scheme_amount = 0.0;
        total_discount = 0.0;

        for (int i = 0; i < arrayList_total_scheme_order_list.size(); i++) {

            total_scheme_amount = total_scheme_amount + Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_qty()) * Double.parseDouble(arrayList_total_scheme_order_list.get(i).getScheme_price());
            binding.tvTotalSchemeValue.setText("" + String.format("%.2f", total_scheme_amount));

            total_discount = total_discount + arrayList_scheme_order_discount.get(i);
            binding.tvTotalDiscountValue.setText("" + String.format("%.2f", total_discount));
        }
    }

    public void getTotalOrderRs() {

        double total_rs = 0.0, total_biz_rs = 0.0;

        //Log.i(TAG, "arrayList_update_order_product=>" + arrayList_update_order_product.size());

        for (int i = 0; i < arrayList_update_order_product.size(); i++) {

            if (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del()) > 0) {

                total_rs = total_rs + (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del())
                        * Double.parseDouble(arrayList_update_order_product.get(i).getP_price()));

                total_biz_rs = total_biz_rs + (Double.parseDouble(arrayList_update_order_product.get(i).getP_qty_del())
                        * Double.parseDouble(arrayList_order_prods.get(i).getProd_price()));
                //binding.edtDeliveryValueRs.setText("" + total_rs);
                //Log.i(TAG, "total_rs=>" + total_rs);
            }
        }
        //delivery_order_rs = total_rs;
        binding.edtDeliveryValueRs.setText("" + total_rs);
        binding.edtDeliveryTpRs.setText("" + total_biz_rs);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //Toast.makeText(this, commanSuchnaList.getArrayList().get(14) + "", Toast.LENGTH_SHORT).show();

            imgfile = new File(AppUtils.imageFilePath);
            if (imgfile.exists()) {
                Uri uri = Uri.fromFile(imgfile);
                //binding.llPhotoOfShopclose.setVisibility(View.VISIBLE);
                //binding.imgPhotoofCloseshop.setImageURI(uri);
                arrayList_del_fail_reasons.get(0).setUri(uri);
                deliveryOrderFailReasonAdapter.notifyDataSetChanged();
            }

            path = AppUtils.comrpess_50(AppUtils.imageFilePath, AppUtils.file);
            Log.i(TAG, "comrpess_50_path==>" + path);

        } else {

            displayLocationSettingsRequest(SalesOrderDeliveryScreen.this);

        }
    }


    //======================== starts logic get current location ==========================================================
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

    public boolean CheckGpsStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (GpsStatus == true) {
            return true;
        }

        return false;
    }

    private void getCurrentLocation() {

        //binding.progressBar.setVisibility(View.VISIBLE);
        //binding.tvLocationStatus.setVisibility(View.VISIBLE);

        //binding.btnSubmitShopOrder.setVisibility(View.GONE);

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(300);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.getFusedLocationProviderClient(SalesOrderDeliveryScreen.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(SalesOrderDeliveryScreen.this)
                                .removeLocationUpdates(this);

                        // Log.i("locationResult=", locationResult.getLocations().size() + "");
                        //Log.i("locationRequest=", locationRequest + "");

                        if (locationRequest != null && locationResult.getLocations().size() > 0) {

                            int lastLocationIndex = locationResult.getLocations().size() - 1;

                            latitude =
                                    locationResult.getLocations().get(lastLocationIndex).getLatitude();

                            longitude =
                                    locationResult.getLocations().get(lastLocationIndex).getLongitude();

                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);

                        }

                    }
                }, Looper.getMainLooper());

    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SalesOrderDeliveryScreen.this, 157);
                        } catch (IntentSender.SendIntentException e) {
                            //Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //  Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    //=============================== ends logic get current location ============================================


    public void salesOrderDelivery(String order_data) {

        Call<String> call = api.salesorder_delivery(order_data);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "order_del_req...>" + call.request());
                Log.i(TAG, "order_del_res...>" + response.body());

                if (response.body() != null && response.body().contains("Sales Order Delivered Successfully"))
                    showSuccessDialog();
                else
                    Toast.makeText(SalesOrderDeliveryScreen.this, "something went wrong...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.i(TAG, "order_del_error...>" + t);

                Toast.makeText(SalesOrderDeliveryScreen.this, "Error : " + t, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void delFailselection(int pos) {
        del_fail_reason = arrayList_del_fail_reasons.get(pos).getReason();
        deliveryOrderFailReasonAdapter.notifyDataSetChanged();

        if (pos == 0) {
            del_fail_reason = "Shop Close Photo";
            path = "";
            AppUtils.openCameraIntent(SalesOrderDeliveryScreen.this);
        } else {
            arrayList_del_fail_reasons.get(0).setUri(null);
            path = "";
        }

    }

    public class salesOrderDeliveryTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.i(TAG, "File Path==>" + path);

            upload_url = getResources().getString(R.string.Base_URL) + "salesorder_delivery_close_shop_img.php";
            Log.i(TAG, "Image Upload Url==>" + upload_url);

        }

        @Override
        protected Void doInBackground(String... strings) {

            try {

                //new MultipartUploadRequest(AddShopActivity.this, image_name, upload_url)
                new MultipartUploadRequest(SalesOrderDeliveryScreen.this, upload_url)
                        .setMethod("POST")
                        .addFileToUpload(path, "uploadedfile")
                        .addParameter("order_id", strings[0])
                        .setMaxRetries(2)
                        .setDelegate(new UploadStatusDelegate() {
                            @Override
                            public void onProgress(Context context, UploadInfo uploadInfo) {

                                /*pdialog = new ProgressDialog(getActivity());
                                pdialog.setCancelable(false);
                                pdialog.setMessage(commanSuchnaList.getArrayList().get(3) + "");
                                pdialog.show();*/

                            }

                            @Override
                            public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                                /*if (pdialog.isShowing()) {
                                    pdialog.dismiss();
                                }
                                Toast.makeText(context, commanSuchnaList.getArrayList().get(14) + "", Toast.LENGTH_SHORT).show();*/

                                Log.i(TAG, "Error serverResponse=>" + serverResponse.getBody() + "");
                                Log.i(TAG, "Error serverResponse=>" + exception);
                                //Toast.makeText(context, "Error serverResponse=>" + serverResponse.getBodyAsString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                                //String obj = new Gson().fromJson(serverResponse.getBodyAsString(), AddShopActivity.class);

                                /*if (pdialog.isShowing()) {
                                    pdialog.dismiss();
                                }

                                if (serverResponse.getBodyAsString().contains("updated Successfully")) {
                                    showDialog();
                                } else {
                                    Toast.makeText(context, commanSuchnaList.getArrayList().get(14) + "", Toast.LENGTH_SHORT).show();
                                }*/

                                Log.i(TAG, "serverResponse=>" + serverResponse.getBodyAsString() + "");
                                //Toast.makeText(context, "serverResponse=>" + "\n" + serverResponse + "\nBodyAsString=" + serverResponse.getBodyAsString() + "\nBody=" + serverResponse.getBody() + "\nHttp Code=" + serverResponse.getHttpCode(), Toast.LENGTH_LONG).show();
                                //Toast.makeText(context, "\nserverResponse Body=>="+ serverResponse.getBodyAsString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {

                                /*if (pdialog.isShowing()) {
                                    pdialog.dismiss();
                                }*/

                                Log.i(TAG, "Cancelled=>" + uploadInfo + "");

                                //Toast.makeText(context, commanSuchnaList.getArrayList().get(14) + "", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(context, "Cancelled=>" + uploadInfo, Toast.LENGTH_LONG).show();

                            }
                        })
                        .startUpload();

            } catch (Exception e) {
                //Toast.makeText(AddNewClients.this, "Image Upload ERROR"+e, Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public void showSuccessDialog() {

        builder = new AlertDialog.Builder(SalesOrderDeliveryScreen.this);
        builder.setMessage("Sales Order Delivered Successfully...");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> finish());

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

}