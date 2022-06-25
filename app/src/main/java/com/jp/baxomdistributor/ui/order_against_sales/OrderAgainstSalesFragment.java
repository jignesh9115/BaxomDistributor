package com.jp.baxomdistributor.ui.order_against_sales;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.jp.baxomdistributor.Adapters.OrderAgainstSalesProdAdapter;
import com.jp.baxomdistributor.Adapters.OrderAgainstSalesSchemeAdapter;
import com.jp.baxomdistributor.BuildConfig;
import com.jp.baxomdistributor.Interfaces.OrderAgainstSalesProdQtyListener;
import com.jp.baxomdistributor.Interfaces.OrderAgainstSalesQtyListener;
import com.jp.baxomdistributor.Interfaces.OrderAgainstSalesSchemeQtyListener;
import com.jp.baxomdistributor.Models.OrderAgainstSalesAutoOrderModel;
import com.jp.baxomdistributor.Models.OrderAgainstSalesOrderModel;
import com.jp.baxomdistributor.Models.OrderAgainstSalesProdModel;
import com.jp.baxomdistributor.Models.OrderAgainstSalesQtyModel;
import com.jp.baxomdistributor.Models.SchemeProductPOJO;
import com.jp.baxomdistributor.Models.ViewSchemesOrderPOJO;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.Api;
import com.jp.baxomdistributor.Utils.ApiClient;
import com.jp.baxomdistributor.Utils.Currency;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.PdfUtils;
import com.jp.baxomdistributor.databinding.FragmentOrderAgainstSalesBinding;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAgainstSalesFragment extends Fragment implements OrderAgainstSalesProdQtyListener,
        OrderAgainstSalesQtyListener,
        OrderAgainstSalesSchemeQtyListener {

    private final String TAG = getClass().getSimpleName();
    Api api;
    SharedPreferences sp_distributor_detail, sp_login;
    ArrayList<OrderAgainstSalesProdModel> salesProdModels;
    ArrayList<Double> arrayList_minvalue;
    OrderAgainstSalesProdAdapter salesProdAdapter;
    ArrayList<ViewSchemesOrderPOJO> arrayList_order_schemes;
    ArrayList<OrderAgainstSalesOrderModel> arrayList_order_prod_sales, arrayList_order_prod_pdf;
    ArrayList<SchemeProductPOJO> arrayList_scheme_prod;
    OrderAgainstSalesSchemeAdapter salesSchemeAdapter;
    ProgressDialog pdialog;
    GDateTime gDateTime = new GDateTime();
    ArrayList<OrderAgainstSalesQtyModel> orderAgainstSalesQtyModels;
    ArrayList<Double> orderAgainstSchemeProdQtyModels, arrayList_auto_order_qty;
    ArrayList<OrderAgainstSalesAutoOrderModel> orderAgainstSalesAutoOrderModels;

    String is_add_scheme_qty_oas, is_add_replace_qty_oas, is_add_shortage_qty_oas, is_add_scheme_detail_oas, distributor_id, default_stock_point_id;

    ArrayList<String> ignore_prod_ids;
    private FragmentOrderAgainstSalesBinding binding;

    double required_order_pts = 0;

    String address_line_1, address_line_2, address_line_3, place_of_supply, last_quotation, default_prefix, terms_condition_1,
            terms_condition_2, terms_condition_3, terms_condition_4, terms_condition_5, stock_point_name, stock_point_address,
            gstin_no, stock_point_gstin_no;


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrderAgainstSalesViewModel orderAgainstSalesViewModel = new ViewModelProvider(this).get(OrderAgainstSalesViewModel.class);
        binding = FragmentOrderAgainstSalesBinding.inflate(inflater, container, false);

        sp_distributor_detail = requireActivity().getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);
        sp_login = requireActivity().getSharedPreferences("login_detail", Context.MODE_PRIVATE);
        api = ApiClient.getClient(requireActivity()).create(Api.class);

        is_add_scheme_qty_oas = sp_login.getString("is_add_scheme_qty_oas", "0");
        is_add_replace_qty_oas = sp_login.getString("is_add_replace_qty_oas", "0");
        is_add_shortage_qty_oas = sp_login.getString("is_add_shortage_qty_oas", "0");
        is_add_scheme_detail_oas = sp_login.getString("is_add_scheme_detail_oas", "0");

        Log.i(TAG, "is_add_scheme_qty_oas...>" + is_add_scheme_qty_oas);
        Log.i(TAG, "is_add_replace_qty_oas...>" + is_add_replace_qty_oas);
        Log.i(TAG, "is_add_shortage_qty_oas...>" + is_add_shortage_qty_oas);
        Log.i(TAG, "is_add_scheme_detail_oas...>" + is_add_scheme_detail_oas);

        load_order_against_sales(sp_distributor_detail.getString("distributor_id", ""));

        binding.edtOrderDateAndTime.setText(gDateTime.getDatedmy() + " " + gDateTime.getTime24());

        binding.imgMobileNo.setOnClickListener(view -> {
            if (!binding.edtDistMobileNo.getText().toString().isEmpty()
                    && binding.edtDistMobileNo.getText().toString().length() == 10) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + binding.edtDistMobileNo.getText().toString()));
                startActivity(intent);
            } else
                Toast.makeText(requireActivity(), "Invalid Mobile No", Toast.LENGTH_SHORT).show();
        });

        binding.imgWhatsappNo.setOnClickListener(view -> {
            if (!binding.edtShopWhatsappNo.getText().toString().isEmpty()
                    && binding.edtShopWhatsappNo.getText().toString().length() == 10) {

                Intent In_Whats = new Intent(Intent.ACTION_VIEW);
                In_Whats.setData(Uri.parse("http://api.whatsapp.com/send?phone=+91"
                        + binding.edtShopWhatsappNo.getText().toString() + "&&text=Hi"));
                startActivity(In_Whats);

            } else
                Toast.makeText(requireActivity(), "Invalid WhatsApp No", Toast.LENGTH_SHORT).show();

        });

        binding.imgContactNo1.setOnClickListener(view -> {
            if (!binding.edtContactNo1.getText().toString().isEmpty()
                    && binding.edtContactNo1.getText().toString().length() == 10) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + binding.edtContactNo1.getText().toString()));
                startActivity(intent);
            } else
                Toast.makeText(requireActivity(), "Invalid Mobile No", Toast.LENGTH_SHORT).show();
        });

        binding.imgContactNo2.setOnClickListener(view -> {
            if (!binding.edtContactNo2.getText().toString().isEmpty()
                    && binding.edtContactNo2.getText().toString().length() == 10) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + binding.edtContactNo2.getText().toString()));
                startActivity(intent);
            } else
                Toast.makeText(requireActivity(), "Invalid Mobile No", Toast.LENGTH_SHORT).show();
        });

        PushDownAnim.setPushDownAnimTo(binding.btnMakeOrder)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> {
                    if (!binding.edtRequiredOrderPtsAom.getText().toString().isEmpty()) {
                        required_order_pts = Double.parseDouble(binding.edtRequiredOrderPtsAom.getText().toString());
                        generateAutoOrder(required_order_pts);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            new Handler().postDelayed(() -> {
                                calordervalue();
                                salesProdAdapter.notifyDataSetChanged();
                            }, 2000, 2000);
                        }
                    } else
                        Toast.makeText(requireActivity(), "please enter Rs.(PTS)", Toast.LENGTH_SHORT).show();
                });

        PushDownAnim.setPushDownAnimTo(binding.btnSubmitAndShare)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> createPDF("view"));

        PushDownAnim.setPushDownAnimTo(binding.btnShareOnWhatsapp)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> createPDF("share"));

        PushDownAnim.setPushDownAnimTo(binding.btnSubmit)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(v -> {

                    JSONArray sales_prod_arr = new JSONArray();// /ItemDetail jsonArray
                    for (int i = 0; i < arrayList_order_prod_sales.size(); i++) {

                        JSONObject jGroup = new JSONObject();// /sub Object
                        try {

                            jGroup.put("prod_id", arrayList_order_prod_sales.get(i).getProd_id());
                            jGroup.put("prod_name", arrayList_order_prod_sales.get(i).getProd_name());
                            jGroup.put("prod_unit", arrayList_order_prod_sales.get(i).getProd_unit());
                            jGroup.put("prod_order_qty", arrayList_order_prod_sales.get(i).getProd_order_qty());
                            jGroup.put("prod_scheme_qty", orderAgainstSalesQtyModels.get(i).getScheme_qty());
                            jGroup.put("prod_replace_qty", orderAgainstSalesQtyModels.get(i).getReplace_qty());
                            jGroup.put("prod_shortage_qty", orderAgainstSalesQtyModels.get(i).getShortage_qty());
                            jGroup.put("prod_round_qty", orderAgainstSalesQtyModels.get(i).getRound_qty());
                            jGroup.put("prod_order_rs", (Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_order_qty())
                                    * Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_ptr_rs())) + "");
                            jGroup.put("prod_pts_rs", arrayList_order_prod_sales.get(i).getProd_ptr_rs());
                            jGroup.put("prod_biz_rs", arrayList_order_prod_sales.get(i).getProd_biz_rs());
                            jGroup.put("prod_day_sale", arrayList_order_prod_sales.get(i).getProd_day_sale());
                            jGroup.put("prod_stock", arrayList_order_prod_sales.get(i).getProd_stock());
                            sales_prod_arr.put(jGroup);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i(TAG, "sales_prod_arr....>" + sales_prod_arr);

                    JSONArray sales_scheme_arr = new JSONArray();// /ItemDetail jsonArray
                    for (int i = 0; i < arrayList_order_schemes.size(); i++) {

                        if (!arrayList_order_schemes.get(i).getScheme_qty().equalsIgnoreCase("0")) {
                            JSONObject jGroup = new JSONObject();// /sub Object
                            try {

                                jGroup.put("scheme_id", arrayList_order_schemes.get(i).getScheme_id());
                                jGroup.put("scheme_name_sort", arrayList_order_schemes.get(i).getScheme_name_sort());
                                jGroup.put("scheme_name_long", arrayList_order_schemes.get(i).getScheme_name_long());
                                jGroup.put("scheme_qty", arrayList_order_schemes.get(i).getScheme_qty());
                                jGroup.put("scheme_qty_del", arrayList_order_schemes.get(i).getScheme_qty_del());
                                jGroup.put("scheme_image", arrayList_order_schemes.get(i).getScheme_image());
                                jGroup.put("scheme_price", arrayList_order_schemes.get(i).getScheme_price());
                                jGroup.put("scheme_stock_qty", arrayList_order_schemes.get(i).getScheme_stock_qty());
                                jGroup.put("scheme_is_half", arrayList_order_schemes.get(i).getIs_scheme_half());
                                jGroup.put("scheme_type_id", arrayList_order_schemes.get(i).getScheme_type_id());
                                jGroup.put("scheme_type_name", arrayList_order_schemes.get(i).getScheme_type_name());
                                jGroup.put("result_prod_id", arrayList_order_schemes.get(i).getResult_product_id());
                                jGroup.put("result_prod_qty", arrayList_order_schemes.get(i).getResult_product_qty());
                                jGroup.put("result_prod_price", arrayList_order_schemes.get(i).getResult_product_price());
                                jGroup.put("result_prod_image", arrayList_order_schemes.get(i).getResult_product_image());
                                jGroup.put("result_prod_tot_value", arrayList_order_schemes.get(i).getTotal_result_prod_value());
                                sales_scheme_arr.put(jGroup);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.i(TAG, "sales_scheme_arr....>" + sales_scheme_arr);

                    JSONArray order_against_sales_arr = new JSONArray();// /ItemDetail jsonArray
                    JSONObject object = new JSONObject();
                    try {
                        object.put("party_id", distributor_id);
                        object.put("party_name", binding.edtPartyName.getText().toString());
                        object.put("mobile_no", binding.edtDistMobileNo.getText().toString());
                        object.put("whatsapp_no", binding.edtShopWhatsappNo.getText().toString());
                        object.put("address", binding.edtDistAddress.getText().toString());
                        object.put("transporter", binding.edtTransporterName.getText().toString());
                        object.put("transporter_contact1", binding.edtContactNo1.getText().toString());
                        object.put("transporter_contact2", binding.edtContactNo2.getText().toString());
                        object.put("default_stock_id", default_stock_point_id);
                        object.put("order_no", binding.edtOrderNo.getText().toString());
                        object.put("invoice_no", binding.edtInvoiceNo.getText().toString());
                        object.put("order_date_and_time", binding.edtOrderDateAndTime.getText().toString());
                        object.put("dispatch_date_and_time", binding.edtDispatchDateAndTime.getText().toString());
                        object.put("parti_buffer_rs_pts", binding.tvBufferRsPts.getText().toString());
                        object.put("parti_buffer_rs_biz", binding.tvBufferRsBiz.getText().toString());
                        object.put("parti_buffer_day_pts", binding.tvBufferInDayPts.getText().toString());
                        object.put("parti_buffer_day_biz", binding.tvBufferInDayBiz.getText().toString());
                        object.put("parti_sales_fromto", binding.tvSalesTitle.getText().toString().replace("Sales(", "").replace(")", ""));
                        object.put("parti_sales_pts", binding.tvSalesPts.getText().toString());
                        object.put("parti_sales_biz", binding.tvSalesBiz.getText().toString());
                        object.put("parti_last_pending_pts", binding.tvLastOrderPendingPts.getText().toString());
                        object.put("parti_last_pending_biz", binding.tvLastOrderPendingBiz.getText().toString());
                        object.put("parti_required_order_pts", binding.tvRequiredOrderPts.getText().toString());
                        object.put("parti_required_order_biz", binding.tvRequiredOrderBiz.getText().toString());
                        object.put("parti_curr_closing_pts", binding.tvCurrentClosingPts.getText().toString());
                        object.put("parti_curr_closing_biz", binding.tvCurrentClosingBiz.getText().toString());
                        object.put("parti_this_order_pts", binding.tvThisOrderPts.getText().toString());
                        object.put("parti_this_order_biz", binding.tvThisOrderBiz.getText().toString());
                        object.put("parti_this_pending_pts", binding.tvPendingThisOrderPts.getText().toString());
                        object.put("parti_this_pending_biz", binding.tvPendingThisOrderBiz.getText().toString());
                        object.put("tot_outstanding_rs", binding.tvOutstandingRs.getText().toString());
                        object.put("tot_outstanding_days", binding.tvOutstandingIndays.getText().toString());
                        object.put("tot_due_rs", binding.tvDueRs.getText().toString());
                        object.put("tot_due_days", binding.tvDueInDays.getText().toString());
                        object.put("paid_order_rs", binding.tvPaidOrderRs.getText().toString().replace("Rs. ", ""));
                        object.put("paid_order_per", binding.tvPaidOrderPer.getText().toString().replace(" %", ""));
                        object.put("scheme_rs", binding.tvSchemeDiscRs.getText().toString().replace("Rs. ", ""));
                        object.put("scheme_per", binding.tvSchemeDiscPer.getText().toString().replace(" %", ""));
                        object.put("replace_rs", binding.tvReplaceRs.getText().toString().replace("Rs. ", ""));
                        object.put("replace_per", binding.tvReplacePer.getText().toString().replace(" %", ""));
                        object.put("shortage_rs", binding.tvShortageRs.getText().toString().replace("Rs. ", ""));
                        object.put("shortage_per", binding.tvShortagePer.getText().toString().replace(" %", ""));
                        object.put("tot_disc_rs", binding.tvTotDiscRs.getText().toString().replace("Rs. ", ""));
                        object.put("tot_disc_per", binding.tvTotDiscPer.getText().toString().replace(" %", ""));
                        object.put("sales_prod_arr", sales_prod_arr);
                        object.put("sales_scheme_arr", sales_scheme_arr);
                        order_against_sales_arr.put(object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "order_against_sales_arr....>" + order_against_sales_arr);

                    store_sales_against_sales(order_against_sales_arr);

                });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void load_order_against_sales(String dist_id) {
        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage("Loading...");
        pdialog.setCancelable(false);
        pdialog.show();

        Call<String> call = api.load_order_against_sales(dist_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "load_order_against_sales_req...>" + call.request());
                Log.i(TAG, "load_order_against_sales_res...>" + response.body());

                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body());
                        JSONObject jsonObject = object.getJSONObject("data");


                        if (jsonObject.getJSONObject("dist_stock_value_obj").length() != 0) {

                            JSONObject dist_stock_value_obj = jsonObject.getJSONObject("dist_stock_value_obj");

                            binding.tvCurrentClosingPts.setText("" + (int) Double.parseDouble(dist_stock_value_obj.getString("stock_purchase_value")));
                            binding.tvCurrentClosingBiz.setText("" + (int) Double.parseDouble(dist_stock_value_obj.getString("stock_bizz_value")));
                            binding.tvSalesTitle.setText("Sales(" + dist_stock_value_obj.getString("from_date")
                                    + " to " + dist_stock_value_obj.getString("to_date") + ")");

                            binding.tvSalesPts.setText("" + (int) Double.parseDouble(dist_stock_value_obj.getString("sales_stock_purchase_value")));
                            binding.tvSalesBiz.setText("" + (int) Double.parseDouble(dist_stock_value_obj.getString("sales_stock_bizz_value")));

                            if (dist_stock_value_obj.getString("last_order_pending_purchase").equalsIgnoreCase(""))
                                required_order_pts = Double.parseDouble(dist_stock_value_obj.getString("sales_stock_purchase_value"));
                            else
                                required_order_pts = Double.parseDouble(dist_stock_value_obj.getString("sales_stock_purchase_value"))
                                        + Double.parseDouble(dist_stock_value_obj.getString("last_order_pending_purchase"));

                            binding.tvRequiredOrderPts.setText("" + (int) required_order_pts);
                            binding.edtRequiredOrderPtsAom.setText("" + (int) required_order_pts);

                            if (dist_stock_value_obj.getString("last_order_pending_biz").equalsIgnoreCase(""))
                                binding.tvRequiredOrderBiz.setText("" +
                                        (int) Double.parseDouble(dist_stock_value_obj.getString("sales_stock_bizz_value")));
                            else
                                binding.tvRequiredOrderBiz.setText("" +
                                        (int) (Double.parseDouble(dist_stock_value_obj.getString("sales_stock_bizz_value"))
                                                + Double.parseDouble(dist_stock_value_obj.getString("last_order_pending_biz"))));

                        }

                        JSONObject dist_obj = jsonObject.getJSONObject("dist_obj");
                        distributor_id = dist_obj.getString("distributor_id");
                        default_stock_point_id = dist_obj.getString("stock_distributor_id");
                        binding.edtPartyName.setText("" + dist_obj.getString("name"));
                        binding.edtDistMobileNo.setText("" + dist_obj.getString("mobile_no"));
                        binding.edtDistAddress.setText("" + dist_obj.getString("address"));
                        binding.edtTransporterName.setText("" + dist_obj.getString("transportername"));
                        binding.edtContactNo1.setText("" + dist_obj.getString("transportercontact1"));
                        binding.edtContactNo2.setText("" + dist_obj.getString("transportercontact2"));

                        binding.tvBufferRsPts.setText("" + dist_obj.getString("purchase_buffer_stock"));
                        binding.tvBufferRsBiz.setText("" + dist_obj.getString("bizz_buffer_stock"));


                        gstin_no = dist_obj.getString("gstin_no");
                        address_line_1 = dist_obj.getString("address_line_1");
                        address_line_2 = dist_obj.getString("address_line_2");
                        address_line_3 = dist_obj.getString("address_line_3");
                        place_of_supply = dist_obj.getString("place_of_supply");
                        last_quotation = dist_obj.getString("last_quotation");
                        default_prefix = dist_obj.getString("default_prefix");
                        terms_condition_1 = dist_obj.getString("terms_condition_1");
                        terms_condition_2 = dist_obj.getString("terms_condition_2");
                        terms_condition_3 = dist_obj.getString("terms_condition_3");
                        terms_condition_4 = dist_obj.getString("terms_condition_4");
                        terms_condition_5 = dist_obj.getString("terms_condition_5");
                        stock_point_name = dist_obj.getString("stock_point_name");
                        stock_point_address = dist_obj.getString("stock_point_address");
                        stock_point_gstin_no = dist_obj.getString("stock_point_gstin_no");


                        binding.tvBufferInDayPts.setText("" + jsonObject.getString("buffer_in_day_purchase"));
                        binding.tvBufferInDayBiz.setText("" + jsonObject.getString("buffer_in_day_biz"));

                        JSONArray dist_stock_order_prod_arr = jsonObject.getJSONArray("dist_stock_order_prod");
                        if (dist_stock_order_prod_arr.length() > 0) {

                            salesProdModels = new ArrayList<>();
                            arrayList_order_prod_sales = new ArrayList<>();
                            orderAgainstSalesQtyModels = new ArrayList<>();
                            orderAgainstSchemeProdQtyModels = new ArrayList<>();
                            orderAgainstSalesAutoOrderModels = new ArrayList<>();
                            arrayList_auto_order_qty = new ArrayList<>();

                            for (int i = 0; i < dist_stock_order_prod_arr.length(); i++) {


                                JSONObject obj = dist_stock_order_prod_arr.getJSONObject(i);

                                JSONArray min_data_array = obj.getJSONArray("min_data");
                                arrayList_minvalue = new ArrayList<>();
                                for (int k = 0; k < min_data_array.length(); k++) {
                                    arrayList_minvalue.add(min_data_array.getDouble(k));
                                }

                                salesProdModels.add(new OrderAgainstSalesProdModel(
                                        obj.getString("prod_id"),
                                        obj.getString("prod_name"),
                                        obj.getString("prod_unit"),
                                        obj.getString("prod_photo"),
                                        "0",
                                        obj.getString("stock_qty"),
                                        obj.getString("one_day_avg"),
                                        "0",
                                        obj.getString("purchase_rate"),
                                        obj.getString("sales_rate"),
                                        arrayList_minvalue));

                                arrayList_order_prod_sales.add(new OrderAgainstSalesOrderModel(
                                        obj.getString("prod_id"),
                                        obj.getString("prod_name"),
                                        obj.getString("prod_unit"),
                                        obj.getString("prod_gst"),
                                        obj.getString("prod_hsn_code"),
                                        obj.getString("purchase_rate"),
                                        obj.getString("sales_rate"),
                                        obj.getString("stock_qty"),
                                        obj.getString("one_day_avg"),
                                        "0"));

                                orderAgainstSalesQtyModels.add(new OrderAgainstSalesQtyModel(
                                        0,
                                        0,
                                        0,
                                        0));

                                orderAgainstSchemeProdQtyModels.add(0.0);
                                arrayList_auto_order_qty.add(0.0);

                                if (!obj.getString("stock_qty").equalsIgnoreCase("0"))
                                    orderAgainstSalesAutoOrderModels.add(new OrderAgainstSalesAutoOrderModel(
                                            obj.getString("prod_id"),
                                            obj.getString("prod_name"),
                                            Double.parseDouble(obj.getString("stock_qty")),
                                            Double.parseDouble(obj.getString("one_day_avg")),
                                            Double.parseDouble(obj.getString("purchase_rate")),
                                            (Double.parseDouble(obj.getString("stock_qty")) /
                                                    Double.parseDouble(obj.getString("one_day_avg"))),
                                            (Double.parseDouble(obj.getString("stock_qty")) *
                                                    Double.parseDouble(obj.getString("purchase_rate"))),
                                            (Double.parseDouble(obj.getString("one_day_avg")) *
                                                    Double.parseDouble(obj.getString("purchase_rate"))),
                                            0, 0));
                            }
                            salesProdAdapter = new OrderAgainstSalesProdAdapter(salesProdModels,
                                    orderAgainstSchemeProdQtyModels,
                                    requireActivity(),
                                    OrderAgainstSalesFragment.this,
                                    OrderAgainstSalesFragment.this,
                                    is_add_scheme_qty_oas,
                                    is_add_replace_qty_oas,
                                    is_add_shortage_qty_oas);

                            binding.rvOrderDetails.setAdapter(salesProdAdapter);
                            generateAutoOrder(required_order_pts);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                new Handler().postDelayed(() -> {
                                    calordervalue();
                                    salesProdAdapter.notifyDataSetChanged();
                                }, 2000, 2000);
                            }
                        }

                        JSONArray scheme_detail_arr = jsonObject.getJSONArray("dist_stock_order_scheme");
                        if (scheme_detail_arr.length() > 0) {


                            arrayList_order_schemes = new ArrayList<>();

                            for (int i = 0; i < scheme_detail_arr.length(); i++) {
                                JSONObject scheme_detail_obj = scheme_detail_arr.getJSONObject(i);

                                JSONArray scheme_prod_array = scheme_detail_obj.getJSONArray("scheme_prod");

                                arrayList_scheme_prod = new ArrayList<>();

                                for (int j = 0; j < scheme_prod_array.length(); j++) {

                                    JSONObject scheme_prod_object = scheme_prod_array.getJSONObject(j);

                                    arrayList_scheme_prod.add(new SchemeProductPOJO(scheme_prod_object.getString("product_id"),
                                            scheme_prod_object.getString("product_title"),
                                            scheme_prod_object.getString("product_qty")));
                                }

                                arrayList_order_schemes.add(new ViewSchemesOrderPOJO(
                                        scheme_detail_obj.getString("scheme_id"),
                                        scheme_detail_obj.getString("scheme_name_sort"),
                                        scheme_detail_obj.getString("scheme_name_long"),
                                        scheme_detail_obj.getString("scheme_type_id"),
                                        scheme_detail_obj.getString("scheme_type_name"),
                                        scheme_detail_obj.getString("scheme_image"),
                                        scheme_detail_obj.getString("scheme_qty"),
                                        scheme_detail_obj.getString("scheme_stock_qty"),
                                        scheme_detail_obj.getString("scheme_qty_del"),
                                        scheme_detail_obj.getString("scheme_price"),
                                        scheme_detail_obj.getString("result_product_id"),
                                        scheme_detail_obj.getString("result_product_qty"),
                                        scheme_detail_obj.getString("result_product_price"),
                                        scheme_detail_obj.getString("total_result_prod_value"),
                                        scheme_detail_obj.getString("result_product_photo"),
                                        scheme_detail_obj.getString("is_scheme_half"),
                                        arrayList_scheme_prod));

                            }

                            salesSchemeAdapter = new OrderAgainstSalesSchemeAdapter(
                                    arrayList_order_schemes,
                                    requireActivity(),
                                    OrderAgainstSalesFragment.this,
                                    is_add_scheme_detail_oas);

                            binding.rvSchemeDetails.setAdapter(salesSchemeAdapter);

                            //binding.tvTotalSchemeValue.setText("Rs. " + total_scheme_order_amount + " /-");
                            //binding.tvTotalDiscountValue.setText("Rs. " + total_discount + " /-");

                        }

                        if (pdialog.isShowing()) {
                            pdialog.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (pdialog.isShowing()) {
                        pdialog.dismiss();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
                Log.i(TAG, "load_order_against_sales_error...>" + t);
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void generateAutoOrder(double required_order_pts) {

        ignore_prod_ids = new ArrayList<>();
        calAutoOrder(required_order_pts);

        if (ignore_prod_ids.size() > 0)
            calAutoOrder(required_order_pts);

        for (int i = 0; i < orderAgainstSalesAutoOrderModels.size(); i++) {
            if (orderAgainstSalesAutoOrderModels.get(i).getOrder_qty() > 0) {

                for (int j = 0; j < salesProdModels.size(); j++) {
                    if (orderAgainstSalesAutoOrderModels.get(i).getProd_id()
                            .equalsIgnoreCase(salesProdModels.get(j).getProd_id())) {

                        salesProdModels.get(j).setOrder_qty("" + orderAgainstSalesAutoOrderModels.get(i).getOrder_qty());

                        Log.i(TAG, "Prod_id---->" + orderAgainstSalesAutoOrderModels.get(i).getProd_id());
                        Log.i(TAG, "Order_qty---->" + orderAgainstSalesAutoOrderModels.get(i).getOrder_qty());
                    }

                }
            }
        }
        salesProdAdapter.notifyDataSetChanged();
    }

    @SuppressLint("DefaultLocale")
    private void calAutoOrder(double required_order_pts) {

        double curr_stock_in_days = 0, curr_stock_pts_value = 0, aday_value_pts = 0,
                required_order_value_days = 0, curr_stock_value_days = 0,
                required_stock_value_in_days = 0;

        for (int i = 0; i < orderAgainstSalesAutoOrderModels.size(); i++) {

            boolean isConsider = true;

            for (int j = 0; j < ignore_prod_ids.size(); j++) {
                if (ignore_prod_ids.get(j).equalsIgnoreCase(orderAgainstSalesAutoOrderModels.get(i).getProd_id()))
                    isConsider = false;
            }

            if (isConsider) {

                Log.i(TAG, "getProd_id...>" + orderAgainstSalesAutoOrderModels.get(i).getProd_id());
                Log.i(TAG, "getCurr_stock_in_days...>" + orderAgainstSalesAutoOrderModels.get(i).getCurr_stock_in_days());
                Log.i(TAG, "getCurr_stock_value_pts...>" + orderAgainstSalesAutoOrderModels.get(i).getCurr_stock_value_pts());
                Log.i(TAG, "getAday_stock_value_pts...>" + orderAgainstSalesAutoOrderModels.get(i).getAday_stock_value_pts());

                curr_stock_in_days += orderAgainstSalesAutoOrderModels.get(i).getCurr_stock_in_days();
                curr_stock_pts_value += orderAgainstSalesAutoOrderModels.get(i).getCurr_stock_value_pts();
                aday_value_pts += orderAgainstSalesAutoOrderModels.get(i).getAday_stock_value_pts();
            }
        }

        Log.i(TAG, "<...........total............>");
        Log.i(TAG, "curr_stock_in_days...>" + curr_stock_in_days);
        Log.i(TAG, "curr_stock_pts_value...>" + curr_stock_pts_value);
        Log.i(TAG, "aday_value_pts...>" + aday_value_pts);

        required_order_value_days = required_order_pts / aday_value_pts;
        curr_stock_value_days = curr_stock_pts_value / aday_value_pts;
        required_stock_value_in_days = required_order_value_days + curr_stock_value_days;

        Log.i(TAG, "<...........required stock............>");
        Log.i(TAG, "required_order_value_days...>" + required_order_value_days);
        Log.i(TAG, "curr_stock_value_days...>" + curr_stock_value_days);
        Log.i(TAG, "required_stock_value_in_days...>" + required_stock_value_in_days);

        for (int i = 0; i < orderAgainstSalesAutoOrderModels.size(); i++) {
            boolean isConsider = true;

            for (int j = 0; j < ignore_prod_ids.size(); j++) {
                if (ignore_prod_ids.get(j).equalsIgnoreCase(orderAgainstSalesAutoOrderModels.get(i).getProd_id()))
                    isConsider = false;
            }

            if (isConsider) {


                Log.i(TAG, "2nd time cal getProd_id...>" + orderAgainstSalesAutoOrderModels.get(i).getProd_id());

                orderAgainstSalesAutoOrderModels.get(i).setRequired_tot_stock_value(
                        (required_stock_value_in_days *
                                orderAgainstSalesAutoOrderModels.get(i).getCurr_stock_value_pts()) /
                                orderAgainstSalesAutoOrderModels.get(i).getCurr_stock_in_days()
                );

                orderAgainstSalesAutoOrderModels.get(i).setOrder_qty(
                        Double.parseDouble(String.format("%.2f", (required_stock_value_in_days -
                                orderAgainstSalesAutoOrderModels.get(i).getCurr_stock_in_days()) *
                                orderAgainstSalesAutoOrderModels.get(i).getAday_sale()))
                );

            }

        }

        double required_total_stock_value = 0, order_qty = 0;
        for (int i = 0; i < orderAgainstSalesAutoOrderModels.size(); i++) {
            required_total_stock_value += orderAgainstSalesAutoOrderModels.get(i).getRequired_tot_stock_value();
            order_qty += orderAgainstSalesAutoOrderModels.get(i).getOrder_qty();
        }

        Log.i(TAG, "required_total_stock_value.....>" + required_total_stock_value);
        Log.i(TAG, "order_qty.....>" + order_qty);

        boolean isRecount = false;
        for (int i = 0; i < orderAgainstSalesAutoOrderModels.size(); i++) {
            if (orderAgainstSalesAutoOrderModels.get(i).getOrder_qty() < 0) {
                //Log.i(TAG, "Order_qty....>" + orderAgainstSalesAutoOrderModels.get(i).getOrder_qty());
                //Log.i(TAG, "Prod_id....>" + orderAgainstSalesAutoOrderModels.get(i).getProd_id());
                ignore_prod_ids.add(orderAgainstSalesAutoOrderModels.get(i).getProd_id());
                isRecount = true;
                //orderAgainstSalesAutoOrderModels.remove(i);
            }

        }

        Log.i(TAG, "orderAgainstSalesAutoOrderModels...>" + new Gson().toJson(orderAgainstSalesAutoOrderModels));

        Log.i(TAG, "isRecount...>" + isRecount);

    }

    @SuppressLint({"DefaultLocale", "SetTextI18n", "NotifyDataSetChanged"})
    public void getTotalOrderRs() {

        double total_pts_rs = 0.0, total_biz_rs = 0.0;

        //Log.i(TAG, "arrayList_update_order_product=>" + arrayList_update_order_product.size());

        for (int i = 0; i < arrayList_order_prod_sales.size(); i++) {

            if (Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_order_qty()) > 0) {

                total_pts_rs = total_pts_rs + (Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_order_qty())
                        * Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_ptr_rs()));

                total_biz_rs = total_biz_rs + (Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_order_qty())
                        * Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_biz_rs()));

            }
        }
        //delivery_order_rs = total_pts_rs;
        binding.tvThisOrderPtsTop.setText("" + (int) total_pts_rs);
        binding.tvThisOrderPts.setText("" + (int) total_pts_rs);
        binding.tvThisOrderBizTop.setText("" + (int) total_biz_rs);
        binding.tvThisOrderBiz.setText("" + (int) total_biz_rs);

        binding.tvPendingThisOrderPts.setText("" +
                (int) (Double.parseDouble(binding.tvRequiredOrderPts.getText().toString()) - total_pts_rs));

        binding.tvPendingThisOrderBiz.setText("" +
                (int) (Double.parseDouble(binding.tvRequiredOrderBiz.getText().toString())
                        - total_biz_rs));

        binding.tvPaidOrderRs.setText("Rs. " + (int) total_pts_rs);

        calpercentage();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void calpercentage() {

        double total_rs = Double.parseDouble(binding.tvPaidOrderRs.getText().toString().replace("Rs. ", "")) +
                Double.parseDouble(binding.tvTotDiscRs.getText().toString().replace("Rs. ", ""));

        double paid_order_per = (Double.parseDouble(binding.tvPaidOrderRs.getText().toString().replace("Rs. ", ""))
                * 100) / total_rs;

        binding.tvPaidOrderPer.setText(String.format("%.2f", paid_order_per) + " %");

    }

    @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
    public void calordervalue() {

        double total_pts_rs = Double.parseDouble(binding.tvThisOrderPts.getText().toString()),
                total_biz_rs = Double.parseDouble(binding.tvThisOrderBiz.getText().toString());

        double required_order_biz_rs = Double.parseDouble(String.format("%.2f", Double.parseDouble(binding.tvRequiredOrderBiz.getText().toString())));

        Log.i(TAG, "<________________total order rs_________________>");
        Log.i(TAG, "required_order_pts_rs___________>" + required_order_pts);
        Log.i(TAG, "total_pts_rs____________________>" + total_pts_rs);
        Log.i(TAG, "required_order_biz_rs___________>" + required_order_biz_rs);
        Log.i(TAG, "total_biz_rs____________________>" + total_biz_rs);

        double tot_diff = 0;
        String action = "";

        if (total_pts_rs > required_order_pts) {

            tot_diff = Double.parseDouble(String.format("%.2f", (total_pts_rs - required_order_pts)));
            Log.i(TAG, "inside if this order < required order....>" + String.format("%.2f", (total_pts_rs - required_order_pts)));

            action = "sub";

        } else if (total_pts_rs < required_order_pts) {

            tot_diff = Double.parseDouble(String.format("%.2f", (required_order_pts - total_pts_rs)));
            Log.i(TAG, "inside else if this order > required order....>" + String.format("%.2f", (required_order_pts - total_pts_rs)));

            action = "add";

        }

        Log.i(TAG, "tot_diff.....>" + tot_diff);

        int pos = 0;
        int diff_qty = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            OrderAgainstSalesOrderModel salesOrderModel = arrayList_order_prod_sales
                    .stream().max(Comparator.comparingInt(x -> (int) Double.parseDouble(x.getProd_order_qty())))
                    .get();

            Log.i(TAG, "MaxProd_order_Prod_id.....>" + salesOrderModel.getProd_id());
            Log.i(TAG, "MaxProd_order_Prod_name.....>" + salesOrderModel.getProd_name());
            Log.i(TAG, "MaxProd_order_Prod_ptr_rs.....>" + salesOrderModel.getProd_ptr_rs());
            Log.i(TAG, "MaxProd_order_Prod_order_qty.....>" + salesOrderModel.getProd_order_qty());

            pos = getposition(salesOrderModel.getProd_id());
        }

        diff_qty = (int) Double.parseDouble(String.format("%.2f", tot_diff
                / Double.parseDouble(arrayList_order_prod_sales.get(pos).getProd_ptr_rs())));

        Log.i(TAG, "diff_qty..............>" + diff_qty);

        if (action.equalsIgnoreCase("add")) {

            Log.i(TAG, ".............inside add qty.............");
            Log.i(TAG, "diff_qty + order_qty..........>" + (Double.parseDouble(salesProdModels.get(pos).getOrder_qty()) + diff_qty));

            salesProdModels.set(pos, new OrderAgainstSalesProdModel(
                    salesProdModels.get(pos).getProd_id(),
                    salesProdModels.get(pos).getProd_name(),
                    salesProdModels.get(pos).getProd_unit(),
                    salesProdModels.get(pos).getProd_photo(),
                    (Double.parseDouble(salesProdModels.get(pos).getOrder_qty()) + diff_qty) + "",
                    salesProdModels.get(pos).getStock_qty(),
                    salesProdModels.get(pos).getOne_day_avg(),
                    salesProdModels.get(pos).getScheme_qty(),
                    salesProdModels.get(pos).getPurchase_rate(),
                    salesProdModels.get(pos).getSales_rate(),
                    salesProdModels.get(pos).getArrayList_minvalue()));

            binding.rvOrderDetails.post((Runnable) () -> salesProdAdapter.notifyDataSetChanged());
        }

        if (action.equalsIgnoreCase("sub")) {

            Log.i(TAG, ".............inside sub qty.............");
            Log.i(TAG, "diff_qty - order_qty..........>" + (Double.parseDouble(salesProdModels.get(pos).getOrder_qty()) - diff_qty));

            salesProdModels.set(pos, new OrderAgainstSalesProdModel(
                    salesProdModels.get(pos).getProd_id(),
                    salesProdModels.get(pos).getProd_name(),
                    salesProdModels.get(pos).getProd_unit(),
                    salesProdModels.get(pos).getProd_photo(),
                    (Double.parseDouble(salesProdModels.get(pos).getOrder_qty()) - diff_qty) + "",
                    salesProdModels.get(pos).getStock_qty(),
                    salesProdModels.get(pos).getOne_day_avg(),
                    salesProdModels.get(pos).getScheme_qty(),
                    salesProdModels.get(pos).getPurchase_rate(),
                    salesProdModels.get(pos).getSales_rate(),
                    salesProdModels.get(pos).getArrayList_minvalue()));

            binding.rvOrderDetails.post((Runnable) () -> salesProdAdapter.notifyDataSetChanged());
        }

    }

    private int getposition(String prod_id) {

        for (int i = 0; i < arrayList_order_prod_sales.size(); i++) {
            if (prod_id.equalsIgnoreCase(arrayList_order_prod_sales.get(i).getProd_id()))
                return i;
        }
        return 0;
    }


    @Override
    public void qtyChange(int pos, double p_qty) {

        //Log.i(TAG, "......inside prod qty change......");

        arrayList_order_prod_sales.set(pos, new OrderAgainstSalesOrderModel(
                salesProdModels.get(pos).getProd_id(),
                salesProdModels.get(pos).getProd_name(),
                salesProdModels.get(pos).getProd_unit(),
                arrayList_order_prod_sales.get(pos).getProd_gst(),
                arrayList_order_prod_sales.get(pos).getProd_hsn_code(),
                salesProdModels.get(pos).getPurchase_rate(),
                salesProdModels.get(pos).getSales_rate(),
                salesProdModels.get(pos).getStock_qty(),
                salesProdModels.get(pos).getOne_day_avg(),
                p_qty + ""));

        getTotalOrderRs();

    }

    @Override
    public void qtyChange(int pos, double scheme_qty, double replace_qty, double shortage_qty, double round_qty) {

        orderAgainstSalesQtyModels.set(pos, new OrderAgainstSalesQtyModel(
                scheme_qty,
                replace_qty,
                shortage_qty,
                round_qty));

        calProdDiscount();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void calProdDiscount() {

        double prod_disc = 0, scheme_disc_rs = 0, replace_disc_rs = 0, shortage_disc_rs = 0;
        for (int i = 0; i < orderAgainstSalesQtyModels.size(); i++) {
            prod_disc += (orderAgainstSalesQtyModels.get(i).getScheme_qty()
                    + orderAgainstSalesQtyModels.get(i).getReplace_qty()
                    + orderAgainstSalesQtyModels.get(i).getShortage_qty())
                    * Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_ptr_rs());

            scheme_disc_rs += (orderAgainstSalesQtyModels.get(i).getScheme_qty())
                    * Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_ptr_rs());

            replace_disc_rs += (orderAgainstSalesQtyModels.get(i).getReplace_qty())
                    * Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_ptr_rs());

            shortage_disc_rs += (orderAgainstSalesQtyModels.get(i).getShortage_qty())
                    * Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_ptr_rs());
        }
        //Log.i(TAG, "prod_disc.....>" + prod_disc);
        binding.tvSchemeDiscRs.setText("Rs. " + String.format("%.2f", scheme_disc_rs));
        binding.tvReplaceRs.setText("Rs. " + String.format("%.2f", replace_disc_rs));
        binding.tvShortageRs.setText("Rs. " + String.format("%.2f", shortage_disc_rs));
        binding.tvTotDiscRs.setText("Rs. " + String.format("%.2f", prod_disc));


        double total_rs = Double.parseDouble(binding.tvPaidOrderRs.getText().toString().replace("Rs. ", "")) +
                Double.parseDouble(binding.tvTotDiscRs.getText().toString().replace("Rs. ", ""));

        double scheme_per = (Double.parseDouble(binding.tvSchemeDiscRs.getText().toString().replace("Rs. ", ""))
                * 100) / total_rs;

        binding.tvSchemeDiscPer.setText(String.format("%.2f", scheme_per) + " %");

        double replace_per = (Double.parseDouble(binding.tvReplaceRs.getText().toString().replace("Rs. ", ""))
                * 100) / total_rs;

        binding.tvReplacePer.setText(String.format("%.2f", replace_per) + " %");

        double shortage_per = (Double.parseDouble(binding.tvShortageRs.getText().toString().replace("Rs. ", ""))
                * 100) / total_rs;

        binding.tvShortagePer.setText(String.format("%.2f", shortage_per) + " %");

        double tot_disc_per = (Double.parseDouble(binding.tvTotDiscRs.getText().toString().replace("Rs. ", ""))
                * 100) / total_rs;

        binding.tvTotDiscPer.setText(String.format("%.2f", tot_disc_per) + " %");

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void qtyChange(int pos, double scheme_qty, String result_prod_id) {

        for (int i = 0; i < salesProdModels.size(); i++) {

            if (salesProdModels.get(i).getProd_id().equalsIgnoreCase(result_prod_id)) {
                orderAgainstSchemeProdQtyModels.set(i, scheme_qty);
            }

        }
        salesProdAdapter.notifyDataSetChanged();
    }


    public void store_sales_against_sales(JSONArray jsonArray) {

        pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage("Loading...");
        pdialog.setCancelable(false);
        pdialog.show();

        Call<String> call = api.store_sales_against_sales(jsonArray);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "store_sales_against_sales_req...>" + call.request());
                Log.i(TAG, "store_sales_against_sales_res...>" + response.body());

                if (response.body() != null && response.body().contains("Store Sales Against Order Successfully")) {
                    showDialog();
                    if (pdialog.isShowing()) {
                        pdialog.dismiss();
                    }
                } else {
                    if (pdialog.isShowing()) {
                        pdialog.dismiss();
                    }
                    Toast.makeText(requireActivity(), "something went wrong...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
                Log.i(TAG, "store_sales_against_sales_error...>" + t);
                Toast.makeText(requireActivity(), "Error" + t, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Store Sales Order Successfully...");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (dialog, which) -> {

        });

        AlertDialog ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    /*----------------------starts code for generate pdf of invoices-------------------*/
    File file;

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createPDF(String action) {

        PdfUtils.initializeDoc();
        arrayList_order_prod_pdf = new ArrayList<>();
        //arrayList_order_prod_pdf.addAll(arrayList_order_prod_sales);

        for (int i = 0; i < arrayList_order_prod_sales.size(); i++) {

            Log.i(TAG, "getProd_order_qty.....>" + arrayList_order_prod_sales.get(i).getProd_order_qty());

            if (!arrayList_order_prod_sales.get(i).getProd_order_qty().equals("0.0"))
                arrayList_order_prod_pdf.add(arrayList_order_prod_sales.get(i));
        }

        Log.i(TAG, "arrayList_order_prod_sales.....>" + arrayList_order_prod_sales.size());
        Log.i(TAG, "actual_order_tot_prod.....>" + arrayList_order_prod_pdf.size());
        double tot_dist_pages = ((double) (arrayList_order_prod_pdf.size() / 13) / 2);

        if (!String.valueOf(tot_dist_pages).split("\\.")[1].equals("0")) {
            tot_dist_pages = tot_dist_pages + 1;
        }
        Log.i(TAG, "tot_dist_pages.....>" + (int) tot_dist_pages);
        int start_dist = 0, end_dist = 2;
        for (int i = 0; i < (int) tot_dist_pages; i++) {

            Log.i(TAG, "start_dist.....>" + start_dist);
            Log.i(TAG, "end_dist.....>" + end_dist);

            createQuotationTable(arrayList_order_prod_pdf.size(), start_dist, end_dist);
            if (arrayList_order_prod_pdf.size() > end_dist) {
                start_dist += 2;
                end_dist += 2;
            }
        }

        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmm");
            file = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/OrderList-" + dateFormat.format(Calendar.getInstance().getTime()) + ".pdf");
            file.getParentFile().mkdirs();
            file.createNewFile();

            PdfUtils.getDocument().writeTo(new FileOutputStream(file));

            openGeneratedPDF(action);

        } catch (IOException e) {
            e.printStackTrace();
        }
        PdfUtils.closePage();

    }

    private void createQuotationTable(double tot_dist_pages, int start_dist, int end_dist) {
        PdfUtils.createPdfPage(0, 660, 975);

        for (int i = start_dist; i < end_dist; i++) {

            if (i < tot_dist_pages) {

                if (i == (start_dist)) {

                    createTopTable(i);

                } else if (i == (start_dist + 1)) {

                    createBottomTable(i);
                }

            }
        }

        PdfUtils.finishPage();

    }

    @SuppressLint({"DefaultLocale", "UseCompatLoadingForDrawables"})
    private void createTopTable(int j) {

        //create page
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
        PdfUtils.drawImage(requireActivity().getDrawable(R.drawable.bill_format_blank), 10, 10, 650, 467);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 25, "BOLD");
        PdfUtils.drawText(stock_point_name, 150, 52);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 15);
        PdfUtils.drawText(stock_point_address, 100, 75);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 15, "BOLD");
        PdfUtils.drawText("Debit Memo", 50, 96);
        PdfUtils.drawText("QUOTATION", 280, 96);
        PdfUtils.drawText("Original", 570, 96);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 12, "BOLD");
        PdfUtils.drawText(binding.edtPartyName.getText().toString(), 38, 116);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 12);
        PdfUtils.drawText(address_line_1, 38, 128);
        PdfUtils.drawText(address_line_2, 38, 140);
        PdfUtils.drawText(address_line_3, 38, 152);

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 11);
        PdfUtils.drawText("Party Contact No.:" + binding.edtDistMobileNo.getText().toString(), 235, 116);
        PdfUtils.drawText("Place of Supply :" + place_of_supply, 235, 128);
        PdfUtils.drawText("Party GSTIN No.:" + gstin_no, 235, 140);
        PdfUtils.drawText("Supplier GSTIN No.:" + stock_point_gstin_no, 235, 152);

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 15);
        PdfUtils.drawText("Quotation No.:" + default_prefix + "/" + last_quotation, 450, 122);
        PdfUtils.drawText("Date            :01/11/2022", 450, 147);

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 9);

        int page_y = 191, nm_y = 191, unit_y = 191, hsn_y = 191, batch_y = 191, mfg_dt_y = 191, exp_dt_y = 191,
                qty_y = 191, rate_y = 191, disc_y = 191, taxble_amount_y = 191, gst_y = 191, cgst_y = 191,
                sgst_y = 191, net_amount_y = 191;

        int prod_start_pos = 0, prod_end_pos = 0;

        if (j == 0) {
            prod_end_pos = 12;
        } else if (j == 1) {
            prod_start_pos = 13;
            prod_end_pos = 25;
        } else if (j == 2) {
            prod_start_pos = 26;
            prod_end_pos = 38;
        } else if (j == 3) {
            prod_start_pos = 39;
            prod_end_pos = 51;
        } else if (j == 4) {
            prod_start_pos = 39;
            prod_end_pos = 51;
        } else if (j == 5) {
            prod_start_pos = 52;
            prod_end_pos = 64;
        } else if (j == 6) {
            prod_start_pos = 65;
            prod_end_pos = 77;
        } else if (j == 7) {
            prod_start_pos = 78;
            prod_end_pos = 90;
        } else if (j == 8) {
            prod_start_pos = 91;
            prod_end_pos = 103;
        } else if (j == 9) {
            prod_start_pos = 104;
            prod_end_pos = 116;
        } else if (j == 10) {
            prod_start_pos = 117;
            prod_end_pos = 129;
        } else if (j == 11) {
            prod_start_pos = 130;
            prod_end_pos = 142;
        }

        Log.i(TAG, "...............inside top table...............>");
        Log.i(TAG, "prod_start_pos...>" + prod_start_pos);
        Log.i(TAG, "prod_end_pos...>" + prod_end_pos);

        double tot_taxble_amount = 0, tot_net_amount = 0, tot_cal_gst = 0;

        for (int i = prod_start_pos; i <= prod_end_pos; i++) {

            double rate, disc, taxble_amount, cal_total_gst, net_amount;

            if (i < arrayList_order_prod_pdf.size()) {

                if (i == 0 || i == 13 || i == 26 || i == 39) {

                    PdfUtils.drawText((i + 1) + "", 34, page_y);
                    if (arrayList_order_prod_pdf.get(i).getProd_name().length() > 15)
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_name().substring(0, 15) + "...", 54, nm_y);
                    else
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_name(), 54, nm_y);

                    if (arrayList_order_prod_pdf.get(i).getProd_unit().length() > 10)
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_unit().substring(0, 9) + "...", 139, unit_y);
                    else
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_unit(), 139, unit_y);
                    PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_hsn_code(), 193, hsn_y);
                    PdfUtils.drawText("", 227, batch_y);
                    PdfUtils.drawText("", 265, mfg_dt_y);
                    PdfUtils.drawText("", 302, exp_dt_y);
                    PdfUtils.drawText(String.format("%.1f", Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_order_qty())), 338, qty_y);

                    rate = (Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_ptr_rs()) /
                            Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_gst()
                                    .replace("%", "")));

                    PdfUtils.drawText(String.format("%.2f", rate), 370, rate_y);

                    disc = (orderAgainstSalesQtyModels.get(i).getScheme_qty()
                            + orderAgainstSalesQtyModels.get(i).getReplace_qty()
                            + orderAgainstSalesQtyModels.get(i).getShortage_qty()) * rate;

                    PdfUtils.drawText(String.format("%.2f", disc), 405, disc_y);

                    taxble_amount = (rate * Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_order_qty())) - disc;

                    if (String.valueOf(taxble_amount).equalsIgnoreCase("NAN"))
                        taxble_amount = 0;

                    cal_total_gst = taxble_amount * Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_gst()
                            .replace("%", "")) / 100;
                    tot_cal_gst += cal_total_gst;
                    tot_taxble_amount += taxble_amount;

                    PdfUtils.drawText(String.format("%.2f", taxble_amount), 440, taxble_amount_y);
                    PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_gst().trim(), 485, gst_y);
                    PdfUtils.drawText(String.format("%.2f", (cal_total_gst / 2)), 512, cgst_y);
                    PdfUtils.drawText(String.format("%.2f", (cal_total_gst / 2)), 550, sgst_y);

                    net_amount = taxble_amount + cal_total_gst;
                    tot_net_amount += net_amount;

                    PdfUtils.drawText(String.format("%.2f", net_amount) + "", 586, net_amount_y);
                } else {

                    PdfUtils.drawText((i + 1) + "", 34, page_y += 10);
                    if (arrayList_order_prod_pdf.get(i).getProd_name().length() > 15)
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_name().substring(0, 15) + "...", 54, nm_y += 10);
                    else
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_name(), 54, nm_y += 10);
                    if (arrayList_order_prod_pdf.get(i).getProd_unit().length() > 10)
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_unit().substring(0, 9) + "...", 139, unit_y += 10);
                    else
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_unit(), 139, unit_y += 10);
                    PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_hsn_code(), 193, hsn_y += 10);
                    PdfUtils.drawText("", 227, batch_y += 10);
                    PdfUtils.drawText("", 265, mfg_dt_y += 10);
                    PdfUtils.drawText("", 302, exp_dt_y += 10);
                    PdfUtils.drawText(String.format("%.1f", Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_order_qty())), 338, qty_y += 10);

                    rate = (Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_ptr_rs()) /
                            Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_gst()
                                    .replace("%", "")));

                    PdfUtils.drawText(String.format("%.2f", rate), 370, rate_y += 10);

                    disc = (orderAgainstSalesQtyModels.get(i).getScheme_qty()
                            + orderAgainstSalesQtyModels.get(i).getReplace_qty()
                            + orderAgainstSalesQtyModels.get(i).getShortage_qty()) * rate;

                    PdfUtils.drawText(String.format("%.2f", disc), 405, disc_y += 10);

                    taxble_amount = rate * Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_order_qty()) - disc;

                    if (String.valueOf(taxble_amount).equalsIgnoreCase("NAN"))
                        taxble_amount = 0;

                    cal_total_gst = taxble_amount * Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_gst()
                            .replace("%", "")) / 100;
                    tot_cal_gst += cal_total_gst;
                    tot_taxble_amount += taxble_amount;

                    PdfUtils.drawText(String.format("%.2f", taxble_amount), 440, taxble_amount_y += 10);
                    PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_gst().trim(), 485, gst_y += 10);
                    PdfUtils.drawText(String.format("%.2f", (cal_total_gst / 2)), 512, cgst_y += 10);
                    PdfUtils.drawText(String.format("%.2f", (cal_total_gst / 2)), 550, sgst_y += 10);

                    net_amount = taxble_amount + cal_total_gst;
                    tot_net_amount += net_amount;

                    PdfUtils.drawText(String.format("%.2f", net_amount) + "", 586, net_amount_y += 10);

                }
            }
        }

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 11);
        PdfUtils.drawText(String.format("%.2f", tot_taxble_amount), 140, 330);
        PdfUtils.drawText(String.format("%.2f", (tot_cal_gst / 2)) + "", 245, 330);
        PdfUtils.drawText(String.format("%.2f", (tot_cal_gst / 2)) + "", 330, 330);

        String[] net_amount_round = String.format("%.2f", tot_net_amount).split("\\.");

        PdfUtils.drawText("(-)0." +net_amount_round[1], 440, 330);
        PdfUtils.drawText(net_amount_round[0], 560, 330);

        PdfUtils.drawText(Currency.convertToIndianCurrency(String.valueOf(tot_taxble_amount)), 140, 349);
        PdfUtils.drawText(Currency.convertToIndianCurrency(net_amount_round[0]), 175, 366);

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 12);
        PdfUtils.drawText("Terms & Conditions : 1. " + terms_condition_1, 34, 386);
        PdfUtils.drawText("2. " + terms_condition_2, 34, 398);
        PdfUtils.drawText("3. " + terms_condition_3, 34, 410);
        PdfUtils.drawText("4. " + terms_condition_4, 34, 422);
        PdfUtils.drawText("5. " + terms_condition_5, 34, 436);

        PdfUtils.drawText("For " + stock_point_name, 430, 386);
    }


    @SuppressLint({"DefaultLocale", "UseCompatLoadingForDrawables"})
    private void createBottomTable(int j) {

        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
        PdfUtils.drawImage(requireActivity().getDrawable(R.drawable.bill_format_blank), 10, 487, 650, 955);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 25, "BOLD");
        PdfUtils.drawText(stock_point_name, 150, 529);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 15);
        PdfUtils.drawText(stock_point_address, 100, 555);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 15, "BOLD");
        PdfUtils.drawText("Debit Memo", 50, 576);
        PdfUtils.drawText("QUOTATION", 280, 576);
        PdfUtils.drawText("Original", 570, 576);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 12, "BOLD");
        PdfUtils.drawText(binding.edtPartyName.getText().toString(), 38, 596);
        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 12);
        PdfUtils.drawText(address_line_1, 38, 608);
        PdfUtils.drawText(address_line_2, 38, 620);
        PdfUtils.drawText(address_line_3, 38, 632);

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 11);
        PdfUtils.drawText("Party Contact No.:" + binding.edtDistMobileNo.getText().toString(), 235, 596);
        PdfUtils.drawText("Place of Supply :" + place_of_supply, 235, 608);
        PdfUtils.drawText("Party GSTIN No.:" + gstin_no, 235, 620);
        PdfUtils.drawText("Supplier GSTIN No.:" + stock_point_gstin_no, 235, 632);

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 15);
        PdfUtils.drawText("Quotation No.:" + default_prefix + "/" + last_quotation, 450, 602);
        PdfUtils.drawText("Date            :01/11/2022", 450, 627);

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 9);
        int page_y = 673, nm_y = 673, unit_y = 673, hsn_y = 673, batch_y = 673, mfg_dt_y = 673, exp_dt_y = 673,
                qty_y = 673, rate_y = 673, disc_y = 673, taxble_amount_y = 673, gst_y = 673, cgst_y = 673,
                sgst_y = 673, net_amount_y = 673;

        int prod_start_pos = 0, prod_end_pos = 0;

        if (j == 0) {
            prod_end_pos = 12;
        } else if (j == 1) {
            prod_start_pos = 13;
            prod_end_pos = 25;
        } else if (j == 2) {
            prod_start_pos = 26;
            prod_end_pos = 38;
        } else if (j == 3) {
            prod_start_pos = 39;
            prod_end_pos = 51;
        } else if (j == 4) {
            prod_start_pos = 39;
            prod_end_pos = 51;
        } else if (j == 5) {
            prod_start_pos = 52;
            prod_end_pos = 64;
        } else if (j == 6) {
            prod_start_pos = 65;
            prod_end_pos = 77;
        } else if (j == 7) {
            prod_start_pos = 78;
            prod_end_pos = 90;
        } else if (j == 8) {
            prod_start_pos = 91;
            prod_end_pos = 103;
        } else if (j == 9) {
            prod_start_pos = 104;
            prod_end_pos = 116;
        } else if (j == 10) {
            prod_start_pos = 117;
            prod_end_pos = 129;
        } else if (j == 11) {
            prod_start_pos = 130;
            prod_end_pos = 142;
        }

        Log.i(TAG, "...............inside bottom table...............>");
        Log.i(TAG, "prod_start_pos...>" + prod_start_pos);
        Log.i(TAG, "prod_end_pos...>" + prod_end_pos);


        double tot_taxble_amount = 0, tot_net_amount = 0, tot_cal_gst = 0;

        for (int i = prod_start_pos; i <= prod_end_pos; i++) {

            double rate, disc, taxble_amount, cal_total_gst, net_amount;

            if (i < arrayList_order_prod_pdf.size()) {

                if (i == 0 || i == 13 || i == 26 || i == 39) {

                    PdfUtils.drawText((i + 1) + "", 34, page_y);
                    if (arrayList_order_prod_pdf.get(i).getProd_name().length() > 15)
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_name().substring(0, 15) + "...", 54, nm_y);
                    else
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_name(), 54, nm_y);
                    if (arrayList_order_prod_pdf.get(i).getProd_unit().length() > 10)
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_unit().substring(0, 9) + "...", 139, unit_y);
                    else
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_unit(), 139, unit_y);
                    PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_hsn_code(), 193, hsn_y);
                    PdfUtils.drawText("", 227, batch_y);
                    PdfUtils.drawText("", 265, mfg_dt_y);
                    PdfUtils.drawText("", 302, exp_dt_y);
                    PdfUtils.drawText(String.format("%.1f", Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_order_qty())), 338, qty_y);

                    rate = (Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_ptr_rs()) /
                            Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_gst()
                                    .replace("%", "")));

                    PdfUtils.drawText(String.format("%.2f", rate), 370, rate_y);

                    disc = (orderAgainstSalesQtyModels.get(i).getScheme_qty()
                            + orderAgainstSalesQtyModels.get(i).getReplace_qty()
                            + orderAgainstSalesQtyModels.get(i).getShortage_qty()) * rate;

                    PdfUtils.drawText(String.format("%.2f", disc), 405, disc_y);

                    taxble_amount = rate * Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_order_qty()) - disc;

                    if (String.valueOf(taxble_amount).equalsIgnoreCase("NAN"))
                        taxble_amount = 0;

                    cal_total_gst = taxble_amount * Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_gst()
                            .replace("%", "")) / 100;
                    tot_cal_gst += cal_total_gst;

                    tot_taxble_amount += taxble_amount;

                    PdfUtils.drawText(String.format("%.2f", taxble_amount), 440, taxble_amount_y);
                    PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_gst().trim(), 485, gst_y);
                    PdfUtils.drawText(String.format("%.2f", (cal_total_gst / 2)), 512, cgst_y);
                    PdfUtils.drawText(String.format("%.2f", (cal_total_gst / 2)), 550, sgst_y);

                    net_amount = taxble_amount + cal_total_gst;
                    tot_net_amount += net_amount;

                    PdfUtils.drawText(String.format("%.2f", net_amount) + "", 586, net_amount_y);

                } else {

                    PdfUtils.drawText((i + 1) + "", 34, page_y += 10);
                    if (arrayList_order_prod_pdf.get(i).getProd_name().length() > 15)
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_name().substring(0, 15) + "...", 54, nm_y += 10);
                    else
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_name(), 54, nm_y += 10);

                    if (arrayList_order_prod_pdf.get(i).getProd_unit().length() > 10)
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_unit().substring(0, 9) + "...", 139, unit_y += 10);
                    else
                        PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_unit(), 139, unit_y += 10);

                    PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_hsn_code(), 193, hsn_y += 10);
                    PdfUtils.drawText("", 227, batch_y += 10);
                    PdfUtils.drawText("", 265, mfg_dt_y += 10);
                    PdfUtils.drawText("", 302, exp_dt_y += 10);
                    PdfUtils.drawText(String.format("%.1f", Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_order_qty())), 338, qty_y += 10);

                    rate = (Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_ptr_rs()) /
                            Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_gst()
                                    .replace("%", "")));

                    PdfUtils.drawText(String.format("%.2f", rate), 370, rate_y += 10);

                    disc = (orderAgainstSalesQtyModels.get(i).getScheme_qty()
                            + orderAgainstSalesQtyModels.get(i).getReplace_qty()
                            + orderAgainstSalesQtyModels.get(i).getShortage_qty()) * rate;

                    PdfUtils.drawText(String.format("%.2f", disc), 405, disc_y += 10);

                    taxble_amount = rate * Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_order_qty()) - disc;

                    if (String.valueOf(taxble_amount).equalsIgnoreCase("NAN"))
                        taxble_amount = 0;

                    cal_total_gst = taxble_amount * Double.parseDouble(arrayList_order_prod_pdf.get(i).getProd_gst()
                            .replace("%", "")) / 100;

                    tot_cal_gst += cal_total_gst;

                    tot_taxble_amount += taxble_amount;

                    PdfUtils.drawText(String.format("%.2f", taxble_amount), 440, taxble_amount_y += 10);
                    PdfUtils.drawText(arrayList_order_prod_pdf.get(i).getProd_gst().trim(), 485, gst_y += 10);
                    PdfUtils.drawText(String.format("%.2f", (cal_total_gst / 2)), 512, cgst_y += 10);
                    PdfUtils.drawText(String.format("%.2f", (cal_total_gst / 2)), 550, sgst_y += 10);

                    net_amount = taxble_amount + cal_total_gst;

                    tot_net_amount += net_amount;

                    PdfUtils.drawText(String.format("%.2f", net_amount) + "", 586, net_amount_y += 10);

                }
            }
        }

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 11);
        PdfUtils.drawText(String.format("%.2f", tot_taxble_amount), 140, 815);
        PdfUtils.drawText(String.format("%.2f", (tot_cal_gst / 2)) + "", 245, 815);
        PdfUtils.drawText(String.format("%.2f", (tot_cal_gst / 2)) + "", 330, 815);

        String[] net_amount_round = String.format("%.2f", tot_net_amount).split("\\.");

        PdfUtils.drawText("(-)0." + net_amount_round[1], 440, 815);
        PdfUtils.drawText(net_amount_round[0], 560, 815);

        PdfUtils.drawText(Currency.convertToIndianCurrency(String.valueOf(tot_taxble_amount)), 140, 835);
        PdfUtils.drawText(Currency.convertToIndianCurrency(net_amount_round[0]), 175, 852);

        PdfUtils.setPaintBrush(Color.BLACK, Paint.Align.LEFT, 12);
        PdfUtils.drawText("Terms & Conditions : 1. " + terms_condition_1, 34, 870);
        PdfUtils.drawText("2. " + terms_condition_2, 34, 882);
        PdfUtils.drawText("3. " + terms_condition_3, 34, 894);
        PdfUtils.drawText("4. " + terms_condition_4, 34, 906);
        PdfUtils.drawText("5. " + terms_condition_5, 34, 920);

        PdfUtils.drawText("For " + stock_point_name, 430, 870);

    }


    private void openGeneratedPDF(String action) {

        //file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/PurchaseOrder.pdf");
        if (file.exists()) {

            if (action.equalsIgnoreCase("share")) {

                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");

                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(requireActivity(),
                            BuildConfig.APPLICATION_ID + ".provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }

                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage("com.whatsapp");
                startActivity(share);

            } else {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(requireActivity(),
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
                    Toast.makeText(requireActivity(), "No Application Available For PDF View", Toast.LENGTH_LONG).show();
                }
            }


        }
    }
    /*----------------------ends of code for generate pdf of invoices-------------------*/

}