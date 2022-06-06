package com.jp.baxomdistributor.ui.mypurchaseorder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.jp.baxomdistributor.Activities.AddPurchaseOrder;
import com.jp.baxomdistributor.BuildConfig;
import com.jp.baxomdistributor.Models.CartoonQtyPOJO;
import com.jp.baxomdistributor.Models.PackingProdlistPOJO;
import com.jp.baxomdistributor.Models.PurchaseOrdersPOJO;
import com.jp.baxomdistributor.Models.TotalPurchaseOrdersPOJO;
import com.jp.baxomdistributor.Models.VerificationProdlistPOJO;
import com.jp.baxomdistributor.Models.ViewPurchaseOrderByIdPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.AppUtils;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.Utils.PdfUtils;
import com.jp.baxomdistributor.Utils.PrefManager;
import com.jp.baxomdistributor.databinding.DialogViewLrSymbolBinding;
import com.jp.baxomdistributor.databinding.EntityPurchaseOrderListBinding;
import com.jp.baxomdistributor.databinding.FragmentMypurchaseOrderBinding;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MyPurchaseOrderFragment extends Fragment {

    private MyPurchaseOrderViewModel homeViewModel;

    FragmentMypurchaseOrderBinding binding;

    String TAG = getClass().getSimpleName();
    String purchase_orders_url = "", purchase_orders_response = "", update_purchase_orders_url = "", update_purchase_orders_response = "", get_orders_url = "", get_orders_response = "", from_date = "", to_date = "";

    ArrayList<PurchaseOrdersPOJO> arrayList_purchase_order;
    PurchaseOrdersPOJO purchaseOrdersPOJO;

    ArrayList<String> arrayList_order_id;

    ArrayList<TotalPurchaseOrdersPOJO> arrayList_TotalPurchaseOrders;
    TotalPurchaseOrdersPOJO totalPurchaseOrdersPOJO;

    ArrayList<ViewPurchaseOrderByIdPOJO> arrayList_view_purchase_order_list;
    ViewPurchaseOrderByIdPOJO viewPurchaseOrderByIdPOJO;

    ArrayList<PackingProdlistPOJO> arrayList_packing_prod_list;
    PackingProdlistPOJO packingProdlistPOJO;

    ArrayList<VerificationProdlistPOJO> arrayList_verification_prod_list;
    VerificationProdlistPOJO verificationProdlistPOJO;

    ArrayList<String> order_status_list;

    GDateTime gDateTime = new GDateTime();

    DatePickerDialog dp;
    Calendar cal;
    int m, y, d, no_of_sleep = 0;

    Snackbar snackbar;

    ProgressDialog pdialog;

    AlertDialog ad;
    AlertDialog.Builder builder;

    String issubmit, isverify, isauthentication, ispacking, isshipping, isshipped, isupdateshop, distributor_id = "";

    SharedPreferences sp_update, sp_login, sp_distributor_detail, sp, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanSuchnaList;

    boolean isUpdate = false;

    ArrayList<CartoonQtyPOJO> arrayList_cartoon_qty;
    ArrayList<Integer> arrayList_qty;

    String path = "", image_name = "", upload_url = "", purchase_id = "", no_of_article = "";
    File imgfile;

    PrefManager prefManager;
    private Uri uri;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(MyPurchaseOrderViewModel.class);
        binding = FragmentMypurchaseOrderBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        prefManager = new PrefManager(requireActivity(), "sp_Image_path");

        sp_update = requireActivity().getSharedPreferences("update_data", Context.MODE_PRIVATE);
        sp_login = requireActivity().getSharedPreferences(" login_detail", Context.MODE_PRIVATE);
        sp_distributor_detail = requireActivity().getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);

        distributor_id = sp_distributor_detail.getString("distributor_id", null);

        Log.i(TAG, "distributor_id=>" + distributor_id);

        sp_multi_lang = requireActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getActivity());

        setLanguage(sp_multi_lang.getString("lang", ""));

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                getActivity(), setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language.getData();


        SharedPreferences.Editor editor = sp_update.edit();
        editor.putBoolean("isUpdate", false);
        editor.apply();

        issubmit = sp_login.getString("issubmit", "");
        isverify = sp_login.getString("isverify", "");
        isauthentication = sp_login.getString("isauthentication", "");
        ispacking = sp_login.getString("ispacking", "");
        isshipping = sp_login.getString("isshipping", "");
        isshipped = sp_login.getString("isshipped", "");
        isupdateshop = sp_login.getString("isupdateshop", "");

        if (path != null) {

            imgfile = new File(path);
            //if (imgfile.exists()) {
            //Bitmap myBitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath());
            //binding.imgAddShopPhoto.setImageBitmap(rotateBitmap(myBitmap, 90));
            //binding.imgAddShopPhoto.setImageBitmap(myBitmap);
           /* Uri uri = Uri.fromFile(imgfile);
            binding.imgAddShopPhoto.setImageURI(uri);*/
            //}
            image_name = imgfile.getName();
        }


        arrayList_order_id = new ArrayList<>();
        order_status_list = new ArrayList<>();
        arrayList_cartoon_qty = new ArrayList<>();

        from_date = gDateTime.getYear() + "-" + gDateTime.getMonth() + "-01";
        to_date = gDateTime.getDateymd();

        order_status_list = new ArrayList<>();

        purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
        new getPurchaseOrdersTask().execute(purchase_orders_url);


        if (ispacking.equalsIgnoreCase("1")) {

            binding.btnGeneratePdfForAll.setVisibility(View.VISIBLE);

        } else {

            binding.btnGeneratePdfForAll.setVisibility(View.GONE);

        }


        binding.tvFromDatePurchaseOrder.setText("" + gDateTime.ymdTodmy(from_date));
        binding.tvFromDatePurchaseOrder.setOnClickListener(v -> {

            y = Integer.parseInt(gDateTime.getYear());
            m = Integer.parseInt(gDateTime.getMonth()) - 1;
            d = Integer.parseInt(gDateTime.getDay());

            dp = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                @SuppressLint("SimpleDateFormat") DateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                from_date = df.format(cal.getTime());
                binding.tvFromDatePurchaseOrder.setText("" + dff.format(cal.getTime()));
                binding.cbThisMonthPurchaseOrder.setChecked(false);

                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                arrayList_purchase_order = new ArrayList<>();
                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            }, y, m, d);
            dp.show();

        });


        binding.tvToDatePurchaseOrder.setText("" + gDateTime.ymdTodmy(to_date));
        binding.tvToDatePurchaseOrder.setOnClickListener(v -> {

            y = Integer.parseInt(gDateTime.getYear());
            m = Integer.parseInt(gDateTime.getMonth()) - 1;
            d = Integer.parseInt(gDateTime.getDay());

            dp = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                @SuppressLint("SimpleDateFormat") DateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                to_date = df.format(cal.getTime());
                binding.tvToDatePurchaseOrder.setText("" + dff.format(cal.getTime()));
                binding.cbThisMonthPurchaseOrder.setChecked(false);

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());

                arrayList_purchase_order = new ArrayList<>();
                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);


            }, y, m, d);
            dp.show();

        });


        binding.cbThisWeekPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbThisWeekPurchaseOrder.isChecked()) {

                Calendar c = GregorianCalendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                from_date = df.format(c.getTime());
                c.add(Calendar.DATE, 6);
                to_date = df.format(c.getTime());

                binding.tvFromDatePurchaseOrder.setText("" + gDateTime.ymdTodmy(from_date));
                binding.tvToDatePurchaseOrder.setText("" + gDateTime.ymdTodmy(to_date));
                binding.cbThisMonthPurchaseOrder.setChecked(false);

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            }

        });

        binding.cbThisMonthPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbThisMonthPurchaseOrder.isChecked()) {

                if (gDateTime.getMonth().length() == 2)
                    from_date = gDateTime.getYear() + "-" + gDateTime.getMonth() + "-01";
                else
                    from_date = gDateTime.getYear() + "-0" + gDateTime.getMonth() + "-01";
                to_date = gDateTime.getDateymd();

                binding.tvFromDatePurchaseOrder.setText("" + gDateTime.ymdTodmy(from_date));
                binding.tvToDatePurchaseOrder.setText("" + gDateTime.ymdTodmy(to_date));
                binding.cbThisWeekPurchaseOrder.setChecked(false);

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);
            }

        });

        binding.cbSubmitedPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbSubmitedPurchaseOrder.isChecked()) {

                order_status_list.add("1");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            } else {
                order_status_list.remove("1");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);
            }

        });


        binding.cbVerificationPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbVerificationPurchaseOrder.isChecked()) {

                order_status_list.add("2");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            } else {
                order_status_list.remove("2");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);
            }

        });

        binding.cbAuthenticationPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbAuthenticationPurchaseOrder.isChecked()) {

                order_status_list.add("3");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            } else {

                order_status_list.remove("3");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);
            }

        });


        binding.cbPackingPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbPackingPurchaseOrder.isChecked()) {

                order_status_list.add("4");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            } else {

                order_status_list.remove("4");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);
            }

        });

        binding.cbShippingPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbShippingPurchaseOrder.isChecked()) {

                order_status_list.add("5");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            } else {

                order_status_list.remove("5");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);
            }

        });

        binding.cbShippingPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbShippingPurchaseOrder.isChecked()) {

                order_status_list.add("5");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            } else {

                order_status_list.remove("5");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);
            }

        });


        binding.cbShippedPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (binding.cbShippedPurchaseOrder.isChecked()) {

                order_status_list.add("6");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            } else {

                order_status_list.remove("6");
                Log.i(TAG, "orderStatusList=>" + orderStatusList());

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            }

        });

        //createPDF();

        binding.cbSelectAllPurchaseOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

           /* if (binding.cbSelectAllPurchaseOrder.isChecked()) {

                binding.cbSubmitedPurchaseOrder.setChecked(true);
                binding.cbVerificationPurchaseOrder.setChecked(true);
                binding.cbAuthenticationPurchaseOrder.setChecked(true);
                binding.cbPackingPurchaseOrder.setChecked(true);
                binding.cbShippingPurchaseOrder.setChecked(true);
                binding.cbShippedPurchaseOrder.setChecked(true);

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=1,2,3,4,5,6&from_date=" + from_date + "&to_date=" + to_date;
                new getPurchaseOrdersTask().execute(purchase_orders_url);


            } else {

                binding.cbSubmitedPurchaseOrder.setChecked(false);
                binding.cbVerificationPurchaseOrder.setChecked(false);
                binding.cbAuthenticationPurchaseOrder.setChecked(false);
                binding.cbPackingPurchaseOrder.setChecked(false);
                binding.cbShippingPurchaseOrder.setChecked(false);
                binding.cbShippedPurchaseOrder.setChecked(false);

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=1&from_date=" + from_date + "&to_date=" + to_date;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            }*/

        });

        binding.fabAddPurchaseOrder.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), AddPurchaseOrder.class);
            intent.putExtra("purchase_id", "");
            startActivity(intent);

            SharedPreferences.Editor editor1 = sp_update.edit();
            editor1.putBoolean("isUpdate", false);
            editor1.apply();

            Log.i(TAG, "isUpdate=>" + isUpdate);

        });

        binding.btnGeneratePdfForAll.setOnClickListener(v -> {


            arrayList_qty = new ArrayList<>();

            JSONObject attendance_data = new JSONObject();// main object
            JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

            for (int i = 0; i < arrayList_cartoon_qty.size(); i++) {
                JSONObject jGroup = new JSONObject();// /sub Object
                try {

                    if (arrayList_cartoon_qty.get(i).getQty() > 0) {
                        jGroup.put("P_id", arrayList_cartoon_qty.get(i).getP_id());
                        jGroup.put("cartoon_qty", arrayList_cartoon_qty.get(i).getQty());
                        jArray.put(jGroup);
                        arrayList_qty.add(arrayList_cartoon_qty.get(i).getQty());
                    }

                    attendance_data.put("cartoon_qty", jArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "jArray=>" + jArray);

            if (jArray.length() > 0) {

                String combine_order_id = "";
                for (int i = 0; i < arrayList_order_id.size(); i++) {

                    if (arrayList_cartoon_qty.get(i).getQty() > 0) {

                        if (arrayList_order_id.size() > 1) {
                            combine_order_id = combine_order_id + arrayList_order_id.get(i) + ",";
                        } else {
                            combine_order_id = combine_order_id + arrayList_order_id.get(i);
                        }
                    }
                }

                Log.i(TAG, "combine_order_id=>" + combine_order_id.replaceAll(",$", ""));


                //Toast.makeText(context, "generate"+combine_order_id.replaceAll(",$", ""), Toast.LENGTH_SHORT).show();
                get_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrderByOrderIds.php?purchase_id=" + combine_order_id.replaceAll(",$", "");
                new getOrdersDetailTask().execute(get_orders_url);

            } else {

                Toast.makeText(getActivity(), commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();
            }


        });

        return root;
    }


    //==============================get purchase order list task starts ========================

    public class getPurchaseOrdersTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(getActivity());
            pdialog.setMessage(commanSuchnaList.getArrayList().get(2) + "");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected Void doInBackground(String... strings) {

            //purchase_orders_url = "http://192.168.43.151/Baxom/API/viewPurchaseOrder.php";
            Log.i("purchase_orders_url=>", purchase_orders_url + "");

            HttpHandler httpHandler = new HttpHandler();
            purchase_orders_response = httpHandler.makeServiceCall(purchase_orders_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("purchase_orders_res=>", purchase_orders_response + "");

                getPurchaseOrders();

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }
    }

    private void getPurchaseOrders() {

        arrayList_purchase_order = new ArrayList<>();
        arrayList_order_id = new ArrayList<>();
        arrayList_cartoon_qty = new ArrayList<>();
        path = "";

        try {

            JSONObject jsonObject = new JSONObject(purchase_orders_response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                purchaseOrdersPOJO = new PurchaseOrdersPOJO(object.getString("purchase_id"),
                        object.getString("purchase_order_status"),
                        object.getString("order_date"),
                        object.getString("shipping_date"),
                        object.getString("order_amount"),
                        object.getString("LR_symbol"),
                        object.getString("no_of_article"),
                        object.getString("billing_amount"),
                        object.getString("transporter_name"),
                        object.getString("transporter_contact1"),
                        object.getString("transporter_contact2"),
                        object.getString("dist_id"),
                        object.getString("dist_name"),
                        object.getString("dist_phone"),
                        object.getString("dist_town"));

                arrayList_purchase_order.add(purchaseOrdersPOJO);
            }

            PurchaseOrderAdapter purchaseOrderAdapter = new PurchaseOrderAdapter(arrayList_purchase_order, getActivity());
            binding.rvPurchaseOrderList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            binding.rvPurchaseOrderList.setAdapter(purchaseOrderAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //==============================get purchase order list task ends ==========================

    //==============================purchase order adapter code starts =========================

    public class PurchaseOrderAdapter extends RecyclerView.Adapter<PurchaseOrderAdapter.MyHolder> {

        ArrayList<PurchaseOrdersPOJO> arrayList_purchase_order;
        Context context;

        boolean isSelectedAll;
        int start = 1;
        String shipping_date = "", no_of_cartoon_sleep = "", artical = "", billing_rs = "";


        public PurchaseOrderAdapter(ArrayList<PurchaseOrdersPOJO> arrayList_purchase_order, Context context) {
            this.arrayList_purchase_order = arrayList_purchase_order;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(EntityPurchaseOrderListBinding.inflate(LayoutInflater.from(context)));
        }

        @SuppressLint("NotifyDataSetChanged")
        public void selectAll() {
            isSelectedAll = true;
            notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        public boolean ischeckedAll() {
            isSelectedAll = true;
            notifyDataSetChanged();
            return true;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void UnselectAll() {
            isSelectedAll = false;
            notifyDataSetChanged();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {

            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    getActivity(), setLangEntity(sp_multi_lang.getString("lang", "")));
            Language.CommanList commanList = language.getData();
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                holder.binding.tvShippingDateTitle.setText("" + commanList.getArrayList().get(0));
                holder.binding.tvTransporterTitle.setText("" + commanList.getArrayList().get(1));
                holder.binding.tvContact1Title.setText("" + commanList.getArrayList().get(2));
                holder.binding.tvContact2Title.setText("" + commanList.getArrayList().get(3));
                holder.binding.tvArticleTitle.setText("" + commanList.getArrayList().get(4));
                holder.binding.tvOrderrsTitle.setText("" + commanList.getArrayList().get(5));
                holder.binding.tvBillingrsTitle.setText("" + commanList.getArrayList().get(6));
                holder.binding.btnGeneratePdf.setText("" + commanList.getArrayList().get(9));

                holder.binding.tvDistNamePurchaseOrder.setText("" + arrayList_purchase_order.get(position).getDist_name());
                holder.binding.tvOrderDatePurchaseOrder.setText(commanList.getArrayList().get(7) +
                        gDateTime.ymdTodmy(arrayList_purchase_order.get(position).getOrder_date().substring(0, 10)) + " " +
                        arrayList_purchase_order.get(position).getOrder_date().substring(11));
            }
            if (Integer.parseInt(arrayList_purchase_order.get(position).getPurchase_order_status()) > 4) {

                holder.binding.tvShippingDatePurchaseOrder.setCompoundDrawables(null, null, null, null);

                holder.binding.tvShippingDatePurchaseOrder.setText(
                        gDateTime.ymdTodmy(arrayList_purchase_order.get(position).getShipping_date().substring(0, 10)) + " " +
                                arrayList_purchase_order.get(position).getShipping_date().substring(11));
            } else
                holder.binding.tvShippingDatePurchaseOrder.setText("" + gDateTime.getDatedmy());


            holder.binding.edtTransporterNamePurchaseOrder.setText("" + arrayList_purchase_order.get(position).getTransporter_name());
            holder.binding.edtTransporterContact1PurchaseOrder.setText("" + arrayList_purchase_order.get(position).getTransporter_contact1());
            holder.binding.edtTransporterContact2PurchaseOrder.setText("" + arrayList_purchase_order.get(position).getTransporter_contact2());
            //holder.binding.edtCartoonQtyPurchaseOrder.setText("" + arrayList_purchase_order.get(position).getQty_cartoon_sleep());

            arrayList_cartoon_qty.add(new CartoonQtyPOJO(Integer.parseInt(arrayList_purchase_order.get(position).getPurchase_id()), 0));
            arrayList_order_id.add(arrayList_purchase_order.get(position).getPurchase_id());

            if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("1")) {
                holder.binding.tvOrderStatusPurchaseOrder.setText(commanList.getArrayList().get(8) + " Submit");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("2")) {
                holder.binding.tvOrderStatusPurchaseOrder.setText(commanList.getArrayList().get(8) + " Verification");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("3")) {
                holder.binding.tvOrderStatusPurchaseOrder.setText(commanList.getArrayList().get(8) + " Authentication");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("4")) {
                holder.binding.tvOrderStatusPurchaseOrder.setText(commanList.getArrayList().get(8) + " Packing");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("5")) {
                holder.binding.tvOrderStatusPurchaseOrder.setText(commanList.getArrayList().get(8) + " Shipping");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("6")) {
                holder.binding.tvOrderStatusPurchaseOrder.setText(commanList.getArrayList().get(8) + " Shipped");
            }


            if (isshipping.equalsIgnoreCase("1")) {

                if (Integer.parseInt(arrayList_purchase_order.get(position).getPurchase_order_status()) >= 4)
                    holder.binding.llShippingDate.setVisibility(View.VISIBLE);
                else
                    holder.binding.llShippingDate.setVisibility(View.GONE);
            } else
                holder.binding.llShippingDate.setVisibility(View.GONE);


           /* if (arrayList_purchase_order.get(position).getShipping_date().equalsIgnoreCase("0000-00-00")) {
                holder.binding.tvShippingDatePurchaseOrder.setText("");
            } else {
                holder.binding.tvShippingDatePurchaseOrder.setText("" + gDateTime.ymdTodmy(arrayList_purchase_order.get(position).getShipping_date()));
            }*/

            if (arrayList_purchase_order.get(position).getNo_of_article().length() > 1)
                holder.binding.edtNoofarticlePurchaseOrder.setText("" + arrayList_purchase_order.get(position).getNo_of_article());

            if (arrayList_purchase_order.get(position).getBilling_amount().length() > 1)
                holder.binding.edtBillingAmount.setText("₹ " + arrayList_purchase_order.get(position).getBilling_amount());

            //holder.binding.tvViewLrPurchaseOrder.setText("" + arrayList_purchase_order.get(position).getLR_symbol());
            holder.binding.tvBillingAmountPurchaseOrder.setText("₹ " + arrayList_purchase_order.get(position).getOrder_amount());


            if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("4")) {

                holder.binding.tvShippingDatePurchaseOrder.setEnabled(true);
                //holder.binding.tvLrSymbol.setEnabled(true);
                holder.binding.edtNoofarticlePurchaseOrder.setEnabled(true);
                holder.binding.edtBillingAmount.setEnabled(true);
            }


            if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("3")
                    || arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("4")
                    || arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("5")
                    || arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("6")) {

                if (ispacking.equalsIgnoreCase("1")) {

                    holder.binding.edtCartoonQtyPurchaseOrder.setEnabled(true);
                    holder.binding.btnGeneratePdf.setVisibility(View.VISIBLE);
                    holder.binding.imgProductPlusQtyAddPurchaseOrder.setEnabled(true);
                    holder.binding.imgProductMinusQtyAddPurchaseOrder.setEnabled(true);

                } else {

                    holder.binding.edtCartoonQtyPurchaseOrder.setEnabled(false);
                    holder.binding.btnGeneratePdf.setVisibility(View.GONE);
                    holder.binding.imgProductPlusQtyAddPurchaseOrder.setEnabled(false);
                    holder.binding.imgProductMinusQtyAddPurchaseOrder.setEnabled(false);

                }


            } else {

                holder.binding.edtCartoonQtyPurchaseOrder.setEnabled(false);
                holder.binding.btnGeneratePdf.setVisibility(View.GONE);
                holder.binding.imgProductPlusQtyAddPurchaseOrder.setEnabled(false);
                holder.binding.imgProductMinusQtyAddPurchaseOrder.setEnabled(false);
            }


            if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("1")) {
                holder.binding.tvNextOrderStatusPurchaseOrder.setText("Verification");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("2")) {
                holder.binding.tvNextOrderStatusPurchaseOrder.setText("Authentication");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("3")) {
                holder.binding.tvNextOrderStatusPurchaseOrder.setText("Packing");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("4")) {
                holder.binding.tvNextOrderStatusPurchaseOrder.setText("Shipping");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("5")) {
                holder.binding.tvNextOrderStatusPurchaseOrder.setText("Shipped");
            } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("6")) {
                holder.binding.tvNextOrderStatusPurchaseOrder.setText("Shipped");
            }

            shipping_date = gDateTime.getDateymd();

            holder.binding.tvShippingDatePurchaseOrder.setOnClickListener(v -> {
                y = Integer.parseInt(gDateTime.getYear());
                m = Integer.parseInt(gDateTime.getMonth()) - 1;
                d = Integer.parseInt(gDateTime.getDay());

                dp = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                    cal = Calendar.getInstance();
                    cal.set(year, month, dayOfMonth);
                    @SuppressLint("SimpleDateFormat") DateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    shipping_date = df.format(cal.getTime());
                    holder.binding.tvShippingDatePurchaseOrder.setText("" + dff.format(cal.getTime()));

                }, y, m, d);
                dp.show();


            });

            holder.binding.tvNextOrderStatusPurchaseOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("3")) {

                        if (ispacking.equalsIgnoreCase("1")) {

                           /* no_of_cartoon_sleep = holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().trim();

                            if (no_of_cartoon_sleep.isEmpty() || no_of_cartoon_sleep.equalsIgnoreCase("0")) {

                                Toast.makeText(context, "please enter cartoon qty", Toast.LENGTH_SHORT).show();

                            } else {*/

                            update_purchase_orders_url = getString(R.string.Base_URL) + "updatePurchaseOrder.php?purchase_id=" + arrayList_purchase_order.get(position).getPurchase_id() + "&purchase_order_status=4&operation_status=packing&shipping_date=&order_amount=&LR_symbol=&no_of_article=";
                            new updatePurchaseOrderTask().execute(update_purchase_orders_url.replace("%20", " "));

                            //}

                        } else
                            Toast.makeText(context, commanSuchnaList.getArrayList().get(5) + "", Toast.LENGTH_SHORT).show();

                    } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("4")) {

                        if (isshipping.equalsIgnoreCase("1")) {

                            artical = holder.binding.edtNoofarticlePurchaseOrder.getText().toString().trim();
                            billing_rs = holder.binding.edtBillingAmount.getText().toString().trim();

                            if (artical.isEmpty() || artical.equalsIgnoreCase("0")) {

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(6) + "", Toast.LENGTH_SHORT).show();

                            } else if (path.isEmpty() || path.equalsIgnoreCase(" ")) {

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(7) + "", Toast.LENGTH_SHORT).show();

                            } else if (billing_rs.isEmpty() || billing_rs.equalsIgnoreCase(" ")) {

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

                            } else {

                                Log.i(TAG, "path=>" + path);

                                if (Double.parseDouble(billing_rs) < (int) (Double.parseDouble(arrayList_purchase_order.get(position).getOrder_amount()) - 1)) {

                                    Toast.makeText(context, commanSuchnaList.getArrayList().get(9) + "", Toast.LENGTH_SHORT).show();

                                } else {

                                    Log.i(TAG, "billing_rs=>" + billing_rs);
                                    new updateShippingOrderTask().execute(arrayList_purchase_order.get(position).getPurchase_id(), artical, shipping_date, billing_rs);
                                }
                                //update_purchase_orders_url = getString(R.string.Base_URL) + "updatePurchaseOrder.php?purchase_id=" + arrayList_purchase_order.get(position).getPurchase_id() + "&dist_id=&purchase_order_status=5&shipping_date=&LR_symbol=" + LR_symbol + "&no_of_article=" + artical + "&transporter_name=&transporter_contact1=&transporter_contact2=&istransporterData=1";
                                //new updatePurchaseOrderTask().execute(update_purchase_orders_url.replace("%20", " "));
                            }

                        } else
                            Toast.makeText(context, commanSuchnaList.getArrayList().get(10) + "", Toast.LENGTH_SHORT).show();


                    } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("5")) {

                        if (isshipped.equalsIgnoreCase("1")) {

                            update_purchase_orders_url = getString(R.string.Base_URL) + "updatePurchaseOrder.php?purchase_id=" + arrayList_purchase_order.get(position).getPurchase_id() + "&purchase_order_status=6&operation_status=shipped&shipping_date=&order_amount=&LR_symbol=&no_of_article=";
                            new updatePurchaseOrderTask().execute(update_purchase_orders_url.replace("%20", " "));

                        } else
                            Toast.makeText(context, commanSuchnaList.getArrayList().get(5) + "", Toast.LENGTH_SHORT).show();

                    } else if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("6")) {
                        Toast.makeText(context, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            holder.binding.llOrderDetail.setOnClickListener(v -> {

                Intent intent = new Intent(getActivity(), AddPurchaseOrder.class);
                intent.putExtra("purchase_id", arrayList_purchase_order.get(position).getPurchase_id());
                startActivity(intent);

                SharedPreferences.Editor editor = sp_update.edit();
                editor.putBoolean("isUpdate", false);
                editor.apply();

                Log.i(TAG, "isUpdate=>" + isUpdate);

            });

            holder.binding.edtCartoonQtyPurchaseOrder.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (ispacking.equalsIgnoreCase("1")) {

                        //holder.binding.edtCartoonQtyPurchaseOrder.requestFocus();
                        if (holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().trim().isEmpty()) {
                            start = 0;
                        } else {
                            start = Integer.parseInt(holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().trim());
                        }


                        arrayList_cartoon_qty.set(position,
                                new CartoonQtyPOJO(Integer.parseInt(arrayList_purchase_order.get(position).getPurchase_id()),
                                        start));
                    } else {
                        Toast.makeText(context, commanSuchnaList.getArrayList().get(10) + "", Toast.LENGTH_SHORT).show();
                        holder.binding.edtCartoonQtyPurchaseOrder.setText("0");
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            holder.binding.imgProductPlusQtyAddPurchaseOrder.setOnClickListener(v -> {

                if (ispacking.equalsIgnoreCase("1")) {

                    holder.binding.edtCartoonQtyPurchaseOrder.requestFocus();
                    if (holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().trim().isEmpty()) {
                        start = 0;
                    } else {
                        start = Integer.parseInt(holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().trim());
                    }

                    if (start < 0) {
                        holder.binding.edtCartoonQtyPurchaseOrder.setText("" + start);
                    } else {
                        if (start >= 0) {

                            start++;
                            holder.binding.edtCartoonQtyPurchaseOrder.setText("" + start);
                        }
                    }

                    arrayList_cartoon_qty.set(position,
                            new CartoonQtyPOJO(Integer.parseInt(arrayList_purchase_order.get(position).getPurchase_id()),
                                    start));
                } else
                    Toast.makeText(context, commanSuchnaList.getArrayList().get(10) + "", Toast.LENGTH_SHORT).show();
            });

            holder.binding.imgProductMinusQtyAddPurchaseOrder.setOnClickListener(v -> {

                if (ispacking.equalsIgnoreCase("1")) {

                    holder.binding.edtCartoonQtyPurchaseOrder.requestFocus();

                    if (holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().trim().isEmpty()) {
                        start = 0;
                    } else {
                        start = Integer.parseInt(holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().trim());
                    }

                    if (start > 0 && start != 0 && start != -1) {
                        start--;
                        holder.binding.edtCartoonQtyPurchaseOrder.setText("" + start);


                    } else {
                        holder.binding.edtCartoonQtyPurchaseOrder.setText("0");
                        //arrayList_order_product_list.remove(arrayList_order_product_list.get(position));
                    }

                    arrayList_cartoon_qty.set(position,
                            new CartoonQtyPOJO(Integer.parseInt(arrayList_purchase_order.get(position).getPurchase_id()),
                                    start));
                } else
                    Toast.makeText(context, commanSuchnaList.getArrayList().get(10) + "", Toast.LENGTH_SHORT).show();

            });

            holder.binding.btnGeneratePdf.setOnClickListener(v -> {

                if (ispacking.equalsIgnoreCase("1")) {

                    if (holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().isEmpty()) {

                        Toast.makeText(getActivity(), commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();

                    } else {

                        no_of_cartoon_sleep = holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().trim();
                        no_of_sleep = Integer.parseInt(holder.binding.edtCartoonQtyPurchaseOrder.getText().toString().trim());

                        if (no_of_cartoon_sleep.isEmpty() || no_of_cartoon_sleep.equalsIgnoreCase("0")) {

                            Toast.makeText(context, commanSuchnaList.getArrayList().get(4) + "", Toast.LENGTH_SHORT).show();

                        } else {

                            arrayList_qty = new ArrayList<>();

                            arrayList_qty.add(no_of_sleep);

                            get_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrderByOrderIds.php?purchase_id=" + arrayList_purchase_order.get(position).getPurchase_id();
                            new getOrdersDetailTask().execute(get_orders_url);
                        }
                    }

                } else
                    Toast.makeText(context, commanSuchnaList.getArrayList().get(5) + "", Toast.LENGTH_SHORT).show();
            });

            holder.binding.tvLrSymbol.setOnClickListener(v -> {


                if (arrayList_purchase_order.get(position).getLR_symbol().isEmpty()) {

                    if (arrayList_purchase_order.get(position).getPurchase_order_status().equalsIgnoreCase("4")) {

                        /*Intent intent1 = new Intent(getActivity(), CameraActivity.class);
                        intent1.putExtra("isLR_SYMBOL", "yes");
                        startActivity(intent1);
                        //getActivity().finish();
                        prefManager.setPrefString("image_path", "");*/

                        AppUtils.openCameraIntent(requireActivity());

                    } else {

                        Toast.makeText(context, commanSuchnaList.getArrayList().get(12) + "", Toast.LENGTH_SHORT).show();

                    }


                } else {

                    DialogViewLrSymbolBinding binding1;
                    builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme);
                    binding1 = DialogViewLrSymbolBinding.inflate(getLayoutInflater());
                    builder.setView(binding1.getRoot());

                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.imagenotfoundicon);

                    Glide.with(getActivity()).load(arrayList_purchase_order.get(position).getLR_symbol() + "")
                            .apply(options)
                            .into(binding1.imgShowShopPhoto);

                    binding1.imgCancelLrsymbol.setOnClickListener(v1 -> ad.dismiss());

                    ad = builder.create();
                    ad.show();

                    Window window = ad.getWindow();
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.height = 1500;
                    window.setAttributes(layoutParams);

                }


            });


        }

        @Override
        public int getItemCount() {
            return arrayList_purchase_order.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            EntityPurchaseOrderListBinding binding;

            public MyHolder(EntityPurchaseOrderListBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

    }

    //==============================purchase order adapter code ends ===========================

    //http://localhost/Baxom/API/updatePurchaseOrder.php?
    // purchase_id=23&
    // dist_id=&
    // purchase_order_status=&
    // shipping_date=&
    // LR_symbol=&
    // no_of_article=&
    // billing_amount=&
    // transporter_name=&
    // transporter_contact1=&
    // transporter_contact2=

    //===============================update order task starts ==================================

    public class updatePurchaseOrderTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(getActivity());
            pdialog.setMessage(commanSuchnaList.getArrayList().get(2) + "");
            pdialog.setCancelable(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i("update_orders_url=>", update_purchase_orders_url + "");

            HttpHandler httpHandler = new HttpHandler();
            update_purchase_orders_response = httpHandler.makeServiceCall(update_purchase_orders_url + "");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("update_orders_res=>", update_purchase_orders_response + "");

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

                if (update_purchase_orders_response.contains("updated Successfully")) {

                    showDialog();

                }

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    //===============================update order task ends ====================================

    public void showDialog() {

        builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(commanSuchnaList.getArrayList().get(0) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(1) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
                to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

                purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
                new getPurchaseOrdersTask().execute(purchase_orders_url);

            }
        });

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }


    //==========================get pdf data code starts ========================================

    public class getOrdersDetailTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(getActivity());
            pdialog.setCancelable(false);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(3) + "");
            pdialog.show();

        }

        @Override
        protected Void doInBackground(String... strings) {

            //get_orders_url = "http://192.168.43.151/Baxom/API/viewPurchaseOrderByOrderIds.php?purchase_id=1";
            Log.i("get_orders_url=>", get_orders_url + "");

            HttpHandler httpHandler = new HttpHandler();
            get_orders_response = httpHandler.makeServiceCall(get_orders_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("get_orders_response=>", get_orders_response + "");

                getOrdersDetail();

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

                binding.cbSelectAllPurchaseOrder.setChecked(false);
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getOrdersDetail() {


        totalPurchaseOrdersPOJO = new TotalPurchaseOrdersPOJO();
        arrayList_TotalPurchaseOrders = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(get_orders_response + "");
            JSONArray data_array = jsonObject.getJSONArray("data");

            for (int i = 0; i < data_array.length(); i++) {

                viewPurchaseOrderByIdPOJO = new ViewPurchaseOrderByIdPOJO();
                arrayList_view_purchase_order_list = new ArrayList<>();

                JSONObject data_object = data_array.getJSONObject(i);
                JSONObject cartoon_sleep_object = data_object.getJSONObject("cartoon_sleep");

                viewPurchaseOrderByIdPOJO.setShipping_code(cartoon_sleep_object.getString("shipping_code"));
                viewPurchaseOrderByIdPOJO.setName_hindi(cartoon_sleep_object.getString("name_hindi"));
                viewPurchaseOrderByIdPOJO.setContact_no(cartoon_sleep_object.getString("contact_no"));
                viewPurchaseOrderByIdPOJO.setDist_town(cartoon_sleep_object.getString("dist_town"));

                JSONObject dist_details_object = data_object.getJSONObject("dist_details");

                viewPurchaseOrderByIdPOJO.setOrder_no(dist_details_object.getString("order_no"));
                viewPurchaseOrderByIdPOJO.setOrder_date(dist_details_object.getString("order_date"));
                viewPurchaseOrderByIdPOJO.setInvoice_type(dist_details_object.getString("invoice_type"));
                viewPurchaseOrderByIdPOJO.setOrder_amount(dist_details_object.getString("order_amount"));
                viewPurchaseOrderByIdPOJO.setStock_distributor_name(dist_details_object.getString("stock_distributor_name"));
                viewPurchaseOrderByIdPOJO.setTax_type(dist_details_object.getString("tax_type"));
                viewPurchaseOrderByIdPOJO.setTransporter_name(dist_details_object.getString("transporter_name"));
                viewPurchaseOrderByIdPOJO.setEway_bill(dist_details_object.getString("eway_bill"));

                JSONObject packing_sleep_object = data_object.getJSONObject("packing_sleep");

                JSONArray packing_prod_detail_array = packing_sleep_object.getJSONArray("prod_detail");

                arrayList_packing_prod_list = new ArrayList<>();

                for (int j = 0; j < packing_prod_detail_array.length(); j++) {

                    JSONObject prod_detail_object = packing_prod_detail_array.getJSONObject(j);

                    packingProdlistPOJO = new PackingProdlistPOJO(prod_detail_object.getString("product_id"),
                            prod_detail_object.getString("dispatch_code"),
                            prod_detail_object.getString("prod_name"),
                            prod_detail_object.getString("prod_name_hindi"),
                            prod_detail_object.getString("product_qty"),
                            prod_detail_object.getString("purchase_rate"),
                            prod_detail_object.getString("basic_rate"),
                            prod_detail_object.getString("gst"),
                            prod_detail_object.getString("total_b4_gst"));

                    arrayList_packing_prod_list.add(packingProdlistPOJO);

                }

                viewPurchaseOrderByIdPOJO.setArrayList_packing_prod_list(arrayList_packing_prod_list);

                JSONObject verification_sleep_object = data_object.getJSONObject("verification_sleep");

                JSONArray verification_prod_detail_array = verification_sleep_object.getJSONArray("prod_detail");

                arrayList_verification_prod_list = new ArrayList<>();

                for (int j = 0; j < verification_prod_detail_array.length(); j++) {

                    JSONObject prod_detail_object = verification_prod_detail_array.getJSONObject(j);

                    verificationProdlistPOJO = new VerificationProdlistPOJO(prod_detail_object.getString("product_id"),
                            prod_detail_object.getString("dispatch_code"),
                            prod_detail_object.getString("prod_name"),
                            prod_detail_object.getString("prod_name_hindi"),
                            prod_detail_object.getString("product_qty"));

                    arrayList_verification_prod_list.add(verificationProdlistPOJO);

                }

                viewPurchaseOrderByIdPOJO.setArrayList_verification_prod_list(arrayList_verification_prod_list);

                arrayList_view_purchase_order_list.add(viewPurchaseOrderByIdPOJO);

                arrayList_TotalPurchaseOrders.add(new TotalPurchaseOrdersPOJO(arrayList_view_purchase_order_list));

            }

            //createPDF(data_array.length());

            createPDFUtils(data_array.length());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //==========================get pdf data code ends ==========================================

    public String comrpess_50(String ref_path, File file) {

        File imgfile = new File(ref_path);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap myBitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath(), options);
        // Bitmap compressed_bitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 5, myBitmap.getHeight() / 5, true);
        Bitmap compressed_bitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), null, true);

        Log.i("file=", file + "");
        Log.i("file size=", Integer.parseInt(String.valueOf(file.length() / 1024)) + "");
        Log.i("file size conpress=", Integer.parseInt(String.valueOf(file.length() / 1024)) / 80 + "");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            compressed_bitmap.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(String.valueOf(file.length() / 1024)) / 80, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*path = prefManager.getPrefString("image_path");
        Log.i(TAG, "path=>" + path);*/

        if (AppUtils.file != null)
            path = comrpess_50(AppUtils.imageFilePath, AppUtils.file);

        Log.i(TAG, "comrpess_50_path==>" + path);

        isUpdate = sp_update.getBoolean("isUpdate", false);

        Log.i(TAG, "isUpdate onResume=>" + isUpdate);


        if (isUpdate) {

            from_date = gDateTime.dmyToymd(binding.tvFromDatePurchaseOrder.getText().toString().trim());
            to_date = gDateTime.dmyToymd(binding.tvToDatePurchaseOrder.getText().toString().trim());

            arrayList_purchase_order = new ArrayList<>();
            purchase_orders_url = getString(R.string.Base_URL) + "viewPurchaseOrder.php?purchase_order_status=" + (!orderStatusList().isEmpty() ? orderStatusList() : "-1") + "&from_date=" + from_date + "&to_date=" + to_date + "&salesman_id=&distributor_id=" + distributor_id;
            new getPurchaseOrdersTask().execute(purchase_orders_url);
        }

    }

    public String orderStatusList() {

        String combine_order_id = "";

        for (int i = 0; i < order_status_list.size(); i++) {

            if (order_status_list.size() > 1) {
                combine_order_id = combine_order_id + order_status_list.get(i) + ",";
            } else {
                combine_order_id = combine_order_id + order_status_list.get(i);
            }
        }

        return combine_order_id.replaceAll(",$", "");
    }


    //=============================update purchase order task starts===============================

    public class updateShippingOrderTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.i("File Path==>", path);

            upload_url = getResources().getString(R.string.Base_URL) + "updateShippingPurchaseOrder.php";
            //upload_url = "http://192.168.43.151/ImageUploadDemo/multipart_upload.php";
            Log.i("Image Upload Url==>", upload_url);

        }

        @Override
        protected Void doInBackground(String... arg0) {

            String purchaseId = arg0[0];
            String artical = arg0[1];
            String shipping_date = arg0[2];
            String billing_rs = arg0[3];

            Log.i(TAG, "purchaseId=>" + purchaseId);
            Log.i(TAG, "artical=>" + artical);
            Log.i(TAG, "shipping_date=>" + shipping_date);
            Log.i(TAG, "billing_rs=>" + billing_rs);

            try {

                //new MultipartUploadRequest(AddShopActivity.this, image_name, upload_url)
                new MultipartUploadRequest(requireActivity(), upload_url)
                        .setMethod("POST")
                        .addFileToUpload(path, "uploadedfile")
                        .addParameter("purchase_id", purchaseId)
                        .addParameter("purchase_order_status", "5")
                        .addParameter("shipping_date", shipping_date)
                        .addParameter("no_of_article", artical)
                        .addParameter("billing_amount", billing_rs)
                        .setMaxRetries(2)
                        .setDelegate(new UploadStatusDelegate() {
                            @Override
                            public void onProgress(Context context, UploadInfo uploadInfo) {

                                pdialog = new ProgressDialog(getActivity());
                                pdialog.setCancelable(false);
                                pdialog.setMessage(commanSuchnaList.getArrayList().get(3) + "");
                                pdialog.show();

                            }

                            @Override
                            public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                                if (pdialog.isShowing()) {
                                    pdialog.dismiss();
                                }
                                Toast.makeText(context, commanSuchnaList.getArrayList().get(14) + "", Toast.LENGTH_SHORT).show();
                                Log.i("Error serverResponse=>", serverResponse.getBodyAsString() + "");
                                //Toast.makeText(context, "Error serverResponse=>" + serverResponse.getBodyAsString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                                //String obj = new Gson().fromJson(serverResponse.getBodyAsString(), AddShopActivity.class);

                                if (pdialog.isShowing()) {
                                    pdialog.dismiss();
                                }

                                if (serverResponse.getBodyAsString().contains("updated Successfully")) {
                                    showDialog();
                                } else {
                                    Toast.makeText(context, commanSuchnaList.getArrayList().get(14) + "", Toast.LENGTH_SHORT).show();
                                }

                                Log.i("serverResponse=>", serverResponse.getBodyAsString() + "");
                                //Toast.makeText(context, "serverResponse=>" + "\n" + serverResponse + "\nBodyAsString=" + serverResponse.getBodyAsString() + "\nBody=" + serverResponse.getBody() + "\nHttp Code=" + serverResponse.getHttpCode(), Toast.LENGTH_LONG).show();
                                //Toast.makeText(context, "\nserverResponse Body=>="+ serverResponse.getBodyAsString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {

                                if (pdialog.isShowing()) {
                                    pdialog.dismiss();
                                }

                                Log.i("Cancelled=>", uploadInfo + "");

                                Toast.makeText(context, commanSuchnaList.getArrayList().get(14) + "", Toast.LENGTH_SHORT).show();
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

    //=============================update purchase order task ends=================================


    @SuppressLint("SetTextI18n")
    public void setLanguage(String key) {

        Language language = new Language(key, getActivity(), setLang(key));
        Language.CommanList commanList = language.getData();
        if (setLang(key).size() > 0) {
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                binding.tvFromDateTitleMpo.setText("" + commanList.getArrayList().get(0));
                binding.tvToDateTitleMpo.setText("" + commanList.getArrayList().get(1));
                binding.cbThisWeekPurchaseOrder.setText("" + commanList.getArrayList().get(2));
                binding.cbThisMonthPurchaseOrder.setText("" + commanList.getArrayList().get(3));
                binding.cbSubmitedPurchaseOrder.setText("" + commanList.getArrayList().get(4));
                binding.cbVerificationPurchaseOrder.setText("" + commanList.getArrayList().get(5));
                binding.cbAuthenticationPurchaseOrder.setText("" + commanList.getArrayList().get(6));
                binding.cbPackingPurchaseOrder.setText("" + commanList.getArrayList().get(7));
                binding.cbShippingPurchaseOrder.setText("" + commanList.getArrayList().get(8));
                binding.cbShippedPurchaseOrder.setText("" + commanList.getArrayList().get(9));
                binding.btnGeneratePdfForAll.setText("" + commanList.getArrayList().get(10));
            }
        }
    }

    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "36");

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
        Cursor cur = db.viewLanguage(key, "23");

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
        Cursor cur = db.viewMultiLangSuchna("36");

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


    //=============================create pdf using pdf utils code starts=================================

    File file;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmm");

    private void createPDFUtils(int length) {

        PdfUtils.initializeDoc();

        //=========code for cartoon sleep starts========
        int tot_sleep_pages = 1;

        for (int i = 0; i < length; i++) {

            double tot_cartoon_pages = 0;

            Log.i(TAG, "arrayList_qty_size==>" + arrayList_qty.get(i));

            if (arrayList_qty.get(i) > 7) {

                tot_cartoon_pages = (Double.valueOf(arrayList_qty.get(i)) / 7);

                Log.i(TAG, "tot_dist_pages==>" + String.valueOf(tot_cartoon_pages).split("\\.")[1]);

                if (!String.valueOf(tot_cartoon_pages).split("\\.")[1].equals("0")) {
                    tot_cartoon_pages = tot_cartoon_pages + 1;
                }

            } else {
                tot_cartoon_pages = 1;
            }

            Log.i(TAG, "tot_dist_pages==>" + tot_cartoon_pages);


            int no_of_sleep = arrayList_qty.get(i);

            for (int j = 0; j < (int) tot_cartoon_pages; j++) {

                tot_sleep_pages++;
                Log.i(TAG, "no_of_sleep==>" + no_of_sleep);
                createCartoonLabel(i, no_of_sleep);
                no_of_sleep -= 7;
            }

        }


        //=========code for Packing  and Verification Table starts========
        int start_packing = 0, end_packing = 2;
        double packing_pages = 0;

        if (length > 2) {

            packing_pages = ((double) length / 2);

            if (!String.valueOf(packing_pages).split("\\.")[1].equals("0")) {
                packing_pages = packing_pages + 1;
            }

        } else {
            packing_pages = 1;
        }

        for (int i = 0; i < (int) packing_pages; i++) {

            createPackingTable(length, start_packing, end_packing);

            if (length > end_packing) {
                start_packing += 2;
                end_packing += 2;
            }
        }


        //=========code for Packing  and Verification Table starts========
        int start_verification = 0, end_verification = 2;
        double verification_pages = 0;

        if (length > 2) {

            verification_pages = ((double) length / 2);

            if (!String.valueOf(verification_pages).split("\\.")[1].equals("0")) {
                verification_pages = verification_pages + 1;
            }

        } else {
            verification_pages = 1;
        }

        for (int i = 0; i < (int) verification_pages; i++) {

            createverificationTable(length, start_verification, end_verification);

            if (length > end_verification) {
                start_verification += 2;
                end_verification += 2;
            }
        }

        try {
            file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Order List/PurchaseOrderRotate.pdf");
            //file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Order List/" + "PurchaseOrder-" + dateFormat.format(Calendar.getInstance().getTime()) + ".pdf");
            file.getParentFile().mkdirs();
            file.createNewFile();

            PdfUtils.getDocument().writeTo(new FileOutputStream(file));
            rotatePage(tot_sleep_pages);
            openGeneratedPDF();

        } catch (IOException e) {
            e.printStackTrace();
        }
        PdfUtils.closePage();

    }

    private void openGeneratedPDF() {

        //file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/PurchaseOrder.pdf");
        file = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Order List/" + "PurchaseOrder-" + dateFormat.format(Calendar.getInstance().getTime()) + ".pdf");
        if (file.exists()) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            //====================fileProvider ===========

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
                Toast.makeText(getActivity(), "No Application Available For PDF View", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void rotatePage(int tot_sleep_pages) {


        if (file.exists()) {

            try {
                PdfDocument pdfDoc = new PdfDocument(new PdfReader(file),
                        new PdfWriter(new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Order List/" + "PurchaseOrder-"
                                + dateFormat.format(Calendar.getInstance().getTime()) + ".pdf")));
                //new PdfWriter(new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Order List/PurchaseOrder.pdf")));
                for (int p = 1; p <= pdfDoc.getNumberOfPages(); p++) {
                    PdfPage page = pdfDoc.getPage(p);
                    //int rotate = page.getRotation();
                    if (p < tot_sleep_pages) {
                        page.setRotation(-90);
                    } /*else {
                        page.setRotation((rotate + 90) % 360);
                    }*/
                }

                pdfDoc.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createCartoonLabel(int order_pos, int tot_sleep) {

        int left = 50, top = 110, left_to_veravel = 300, top_to_veravel = 50, left_to_dist = 200, top_to_dist = 80, left_to_transport = 300, top_to_transport = 100;

        //create page
        PdfUtils.createPdfPage(0, 660, 975);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 18);

        for (int i = 0; i < tot_sleep; i++) {

            if (i == 0) {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(order_pos).getArrayList().get(0).getShipping_code(), left, top, -90, left, top);
                PdfUtils.drawText("TO. वेरावल", left_to_veravel, top_to_veravel);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(order_pos).getArrayList().get(0).getName_hindi() + " "
                        + arrayList_TotalPurchaseOrders.get(order_pos).getArrayList().get(0).getContact_no(), left_to_dist, top_to_dist);
                PdfUtils.drawText("ट्रांसपोर्ट : " + arrayList_TotalPurchaseOrders.get(order_pos).getArrayList().get(0).getDist_town(), left_to_transport, top_to_transport);

            } else {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(order_pos).getArrayList().get(0).getShipping_code(), left, top += 140, -90, left, top);
                PdfUtils.drawText("TO. वेरावल", left_to_veravel, top_to_veravel += 140);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(order_pos).getArrayList().get(0).getName_hindi() + " "
                        + arrayList_TotalPurchaseOrders.get(order_pos).getArrayList().get(0).getContact_no(), left_to_dist, top_to_dist += 140);
                PdfUtils.drawText("ट्रांसपोर्ट : " + arrayList_TotalPurchaseOrders.get(order_pos).getArrayList().get(0).getDist_town(), left_to_transport, top_to_transport += 140);
            }

        }

        PdfUtils.finishPage();
    }

    private void createPackingTable(int length, int start, int end) {

        //create page
        PdfUtils.createPdfPage(0, 975, 660);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);

        for (int i = start; i < end; i++) {

            if (i < length) {

                if (i == (start))
                    createLeftPackingTable(i);
                else if (i == (start + 1))
                    createRigthPackingTable(i);
            }
        }

        PdfUtils.finishPage();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    private void createLeftPackingTable(int pos) {

        //=======code for packing table left side starts
        PdfUtils.drawImage(requireActivity().getDrawable(R.drawable.packing_table), 20, 50, 457, 600);
        PdfUtils.drawText("" + gDateTime.ymdTodmy(arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_date()), 270, 72);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_no(), 370, 72);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getShipping_code(), 40, 89);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getName_hindi(), 120, 89);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getInvoice_type(), 345, 89);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getDist_town(), 40, 105);
        PdfUtils.drawText("ट्रांसपोर्टर : " + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTransporter_name(), 210, 105);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getStock_distributor_name(), 40, 123);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTax_type(), 200, 123);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getEway_bill(), 420, 123);

        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
        int dispatch_left = 35, disparch_top = 170, prod_left = 75, prod_top = 170, qty_left = 263, qty_top = 170, basicrate_left = 300, basicrate_top = 170,
                gst_left = 380, gst_top = 170, b4gst_left = 407, b4gst_top = 170;

        double total = 0.0;

        for (int i = 0; i < arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().size(); i++) {

            if (i == 0) {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left, disparch_top);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left, prod_top);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProduct_qty(),
                        qty_left, qty_top);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getBasic_rate(),
                        basicrate_left, basicrate_top);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getGst(),
                        gst_left, gst_top);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getTotal_b4_gst(),
                        b4gst_left, b4gst_top);

            } else if (i > 4 && i < 10) {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left, disparch_top += 18);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left, prod_top += 18);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProduct_qty(),
                        qty_left, qty_top += 18);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getBasic_rate(),
                        basicrate_left, basicrate_top += 18);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getGst(),
                        gst_left, gst_top += 18);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getTotal_b4_gst(),
                        b4gst_left, b4gst_top += 18);
            } else {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left, disparch_top += 20);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left, prod_top += 20);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProduct_qty(),
                        qty_left, qty_top += 20);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getBasic_rate(),
                        basicrate_left, basicrate_top += 20);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getGst(),
                        gst_left, gst_top += 20);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getTotal_b4_gst(),
                        b4gst_left, b4gst_top += 20);
            }

            total = total + Double.parseDouble(arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getTotal_b4_gst());
        }

        PdfUtils.drawText("₹ " + String.format("%.2f", total), 360, 550);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTax_type(), 360, 568);
        PdfUtils.drawText("₹ " + String.format("%.2f", Double.parseDouble(arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_amount())), 360, 585);
        //=======code for packing table left side ends
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    private void createRigthPackingTable(int pos) {

        //=======code for packing table Right side starts
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
        PdfUtils.drawImage(requireActivity().getDrawable(R.drawable.packing_table), 477, 50, 957, 600);
        PdfUtils.drawText("" + gDateTime.ymdTodmy(arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_date()), 750, 72);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_no(), 850, 72);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getShipping_code(), 497, 89);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getName_hindi(), 577, 89);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getInvoice_type(), 837, 89);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getDist_town(), 497, 105);
        PdfUtils.drawText("ट्रांसपोर्टर : " + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTransporter_name(), 677, 105);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getStock_distributor_name(), 497, 123);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTax_type(), 670, 123);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getEway_bill(), 905, 123);

        int dispatch_left2 = 497, disparch_top2 = 170, prod_left2 = 537, prod_top2 = 170, qty_left2 = 745, qty_top2 = 170, basicrate_left2 = 780, basicrate_top2 = 170,
                gst_left2 = 875, gst_top2 = 170, b4gst_left2 = 905, b4gst_top2 = 170;
        double total2 = 0.0;

        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);

        for (int i = 0; i < arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().size(); i++) {

            if (i == 0) {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left2, disparch_top2);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left2, prod_top2);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProduct_qty(),
                        qty_left2, qty_top2);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getBasic_rate(),
                        basicrate_left2, basicrate_top2);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getGst(),
                        gst_left2, gst_top2);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getTotal_b4_gst(),
                        b4gst_left2, b4gst_top2);

            } else if (i > 4 && i < 10) {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left2, disparch_top2 += 18);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left2, prod_top2 += 18);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProduct_qty(),
                        qty_left2, qty_top2 += 18);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getBasic_rate(),
                        basicrate_left2, basicrate_top2 += 18);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getGst(),
                        gst_left2, gst_top2 += 18);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getTotal_b4_gst(),
                        b4gst_left2, b4gst_top2 += 18);
            } else {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left2, disparch_top2 += 20);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left2, prod_top2 += 20);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProduct_qty(),
                        qty_left2, qty_top2 += 20);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getBasic_rate(),
                        basicrate_left2, basicrate_top2 += 20);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getGst(),
                        gst_left2, gst_top2 += 20);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getTotal_b4_gst(),
                        b4gst_left2, b4gst_top2 += 20);
            }

            total2 = total2 + Double.parseDouble(arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getTotal_b4_gst());
        }

        PdfUtils.drawText("₹ " + String.format("%.2f", total2), 840, 550);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTax_type(), 840, 568);
        PdfUtils.drawText("₹ " + String.format("%.2f", Double.parseDouble(arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_amount())), 840, 585);
        //=======code for packing table Right side ends
    }

    private void createverificationTable(int length, int start, int end) {

        //create page
        PdfUtils.createPdfPage(3, 975, 660);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);

        for (int i = start; i < end; i++) {

            if (i < length) {

                if (i == (start))
                    createLeftVerificationTable(i);
                else if (i == (start + 1))
                    createRightVerificationTable(i);
            }
        }

        PdfUtils.finishPage();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createLeftVerificationTable(int pos) {

        //===========code for verification left table starts==
        PdfUtils.drawImage(requireActivity().getDrawable(R.drawable.verification_table), 20, 50, 457, 600);
        PdfUtils.drawText("" + gDateTime.ymdTodmy(arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_date()), 270, 75);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_no(), 370, 75);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getShipping_code(), 35, 92);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getName_hindi(), 120, 92);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getInvoice_type(), 345, 92);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getDist_town(), 35, 110);
        PdfUtils.drawText("ट्रांसपोर्टर : " + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTransporter_name(), 200, 110);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getStock_distributor_name(), 35, 126);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTax_type(), 195, 126);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getEway_bill(), 415, 126);

        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
        int dispatch_left = 35, disparch_top = 167, prod_left = 70, prod_top = 167;

        for (int i = 0; i < arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().size(); i++) {

            if (i == 0) {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left, disparch_top);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left, prod_top);
            } else {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left, disparch_top += 20);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left, prod_top += 20);
            }

        }
        //===========code for verification left table ends==

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createRightVerificationTable(int pos) {

        //===========code for verification Right table starts==
        PdfUtils.drawImage(requireActivity().getDrawable(R.drawable.verification_table), 477, 50, 957, 600);
        PdfUtils.drawText("" + gDateTime.ymdTodmy(arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_date()), 750, 75);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getOrder_no(), 850, 75);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getShipping_code(), 497, 92);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getName_hindi(), 577, 92);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getInvoice_type(), 837, 92);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getDist_town(), 497, 110);
        PdfUtils.drawText("ट्रांसपोर्टर : " + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTransporter_name(), 677, 110);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getStock_distributor_name(), 497, 126);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getTax_type(), 670, 126);
        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
        PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getEway_bill(), 905, 126);

        PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 10);
        int dispatch_left2 = 497, disparch_top2 = 167, prod_left2 = 537, prod_top2 = 167;

        for (int i = 0; i < arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().size(); i++) {

            if (i == 0) {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left2, disparch_top2);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left2, prod_top2);
            } else {

                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getDispatch_code(),
                        dispatch_left2, disparch_top2 += 20);
                PdfUtils.setPaintBrushNormal(Color.BLACK, Paint.Align.LEFT, 12);
                PdfUtils.drawText("" + arrayList_TotalPurchaseOrders.get(pos).getArrayList().get(0).getArrayList_packing_prod_list().get(i).getProd_name_hindi(),
                        prod_left2, prod_top2 += 20);
            }

        }
        //===========code for verification Right table ends==
    }

    //=============================create pdf using pdf utils code ends===================================

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pdialog != null && pdialog.isShowing())
            pdialog.dismiss();
    }
}