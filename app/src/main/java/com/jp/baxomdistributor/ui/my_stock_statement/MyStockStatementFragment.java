package com.jp.baxomdistributor.ui.my_stock_statement;

import static android.content.Context.WIFI_SERVICE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.jp.baxomdistributor.Models.ProdWiseSummeryPOJO;
import com.jp.baxomdistributor.Models.SalesmanSecondarySummeryPOJO;
import com.jp.baxomdistributor.Models.SalesmanSignAuthoritisPOJO;
import com.jp.baxomdistributor.Models.SchemeWiseSummeryPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.Entity2ndParentStockBinding;
import com.jp.baxomdistributor.databinding.Entity3rdParentStockBinding;
import com.jp.baxomdistributor.databinding.EntityImmediateParentStockBinding;
import com.jp.baxomdistributor.databinding.EntityProdWiseSummeryStockBinding;
import com.jp.baxomdistributor.databinding.EntitySalesWiseSummeryStockBinding;
import com.jp.baxomdistributor.databinding.EntitySchemeWiseSummeryStockBinding;
import com.jp.baxomdistributor.databinding.FragmentMyStockStatementBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

public class MyStockStatementFragment extends Fragment {

    private MyStockStatementViewModel homeViewModel;
    FragmentMyStockStatementBinding binding;
    private String TAG = getClass().getSimpleName();

    GDateTime gDateTime = new GDateTime();
    int m, y, d;

    SharedPreferences sp, sp_distributor_detail, sp_update, sp_login, sp_multi_lang;

    String distributor_id = "", salesman_id = "", month_val = "", year_val = "", selected_dist_id = "", stock_id = "", is_delivery_pending = "",
            month_last_date = "";
    String dist_list_url = "", dist_list_response = "", get_stock_url = "", get_stock_response = "", add_stock_url = "", add_stock_response = "";

    ArrayList<String> arrayList_distributor_id, arrayList_distributor_name;
    ArrayAdapter<String> arrayAdapter;

    ArrayList<SalesmanSecondarySummeryPOJO> arrayList_sales_secondary_summery;
    SalesmanSecondarySummeryPOJO salesmanSecondarySummeryPOJO;

    ArrayList<ProdWiseSummeryPOJO> arrayList_prod_wise_summery;
    ProdWiseSummeryPOJO prodWiseSummeryPOJO;

    ArrayList<SchemeWiseSummeryPOJO> arrayList_scheme_wise_summery;
    SchemeWiseSummeryPOJO schemeWiseSummeryPOJO;

    ArrayList<SalesmanSignAuthoritisPOJO> arrayList_sign_authorities;
    SalesmanSignAuthoritisPOJO salesmanSignAuthoritisPOJO;

    ProgressDialog pdialog;

    AlertDialog ad;
    AlertDialog.Builder builder;
    private String IPaddress;

    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanSuchnaList, commanList;


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(MyStockStatementViewModel.class);
        binding = FragmentMyStockStatementBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();


        Log.i(TAG, "IP_ADDRESS=>" + getIpAddress());
        Log.i(TAG, "IMEI_Id=>" + getIMEIId(getActivity()));

        sp_update = requireActivity().getSharedPreferences("update_data", Context.MODE_PRIVATE);
        sp = requireActivity().getSharedPreferences("salesman_detail", Context.MODE_PRIVATE);
        sp_distributor_detail = requireActivity().getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);
        sp_login = requireActivity().getSharedPreferences("login_detail", Context.MODE_PRIVATE);
        sp_multi_lang = requireActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getActivity());


        Language language = new Language(sp_multi_lang.getString("lang", ""),
                getActivity(), setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language.getData();

        Language language1 = new Language(sp_multi_lang.getString("lang", ""),
                getActivity(), setLang(sp_multi_lang.getString("lang", "")));
        commanList = language1.getData();

        binding.tvNameOfDistTitle.setText("" + commanList.getArrayList().get(0));
        binding.tvMonthTitle.setText("" + commanList.getArrayList().get(1));
        binding.tvViewDetailTitle.setText("" + commanList.getArrayList().get(2));
        binding.tvSalesmanNameTitle.setText("" + commanList.getArrayList().get(3));
        binding.tvBookingTitle.setText("" + commanList.getArrayList().get(4));
        binding.tvDeliveryTitle.setText("" + commanList.getArrayList().get(5));
        binding.tvProdSecondaryDeliveryTitle.setText("" + commanList.getArrayList().get(5));
        binding.tvProdClosingDeliveryTitle.setText("" + commanList.getArrayList().get(5));
        binding.tvDelFailTitle.setText("" + commanList.getArrayList().get(6));
        binding.tvTotDelPendingRsTitle.setText("" + commanList.getArrayList().get(7));
        binding.tvSalesWiseSummeryTitle.setText("" + commanList.getArrayList().get(8));
        binding.tvParticularsTitle.setText("" + commanList.getArrayList().get(9));
        binding.tvBussinessRsTitle.setText("" + commanList.getArrayList().get(10));
        binding.tvSchemeRsTitle.setText("" + commanList.getArrayList().get(11));
        binding.tvTotSecondaryRsTitle.setText("" + commanList.getArrayList().get(12));

        binding.tvLastMonthPendingRsTitle.setText("" + commanList.getArrayList().get(13));
        binding.tvThisMonthPendingRsTitle.setText("" + commanList.getArrayList().get(14));
        binding.tvProdWiseSummeryTitle.setText("" + commanList.getArrayList().get(15));
        binding.tvProdWithUnitTitle.setText("" + commanList.getArrayList().get(16));
        binding.tvProdOpeningTitle.setText("" + commanList.getArrayList().get(17));
        binding.tvProdPrimaryTitle.setText("" + commanList.getArrayList().get(18));

        binding.tvProdSecondaryTitle.setText("" + commanList.getArrayList().get(19));
        binding.tvProdClosingTitle.setText("" + commanList.getArrayList().get(20));

        binding.tvPhycalVerificationTitle.setText("" + commanList.getArrayList().get(21));

        binding.cbLastMonthStock.setText("" + commanList.getArrayList().get(22));
        binding.cbThisMonthStock.setText("" + commanList.getArrayList().get(23));

        binding.tvTargetRsTitle.setText("" + commanList.getArrayList().get(24));
        binding.tvAchivRsTitle.setText("" + commanList.getArrayList().get(25));
        binding.tvStockSummeryTitle.setText("" + commanList.getArrayList().get(26));
        binding.tvSchemeSummeryTitle.setText("" + commanList.getArrayList().get(27));
        binding.tvSchemeTitle.setText("" + commanList.getArrayList().get(28));
        binding.tvSchemeOpeningTitle.setText("" + commanList.getArrayList().get(17));
        binding.tvSchemePrimaryTitle.setText("" + commanList.getArrayList().get(18));
        binding.tvSchemeSecondaryTitle.setText("" + commanList.getArrayList().get(19));
        binding.tvSchemeClosingTitle.setText("" + commanList.getArrayList().get(20));

        binding.tvSigningAuthorityTitle.setText("" + commanList.getArrayList().get(29));
        binding.tvStatusTitle.setText("" + commanList.getArrayList().get(30));
        binding.tvSignButtonTitle.setText("" + commanList.getArrayList().get(31));
        binding.tvParentUniverified.setText("" + commanList.getArrayList().get(32));

        binding.tvVerifiedOnTitle.setText("" + commanList.getArrayList().get(35));
        binding.tvTargetLeftRsTitle.setText("" + commanList.getArrayList().get(38));

        if (!sp_multi_lang.getString("lang", "").equalsIgnoreCase("ENG")) {
            binding.tvProdSecondaryPenDelTitle.setText("" + commanList.getArrayList().get(42));
            binding.tvProdClosingPenDelTitle.setText("" + commanList.getArrayList().get(42));
        }

        salesman_id = sp.getString("salesman_id", "");
        distributor_id = sp_distributor_detail.getString("distributor_id", "");

        if (!distributor_id.equalsIgnoreCase("")) {

            dist_list_url = getString(R.string.Base_URL) + getString(R.string.purchaseorder_distributor_list_by_distId_url) + distributor_id;
            new getDistListTask().execute(dist_list_url);

        } else {

            dist_list_url = getString(R.string.Base_URL) + "stock_distributor_list.php?salesman_id=" + salesman_id;
            new getDistListTask().execute(dist_list_url);

        }

        if (gDateTime.getMonth().length() > 1)
            month_val = gDateTime.getMonth();
        else
            month_val = "0" + gDateTime.getMonth();

        year_val = gDateTime.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year_val));
        calendar.set(Calendar.MONTH, Integer.parseInt(month_val));
        month_last_date = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + "/" + month_val + "/" + year_val;


        binding.tvMonthDateStock.setText("" + gDateTime.getMonth_name(Integer.parseInt(gDateTime.getMonth())) + "-" + gDateTime.getYear());
        binding.tvMonthDateStock.setOnClickListener(v -> {

            y = Integer.parseInt(gDateTime.getYear());
            m = Integer.parseInt(gDateTime.getMonth()) - 1;
            d = Integer.parseInt(gDateTime.getDay());

            MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment
                    .getInstance(m, y);

            dialogFragment.setOnDateSetListener((year, monthOfYear) -> {

                binding.tvMonthDateStock.setText("" + gDateTime.getMonth_name(monthOfYear + 1) + "-" + year);

                binding.cbThisMonthStock.setChecked(false);
                binding.cbLastMonthStock.setChecked(false);

                month_val = String.valueOf(monthOfYear + 1);
                year_val = String.valueOf(year);

                get_stock_url = getString(R.string.Base_URL) + getString(R.string.get_stock_statement_url) + selected_dist_id +
                        "&month_val=" + month_val + "&year_val=" + year_val;
                new getStockDataTask().execute(get_stock_url);

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, monthOfYear);

                month_last_date = calendar1.getActualMaximum(Calendar.DAY_OF_MONTH) + "/" + month_val + "/" + year_val;


            });

            dialogFragment.show(requireActivity().getSupportFragmentManager(), null);

        });

        binding.spnDistNameStock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_dist_id = arrayList_distributor_id.get(position);

                get_stock_url = getString(R.string.Base_URL) + getString(R.string.get_stock_statement_url) + arrayList_distributor_id.get(position) +
                        "&month_val=" + month_val + "&year_val=" + year_val;
                new getStockDataTask().execute(get_stock_url);

                binding.tvLoginDistName.setText("" + arrayList_distributor_name.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.cbThisMonthStock.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbThisMonthStock.isChecked()) {

                month_val = gDateTime.getMonth();
                year_val = gDateTime.getYear();

                binding.tvMonthDateStock.setText("" + gDateTime.getMonth_name(Integer.parseInt(month_val)) + "-" + year_val);
                get_stock_url = getString(R.string.Base_URL) + getString(R.string.get_stock_statement_url) + selected_dist_id +
                        "&month_val=" + month_val + "&year_val=" + year_val;
                new getStockDataTask().execute(get_stock_url);

                binding.cbLastMonthStock.setChecked(false);
            }

        });

        binding.cbLastMonthStock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (binding.cbLastMonthStock.isChecked()) {

                    month_val = Integer.parseInt(gDateTime.getMonth()) - 1 + "";
                    year_val = gDateTime.getYear();

                    binding.tvMonthDateStock.setText("" + gDateTime.getMonth_name(Integer.parseInt(month_val)) + "-" + year_val);

                    get_stock_url = getString(R.string.Base_URL) + getString(R.string.get_stock_statement_url) + selected_dist_id +
                            "&month_val=" + month_val + "&year_val=" + year_val;
                    new getStockDataTask().execute(get_stock_url);
                    binding.cbThisMonthStock.setChecked(false);
                }

            }
        });

        binding.tvSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (distributor_id.equalsIgnoreCase(selected_dist_id)) {

                    int curr_month_year = Integer.parseInt(gDateTime.getMonth() + gDateTime.getYear());
                    int selected_month_year = Integer.parseInt(month_val + year_val);

                    Log.i(TAG, "curr_month_year=>" + curr_month_year);
                    Log.i(TAG, "selected_month_year=>" + selected_month_year);

                    if (is_delivery_pending.equalsIgnoreCase("no")) {

                        if (selected_month_year < curr_month_year) {
                            showConfirmDialog();
                        } else
                            showinfoDialog("" + commanSuchnaList.getArrayList().get(0));
                    } else
                        showinfoDialog("" + commanSuchnaList.getArrayList().get(1));

                } else
                    showinfoDialog("" + commanSuchnaList.getArrayList().get(2));

            }
        });

        return root;
    }

    private void showConfirmDialog() {

        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("" + commanSuchnaList.getArrayList().get(6));
        builder.setMessage(commanSuchnaList.getArrayList().get(7) + " " + month_last_date + " " + commanSuchnaList.getArrayList().get(8));
        builder.setCancelable(false);
        builder.setPositiveButton("" + commanList.getArrayList().get(39), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addstock();
                ad.dismiss();
            }
        });

        builder.setNegativeButton("" + commanList.getArrayList().get(40), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
            }
        });

        ad = builder.create();
        ad.show();

        /*ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));*/

    }


    private void showinfoDialog(String m) {

        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(m);
        builder.setCancelable(false);
        builder.setPositiveButton("" + commanList.getArrayList().get(41), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
            }
        });

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    public void addstock() {

        add_stock_url = getString(R.string.Base_URL) + getString(R.string.add_stock_statement_url) + selected_dist_id +
                "&target_rs=" + binding.tvTargetRs.getText().toString() + "&achiv_rs=" + binding.tvAchivRs.getText().toString() +
                "&target_left_rs=" + binding.tvTargetLeftRs.getText().toString() + "&tot_sec_booking=" + binding.tvTotBookingRs.getText().toString() +
                "&tot_sec_delivery=" + binding.tvTotDeliveryRs.getText().toString() + "&tot_sec_del_fail=" + binding.tvTotDelFailRs.getText().toString() +
                "&tot_del_pending_rs=" + binding.tvTotDelPendingRs.getText().toString() + "&opening_bussiness_rs=" + binding.tvOpeningBussinessRs.getText().toString() +
                "&opening_scheme_rs=" + binding.tvOpeningSchemeRs.getText().toString() + "&primary_bussiness_rs=" + binding.tvPrimaryBussinessRs.getText().toString() +
                "&primary_scheme_rs=" + binding.tvPrimarySchemeRs.getText().toString() + "&secondary_bussiness_rs=" + binding.tvSecondaryBussinessRs.getText().toString() +
                "&secondary_scheme_rs=" + binding.tvTargetRs.getText().toString() + "&closing_bussiness_rs=" + binding.tvClosingBussinessRs.getText().toString() +
                "&closing_scheme_rs=" + binding.tvTargetRs.getText().toString() + "&last_bussin_month_primary_rs=" + binding.tvLastMonthPendingBussinessRs.getText().toString() +
                "&this_bussin_month_primary_rs=" + binding.tvThisMonthPendingBussinessRs.getText().toString() + "&last_scheme_month_primary_rs=" + binding.tvLastMonthPendingSchemeRs.getText().toString() +
                "&this_scheme_month_primary_rs=" + binding.tvThisMonthPendingSchemeRs.getText().toString() + "&dist_login_sign=1&dist_sales_imm_parent_sign=0" +
                "&dist_sales_2nd_parent_sign=0&dist_sales_3rd_parent_sign=0" +
                "&month_val=" + month_val +
                "&year_val=" + year_val +
                "&login_dist_name=" + binding.spnDistNameStock.getSelectedItem().toString() +
                "&login_dist_ip=" + getIpAddress() +
                "&login_dist_imei=" + getIMEIId(getActivity());
        //Log.i(TAG, "add_stock_url==>" + add_stock_url);
        new AddStockTask().execute(add_stock_url.replace("%20", ""));
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

        try {

            JSONObject jsonObject = new JSONObject(dist_list_response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                arrayList_distributor_id.add(object.getString("dist_id"));
                arrayList_distributor_name.add(object.getString("dist_name"));

            }

            arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.mytextview_12sp, arrayList_distributor_name);
            binding.spnDistNameStock.setAdapter(arrayAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class getStockDataTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(getActivity());
            pdialog.setMessage("" + commanSuchnaList.getArrayList().get(10));
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "get_stock_url=>" + strings[0]);

            HttpHandler httpHandler = new HttpHandler();
            get_stock_response = httpHandler.makeServiceCall(strings[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "get_stock_res=>" + get_stock_response);
                getStockData();

                if (pdialog.isShowing())
                    pdialog.dismiss();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getStockData() {

        try {
            JSONObject jsonObject = new JSONObject(get_stock_response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject data_object = jsonArray.getJSONObject(i);

                    JSONObject monthly_stock_data_object = data_object.getJSONObject("monthly_stock_data");

                    binding.tvTargetRs.setText("" + monthly_stock_data_object.getString("target_rs"));
                    binding.tvAchivRs.setText("" + monthly_stock_data_object.getString("achievement_rs"));
                    binding.tvTargetLeftRs.setText("" + monthly_stock_data_object.getString("target_left_rs"));

                    is_delivery_pending = monthly_stock_data_object.getString("is_delivery_pending");

                    JSONArray salesman_secondary_summery_Array = data_object.getJSONArray("salesman_wise_secondary_summery");

                    if (salesman_secondary_summery_Array.length() > 0) {

                        binding.rvSalesWiseSummery.setVisibility(View.VISIBLE);

                        arrayList_sales_secondary_summery = new ArrayList<>();

                        for (int j = 0; j < salesman_secondary_summery_Array.length(); j++) {

                            JSONObject salesman_secondary_summery_object = salesman_secondary_summery_Array.getJSONObject(j);

                            salesmanSecondarySummeryPOJO = new SalesmanSecondarySummeryPOJO(salesman_secondary_summery_object.getString("salesman_id"),
                                    salesman_secondary_summery_object.getString("salesman"),
                                    salesman_secondary_summery_object.getString("booking"),
                                    salesman_secondary_summery_object.getString("delivery"),
                                    salesman_secondary_summery_object.getString("delivery_fail"));

                            arrayList_sales_secondary_summery.add(salesmanSecondarySummeryPOJO);
                        }

                        SalesmanWiseSummeryAdapter salesmanWiseSummeryAdapter = new SalesmanWiseSummeryAdapter(arrayList_sales_secondary_summery, getActivity());
                        binding.rvSalesWiseSummery.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                        binding.rvSalesWiseSummery.setAdapter(salesmanWiseSummeryAdapter);
                    } else
                        binding.rvSalesWiseSummery.setVisibility(View.GONE);

                    JSONObject salesman_secondary_total_summery_object = data_object.getJSONObject("salesman_wise_secondary_total_summery");

                    binding.tvTotBookingRs.setText("" + salesman_secondary_total_summery_object.getString("total_booking"));
                    binding.tvTotDeliveryRs.setText("" + salesman_secondary_total_summery_object.getString("total_delivery"));
                    binding.tvTotDelFailRs.setText("" + salesman_secondary_total_summery_object.getString("total_del_fail"));
                    binding.tvTotDelPendingRs.setText("" + monthly_stock_data_object.getString("delivery_pending_rs"));

                    JSONObject stock_summery_object = data_object.getJSONObject("stock_summery");

                    binding.tvOpeningBussinessRs.setText("" + stock_summery_object.getString("bussiness_opening_rs"));
                    binding.tvPrimaryBussinessRs.setText("" + stock_summery_object.getString("bussiness_primary_rs"));
                    binding.tvSecondaryBussinessRs.setText("" + stock_summery_object.getString("bussiness_secondary_rs"));
                    binding.tvClosingBussinessRs.setText("" + stock_summery_object.getString("bussiness_closing_rs"));
                    binding.tvLastMonthPendingBussinessRs.setText("" + stock_summery_object.getString("bussiness_last_month_primary_rs"));
                    binding.tvThisMonthPendingBussinessRs.setText("" + stock_summery_object.getString("bussiness_this_month_primary_rs"));
                    binding.tvPrimarySchemeRs.setText("" + stock_summery_object.getString("scheme_primary_rs"));
                    binding.tvSecondarySchemeRs.setText("" + stock_summery_object.getString("scheme_secondary_rs"));
                    binding.tvLastMonthPendingSchemeRs.setText("" + stock_summery_object.getString("scheme_last_month_primary_rs"));
                    binding.tvThisMonthPendingSchemeRs.setText("" + stock_summery_object.getString("scheme_this_month_primary_rs"));

                    JSONArray prod_wise_summery_array = data_object.getJSONArray("prod_wise_summery");

                    if (prod_wise_summery_array.length() > 0) {

                        binding.rvProdWiseSummery.setVisibility(View.VISIBLE);

                        arrayList_prod_wise_summery = new ArrayList<>();

                        for (int j = 0; j < prod_wise_summery_array.length(); j++) {

                            JSONObject prod_wise_summery_object = prod_wise_summery_array.getJSONObject(j);

                            prodWiseSummeryPOJO = new ProdWiseSummeryPOJO(prod_wise_summery_object.getString("product_id"),
                                    prod_wise_summery_object.getString("prod_name"),
                                    prod_wise_summery_object.getString("prod_unit"),
                                    prod_wise_summery_object.getString("opening"),
                                    prod_wise_summery_object.getString("primary"),
                                    prod_wise_summery_object.getString("secondary"),
                                    prod_wise_summery_object.getString("secondary_pending"),
                                    prod_wise_summery_object.getString("secondary_pen_del"),
                                    prod_wise_summery_object.getString("closing"),
                                    prod_wise_summery_object.getString("closing_pen_del"));

                            arrayList_prod_wise_summery.add(prodWiseSummeryPOJO);
                        }

                        ProdWiseSummeryAdapter prodWiseSummeryAdapter = new ProdWiseSummeryAdapter(arrayList_prod_wise_summery, getActivity());
                        binding.rvProdWiseSummery.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                        binding.rvProdWiseSummery.setAdapter(prodWiseSummeryAdapter);
                    } else
                        binding.rvProdWiseSummery.setVisibility(View.GONE);


                    JSONArray scheme_wise_summery_array = data_object.getJSONArray("scheme_wise_summery");

                    if (scheme_wise_summery_array.length() > 0) {

                        binding.rvSchmeSummery.setVisibility(View.VISIBLE);

                        arrayList_scheme_wise_summery = new ArrayList<>();

                        for (int j = 0; j < scheme_wise_summery_array.length(); j++) {

                            JSONObject scheme_wise_summery_object = scheme_wise_summery_array.getJSONObject(j);

                            schemeWiseSummeryPOJO = new SchemeWiseSummeryPOJO(scheme_wise_summery_object.getString("scheme_id"),
                                    scheme_wise_summery_object.getString("scheme_name"),
                                    scheme_wise_summery_object.getString("scheme_long_name"),
                                    scheme_wise_summery_object.getString("opening"),
                                    scheme_wise_summery_object.getString("primary"),
                                    scheme_wise_summery_object.getString("secondary"),
                                    scheme_wise_summery_object.getString("closing"));

                            arrayList_scheme_wise_summery.add(schemeWiseSummeryPOJO);

                        }

                        SchemeWiseSummeryAdapter schemeWiseSummeryAdapter = new SchemeWiseSummeryAdapter(arrayList_scheme_wise_summery, getActivity());
                        binding.rvSchmeSummery.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                        binding.rvSchmeSummery.setAdapter(schemeWiseSummeryAdapter);
                    } else
                        binding.rvSchmeSummery.setVisibility(View.GONE);

                    JSONArray sign_autorities_array = data_object.getJSONArray("sign_autorities");

                    if (sign_autorities_array.length() > 0) {

                        binding.rvSignAuthorities.setVisibility(View.VISIBLE);

                        arrayList_sign_authorities = new ArrayList<>();

                        for (int j = 0; j < sign_autorities_array.length(); j++) {

                            JSONObject sign_autorities_object = sign_autorities_array.getJSONObject(j);

                            salesmanSignAuthoritisPOJO = new SalesmanSignAuthoritisPOJO(sign_autorities_object.getString("parent_salesman_id"),
                                    sign_autorities_object.getString("salesman"),
                                    sign_autorities_object.getString("parent"),
                                    sign_autorities_object.getString("previos_sign"),
                                    sign_autorities_object.getString("verified_date"),
                                    sign_autorities_object.getString("imei_no"),
                                    sign_autorities_object.getString("ip_address"));

                            arrayList_sign_authorities.add(salesmanSignAuthoritisPOJO);
                        }

                        SignAuthoritiesAdapter signAuthoritiesAdapter = new SignAuthoritiesAdapter(arrayList_sign_authorities, getActivity());
                        binding.rvSignAuthorities.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                        binding.rvSignAuthorities.setAdapter(signAuthoritiesAdapter);


                    } else
                        binding.rvSignAuthorities.setVisibility(View.GONE);

                    JSONObject sign_status_object = data_object.getJSONObject("sign_status");

                    if (sign_status_object.getString("dist_login_sign").equalsIgnoreCase("1")) {

                        binding.llVerified.setVisibility(View.VISIBLE);
                        binding.tvParentUniverified.setVisibility(View.GONE);
                        binding.tvSignButton.setText("" + commanList.getArrayList().get(34));
                        binding.tvSignButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                        binding.tvSignButton.setEnabled(false);

                    } else if (sign_status_object.getString("dist_login_sign").equalsIgnoreCase("0")) {

                        binding.llVerified.setVisibility(View.GONE);
                        binding.tvParentUniverified.setVisibility(View.VISIBLE);
                        binding.tvSignButton.setText("" + commanList.getArrayList().get(33));
                        binding.tvSignButton.setBackgroundColor(Color.parseColor("#ffff0000"));
                        binding.tvSignButton.setEnabled(true);
                    }

                    stock_id = sign_status_object.getString("stock_summery_id");
                    binding.tvVerifiedOn.setText("" + sign_status_object.getString("verified_on"));
                    binding.tvImeiNo.setText(commanList.getArrayList().get(36) + ": " + sign_status_object.getString("login_dist_imei"));
                    binding.tvIpAddress.setText(commanList.getArrayList().get(37) + ": " + sign_status_object.getString("login_dist_ip"));


                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class SalesmanWiseSummeryAdapter extends RecyclerView.Adapter<SalesmanWiseSummeryAdapter.MyHolder> {

        ArrayList<SalesmanSecondarySummeryPOJO> arrayList_sales_secondary_summery;
        Context context;

        public SalesmanWiseSummeryAdapter(ArrayList<SalesmanSecondarySummeryPOJO> arrayList_sales_secondary_summery, Context context) {
            this.arrayList_sales_secondary_summery = arrayList_sales_secondary_summery;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntitySalesWiseSummeryStockBinding.inflate(LayoutInflater.from(context), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {

            holder.binding.tvSalesmanName.setText("" + arrayList_sales_secondary_summery.get(position).getSalesman());
            holder.binding.tvBooking.setText("" + arrayList_sales_secondary_summery.get(position).getBooking());
            holder.binding.tvDelivery.setText("" + arrayList_sales_secondary_summery.get(position).getDelivery());
            holder.binding.tvDelFail.setText("" + arrayList_sales_secondary_summery.get(position).getDelivery_fail());

        }

        @Override
        public int getItemCount() {
            return arrayList_sales_secondary_summery.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntitySalesWiseSummeryStockBinding binding;

            public MyHolder(EntitySalesWiseSummeryStockBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    public class ProdWiseSummeryAdapter extends RecyclerView.Adapter<ProdWiseSummeryAdapter.MyHolder> {

        ArrayList<ProdWiseSummeryPOJO> arrayList_prod_wise_summery;
        Context context;

        public ProdWiseSummeryAdapter(ArrayList<ProdWiseSummeryPOJO> arrayList_prod_wise_summery, Context context) {
            this.arrayList_prod_wise_summery = arrayList_prod_wise_summery;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityProdWiseSummeryStockBinding.inflate(LayoutInflater.from(context), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {

            holder.binding.tvProdName.setText("" + arrayList_prod_wise_summery.get(position).getProd_name());
            holder.binding.tvProdUnit.setText("(" + arrayList_prod_wise_summery.get(position).getProd_unit() + ")");
            holder.binding.tvOpeningQty.setText("" + arrayList_prod_wise_summery.get(position).getOpening());
            holder.binding.tvPrimaryQty.setText("" + arrayList_prod_wise_summery.get(position).getPrimary());
            holder.binding.tvSecondaryQty.setText("" + arrayList_prod_wise_summery.get(position).getSecondary());
            holder.binding.tvSecondaryQtyPenDel.setText("" + arrayList_prod_wise_summery.get(position).getSecondary_pen_del());
            holder.binding.tvClosingQty.setText("" + arrayList_prod_wise_summery.get(position).getClosing());
            holder.binding.tvClosingQtyPenDel.setText("" + arrayList_prod_wise_summery.get(position).getClosing_pen_del());

        }

        @Override
        public int getItemCount() {
            return arrayList_prod_wise_summery.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityProdWiseSummeryStockBinding binding;

            public MyHolder(EntityProdWiseSummeryStockBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    public class SchemeWiseSummeryAdapter extends RecyclerView.Adapter<SchemeWiseSummeryAdapter.MyHolder> {

        ArrayList<SchemeWiseSummeryPOJO> arrayList_scheme_wise_summery;
        Context context;

        public SchemeWiseSummeryAdapter(ArrayList<SchemeWiseSummeryPOJO> arrayList_scheme_wise_summery, Context context) {
            this.arrayList_scheme_wise_summery = arrayList_scheme_wise_summery;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntitySchemeWiseSummeryStockBinding.inflate(LayoutInflater.from(context), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {

            holder.binding.tvSchemeName.setText("" + arrayList_scheme_wise_summery.get(position).getScheme_name());
            holder.binding.tvOpeningQty.setText("" + arrayList_scheme_wise_summery.get(position).getOpening());
            holder.binding.tvPrimaryQty.setText("" + arrayList_scheme_wise_summery.get(position).getPrimary());
            holder.binding.tvSecondaryQty.setText("" + arrayList_scheme_wise_summery.get(position).getSecondary());
            holder.binding.tvClosingQty.setText("" + arrayList_scheme_wise_summery.get(position).getClosing());

        }

        @Override
        public int getItemCount() {
            return arrayList_scheme_wise_summery.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntitySchemeWiseSummeryStockBinding binding;

            public MyHolder(EntitySchemeWiseSummeryStockBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    public class SignAuthoritiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<SalesmanSignAuthoritisPOJO> arrayList_sign_authorities;
        Context context;

        public SignAuthoritiesAdapter(ArrayList<SalesmanSignAuthoritisPOJO> arrayList_sign_authorities, Context context) {
            this.arrayList_sign_authorities = arrayList_sign_authorities;
            this.context = context;
        }

        public static final int immparent = 1, parent2nd = 2, parent3rd = 3;

        @Override
        public int getItemViewType(int position) {

            if (arrayList_sign_authorities.get(position).getParent().equalsIgnoreCase("immediate"))
                return immparent;
            else if (arrayList_sign_authorities.get(position).getParent().equalsIgnoreCase("second"))
                return parent2nd;
            else
                return parent3rd;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (viewType) {

                case immparent:
                    return new ParentMyHolder(EntityImmediateParentStockBinding.inflate(LayoutInflater.from(context), parent, false));
                case parent2nd:
                    return new SecondParentMyHolder(Entity2ndParentStockBinding.inflate(LayoutInflater.from(context), parent, false));
                case parent3rd:
                    return new ThirdParentMyHolder(Entity3rdParentStockBinding.inflate(LayoutInflater.from(context), parent, false));
                default:
                    throw new IllegalStateException("Unexpected value: " + viewType);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            //Log.i(TAG, "countParentSign==>" + countParentSign());
            //Log.i(TAG, "count2ndParentSign==>" + count2ndParentSign());
            //Log.i(TAG, "count3rdParentSign==>" + count3rdParentSign());

            if (arrayList_sign_authorities.get(position).getParent().equalsIgnoreCase("immediate")) {
                ParentMyHolder parentMyHolder = (ParentMyHolder) holder;

                parentMyHolder.bindingParent.tvImmediateParentSalesmanName.setText("" + arrayList_sign_authorities.get(position).getSaleman());

                if (arrayList_sign_authorities.get(position).getPrevios_sign().equalsIgnoreCase("pending"))
                    parentMyHolder.bindingParent.tvSignButton.setEnabled(false);
                else {

                    if (arrayList_sign_authorities.get(position).getVerified_date().equalsIgnoreCase(""))
                        parentMyHolder.bindingParent.tvSignButton.setEnabled(true);
                    else
                        parentMyHolder.bindingParent.tvSignButton.setEnabled(false);
                }

                parentMyHolder.bindingParent.tvVerifiedOnTitle.setText("" + commanList.getArrayList().get(35));
                parentMyHolder.bindingParent.tvImmediateUniverified.setText("" + commanList.getArrayList().get(32));

                if (!arrayList_sign_authorities.get(position).getVerified_date().equalsIgnoreCase("")) {

                    parentMyHolder.bindingParent.llVerified.setVisibility(View.VISIBLE);
                    parentMyHolder.bindingParent.tvVerifiedOn.setText("" + arrayList_sign_authorities.get(position).getVerified_date());
                    parentMyHolder.bindingParent.tvImeiNo.setText(commanList.getArrayList().get(36) + ": " + arrayList_sign_authorities.get(position).getImei_no());
                    parentMyHolder.bindingParent.tvIpAddress.setText(commanList.getArrayList().get(37) + ": " + arrayList_sign_authorities.get(position).getIp_address());
                    parentMyHolder.bindingParent.tvImmediateUniverified.setVisibility(View.GONE);
                    parentMyHolder.bindingParent.tvSignButton.setText("" + commanList.getArrayList().get(34));
                    parentMyHolder.bindingParent.tvSignButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                } else {
                    parentMyHolder.bindingParent.tvSignButton.setText("" + commanList.getArrayList().get(33));
                    parentMyHolder.bindingParent.llVerified.setVisibility(View.GONE);
                    parentMyHolder.bindingParent.tvImmediateUniverified.setVisibility(View.VISIBLE);
                }


                parentMyHolder.bindingParent.tvSignButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (salesman_id.equalsIgnoreCase(arrayList_sign_authorities.get(position).getParent_salesman_id())) {

                            builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("" + commanSuchnaList.getArrayList().get(6));
                            builder.setMessage(commanSuchnaList.getArrayList().get(7) + " " + month_last_date + " " + commanSuchnaList.getArrayList().get(8));
                            builder.setCancelable(false);
                            builder.setPositiveButton("" + commanList.getArrayList().get(39), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (countParentSign() == 1)
                                        add_stock_url = getString(R.string.Base_URL) + "update_stock_statement.php?dist_id=" + selected_dist_id +
                                                "&salesman_id=" + arrayList_sign_authorities.get(position).getParent_salesman_id() +
                                                "&stock_id=" + stock_id +
                                                "&salesman_name=" + arrayList_sign_authorities.get(position).getSaleman() +
                                                "&imei_no=" + getIMEIId(getActivity()) + "&ip_address=" + getIpAddress() +
                                                "&parent=1";
                                    else
                                        add_stock_url = getString(R.string.Base_URL) + "update_stock_statement.php?dist_id=" + selected_dist_id +
                                                "&salesman_id=" + arrayList_sign_authorities.get(position).getParent_salesman_id() +
                                                "&stock_id=" + stock_id +
                                                "&salesman_name=" + arrayList_sign_authorities.get(position).getSaleman() +
                                                "&imei_no=" + getIMEIId(getActivity()) + "&ip_address=" + getIpAddress() +
                                                "&parent=0";

                                    new UpdateStockTask().execute(add_stock_url.replace("%20", ""));

                                    ad.dismiss();
                                }
                            });

                            builder.setNegativeButton("" + commanList.getArrayList().get(40), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ad.dismiss();
                                }
                            });

                            ad = builder.create();
                            ad.show();

                        } else
                            showinfoDialog("" + commanSuchnaList.getArrayList().get(2));
                    }
                });


            } else if (arrayList_sign_authorities.get(position).getParent().equalsIgnoreCase("second")) {
                SecondParentMyHolder secondParentMyHolder = (SecondParentMyHolder) holder;

                secondParentMyHolder.binding2ndParent.tv2ndParentSalesmanName.setText("" + arrayList_sign_authorities.get(position).getSaleman());

                if (arrayList_sign_authorities.get(position).getPrevios_sign().equalsIgnoreCase("pending"))
                    secondParentMyHolder.binding2ndParent.tvSignButton.setEnabled(false);
                else {

                    if (arrayList_sign_authorities.get(position).getVerified_date().equalsIgnoreCase(""))
                        secondParentMyHolder.binding2ndParent.tvSignButton.setEnabled(true);
                    else
                        secondParentMyHolder.binding2ndParent.tvSignButton.setEnabled(false);
                }
                secondParentMyHolder.binding2ndParent.tvVerifiedOnTitle.setText("" + commanList.getArrayList().get(35));
                secondParentMyHolder.binding2ndParent.tv2ndUniverified.setText("" + commanList.getArrayList().get(32));

                if (!arrayList_sign_authorities.get(position).getVerified_date().equalsIgnoreCase("")) {

                    secondParentMyHolder.binding2ndParent.llVerified.setVisibility(View.VISIBLE);
                    secondParentMyHolder.binding2ndParent.tvVerifiedOn.setText("" + arrayList_sign_authorities.get(position).getVerified_date());
                    secondParentMyHolder.binding2ndParent.tvImeiNo.setText(commanList.getArrayList().get(36) + ": " + arrayList_sign_authorities.get(position).getImei_no());
                    secondParentMyHolder.binding2ndParent.tvIpAddress.setText(commanList.getArrayList().get(37) + ": " + arrayList_sign_authorities.get(position).getIp_address());
                    secondParentMyHolder.binding2ndParent.tv2ndUniverified.setVisibility(View.GONE);
                    secondParentMyHolder.binding2ndParent.tvSignButton.setText("" + commanList.getArrayList().get(34));
                    secondParentMyHolder.binding2ndParent.tvSignButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                } else {
                    secondParentMyHolder.binding2ndParent.tvSignButton.setText("" + commanList.getArrayList().get(33));
                    secondParentMyHolder.binding2ndParent.llVerified.setVisibility(View.GONE);
                    secondParentMyHolder.binding2ndParent.tv2ndUniverified.setVisibility(View.VISIBLE);
                }


                secondParentMyHolder.binding2ndParent.tvSignButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (salesman_id.equalsIgnoreCase(arrayList_sign_authorities.get(position).getParent_salesman_id())) {

                            builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("" + commanSuchnaList.getArrayList().get(6));
                            builder.setMessage(commanSuchnaList.getArrayList().get(7) + " " + month_last_date + " " + commanSuchnaList.getArrayList().get(8));
                            builder.setCancelable(false);
                            builder.setPositiveButton("" + commanList.getArrayList().get(39), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (count2ndParentSign() == 1)
                                        add_stock_url = getString(R.string.Base_URL) + "update_stock_statement.php?dist_id=" + selected_dist_id +
                                                "&salesman_id=" + arrayList_sign_authorities.get(position).getParent_salesman_id() +
                                                "&stock_id=" + stock_id +
                                                "&salesman_name=" + arrayList_sign_authorities.get(position).getSaleman() +
                                                "&imei_no=" + getIMEIId(getActivity()) + "&ip_address=" + getIpAddress() +
                                                "&parent=2";
                                    else
                                        add_stock_url = getString(R.string.Base_URL) + "update_stock_statement.php?dist_id=" + selected_dist_id +
                                                "&salesman_id=" + arrayList_sign_authorities.get(position).getParent_salesman_id() +
                                                "&stock_id=" + stock_id +
                                                "&salesman_name=" + arrayList_sign_authorities.get(position).getSaleman() +
                                                "&imei_no=" + getIMEIId(getActivity()) + "&ip_address=" + getIpAddress() +
                                                "&parent=0";

                                    new UpdateStockTask().execute(add_stock_url.replace("%20", ""));
                                    ad.dismiss();
                                }
                            });

                            builder.setNegativeButton("" + commanList.getArrayList().get(40), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ad.dismiss();
                                }
                            });

                            ad = builder.create();
                            ad.show();
                        } else
                            showinfoDialog("" + commanSuchnaList.getArrayList().get(2));
                    }
                });
            } else {
                ThirdParentMyHolder thirdParentMyHolder = (ThirdParentMyHolder) holder;

                thirdParentMyHolder.binding3rdParent.tv3rdParentSalesmanName.setText("" + arrayList_sign_authorities.get(position).getSaleman());

                if (arrayList_sign_authorities.get(position).getPrevios_sign().equalsIgnoreCase("pending"))
                    thirdParentMyHolder.binding3rdParent.tvSignButton.setEnabled(false);
                else {

                    if (arrayList_sign_authorities.get(position).getVerified_date().equalsIgnoreCase(""))
                        thirdParentMyHolder.binding3rdParent.tvSignButton.setEnabled(true);
                    else
                        thirdParentMyHolder.binding3rdParent.tvSignButton.setEnabled(false);
                }

                thirdParentMyHolder.binding3rdParent.tvVerifiedOnTitle.setText("" + commanList.getArrayList().get(35));
                thirdParentMyHolder.binding3rdParent.tv3rdUniverified.setText("" + commanList.getArrayList().get(32));

                if (!arrayList_sign_authorities.get(position).getVerified_date().equalsIgnoreCase("")) {

                    thirdParentMyHolder.binding3rdParent.llVerified.setVisibility(View.VISIBLE);
                    thirdParentMyHolder.binding3rdParent.tvVerifiedOn.setText("" + arrayList_sign_authorities.get(position).getVerified_date());
                    thirdParentMyHolder.binding3rdParent.tvImeiNo.setText(commanList.getArrayList().get(36) + ": " + arrayList_sign_authorities.get(position).getImei_no());
                    thirdParentMyHolder.binding3rdParent.tvIpAddress.setText(commanList.getArrayList().get(37) + ": " + arrayList_sign_authorities.get(position).getIp_address());
                    thirdParentMyHolder.binding3rdParent.tv3rdUniverified.setVisibility(View.GONE);
                    thirdParentMyHolder.binding3rdParent.tvSignButton.setText("" + commanList.getArrayList().get(34));
                    thirdParentMyHolder.binding3rdParent.tvSignButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                } else {
                    thirdParentMyHolder.binding3rdParent.tvSignButton.setText("" + commanList.getArrayList().get(33));
                    thirdParentMyHolder.binding3rdParent.llVerified.setVisibility(View.GONE);
                    thirdParentMyHolder.binding3rdParent.tv3rdUniverified.setVisibility(View.VISIBLE);
                }


                thirdParentMyHolder.binding3rdParent.tvSignButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (salesman_id.equalsIgnoreCase(arrayList_sign_authorities.get(position).getParent_salesman_id())) {
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("" + commanSuchnaList.getArrayList().get(6));
                            builder.setMessage(commanSuchnaList.getArrayList().get(7) + " " + month_last_date + " " + commanSuchnaList.getArrayList().get(8));
                            builder.setCancelable(false);
                            builder.setPositiveButton("" + commanList.getArrayList().get(39), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (count3rdParentSign() == 1)
                                        add_stock_url = getString(R.string.Base_URL) + "update_stock_statement.php?dist_id=" + selected_dist_id +
                                                "&salesman_id=" + arrayList_sign_authorities.get(position).getParent_salesman_id() +
                                                "&stock_id=" + stock_id +
                                                "&salesman_name=" + arrayList_sign_authorities.get(position).getSaleman() +
                                                "&imei_no=" + getIMEIId(getActivity()) + "&ip_address=" + getIpAddress() +
                                                "&parent=3";
                                    else
                                        add_stock_url = getString(R.string.Base_URL) + "update_stock_statement.php?dist_id=" + selected_dist_id +
                                                "&salesman_id=" + arrayList_sign_authorities.get(position).getParent_salesman_id() +
                                                "&stock_id=" + stock_id +
                                                "&salesman_name=" + arrayList_sign_authorities.get(position).getSaleman() +
                                                "&imei_no=" + getIMEIId(getActivity()) + "&ip_address=" + getIpAddress() +
                                                "&parent=0";

                                    new UpdateStockTask().execute(add_stock_url.replace("%20", ""));

                                    ad.dismiss();
                                }
                            });

                            builder.setNegativeButton("" + commanList.getArrayList().get(40), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ad.dismiss();
                                }
                            });

                            ad = builder.create();
                            ad.show();

                        } else
                            showinfoDialog("" + commanSuchnaList.getArrayList().get(2));
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return arrayList_sign_authorities.size();
        }

        public class ParentMyHolder extends RecyclerView.ViewHolder {

            EntityImmediateParentStockBinding bindingParent;

            public ParentMyHolder(EntityImmediateParentStockBinding b) {
                super(b.getRoot());
                bindingParent = b;
            }
        }

        public class SecondParentMyHolder extends RecyclerView.ViewHolder {

            Entity2ndParentStockBinding binding2ndParent;

            public SecondParentMyHolder(Entity2ndParentStockBinding b) {
                super(b.getRoot());
                binding2ndParent = b;
            }
        }


        public class ThirdParentMyHolder extends RecyclerView.ViewHolder {

            Entity3rdParentStockBinding binding3rdParent;

            public ThirdParentMyHolder(Entity3rdParentStockBinding b) {
                super(b.getRoot());
                binding3rdParent = b;
            }
        }

    }

    public class AddStockTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(getActivity());
            pdialog.setMessage("" + commanSuchnaList.getArrayList().get(9));
            pdialog.setCancelable(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "add_stock_url=>" + strings[0]);

            HttpHandler httpHandler = new HttpHandler();
            add_stock_response = httpHandler.makeServiceCall(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "add_stock_response=>" + add_stock_response);

                if (add_stock_response.contains("Added Successfully"))
                    showDialog("" + commanSuchnaList.getArrayList().get(4));
                else
                    showinfoDialog("" + commanSuchnaList.getArrayList().get(3));
                if (pdialog.isShowing())
                    pdialog.dismiss();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    public class UpdateStockTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(getActivity());
            pdialog.setMessage("" + commanSuchnaList.getArrayList().get(9));
            pdialog.setCancelable(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "update_stock_url=>" + strings[0]);

            HttpHandler httpHandler = new HttpHandler();
            add_stock_response = httpHandler.makeServiceCall(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "update_stock_response=>" + add_stock_response);

                if (add_stock_response.contains("Update Successfully"))
                    showDialog("" + commanSuchnaList.getArrayList().get(5));
                else
                    showinfoDialog("" + commanSuchnaList.getArrayList().get(3));

                if (pdialog.isShowing())
                    pdialog.dismiss();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }


    public void showDialog(String m) {

        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(m);
        builder.setCancelable(false);
        builder.setPositiveButton("" + commanList.getArrayList().get(41), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                get_stock_url = getString(R.string.Base_URL) + getString(R.string.get_stock_statement_url) + selected_dist_id +
                        "&month_val=" + month_val + "&year_val=" + year_val;
                new getStockDataTask().execute(get_stock_url);
                ad.dismiss();
            }
        });

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    public String getIMEIId(Context context) {

        try {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            @SuppressLint({"HardwareIds", "MissingPermission"})
            String imei = telephonyManager.getDeviceId();
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }

        } catch (Exception e) {
            e.getMessage();
        }
        return "not_found";
    }


    private String getIpAddress() {

        boolean WIFI = false, MOBILE = false;

        ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = CM.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {

            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    WIFI = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    MOBILE = true;
        }

        if (WIFI == true) {
            IPaddress = GetDeviceipWiFiData();
        }

        if (MOBILE == true) {
            IPaddress = GetDeviceipMobileData();
        }

        return IPaddress;
    }

    public String GetDeviceipMobileData() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface networkinterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("Current IP", ex.toString());
        }
        return null;
    }

    public String GetDeviceipWiFiData() {

        WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        @SuppressWarnings("deprecation")
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;

    }

    public int countParentSign() {
        int counter = 0;

        for (int i = 0; i < arrayList_sign_authorities.size(); i++) {

            if (arrayList_sign_authorities.get(i).getParent().equalsIgnoreCase("immediate")) {

                if (arrayList_sign_authorities.get(i).getVerified_date().equalsIgnoreCase("")) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public int count2ndParentSign() {
        int counter = 0;

        for (int i = 0; i < arrayList_sign_authorities.size(); i++) {

            if (arrayList_sign_authorities.get(i).getParent().equalsIgnoreCase("second")) {

                if (arrayList_sign_authorities.get(i).getVerified_date().equalsIgnoreCase("")) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public int count3rdParentSign() {
        int counter = 0;

        for (int i = 0; i < arrayList_sign_authorities.size(); i++) {

            if (arrayList_sign_authorities.get(i).getParent().equalsIgnoreCase("third")) {

                if (arrayList_sign_authorities.get(i).getVerified_date().equalsIgnoreCase("")) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "35");

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
        Cursor cur = db.viewMultiLangSuchna("35");

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