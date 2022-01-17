package com.jp.baxomdistributor.ui.bussiness_summery;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jp.baxomdistributor.Adapters.DateWiseSummeryAdapter;
import com.jp.baxomdistributor.Adapters.ProdWiseSummeryAdapter;
import com.jp.baxomdistributor.Models.DateWiseSummeryModel;
import com.jp.baxomdistributor.Models.ProdWiseSummeryModel;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.Api;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.databinding.FragmentBussinessSummeryBinding;
import com.jp.baxomdistributor.databinding.FragmentHomeBinding;
import com.jp.baxomdistributor.ui.mypurchaseorder.MyPurchaseOrderFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BussinessSummeryFragment extends Fragment {

    private BussinessSummeryViewModel homeViewModel;
    private FragmentBussinessSummeryBinding binding;
    String TAG = getClass().getSimpleName();

    GDateTime gDateTime = new GDateTime();
    DatePickerDialog dp;
    Calendar cal;
    int pic_id = 1;
    int y, m, d;

    ArrayList<DateWiseSummeryModel> arrayList_date_wise;
    DateWiseSummeryModel dateWiseSummeryModel;
    DateWiseSummeryAdapter dateWiseSummeryAdapter;

    ArrayList<ProdWiseSummeryModel> arrayList_prod_wise;
    ProdWiseSummeryModel prodWiseSummeryModel;
    ProdWiseSummeryAdapter prodWiseSummeryAdapter;

    Retrofit retrofit = null;
    Api api;

    SharedPreferences sp_distributor_detail;

    String from_date = "", to_date = "", last_week_start = "", last_week_end = "";
    ProgressDialog pdialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(BussinessSummeryViewModel.class);
        binding = FragmentBussinessSummeryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sp_distributor_detail = requireActivity().getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);

        retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.Base_URL))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        binding.tvDistName.setText("" + sp_distributor_detail.getString("name", ""));

        binding.tvFromDate.setText("" + gDateTime.getDatedmy());
        binding.layoutFromDate.setOnClickListener(v -> {

            y = Integer.parseInt(gDateTime.getYear());
            m = Integer.parseInt(gDateTime.getMonth()) - 1;
            d = Integer.parseInt(gDateTime.getDay());

            dp = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                DateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                //  today_date = df.format(cal.getTime());

                binding.cbToday.setChecked(false);
                binding.cbThisWeek.setChecked(false);
                binding.cbThisMonth.setChecked(false);
                binding.cbLastWeek.setChecked(false);

                binding.tvFromDate.setText(dff.format(cal.getTime()));

            }, y, m, d);
            dp.show();

        });

        binding.tvToDate.setText("" + gDateTime.getDatedmy());
        binding.layoutToDate.setOnClickListener(v -> {

            y = Integer.parseInt(gDateTime.getYear());
            m = Integer.parseInt(gDateTime.getMonth()) - 1;
            d = Integer.parseInt(gDateTime.getDay());

            dp = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                DateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                //  today_date = df.format(cal.getTime());
                binding.cbToday.setChecked(false);
                binding.cbThisWeek.setChecked(false);
                binding.cbThisMonth.setChecked(false);
                binding.cbLastWeek.setChecked(false);

                binding.tvToDate.setText(dff.format(cal.getTime()));

            }, y, m, d);
            dp.show();

        });

        binding.cbToday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.cbThisWeek.setChecked(false);
                binding.cbThisMonth.setChecked(false);
                binding.cbLastWeek.setChecked(false);

                binding.tvFromDate.setText("" + gDateTime.getDatedmy());
                binding.tvToDate.setText("" + gDateTime.getDatedmy());
            }
        });

        binding.cbThisWeek.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {

                Calendar c = GregorianCalendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                from_date = df.format(c.getTime());
                c.add(Calendar.DATE, 6);
                to_date = df.format(c.getTime());

                binding.tvFromDate.setText("" + gDateTime.ymdTodmy(from_date));
                binding.tvToDate.setText("" + gDateTime.ymdTodmy(to_date));

                binding.cbToday.setChecked(false);
                binding.cbLastWeek.setChecked(false);
                binding.cbThisMonth.setChecked(false);


            }
        });

        binding.cbLastWeek.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {

                /*Calendar c = GregorianCalendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                from_date = df.format(c.getTime());
                c.add(Calendar.DATE, 6);
                to_date = df.format(c.getTime());*/

                //int monthMaxDays = c.get(Calendar.DAY_OF_MONTH);

                binding.tvFromDate.setText("" + sp_distributor_detail.getString("last_week_start", ""));
                binding.tvToDate.setText("" + sp_distributor_detail.getString("last_week_end", ""));

                binding.cbToday.setChecked(false);
                binding.cbThisWeek.setChecked(false);
                binding.cbThisMonth.setChecked(false);

            }
        });

        binding.cbThisMonth.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {

                binding.cbToday.setChecked(false);
                binding.cbLastWeek.setChecked(false);
                binding.cbThisWeek.setChecked(false);

                if (gDateTime.getMonth().length() == 2)
                    from_date = gDateTime.getYear() + "-" + gDateTime.getMonth() + "-01";
                else
                    from_date = gDateTime.getYear() + "-0" + gDateTime.getMonth() + "-01";
                to_date = gDateTime.getDateymd();

                binding.tvFromDate.setText("" + gDateTime.ymdTodmy(from_date));
                binding.tvToDate.setText("" + gDateTime.ymdTodmy(to_date));
            }
        });


        binding.btnApply.setOnClickListener(v -> {

            pdialog = new ProgressDialog(getActivity());
            pdialog.setMessage("Loading...");
            pdialog.setCancelable(false);
            pdialog.show();
            getBussinessSummery(gDateTime.dmyToymd(binding.tvFromDate.getText().toString()),
                    gDateTime.dmyToymd(binding.tvToDate.getText().toString()));
        });

        return root;
    }

    public void getBussinessSummery(String from_date, String to_date) {

        Call<String> call = api.bussiness_summery_by_dist(sp_distributor_detail.getString("distributor_id", ""),
                from_date, to_date);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "buss_summery_req...>" + call.request());
                Log.i(TAG, "buss_summery_res...>" + response.body());

                if (response.body() != null && response.body().contains("View Successfully")) {

                    if (pdialog.isShowing()) {
                        pdialog.dismiss();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response.body() + "");
                        JSONArray date_wise_arr = jsonObject.getJSONArray("date_wise_arr");
                        if (date_wise_arr.length() > 0) {

                            binding.nvMain.setVisibility(View.VISIBLE);
                            binding.imgEmpty.setVisibility(View.GONE);

                            arrayList_date_wise = new ArrayList<>();
                            for (int i = 0; i < date_wise_arr.length(); i++) {
                                JSONObject date_wise_obj = date_wise_arr.getJSONObject(i);
                                dateWiseSummeryModel = new DateWiseSummeryModel(
                                        date_wise_obj.getString("entry_date"),
                                        date_wise_obj.getString("pts_rs"),
                                        date_wise_obj.getString("ptr_rs"),
                                        date_wise_obj.getString("biz_rs"));
                                arrayList_date_wise.add(dateWiseSummeryModel);
                            }

                            dateWiseSummeryAdapter = new DateWiseSummeryAdapter(arrayList_date_wise, getActivity());
                            binding.rvDateWiseSummery.setLayoutManager(new LinearLayoutManager(getActivity(),
                                    RecyclerView.VERTICAL, false));
                            binding.rvDateWiseSummery.setAdapter(dateWiseSummeryAdapter);

                            JSONObject date_wise_tot_obj = jsonObject.getJSONObject("date_wise_tot_arr");
                            binding.tvDateWiseTotalBizValue.setText("" + date_wise_tot_obj.getString("tot_biz_rs") + " /-");
                            binding.tvDateWiseTotalPtrValue.setText("" + date_wise_tot_obj.getString("tot_ptr_rs") + " /-");
                            binding.tvDateWiseTotalPtsValue.setText("" + date_wise_tot_obj.getString("tot_pts_rs") + " /-");

                        } else {

                            binding.nvMain.setVisibility(View.GONE);
                            binding.imgEmpty.setVisibility(View.VISIBLE);
                        }

                        JSONArray prod_wise_arr = jsonObject.getJSONArray("prod_wise_arr");
                        if (prod_wise_arr.length() > 0) {
                            arrayList_prod_wise = new ArrayList<>();
                            for (int i = 0; i < prod_wise_arr.length(); i++) {
                                JSONObject prod_wise_obj = prod_wise_arr.getJSONObject(i);
                                prodWiseSummeryModel = new ProdWiseSummeryModel(
                                        prod_wise_obj.getString("prod_id"),
                                        prod_wise_obj.getString("prod_name"),
                                        prod_wise_obj.getString("prod_qty"),
                                        prod_wise_obj.getString("pts_rs"),
                                        prod_wise_obj.getString("ptr_rs"),
                                        prod_wise_obj.getString("biz_rs"));
                                arrayList_prod_wise.add(prodWiseSummeryModel);
                            }

                            prodWiseSummeryAdapter = new ProdWiseSummeryAdapter(arrayList_prod_wise, getActivity());
                            binding.rvProdWiseSummery.setLayoutManager(new LinearLayoutManager(getActivity(),
                                    RecyclerView.VERTICAL, false));
                            binding.rvProdWiseSummery.setAdapter(prodWiseSummeryAdapter);

                            JSONObject prod_wise_tot_obj = jsonObject.getJSONObject("prod_wise_tot_arr");
                            //binding.tvTitleTotalProdWiseQuantity.setText("" + prod_wise_tot_obj.getString("p_tot_qty"));
                            binding.tvProdWiseBizTotal.setText("" + prod_wise_tot_obj.getString("p_tot_biz_rs") + " /-");
                            binding.tvProdWisePtrTotal.setText("" + prod_wise_tot_obj.getString("p_tot_ptr_rs") + " /-");
                            binding.tvProdWisePtsTotal.setText("" + prod_wise_tot_obj.getString("p_tot_pts_rs") + " /-");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    binding.nvMain.setVisibility(View.GONE);

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

                Toast.makeText(getActivity(), "something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}