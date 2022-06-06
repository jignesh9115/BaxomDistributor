package com.jp.baxomdistributor.ui.distributor_scheme_stock;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.jp.baxomdistributor.Adapters.DistributorSchemeStockAdapter;
import com.jp.baxomdistributor.Adapters.DistributorStockAdapter;
import com.jp.baxomdistributor.Models.DistributorSchemeStockModel;
import com.jp.baxomdistributor.Models.DistributorStockModel;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.Api;
import com.jp.baxomdistributor.Utils.ApiClient;
import com.jp.baxomdistributor.databinding.FragmentDistSchemeStockBinding;
import com.jp.baxomdistributor.databinding.FragmentDistStockBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DistributorSchemeStockFragment extends Fragment {

    private DistributorSchemeStockViewModel distributorStockViewModel;
    private FragmentDistSchemeStockBinding binding;
    String TAG = getClass().getSimpleName();
    Api api;
    ArrayList<String> arrayList_distributor_id, arrayList_distributor_name;
    ArrayAdapter<String> arrayAdapter;

    ArrayList<DistributorSchemeStockModel> distributorSchemeStockModels;
    private DistributorSchemeStockAdapter distributorStockAdapter;

    AlertDialog ad;
    AlertDialog.Builder builder;

    ProgressDialog pdialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        distributorStockViewModel =
                new ViewModelProvider(this).get(DistributorSchemeStockViewModel.class);

        binding = FragmentDistSchemeStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        api = ApiClient.getClient(requireActivity()).create(Api.class);

        get_distributor_list();

        binding.spnDistNameStock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                get_distributor_stock_by_id(arrayList_distributor_id.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.tvRefresh.setOnClickListener(v -> update_distributor_stock());

        binding.btnUpdateStock.setOnClickListener(v -> {

            Log.i(TAG, "gson...>" + new Gson().toJson(distributorSchemeStockModels));

            JSONArray array = new JSONArray();

            for (int i = 0; i < distributorSchemeStockModels.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("scheme_id", distributorSchemeStockModels.get(i).getScheme_id());
                    object.put("scheme_update_qty", distributorSchemeStockModels.get(i).getScheme_update_qty());
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //update_distributor_stock(new Gson().toJson(distributorSchemeStockModels));
            update_distributor_stock(array);

        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pdialog != null && pdialog.isShowing())
            pdialog.dismiss();
    }

    public void get_distributor_list() {

        Call<String> call = api.get_distributor_list();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "get_dist_list_req...>" + call.request());
                Log.i(TAG, "get_dist_list_res...>" + response.body());

                if (response.body() != null && response.body().contains("View Successfully")) {
                    arrayList_distributor_id = new ArrayList<>();
                    arrayList_distributor_name = new ArrayList<>();

                    try {

                        JSONObject jsonObject = new JSONObject(response.body() + "");
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
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.i(TAG, "get_dist_list_error...>" + t);
            }
        });

    }

    public void get_distributor_stock_by_id(String dist_id) {

        Call<String> call = api.get_distributor_scehem_stock_by_id(dist_id);

        call.enqueue(new Callback<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "get_dist_stock_req...>" + call.request());
                Log.i(TAG, "get_dist_stock_res...>" + response.body());


                if (response.body() != null && response.body().contains("View Successfully")) {

                    distributorSchemeStockModels = new ArrayList<>();

                    try {

                        JSONObject jsonObject = new JSONObject(response.body() + "");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        binding.tvLastUpdates.setText("Last update on : " + jsonObject.getString("last_stock_updated"));
                        if (jsonArray.length() > 0) {
                            binding.rvDistSchemeStock.setVisibility(View.VISIBLE);
                            binding.btnUpdateStock.setVisibility(View.VISIBLE);
                            binding.imgEmptyStock.setVisibility(View.GONE);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject object = jsonArray.getJSONObject(i);
                                distributorSchemeStockModels.add(new DistributorSchemeStockModel(object.getString("dist_scheme_stock_id"),
                                        object.getString("dist_id"),
                                        object.getString("scheme_id"),
                                        object.getString("scheme_name"),
                                        object.getString("scheme_qty"),
                                        object.getString("scheme_qty"),
                                        object.getString("entry_date")));

                            }

                            distributorStockAdapter = new DistributorSchemeStockAdapter(distributorSchemeStockModels,
                                    requireActivity());
                            binding.rvDistSchemeStock.setLayoutManager(new LinearLayoutManager(getActivity(),
                                    LinearLayoutManager.VERTICAL, false));
                            binding.rvDistSchemeStock.setAdapter(distributorStockAdapter);


                        } else {
                            binding.rvDistSchemeStock.setVisibility(View.GONE);
                            binding.btnUpdateStock.setVisibility(View.GONE);
                            binding.imgEmptyStock.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.i(TAG, "get_dist_stock_error...>" + t);
            }
        });

    }

    public void update_distributor_stock() {

        pdialog = new ProgressDialog(requireActivity());
        pdialog.setMessage("updating stock...");
        pdialog.setCancelable(false);
        pdialog.show();

        Call<String> call = api.update_distributor_scheme_stock();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "update_dist_stock_req...>" + call.request());
                Log.i(TAG, "update_dist_stock_res...>" + response.body());

                if (pdialog.isShowing())
                    pdialog.dismiss();

                if (response.body() != null && response.body().contains("Stock Update Successfully")) {
                    showsuccessdialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.i(TAG, "update_dist_stock_error...>" + t);
            }
        });

    }

    public void update_distributor_stock(JSONArray update_prods) {

        pdialog = new ProgressDialog(requireActivity());
        pdialog.setMessage("updating stock...");
        pdialog.setCancelable(false);
        pdialog.show();

        Call<String> call = api.update_distributor_scheme_stock(
                arrayList_distributor_id.get(binding.spnDistNameStock.getSelectedItemPosition()),
                update_prods);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                Log.i(TAG, "update_dist_stock_req...>" + call.request());
                Log.i(TAG, "update_dist_stock_res...>" + response.body());

                if (pdialog.isShowing())
                    pdialog.dismiss();

                if (response.body() != null && response.body().contains("Stock Update Successfully")) {
                    showsuccessdialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.i(TAG, "update_dist_stock_error...>" + t);
            }
        });

    }


    public void showsuccessdialog() {
        builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Distributor Scheme Stock Update Successfully !");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> get_distributor_stock_by_id(
                arrayList_distributor_id.get(
                        binding.spnDistNameStock.getSelectedItemPosition())));

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

}