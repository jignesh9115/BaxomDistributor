package com.jp.baxomdistributor.ui.order_against_sales;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.jp.baxomdistributor.Adapters.OrderAgainstSalesProdAdapter;
import com.jp.baxomdistributor.Adapters.OrderAgainstSalesSchemeAdapter;
import com.jp.baxomdistributor.Interfaces.OrderAgainstSalesProdQtyListener;
import com.jp.baxomdistributor.Interfaces.OrderAgainstSalesQtyListener;
import com.jp.baxomdistributor.Interfaces.OrderAgainstSalesSchemeQtyListener;
import com.jp.baxomdistributor.Models.OrderAgainstSalesAutoOrderModel;
import com.jp.baxomdistributor.Models.OrderAgainstSalesOrderModel;
import com.jp.baxomdistributor.Models.OrderAgainstSalesProdModel;
import com.jp.baxomdistributor.Models.OrderAgainstSalesQtyModel;
import com.jp.baxomdistributor.Models.SchemeProductPOJO;
import com.jp.baxomdistributor.Models.ViewSchemesOrderPOJO;
import com.jp.baxomdistributor.Utils.Api;
import com.jp.baxomdistributor.Utils.ApiClient;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.databinding.FragmentOrderAgainstSalesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    ArrayList<OrderAgainstSalesOrderModel> arrayList_order_prod_sales;
    ArrayList<SchemeProductPOJO> arrayList_scheme_prod;
    OrderAgainstSalesSchemeAdapter salesSchemeAdapter;
    ProgressDialog pdialog;
    GDateTime gDateTime = new GDateTime();
    ArrayList<OrderAgainstSalesQtyModel> orderAgainstSalesQtyModels;
    ArrayList<Double> orderAgainstSchemeProdQtyModels, arrayList_auto_order_qty;
    ArrayList<OrderAgainstSalesAutoOrderModel> orderAgainstSalesAutoOrderModels;
    String is_add_scheme_qty_oas, is_add_replace_qty_oas, is_add_shortage_qty_oas, is_add_scheme_detail_oas;
    ArrayList<String> ignore_prod_ids;
    private FragmentOrderAgainstSalesBinding binding;

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

        binding.tvOrderDetailTitle.setOnClickListener(v -> {
            calordervalue();
            salesProdAdapter.notifyDataSetChanged();
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

                            binding.tvCurrentClosingPts.setText("" + dist_stock_value_obj.getString("stock_purchase_value"));
                            binding.tvCurrentClosingBiz.setText("" + dist_stock_value_obj.getString("stock_bizz_value"));
                            binding.tvSalesTitle.setText("Sales(" + dist_stock_value_obj.getString("from_date")
                                    + " to " + dist_stock_value_obj.getString("to_date") + " )");

                            binding.tvSalesPts.setText("" + dist_stock_value_obj.getString("sales_stock_purchase_value"));
                            binding.tvSalesBiz.setText("" + dist_stock_value_obj.getString("sales_stock_bizz_value"));

                            if (dist_stock_value_obj.getString("last_order_pending_purchase").equalsIgnoreCase(""))
                                binding.tvRequiredOrderPts.setText("" +
                                        dist_stock_value_obj.getString("sales_stock_purchase_value"));
                            else
                                binding.tvRequiredOrderPts.setText("" +
                                        (Double.parseDouble(dist_stock_value_obj.getString("sales_stock_purchase_value"))
                                                + Double.parseDouble(dist_stock_value_obj.getString("last_order_pending_purchase"))));

                            if (dist_stock_value_obj.getString("last_order_pending_biz").equalsIgnoreCase(""))
                                binding.tvRequiredOrderBiz.setText("" +
                                        dist_stock_value_obj.getString("sales_stock_bizz_value"));
                            else
                                binding.tvRequiredOrderBiz.setText("" +
                                        (Double.parseDouble(dist_stock_value_obj.getString("sales_stock_bizz_value"))
                                                + Double.parseDouble(dist_stock_value_obj.getString("last_order_pending_biz"))));

                        }

                        JSONObject dist_obj = jsonObject.getJSONObject("dist_obj");
                        binding.edtPartyName.setText("" + dist_obj.getString("name"));
                        binding.edtDistMobileNo.setText("" + dist_obj.getString("mobile_no"));
                        binding.edtDistAddress.setText("" + dist_obj.getString("address"));
                        binding.edtTransporterName.setText("" + dist_obj.getString("transportername"));
                        binding.edtContactNo1.setText("" + dist_obj.getString("transportercontact1"));
                        binding.edtContactNo2.setText("" + dist_obj.getString("transportercontact2"));

                        binding.tvBufferRsPts.setText("" + dist_obj.getString("purchase_buffer_stock"));
                        binding.tvBufferRsBiz.setText("" + dist_obj.getString("bizz_buffer_stock"));

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
                                        obj.getString("purchase_rate"),
                                        obj.getString("sales_rate"),
                                        obj.getString("stock_qty"),
                                        obj.getString("one_day_avg"),
                                        "0", "0",
                                        "0", "0"));

                                orderAgainstSalesQtyModels.add(new OrderAgainstSalesQtyModel(
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
                            ignore_prod_ids = new ArrayList<>();
                            calAutoOrder();

                            if (ignore_prod_ids.size() > 0)
                                calAutoOrder();

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

    @SuppressLint("DefaultLocale")
    private void calAutoOrder() {

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

        required_order_value_days = Double.parseDouble(binding.tvRequiredOrderPts.getText().toString()) / aday_value_pts;
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
        binding.tvThisOrderPtsTop.setText("" + String.format("%.2f", total_pts_rs));
        binding.tvThisOrderPts.setText("" + String.format("%.2f", total_pts_rs));
        binding.tvThisOrderBizTop.setText("" + String.format("%.2f", total_biz_rs));
        binding.tvThisOrderBiz.setText("" + String.format("%.2f", total_biz_rs));

        binding.tvPendingThisOrderPts.setText("" +
                String.format("%.2f", (Double.parseDouble(binding.tvRequiredOrderPts.getText().toString())
                        - total_pts_rs)));

        binding.tvPendingThisOrderBiz.setText("" +
                String.format("%.2f", (Double.parseDouble(binding.tvRequiredOrderBiz.getText().toString())
                        - total_biz_rs)));
    }

    @SuppressLint("DefaultLocale")
    public void calordervalue() {

        double total_pts_rs = Double.parseDouble(binding.tvThisOrderPts.getText().toString()),
                total_biz_rs = Double.parseDouble(binding.tvThisOrderBiz.getText().toString());

        double required_order_biz_rs = Double.parseDouble(String.format("%.2f", Double.parseDouble(binding.tvRequiredOrderBiz.getText().toString()))),
                required_order_pts_rs = Double.parseDouble(String.format("%.2f", Double.parseDouble(binding.tvRequiredOrderPts.getText().toString())));

        Log.i(TAG, "<________________total order rs_________________>");
        Log.i(TAG, "required_order_pts_rs___________>" + required_order_pts_rs);
        Log.i(TAG, "total_pts_rs____________________>" + total_pts_rs);
        Log.i(TAG, "required_order_biz_rs___________>" + required_order_biz_rs);
        Log.i(TAG, "total_biz_rs____________________>" + total_biz_rs);

        double tot_diff = 0;
        String action = "";

        if (total_pts_rs > required_order_pts_rs) {

            tot_diff = Double.parseDouble(String.format("%.2f", (total_pts_rs - required_order_pts_rs)));
            Log.i(TAG, "inside if this order < required order....>" + String.format("%.2f", (total_pts_rs - required_order_pts_rs)));

            action = "sub";

        } else if (total_pts_rs < required_order_pts_rs) {

            tot_diff = Double.parseDouble(String.format("%.2f", (required_order_pts_rs - total_pts_rs)));
            Log.i(TAG, "inside else if this order > required order....>" + String.format("%.2f", (required_order_pts_rs - total_pts_rs)));

            action = "add";

        }

        Log.i(TAG, "tot_diff.....>" + tot_diff);

        int pos = 0;
        double diff_qty = 0;

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

        diff_qty = Double.parseDouble(String.format("%.2f", tot_diff
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
                salesProdModels.get(pos).getPurchase_rate(),
                salesProdModels.get(pos).getSales_rate(),
                salesProdModels.get(pos).getStock_qty(),
                salesProdModels.get(pos).getOne_day_avg(),
                p_qty + "",
                "0",
                "0",
                "0"));

        getTotalOrderRs();

    }

    @Override
    public void qtyChange(int pos, double scheme_qty, double replace_qty, double shortage_qty) {

        orderAgainstSalesQtyModels.set(pos, new OrderAgainstSalesQtyModel(
                scheme_qty,
                replace_qty,
                shortage_qty));

        calProdDiscount();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void calProdDiscount() {

        double prod_disc = 0;
        for (int i = 0; i < orderAgainstSalesQtyModels.size(); i++) {
            prod_disc += (orderAgainstSalesQtyModels.get(i).getScheme_qty()
                    + orderAgainstSalesQtyModels.get(i).getReplace_qty()
                    + orderAgainstSalesQtyModels.get(i).getShortage_qty())
                    * Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_ptr_rs());
        }
        //Log.i(TAG, "prod_disc.....>" + prod_disc);
        binding.tvTotSchemeDiscValue.setText("Rs. " + String.format("%.2f", prod_disc));

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void qtyChange(int pos, double scheme_qty, String result_prod_id) {

        for (int i = 0; i < salesProdModels.size(); i++) {

            if (salesProdModels.get(i).getProd_id().equalsIgnoreCase(result_prod_id)) {

                /*if (scheme_qty > 1)
                    orderAgainstSchemeProdQtyModels.set(i,
                            ((Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_order_qty()))
                                    - scheme_qty) + prod_qty);
                else
                    orderAgainstSchemeProdQtyModels.set(i,
                            (Double.parseDouble(arrayList_order_prod_sales.get(i).getProd_order_qty()))
                                    + prod_qty);*/
                orderAgainstSchemeProdQtyModels.set(i, scheme_qty);
            }

        }
        salesProdAdapter.notifyDataSetChanged();
    }
}