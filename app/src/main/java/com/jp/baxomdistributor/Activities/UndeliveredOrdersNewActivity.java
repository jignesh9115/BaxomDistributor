package com.jp.baxomdistributor.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.jp.baxomdistributor.Adapters.UndeliveredOrdersBitAdapter;
import com.jp.baxomdistributor.Adapters.UndeliveredOrdersDatesAdapter;
import com.jp.baxomdistributor.Adapters.UndeliveredOrdersSalesmansAdapter;
import com.jp.baxomdistributor.BuildConfig;
import com.jp.baxomdistributor.Interfaces.BitClickListener;
import com.jp.baxomdistributor.Interfaces.SalesmanDateCheckedListener;
import com.jp.baxomdistributor.Interfaces.SalesmanSelectionListener;
import com.jp.baxomdistributor.Models.DeliverySummeryDetailPOJO;
import com.jp.baxomdistributor.Models.DeliverySummeryPOJO;
import com.jp.baxomdistributor.Models.DitributorTablePOJO;
import com.jp.baxomdistributor.Models.GoodsSummeryDetailsPOJO;
import com.jp.baxomdistributor.Models.GoodsSummeryPOJO;
import com.jp.baxomdistributor.Models.GroupDatesOfSalesmanPOJO;
import com.jp.baxomdistributor.Models.SalesProdDetailPOJO;
import com.jp.baxomdistributor.Models.SchemeListPDFPOJO;
import com.jp.baxomdistributor.Models.UdeliveredOrdersModel;
import com.jp.baxomdistributor.Models.UndeliveredOrdersSalesmanModel;
import com.jp.baxomdistributor.Models.ViewSalesOrderByOrderIdPOJO;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.Api;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.PdfUtils;
import com.jp.baxomdistributor.databinding.ActivityUndeliveredOrdersNewBinding;
import com.jp.baxomdistributor.databinding.DialogSalesmanSelectionBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UndeliveredOrdersNewActivity extends AppCompatActivity implements BitClickListener,
        SalesmanSelectionListener, SalesmanDateCheckedListener {

    private final String TAG = getClass().getSimpleName();
    ActivityUndeliveredOrdersNewBinding binding;
    Retrofit retrofit;
    Api api;

    ArrayList<String> arrayList_salesman_id, arrayList_salesman_name, arrayList_order_date,
            arrayList_date1_salesman, arrayList_date2_salesman, arrayList_date3_salesman,
            arrayList_date4_salesman, arrayList_date5_salesman;

    SharedPreferences sp_distributor_detail;

    String dist_id;

    GDateTime gDateTime = new GDateTime();

    UndeliveredOrdersSalesmansAdapter salesmansAdapter;
    UndeliveredOrdersDatesAdapter salesDatesAdapter1, salesDatesAdapter2, salesDatesAdapter3,
            salesDatesAdapter4, salesDatesAdapter5;

    ArrayList<UdeliveredOrdersModel> arrayList_bit_orders;
    UdeliveredOrdersModel udeliveredOrdersModel;

    ArrayList<UndeliveredOrdersSalesmanModel> arrayList_bit_salesman;
    UndeliveredOrdersSalesmanModel undeliveredOrdersSalesmanModel;

    UndeliveredOrdersBitAdapter undeliveredOrdersBitAdapter;

    ProgressDialog pdialog;

    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    ArrayList<GroupDatesOfSalesmanPOJO> arrayList_salesman_dates;

    /*-----------PDF variable declaration starts---------*/
    ArrayList<DitributorTablePOJO> arrayList_dist_table;
    DitributorTablePOJO ditributorTablePOJO;

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

    ArrayList<ViewSalesOrderByOrderIdPOJO> arrayList_view_sales_orders;
    ViewSalesOrderByOrderIdPOJO viewSalesOrderByOrderIdPOJO;

    ArrayList<SchemeListPDFPOJO> arrayList_scheme_name, arrayList_all_order_schemes;
    SchemeListPDFPOJO schemeListPDFPOJO;

    String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private Uri uri;

    /*-----------PDF variable declaration ends---------*/


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUndeliveredOrdersNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //================code for allow strictMode starts ================

        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
        }

        //================code for allow strictMode ends ===================


        if (!AskPermissions(UndeliveredOrdersNewActivity.this, permissionsRequired)) {
            ActivityCompat.requestPermissions(UndeliveredOrdersNewActivity.this, permissionsRequired, 1);
        }

        sp_distributor_detail = getSharedPreferences("distributor_detail", MODE_PRIVATE);
        dist_id = sp_distributor_detail.getString("distributor_id", "");

        binding.tvDistName.setText("" + sp_distributor_detail.getString("name", ""));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();


        retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.Base_URL))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        api = retrofit.create(Api.class);

        Log.i(TAG, "date_list==>" + getIntent().getExtras().getString("date_list"));

        arrayList_order_date = stringToDateArrayList(getIntent().getExtras().getString("date_list"));

        Log.i(TAG, "arrayList_order_date==>" + arrayList_order_date.size());

        Collections.sort(arrayList_order_date);

        for (int i = 0; i < arrayList_order_date.size(); i++) {

            if (i == 0) {
                binding.llDate1.setVisibility(View.VISIBLE);
                binding.tvDate1.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            } else if (i == 1) {
                binding.llDate2.setVisibility(View.VISIBLE);
                binding.tvDate2.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            } else if (i == 2) {
                binding.llDate3.setVisibility(View.VISIBLE);
                binding.tvDate3.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            } else if (i == 3) {
                binding.llDate4.setVisibility(View.VISIBLE);
                binding.tvDate4.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            } else if (i == 4) {
                binding.llDate5.setVisibility(View.VISIBLE);
                binding.tvDate5.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            }
        }

        binding.imgBack.setOnClickListener(v -> finish());
        getSalesmanListByDist(getIntent().getExtras().getString("date_list"));


        binding.btnApply.setOnClickListener(v -> {

            Log.i(TAG, "listToJarray..>" + listToJarray());
            if (listToJarray().length() > 0)
                getBitlist(listToJarray());
            else
                Toast.makeText(getApplicationContext(), "please select salesman !", Toast.LENGTH_SHORT).show();
        });

        binding.btnClearlist.setOnClickListener(v -> {
            arrayList_date1_salesman.clear();
            arrayList_date2_salesman.clear();
            arrayList_date3_salesman.clear();
            arrayList_date4_salesman.clear();
            arrayList_date5_salesman.clear();

            if (arrayList_salesman_dates != null)
                arrayList_salesman_dates.clear();

            initializeAdapters();

            binding.rvBitList.setVisibility(View.GONE);

        });

        binding.btnOrderSummery.setOnClickListener(v -> {
            Log.i(TAG, "arrayList_salesman_dates..>" + new Gson().toJson(arrayList_salesman_dates));
            if (arrayList_salesman_dates != null && arrayList_salesman_dates.size() > 0)
                getSalesOrdersByGroupDates(new Gson().toJson(arrayList_salesman_dates), "");
            else
                Toast.makeText(UndeliveredOrdersNewActivity.this, "please select salesman", Toast.LENGTH_SHORT).show();
        });

        binding.btnGeneratePdfMergedshop.setOnClickListener(v -> {
            Log.i(TAG, "arrayList_salesman_dates..>" + new Gson().toJson(arrayList_salesman_dates));
            if (arrayList_salesman_dates != null && arrayList_salesman_dates.size() > 0)
                getSalesOrdersByGroupDates(new Gson().toJson(arrayList_salesman_dates), "merge shop");
            else
                Toast.makeText(UndeliveredOrdersNewActivity.this, "please select salesman", Toast.LENGTH_SHORT).show();

        });

        binding.btnShopLocation.setOnClickListener(v -> {

            Log.i(TAG, "jArray==>" + new Gson().toJson(arrayList_salesman_dates));

            if (arrayList_salesman_dates != null && arrayList_salesman_dates.size() > 0) {

                Intent intent = new Intent(UndeliveredOrdersNewActivity.this, MapsActivity.class);
                intent.putExtra("jArray", new Gson().toJson(arrayList_salesman_dates));
                intent.putExtra("map_action", "multiple_date");
                startActivity(intent);

            } else {
                Toast.makeText(UndeliveredOrdersNewActivity.this, "please select salesman", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSetDefault.setOnClickListener(v -> {

            JSONArray array = new JSONArray();

            for (int i = 0; i < arrayList_date1_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesmen", arrayList_date1_salesman.get(i));
                    object.put("date_type", "type1");
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < arrayList_date2_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesmen", arrayList_date2_salesman.get(i));
                    object.put("date_type", "type2");
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < arrayList_date3_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesmen", arrayList_date3_salesman.get(i));
                    object.put("date_type", "type3");
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < arrayList_date4_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesmen", arrayList_date4_salesman.get(i));
                    object.put("date_type", "type4");
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < arrayList_date5_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesmen", arrayList_date5_salesman.get(i));
                    object.put("date_type", "type5");
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.i(TAG, "array....>" + array);

            if (array.length() > 0)
                setDistPattern(array + "");
            else
                Toast.makeText(getApplicationContext(), "please select salesman", Toast.LENGTH_SHORT).show();

        });
    }

    /*------------get salesman list code -----------*/
    public void getSalesmanListByDist(String dates) {

        Log.i(TAG, "dates...>" + dates);

        Call<String> call = api.getSalesmanListByDistId(dist_id, dates);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "salesman_list_req..>" + call.request());
                Log.i(TAG, "salesman_list_res..>" + response.body());

                try {

                    JSONObject jsonObject = new JSONObject(response.body() + "");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    arrayList_salesman_id = new ArrayList<>();
                    arrayList_salesman_name = new ArrayList<>();
                    arrayList_date1_salesman = new ArrayList<>();
                    arrayList_date2_salesman = new ArrayList<>();
                    arrayList_date3_salesman = new ArrayList<>();
                    arrayList_date4_salesman = new ArrayList<>();
                    arrayList_date5_salesman = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        arrayList_salesman_id.add(object.getString("salesman_id"));
                        arrayList_salesman_name.add(object.getString("salesman"));
                    }

                    salesmansAdapter = new UndeliveredOrdersSalesmansAdapter(UndeliveredOrdersNewActivity.this, arrayList_salesman_name,
                            UndeliveredOrdersNewActivity.this);
                    binding.rvSalesmanNames.setAdapter(salesmansAdapter);

                    JSONArray dist_pattern_arr = jsonObject.getJSONArray("dist_pattern_arr");
                    if (dist_pattern_arr.length() > 0) {
                        for (int i = 0; i < dist_pattern_arr.length(); i++) {
                            JSONObject object = dist_pattern_arr.getJSONObject(i);

                            if (object.getString("date_type").equalsIgnoreCase("type1"))
                                arrayList_date1_salesman.add(object.getString("salesmen"));

                            if (object.getString("date_type").equalsIgnoreCase("type2"))
                                arrayList_date2_salesman.add(object.getString("salesmen"));

                            if (object.getString("date_type").equalsIgnoreCase("type3"))
                                arrayList_date3_salesman.add(object.getString("salesmen"));

                            if (object.getString("date_type").equalsIgnoreCase("type4"))
                                arrayList_date4_salesman.add(object.getString("salesmen"));

                            if (object.getString("date_type").equalsIgnoreCase("type5"))
                                arrayList_date5_salesman.add(object.getString("salesmen"));
                        }
                        initializeAdapters();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });

    }

    /*------------get bit list code -----------*/
    private void getBitlist(JSONArray jsonArray) {

        pdialog = new ProgressDialog(UndeliveredOrdersNewActivity.this);
        pdialog.setMessage("Loading...");
        pdialog.setCancelable(false);
        pdialog.show();

        Call<String> call = api.getUndeliveredOrdersByDist(jsonArray);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "bit_req...>" + call.request());
                Log.i(TAG, "bit_res...>" + response.body());

                if (pdialog.isShowing())
                    pdialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response.body() + "");
                    JSONArray data_array = jsonObject.getJSONArray("data");

                    if (data_array.length() > 0) {

                        binding.rvBitList.setVisibility(View.VISIBLE);
                        binding.imgEmpty.setVisibility(View.GONE);

                        arrayList_bit_orders = new ArrayList<>();
                        arrayList_salesman_dates = new ArrayList<>();

                        for (int i = 0; i < data_array.length(); i++) {

                            JSONObject object = data_array.getJSONObject(i);

                            JSONArray salesman_arr = object.getJSONArray("sales_arr");

                            arrayList_bit_salesman = new ArrayList<>();
                            for (int j = 0; j < salesman_arr.length(); j++) {

                                JSONObject salesman_object = salesman_arr.getJSONObject(j);
                                undeliveredOrdersSalesmanModel = new UndeliveredOrdersSalesmanModel(
                                        salesman_object.getString("salesman_id"),
                                        salesman_object.getString("salesman"),
                                        salesman_object.getString("tot_order"),
                                        salesman_object.getString("tot_amount"),
                                        salesman_object.getString("entry_date"),
                                        false);
                                arrayList_bit_salesman.add(undeliveredOrdersSalesmanModel);
                            }

                            udeliveredOrdersModel = new UdeliveredOrdersModel(
                                    object.getString("bit_id"),
                                    object.getString("bit_name"),
                                    object.getString("tot_order"),
                                    object.getString("tot_amount"),
                                    arrayList_bit_salesman);

                            arrayList_bit_orders.add(udeliveredOrdersModel);

                        }

                        undeliveredOrdersBitAdapter = new UndeliveredOrdersBitAdapter(arrayList_bit_orders, UndeliveredOrdersNewActivity.this,
                                UndeliveredOrdersNewActivity.this, UndeliveredOrdersNewActivity.this);
                        binding.rvBitList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        binding.rvBitList.setAdapter(undeliveredOrdersBitAdapter);
                    } else {
                        binding.imgEmpty.setVisibility(View.VISIBLE);
                        binding.rvBitList.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.i(TAG, "bit_req_failed...>" + t);
                if (pdialog.isShowing())
                    pdialog.dismiss();
            }
        });

    }

    /*--------------set distributor pattern------*/
    private void setDistPattern(String dates) {
        Call<String> call = api.set_dist_pattern(dist_id, dates);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.i(TAG, "set pattern req..>" + call.request());
                Log.i(TAG, "set pattern res..>" + response.body());

                if (response.body() != null && response.body().contains("Pattern set sucessfully")) {
                    Toast.makeText(getApplicationContext(), "Pattern set sucessfully", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "something went wrong...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.i(TAG, "set pattern error..>" + t);
            }
        });
    }


    public JSONArray listToJarray() {

        JSONArray array = new JSONArray();

        if (binding.llDate1.getVisibility() == View.VISIBLE) {
            for (int i = 0; i < arrayList_date1_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesman_id", getSalesIdFromName(arrayList_date1_salesman.get(i)));
                    object.put("date", gDateTime.ymdTodmy(arrayList_order_date.get(0)));
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (binding.llDate2.getVisibility() == View.VISIBLE)
            for (int i = 0; i < arrayList_date2_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesman_id", getSalesIdFromName(arrayList_date2_salesman.get(i)));
                    object.put("date", gDateTime.ymdTodmy(arrayList_order_date.get(1)));
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        if (binding.llDate3.getVisibility() == View.VISIBLE)
            for (int i = 0; i < arrayList_date3_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesman_id", getSalesIdFromName(arrayList_date3_salesman.get(i)));
                    object.put("date", gDateTime.ymdTodmy(arrayList_order_date.get(2)));
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        if (binding.llDate4.getVisibility() == View.VISIBLE)
            for (int i = 0; i < arrayList_date4_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesman_id", getSalesIdFromName(arrayList_date4_salesman.get(i)));
                    object.put("date", gDateTime.ymdTodmy(arrayList_order_date.get(3)));
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        if (binding.llDate5.getVisibility() == View.VISIBLE)
            for (int i = 0; i < arrayList_date5_salesman.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("salesman_id", getSalesIdFromName(arrayList_date5_salesman.get(i)));
                    object.put("date", gDateTime.ymdTodmy(arrayList_order_date.get(4)));
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        return array;
    }

    public ArrayList<String> stringToDateArrayList(String data) {
        if (data == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        com.google.gson.stream.JsonReader reader = new JsonReader(new StringReader(data));
        reader.setLenient(true);
        return new Gson().fromJson(reader, listType);
    }

    /*------------get Bit salesman click listener code -----------*/
    @Override
    public void onclick(UndeliveredOrdersSalesmanModel model) {

        /*Log.i(TAG, "Salesman_id...>" + model.getSalesman_id());
        Log.i(TAG, "Salesman...>" + model.getSalesman());
        Log.i(TAG, "Entry_date...>" + model.getEntry_date());*/

        Intent intent = new Intent(UndeliveredOrdersNewActivity.this, ViewUndeliveredOrdersBySalesman.class);
        intent.putExtra("entry_date", gDateTime.dmyToymd(model.getEntry_date()));
        intent.putExtra("distributor_id", dist_id);
        intent.putExtra("distributor_name", sp_distributor_detail.getString("name", ""));
        intent.putExtra("salesman_id", model.getSalesman_id());
        intent.putExtra("salesman_name", model.getSalesman());
        startActivity(intent);

    }

    /*------------get salesman add date click listener code -----------*/
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(String salesman) {
        DialogSalesmanSelectionBinding selectionBinding;
        Dialog dialog = new Dialog(UndeliveredOrdersNewActivity.this);
        selectionBinding = DialogSalesmanSelectionBinding.inflate(getLayoutInflater());
        View view = selectionBinding.getRoot();
        dialog.setContentView(view);

        for (int i = 0; i < arrayList_order_date.size(); i++) {

            if (i == 0) {
                selectionBinding.llDate1.setVisibility(View.VISIBLE);
                selectionBinding.tvDate1Selection.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            } else if (i == 1) {
                selectionBinding.llDate2.setVisibility(View.VISIBLE);
                selectionBinding.tvDate2Selection.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            } else if (i == 2) {
                selectionBinding.llDate3.setVisibility(View.VISIBLE);
                selectionBinding.tvDate3Selection.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            } else if (i == 3) {
                selectionBinding.llDate4.setVisibility(View.VISIBLE);
                selectionBinding.tvDate4Selection.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            } else if (i == 4) {
                selectionBinding.llDate5.setVisibility(View.VISIBLE);
                selectionBinding.tvDate5Selection.setText("" + gDateTime.ymdTodmy(arrayList_order_date.get(i)));
            }
        }

        if (checkIsAvailable(arrayList_date1_salesman, salesman))
            selectionBinding.llDate1.setVisibility(View.GONE);

        if (checkIsAvailable(arrayList_date2_salesman, salesman))
            selectionBinding.llDate2.setVisibility(View.GONE);

        if (checkIsAvailable(arrayList_date3_salesman, salesman))
            selectionBinding.llDate3.setVisibility(View.GONE);

        if (checkIsAvailable(arrayList_date4_salesman, salesman))
            selectionBinding.llDate4.setVisibility(View.GONE);

        if (checkIsAvailable(arrayList_date5_salesman, salesman))
            selectionBinding.llDate5.setVisibility(View.GONE);

        selectionBinding.chkDate1.setOnCheckedChangeListener((compoundButton, b) -> {
            if (selectionBinding.chkDate1.isChecked())
                arrayList_date1_salesman.add(salesman);
            else {
                if (arrayList_date1_salesman.size() > 0)
                    arrayList_date1_salesman.remove(salesman);
            }
        });

        selectionBinding.chkDate2.setOnCheckedChangeListener((compoundButton, b) -> {
            if (selectionBinding.chkDate2.isChecked())
                arrayList_date2_salesman.add(salesman);
            else {
                if (arrayList_date2_salesman.size() > 0)
                    arrayList_date2_salesman.remove(salesman);
            }
        });
        selectionBinding.chkDate3.setOnCheckedChangeListener((compoundButton, b) -> {
            if (selectionBinding.chkDate3.isChecked())
                arrayList_date3_salesman.add(salesman);
            else {
                if (arrayList_date3_salesman.size() > 0)
                    arrayList_date3_salesman.remove(salesman);
            }
        });
        selectionBinding.chkDate4.setOnCheckedChangeListener((compoundButton, b) -> {
            if (selectionBinding.chkDate4.isChecked())
                arrayList_date4_salesman.add(salesman);
            else {
                if (arrayList_date4_salesman.size() > 0)
                    arrayList_date4_salesman.remove(salesman);
            }
        });
        selectionBinding.chkDate5.setOnCheckedChangeListener((compoundButton, b) -> {
            if (selectionBinding.chkDate5.isChecked())
                arrayList_date5_salesman.add(salesman);
            else {
                if (arrayList_date5_salesman.size() > 0)
                    arrayList_date5_salesman.remove(salesman);
            }
        });

        selectionBinding.imgCancelSelection.setOnClickListener(v -> dialog.dismiss());

        selectionBinding.btnDone.setOnClickListener(v -> {
            initializeAdapters();
            dialog.dismiss();

        });

        if (checkIsAvailable(arrayList_date1_salesman, salesman)
                && checkIsAvailable(arrayList_date2_salesman, salesman)
                && checkIsAvailable(arrayList_date3_salesman, salesman)
                && checkIsAvailable(arrayList_date4_salesman, salesman)
                && checkIsAvailable(arrayList_date5_salesman, salesman)
                && arrayList_order_date.size() == 5)
            Toast.makeText(getApplicationContext(), "Selected In All Dates", Toast.LENGTH_SHORT).show();
        else if (checkIsAvailable(arrayList_date1_salesman, salesman)
                && checkIsAvailable(arrayList_date2_salesman, salesman)
                && checkIsAvailable(arrayList_date3_salesman, salesman)
                && checkIsAvailable(arrayList_date4_salesman, salesman)
                && arrayList_order_date.size() == 4)
            Toast.makeText(getApplicationContext(), "Selected In All Dates", Toast.LENGTH_SHORT).show();
        else if (checkIsAvailable(arrayList_date1_salesman, salesman)
                && checkIsAvailable(arrayList_date2_salesman, salesman)
                && checkIsAvailable(arrayList_date3_salesman, salesman)
                && arrayList_order_date.size() == 3)
            Toast.makeText(getApplicationContext(), "Selected In All Dates", Toast.LENGTH_SHORT).show();
        else if (checkIsAvailable(arrayList_date1_salesman, salesman)
                && checkIsAvailable(arrayList_date2_salesman, salesman)
                && arrayList_order_date.size() == 2)
            Toast.makeText(getApplicationContext(), "Selected In All Dates", Toast.LENGTH_SHORT).show();
        else if (checkIsAvailable(arrayList_date1_salesman, salesman)
                && arrayList_order_date.size() == 1)
            Toast.makeText(getApplicationContext(), "Selected In All Dates", Toast.LENGTH_SHORT).show();
        else
            dialog.show();
    }

    public boolean checkIsAvailable(ArrayList<String> arrayList, String name) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (name.equalsIgnoreCase(arrayList.get(i)))
                return true;

        }
        return false;
    }

    public int getSalesIdFromName(String name) {
        for (int i = 0; i < arrayList_salesman_name.size(); i++) {
            if (name.equalsIgnoreCase(arrayList_salesman_name.get(i)))
                return Integer.parseInt(arrayList_salesman_id.get(i));
        }
        return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initializeAdapters() {

        salesDatesAdapter1 = new UndeliveredOrdersDatesAdapter(UndeliveredOrdersNewActivity.this,
                arrayList_date1_salesman);
        binding.rvDate1Salesman.setAdapter(salesDatesAdapter1);
        salesDatesAdapter1.setOnItemClickListener(salesman -> {
            builder = new AlertDialog.Builder(UndeliveredOrdersNewActivity.this);
            builder.setMessage("Do you want to remove ? ");
            builder.setPositiveButton("remove", (dialogInterface, i) -> {
                if (arrayList_date1_salesman.size() > 0)
                    arrayList_date1_salesman.remove(salesman);
                salesDatesAdapter1.notifyDataSetChanged();
            });
            alertDialog = builder.create();
            alertDialog.show();

        });

        salesDatesAdapter2 = new UndeliveredOrdersDatesAdapter(UndeliveredOrdersNewActivity.this,
                arrayList_date2_salesman);
        binding.rvDate2Salesman.setAdapter(salesDatesAdapter2);
        salesDatesAdapter2.setOnItemClickListener(salesman -> {
            builder = new AlertDialog.Builder(UndeliveredOrdersNewActivity.this);
            builder.setMessage("Do you want to remove ? ");
            builder.setPositiveButton("remove", (dialogInterface, i) -> {
                if (arrayList_date2_salesman.size() > 0)
                    arrayList_date2_salesman.remove(salesman);
                salesDatesAdapter2.notifyDataSetChanged();
            });
            alertDialog = builder.create();
            alertDialog.show();

        });

        salesDatesAdapter3 = new UndeliveredOrdersDatesAdapter(UndeliveredOrdersNewActivity.this,
                arrayList_date3_salesman);
        binding.rvDate3Salesman.setAdapter(salesDatesAdapter3);
        salesDatesAdapter3.setOnItemClickListener(salesman -> {
            builder = new AlertDialog.Builder(UndeliveredOrdersNewActivity.this);
            builder.setMessage("Do you want to remove ? ");
            builder.setPositiveButton("remove", (dialogInterface, i) -> {
                if (arrayList_date3_salesman.size() > 0)
                    arrayList_date3_salesman.remove(salesman);
                salesDatesAdapter3.notifyDataSetChanged();
            });
            alertDialog = builder.create();
            alertDialog.show();

        });

        salesDatesAdapter4 = new UndeliveredOrdersDatesAdapter(UndeliveredOrdersNewActivity.this,
                arrayList_date4_salesman);
        binding.rvDate4Salesman.setAdapter(salesDatesAdapter4);
        salesDatesAdapter4.setOnItemClickListener(salesman -> {
            builder = new AlertDialog.Builder(UndeliveredOrdersNewActivity.this);
            builder.setMessage("Do you want to remove ? ");
            builder.setPositiveButton("remove", (dialogInterface, i) -> {
                if (arrayList_date4_salesman.size() > 0)
                    arrayList_date4_salesman.remove(salesman);
                salesDatesAdapter4.notifyDataSetChanged();
            });
            alertDialog = builder.create();
            alertDialog.show();

        });

        salesDatesAdapter5 = new UndeliveredOrdersDatesAdapter(UndeliveredOrdersNewActivity.this,
                arrayList_date5_salesman);
        binding.rvDate5Salesman.setAdapter(salesDatesAdapter5);
        salesDatesAdapter5.setOnItemClickListener(salesman -> {
            builder = new AlertDialog.Builder(UndeliveredOrdersNewActivity.this);
            builder.setMessage("Do you want to remove ? ");
            builder.setPositiveButton("remove", (dialogInterface, i) -> {
                if (arrayList_date5_salesman.size() > 0)
                    arrayList_date5_salesman.remove(salesman);
                salesDatesAdapter5.notifyDataSetChanged();
            });
            alertDialog = builder.create();
            alertDialog.show();

        });
    }

    public void StringdatesToJsonArr() {

        JSONArray array = new JSONArray();

        for (int i = 0; i < arrayList_order_date.size(); i++) {
            JSONObject object = new JSONObject();
            try {
                object.getString(arrayList_order_date.get(i));
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

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
    public void onclick(UndeliveredOrdersSalesmanModel model, boolean b) {

        Log.i(TAG, "getSalesman...>" + model.getSalesman());
        Log.i(TAG, "checked...>" + b);

        if (b) {
            if (!isSalesmanAvailable(model.getSalesman_id(),
                    gDateTime.dmyToymd(model.getEntry_date()))) {

                arrayList_salesman_dates.add(new GroupDatesOfSalesmanPOJO(model.getSalesman_id(),
                        gDateTime.dmyToymd(model.getEntry_date())));
            }
        } else {
            if (arrayList_salesman_dates.size() > 0)
                arrayList_salesman_dates.remove(getPosition(model.getSalesman_id(),
                        gDateTime.dmyToymd(model.getEntry_date())));
        }

    }


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

    /*-----------------code for get sales orders for PDF--------*/
    public void getSalesOrdersByGroupDates(String sales_dates, String action) {

        pdialog = new ProgressDialog(UndeliveredOrdersNewActivity.this);
        pdialog.setMessage("Loading...");
        pdialog.setCancelable(false);
        pdialog.show();

        Call<String> call;

        if (action.equalsIgnoreCase("merge shop"))
            call = api.getMergeShop_SalesOrderpdf_by_group_dates(dist_id, sales_dates);
        else
            call = api.getSalesOrderpdf_by_group_dates(dist_id, sales_dates);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "get data PDF req...>" + call.request());
                Log.i(TAG, "get data PDF res...>" + response.body());

                ditributorTablePOJO = new DitributorTablePOJO();
                arrayList_dist_table = new ArrayList<>();

                String order_time = "";

                try {

                    JSONObject jsonObject = new JSONObject(response.body() + "");
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
                                details_object.getString("coll_order_amt"));

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

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (pdialog.isShowing())
                        pdialog.dismiss();
                }

                if (pdialog.isShowing())
                    pdialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                if (pdialog.isShowing())
                    pdialog.dismiss();
                Log.i(TAG, " Error Occured : " + t);
            }
        });

    }

    //=====================code start for create pdf file =====================================


    File file;

    private void createPDFUtils(int tot_dist) {

        //try {


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
            file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Order List/" + "OrderList-" + dateFormat.format(Calendar.getInstance().getTime()) + ".pdf");
            //file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Order List/Invoice.pdf");
            file.getParentFile().mkdirs();
            file.createNewFile();

            PdfUtils.getDocument().writeTo(new FileOutputStream(file));
            openGeneratedPDF();

        } catch (IOException e) {
            e.printStackTrace();
        }
        PdfUtils.closePage();

        /*} catch (Exception e) {

            Log.i(TAG, "Error Occured: " + e.getMessage());
            Toast.makeText(this, "Error Occured: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }*/
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


        PdfUtils.drawLine(95, 80, 98, 940);
        PdfUtils.drawLine(280, 80, 280, 940);
        PdfUtils.drawLine(400, 112, 400, 940);
        PdfUtils.drawLine(475, 112, 475, 940);
        PdfUtils.drawLine(544, 112, 544, 940);

        PdfUtils.drawText("NAME OF DELIVERY MAN", 180, 75);
        PdfUtils.drawLine(40, 80, 620, 80);
        PdfUtils.drawText("DATE : ", 450, 75);
        PdfUtils.drawText(" TOTAL", 45, 93);
        PdfUtils.drawText("ORDERS", 45, 108);
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
        PdfUtils.drawText("SALESMAN NAME", 283, 130);
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

        int orderno_top = 155, shopname_top = 155, salesman_top = 155, orderamt_top = 155, total_orders = 0;
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

                    if (arrayList_delivery_summery_detail.get(i).getShop_name().length() > 28)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name().substring(0, 28) + "...", 102, shopname_top);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name(), 102, shopname_top);

                    if (arrayList_delivery_summery_detail.get(i).getSalesman().length() > 15)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman().substring(0, 14) + "...", 283, salesman_top);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman(), 283, salesman_top);

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

                    if (arrayList_delivery_summery_detail.get(i).getShop_name().length() > 28)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name().substring(0, 28) + "...", 102, shopname_top += 20);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getShop_name(), 102, shopname_top += 20);

                    if (arrayList_delivery_summery_detail.get(i).getSalesman().length() > 15)
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman().substring(0, 14) + "...", 283, salesman_top += 20);
                    else
                        PdfUtils.drawText(arrayList_delivery_summery_detail.get(i).getSalesman(), 283, salesman_top += 20);

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

    public void clearMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    //=====================code end for create pdf file =======================================

}