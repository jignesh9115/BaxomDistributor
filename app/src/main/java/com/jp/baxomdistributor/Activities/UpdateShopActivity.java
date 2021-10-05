package com.jp.baxomdistributor.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.ActivityUpdateShopBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdateShopActivity extends AppCompatActivity {

    ActivityUpdateShopBinding binding;
    String TAG = getClass().getSimpleName();

    String shopCategory_url = "", shopCategory_response = "", get_shop_url = "", get_shop_response = "", update_shop_url = "",
            update_shop_response = "", is_shop_all_mendetory = "", is_shop_name = "", is_shopkeeper_name = "", is_contact_no = "",
            is_whatsapp_no = "", is_address = "";

    String shop_id = "", title = "", address = "", shop_keeper_name = "", shop_keeper_no1 = "", shop_keeper_no2 = "", shop_category_id = "",
            invoice_type = "", gst_no = "", isupdateshop = "", category_type = "", delete_shop_url = "", delete_shop_response = "", is_delete_shop = "";

    ArrayList<String> arrayList_shop_category_id, arrayList_shop_category_name, arrayList_invoice_type;
    ArrayAdapter<String> arrayAdapter;

    AlertDialog ad, ad_net_connection;
    AlertDialog.Builder builder;

    ProgressDialog pdialog;
    SharedPreferences sp_login, sp, sp_deleteshop, sp_update, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanSuchnaList;
    private IntentFilter minIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateShopBinding.inflate(getLayoutInflater());
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

        sp_login = getSharedPreferences("login_detail", Context.MODE_PRIVATE);
        sp = getSharedPreferences("salesman_detail", Context.MODE_PRIVATE);


        is_delete_shop = sp.getString("is_delete_shop", "");

        sp_deleteshop = getSharedPreferences("shop_delete", Context.MODE_PRIVATE);

        isupdateshop = sp_login.getString("isupdateshop", "");
        is_shop_all_mendetory = sp.getString("is_shop_all_mendetory", "");
        is_shop_name = sp.getString("is_shop_name", "");
        is_shopkeeper_name = sp.getString("is_shopkeeper_name", "");
        is_contact_no = sp.getString("is_contact_no", "");
        is_whatsapp_no = sp.getString("is_whatsapp_no", "");
        is_address = sp.getString("is_address", "");


        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        setLanguage(sp_multi_lang.getString("lang", ""));

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                UpdateShopActivity.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language.getData();

       /* if (is_shop_all_mendetory.equalsIgnoreCase("0")) {

            binding.tvShopNameRequired.setVisibility(View.GONE);
            binding.tvShopAddressRequired.setVisibility(View.GONE);
            binding.tvShopContactPersonNameRequired.setVisibility(View.GONE);
            binding.tvContact1Required.setVisibility(View.GONE);
            binding.tvGstStarUpdateshop.setVisibility(View.GONE);

        } else if (is_shop_all_mendetory.equalsIgnoreCase("1")) {

            binding.tvShopNameRequired.setVisibility(View.VISIBLE);
            binding.tvShopAddressRequired.setVisibility(View.VISIBLE);
            binding.tvShopContactPersonNameRequired.setVisibility(View.VISIBLE);
            binding.tvContact1Required.setVisibility(View.VISIBLE);
            binding.tvGstStarUpdateshop.setVisibility(View.VISIBLE);
        }*/

        if (is_shop_name.equalsIgnoreCase("1"))
            binding.tvShopNameRequired.setVisibility(View.VISIBLE);
        else
            binding.tvShopNameRequired.setVisibility(View.GONE);

        if (is_shopkeeper_name.equalsIgnoreCase("1"))
            binding.tvShopContactPersonNameRequired.setVisibility(View.VISIBLE);
        else
            binding.tvShopContactPersonNameRequired.setVisibility(View.GONE);

        if (is_address.equalsIgnoreCase("1"))
            binding.tvShopAddressRequired.setVisibility(View.VISIBLE);
        else
            binding.tvShopAddressRequired.setVisibility(View.GONE);

        if (is_contact_no.equalsIgnoreCase("1"))
            binding.tvContact1Required.setVisibility(View.VISIBLE);
        else
            binding.tvContact1Required.setVisibility(View.GONE);

        if (is_whatsapp_no.equalsIgnoreCase("1"))
            binding.tvContact2Required.setVisibility(View.VISIBLE);
        else
            binding.tvContact2Required.setVisibility(View.GONE);

        if (sp_login.getString("login_person", "").equalsIgnoreCase("salesman")) {

            if (is_delete_shop.equalsIgnoreCase("1"))
                binding.btnDeleteShop.setVisibility(View.VISIBLE);
            else if (is_delete_shop.equalsIgnoreCase("0"))
                binding.btnDeleteShop.setVisibility(View.GONE);

        }

        if (isupdateshop.equalsIgnoreCase("0")
                && is_shop_name.equalsIgnoreCase("0")
                && is_shopkeeper_name.equalsIgnoreCase("0")
                && is_address.equalsIgnoreCase("0")
                && is_whatsapp_no.equalsIgnoreCase("0")
                && is_contact_no.equalsIgnoreCase("0")) {

            binding.btnUpdateShop.setVisibility(View.GONE);
            binding.btnDeleteShop.setVisibility(View.GONE);

        }


        if (isupdateshop.equalsIgnoreCase("0")) {

            if (is_shop_name.equalsIgnoreCase("1")) {
                binding.edtShopTitleUpdateshop.setFocusableInTouchMode(true);
            }

            if (is_shopkeeper_name.equalsIgnoreCase("1")) {
                binding.edtShopKeeperNameUpdateshop.setFocusableInTouchMode(true);
            }

            if (is_address.equalsIgnoreCase("1")) {
                binding.edtShopAddressUpdateshop.setFocusableInTouchMode(true);
            }

            if (is_contact_no.equalsIgnoreCase("1")) {
                binding.edtShopKeeperNo1Updateshop.setFocusableInTouchMode(true);
            }

            if (is_whatsapp_no.equalsIgnoreCase("1")) {
                binding.edtShopKeeperNo2Updateshop.setFocusableInTouchMode(true);
            }


            binding.edtShopGstnoUpdateshop.setFocusableInTouchMode(true);
            binding.spnShopCategoryUpdateshop.setEnabled(true);
            binding.spnShopInvoiceTypeUpdateshop.setEnabled(true);
            binding.btnVerifyGstnoUpdateshop.setEnabled(true);


        } else {

            binding.edtShopTitleUpdateshop.setFocusableInTouchMode(true);
            binding.edtShopKeeperNameUpdateshop.setFocusableInTouchMode(true);
            binding.edtShopAddressUpdateshop.setFocusableInTouchMode(true);
            binding.edtShopKeeperNo1Updateshop.setFocusableInTouchMode(true);
            binding.edtShopKeeperNo2Updateshop.setFocusableInTouchMode(true);

            binding.edtShopGstnoUpdateshop.setFocusableInTouchMode(true);
            binding.spnShopCategoryUpdateshop.setEnabled(true);
            binding.spnShopInvoiceTypeUpdateshop.setEnabled(true);
            binding.btnVerifyGstnoUpdateshop.setEnabled(true);

        }


        shop_id = getIntent().getExtras().getString("shop_id");

        arrayList_shop_category_id = new ArrayList<>();
        arrayList_shop_category_name = new ArrayList<>();

        arrayList_invoice_type = new ArrayList<>();
        arrayList_invoice_type.add("TAX INVOICE");
        arrayList_invoice_type.add("RETAIL INVOICE");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.mytextview_14sp, arrayList_invoice_type);
        binding.spnShopInvoiceTypeUpdateshop.setAdapter(arrayAdapter);

        new getShopCategoryTask().execute();
        new getShopDetailTask().execute();


        binding.imgBackUpdateshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       /* if (isupdateshop.equalsIgnoreCase("1")) {

            binding.imgEditShop.setVisibility(View.VISIBLE);
            binding.btnVerifyGstnoUpdateshop.setVisibility(View.VISIBLE);

        } else {

            binding.imgEditShop.setVisibility(View.GONE);
            binding.btnVerifyGstnoUpdateshop.setVisibility(View.GONE);
        }*/

        /*binding.imgEditShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isupdateshop.equalsIgnoreCase("1")) {

                    if (is_shop_name.equalsIgnoreCase("1")) {
                        binding.edtShopTitleUpdateshop.setFocusableInTouchMode(true);
                    }

                    if (is_shopkeeper_name.equalsIgnoreCase("1")) {
                        binding.edtShopKeeperNameUpdateshop.setFocusableInTouchMode(true);
                    }

                    if (is_address.equalsIgnoreCase("1")) {
                        binding.edtShopAddressUpdateshop.setFocusableInTouchMode(true);
                    }

                    if (is_contact_no.equalsIgnoreCase("1")) {
                        binding.edtShopKeeperNo1Updateshop.setFocusableInTouchMode(true);
                    }

                    if (is_whatsapp_no.equalsIgnoreCase("1")) {
                        binding.edtShopKeeperNo2Updateshop.setFocusableInTouchMode(true);
                    }


                    binding.edtShopGstnoUpdateshop.setFocusableInTouchMode(true);
                    binding.spnShopCategoryUpdateshop.setEnabled(true);
                    binding.spnShopInvoiceTypeUpdateshop.setEnabled(true);
                    binding.btnVerifyGstnoUpdateshop.setEnabled(true);
                    binding.btnUpdateShop.setVisibility(View.VISIBLE);
               *//* binding.edtShopAddressUpdateshop.setFocusableInTouchMode(true);
                binding.edtShopKeeperNameUpdateshop.setFocusableInTouchMode(true);
                binding.edtShopKeeperNo1Updateshop.setFocusableInTouchMode(true);
                binding.edtShopKeeperNo2Updateshop.setFocusableInTouchMode(true);*//*

                    //binding.imgEditShop.setVisibility(View.GONE);

                } else {

                    Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(6) + "", Toast.LENGTH_SHORT).show();
                }

            }
        });*/


        binding.spnShopCategoryUpdateshop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                shop_category_id = arrayList_shop_category_id.get(position);
                category_type = arrayList_shop_category_name.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.spnShopInvoiceTypeUpdateshop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //if (binding.imgEditShop.getVisibility() == View.GONE) {

                if (binding.spnShopInvoiceTypeUpdateshop.getSelectedItem() == "TAX INVOICE") {

                    binding.edtShopGstnoUpdateshop.setEnabled(true);
                    binding.tvGstStarUpdateshop.setVisibility(View.VISIBLE);
                    binding.btnVerifyGstnoUpdateshop.setEnabled(true);

                } else {

                    binding.edtShopGstnoUpdateshop.setEnabled(false);
                    binding.tvGstStarUpdateshop.setVisibility(View.GONE);
                    binding.btnVerifyGstnoUpdateshop.setEnabled(false);
                    binding.edtShopGstnoUpdateshop.setText("");
                }
                //}

                invoice_type = arrayList_invoice_type.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnVerifyGstnoUpdateshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = binding.edtShopGstnoUpdateshop.getText().toString().trim();

                if (text.length() != 15) {

                    binding.edtShopGstnoUpdateshop.requestFocus();
                    Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(7) + "", Toast.LENGTH_SHORT).show();

                } else {

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(text, text);
                    clipboard.setPrimaryClip(clip);

                    String url = "https://cleartax.in/s/gst-number-search";

                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("" + url));
                    startActivity(intent1);
                }


            }
        });

        binding.btnUpdateShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = binding.edtShopTitleUpdateshop.getText().toString().trim();
                address = binding.edtShopAddressUpdateshop.getText().toString().trim();
                shop_keeper_name = binding.edtShopKeeperNameUpdateshop.getText().toString().trim();
                shop_keeper_no1 = binding.edtShopKeeperNo1Updateshop.getText().toString().trim();
                shop_keeper_no2 = binding.edtShopKeeperNo2Updateshop.getText().toString().trim();
                gst_no = binding.edtShopGstnoUpdateshop.getText().toString().trim();

                if (sp_login.getString("login_person", "").equalsIgnoreCase("salesman")) {

                    /*if (is_shop_all_mendetory.equalsIgnoreCase("0")) {

                        update_shop_url = getString(R.string.Base_URL) + getString(R.string.updateShopDetail_url) + shop_id + "&title=" + title + "&address=" + address + "&shop_keeper_name=" + shop_keeper_name + "&shop_keeper_no=" + shop_keeper_no1 + "&contact_no=" + shop_keeper_no2 + "&category_type=" + category_type + "&invoice_type=" + invoice_type + "&tin_no=" + gst_no;
                        new updateShopDetail().execute(update_shop_url.replace("%20", " "));

                        SharedPreferences.Editor editor = sp_update.edit();
                        editor.putBoolean("isUpdate", true);
                        editor.apply();

                    } else if (is_shop_all_mendetory.equalsIgnoreCase("1"))*/
                    {

                        if (binding.edtShopTitleUpdateshop.getText().toString().isEmpty() && is_shop_name.equalsIgnoreCase("1")) {

                            Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(8) + "", Toast.LENGTH_SHORT).show();

                        } else if (binding.edtShopAddressUpdateshop.getText().toString().isEmpty() && is_address.equalsIgnoreCase("1")) {

                            Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(9) + "", Toast.LENGTH_SHORT).show();

                        } else if (binding.edtShopKeeperNameUpdateshop.getText().toString().isEmpty() && is_shopkeeper_name.equalsIgnoreCase("1")) {

                            Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(10) + "", Toast.LENGTH_SHORT).show();

                        } else if (binding.edtShopKeeperNo1Updateshop.getText().toString().isEmpty() && is_contact_no.equalsIgnoreCase("1")) {

                            Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(11) + "", Toast.LENGTH_SHORT).show();

                        } else if (binding.edtShopKeeperNo2Updateshop.getText().toString().isEmpty() && is_whatsapp_no.equalsIgnoreCase("1")) {

                            Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(15) + "", Toast.LENGTH_SHORT).show();

                        } else if (binding.edtShopGstnoUpdateshop.getText().toString().isEmpty() && binding.spnShopInvoiceTypeUpdateshop.getSelectedItem() == "TAX INVOICE") {

                            Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(12) + "", Toast.LENGTH_SHORT).show();

                        } else {

                            //Toast.makeText(UpdateShopActivity.this, "\ntitle=" + title + "\naddress=" + address + "\nshop_keeper_name=" + shop_keeper_name + "\nshop_keeper_no1=" + shop_keeper_no1 + "\nshop_keeper_no2=" + shop_keeper_no2 + "\nshop_category_id=" + shop_category_id + "\ncategory_type=" + category_type + "\ninvoice_type=" + invoice_type + "\ngst_no=" + gst_no, Toast.LENGTH_LONG).show();
                            update_shop_url = getString(R.string.Base_URL) + getString(R.string.updateShopDetail_url) + shop_id + "&title=" + title + "&address=" + address + "&shop_keeper_name=" + shop_keeper_name + "&shop_keeper_no=" + shop_keeper_no1 + "&contact_no=" + shop_keeper_no2 + "&category_type=" + category_type + "&invoice_type=" + invoice_type + "&tin_no=" + gst_no;
                            new updateShopDetail().execute(update_shop_url.replace("%20", " "));

                            SharedPreferences.Editor editor = sp_update.edit();
                            editor.putBoolean("isUpdate", true);
                            editor.apply();
                        }
                    }

                } else {

                    update_shop_url = getString(R.string.Base_URL) + getString(R.string.updateShopDetail_url) + shop_id + "&title=" + title + "&address=" + address + "&shop_keeper_name=" + shop_keeper_name + "&shop_keeper_no=" + shop_keeper_no1 + "&contact_no=" + shop_keeper_no2 + "&category_type=" + category_type + "&invoice_type=" + invoice_type + "&tin_no=" + gst_no;
                    new updateShopDetail().execute(update_shop_url.replace("%20", " "));

                    SharedPreferences.Editor editor = sp_update.edit();
                    editor.putBoolean("isUpdate", true);
                    editor.apply();
                }
            }
        });


        binding.btnDeleteShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showWarningDialog(shop_id);

            }
        });


    }


    //=========================get shop detail task starts ============================

    public class getShopDetailTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(UpdateShopActivity.this);
            pdialog.setCancelable(false);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(14) + "");
            pdialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            get_shop_url = getString(R.string.Base_URL) + getString(R.string.getShopDetailbyId_url) + shop_id;
            Log.i(TAG, "get_shop_url=>" + get_shop_url);

            HttpHandler httpHandler = new HttpHandler();
            get_shop_response = httpHandler.makeServiceCall(get_shop_url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "get_shop_response=>" + get_shop_response);
                getShopDetail();

                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getShopDetail() {

        try {
            JSONObject jsonObject = new JSONObject(get_shop_response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);

                    binding.edtShopTitleUpdateshop.setText("" + object.getString("title"));
                    binding.edtShopAddressUpdateshop.setText("" + object.getString("address"));
                    binding.edtShopKeeperNameUpdateshop.setText("" + object.getString("shop_keeper_name"));
                    binding.edtShopKeeperNo1Updateshop.setText("" + object.getString("shop_keeper_no"));
                    binding.edtShopKeeperNo2Updateshop.setText("" + object.getString("contact_no"));
                    binding.edtShopGstnoUpdateshop.setText("" + object.getString("tin_no"));

                    binding.spnShopInvoiceTypeUpdateshop.setSelection(arrayList_invoice_type.indexOf(object.getString("invoice_type")));
                    binding.spnShopCategoryUpdateshop.setSelection(arrayList_shop_category_name.indexOf(object.getString("category_name")));


                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.imagenotfoundicon);

                    Glide.with(getApplicationContext()).load(object.getString("photo") + "")
                            .apply(options)
                            .into(binding.imgShopPhotoUpdateshop);


                    if (is_shop_name.equalsIgnoreCase("1") && object.getString("title").equalsIgnoreCase("")) {
                        binding.edtShopTitleUpdateshop.setBackgroundColor(Color.parseColor("#11B5CA"));
                        binding.edtShopTitleUpdateshop.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    if (is_shopkeeper_name.equalsIgnoreCase("1") && object.getString("shop_keeper_name").equalsIgnoreCase("")) {
                        binding.edtShopKeeperNameUpdateshop.setBackgroundColor(Color.parseColor("#11B5CA"));
                        binding.edtShopKeeperNameUpdateshop.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    if (is_contact_no.equalsIgnoreCase("1") && object.getString("shop_keeper_no").equalsIgnoreCase("")) {
                        Log.i(TAG, "inside contact no");
                        binding.edtShopKeeperNo1Updateshop.setBackgroundColor(Color.parseColor("#11B5CA"));
                        binding.edtShopKeeperNo1Updateshop.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    if (is_whatsapp_no.equalsIgnoreCase("1") && object.getString("contact_no").equalsIgnoreCase("")) {
                        Log.i(TAG, "inside whatsapp_no");
                        binding.edtShopKeeperNo2Updateshop.setBackgroundColor(Color.parseColor("#11B5CA"));
                        binding.edtShopKeeperNo2Updateshop.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    if (is_address.equalsIgnoreCase("1") && object.getString("address").equalsIgnoreCase("")) {
                        binding.edtShopAddressUpdateshop.setBackgroundColor(Color.parseColor("#11B5CA"));
                        binding.edtShopAddressUpdateshop.setTextColor(Color.parseColor("#FFFFFF"));
                    }

                    Log.i(TAG, "is_shop_name==>" + is_shop_name);
                    Log.i(TAG, "is_shopkeeper_name==>" + is_shopkeeper_name);
                    Log.i(TAG, "is_contact_no==>" + is_contact_no);
                    Log.i(TAG, "is_whatsapp_no==>" + is_whatsapp_no);
                    Log.i(TAG, "is_address==>" + is_address);
                    Log.i(TAG, "get_shop_name==>" + object.getString("title"));
                    Log.i(TAG, "get_shopkeeper_name==>" + object.getString("shop_keeper_name"));
                    Log.i(TAG, "get_contact_no==>" + object.getString("shop_keeper_no"));
                    Log.i(TAG, "get_whatsapp_no==>" + object.getString("contact_no"));
                    Log.i(TAG, "get_address==>" + object.getString("address"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //=========================get shop detail task ends ==============================


    //======================get shop category list task starts=========================

    public class getShopCategoryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            shopCategory_url = getString(R.string.Base_URL) + getString(R.string.get_shop_category_list_url);
            Log.i("Shop Category url=>", shopCategory_url + "");

            HttpHandler httpHandler = new HttpHandler();
            shopCategory_response = httpHandler.makeServiceCall(shopCategory_url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("ShopCategory response=>", shopCategory_response + "");
                getShopCategory();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }
    }

    private void getShopCategory() {

        try {
            JSONObject jsonObject = new JSONObject(shopCategory_response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                arrayList_shop_category_id.add(object.getString("Id"));
                arrayList_shop_category_name.add(object.getString("shop_title"));

            }

            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.mytextview_14sp, arrayList_shop_category_name);
            binding.spnShopCategoryUpdateshop.setAdapter(arrayAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //======================get shop category list task ends===========================

    //==========================update shop data task starts ==========================

    public class updateShopDetail extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "update_shop_url=>" + update_shop_url);

            HttpHandler httpHandler = new HttpHandler();
            update_shop_response = httpHandler.makeServiceCall(update_shop_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "update_shop_response=>" + update_shop_response);

                if (update_shop_response.contains("update Successfully")) {

                    showDialog();

                } else {

                    Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(13) + "", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    //==========================update shop data task ends ============================


    public void showDialog() {

        builder = new AlertDialog.Builder(UpdateShopActivity.this);
        builder.setMessage(commanSuchnaList.getArrayList().get(0) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(1) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                /*binding.edtShopTitleUpdateshop.setFocusable(false);
                binding.edtShopAddressUpdateshop.setFocusable(false);
                binding.edtShopKeeperNameUpdateshop.setFocusable(false);
                binding.edtShopKeeperNo1Updateshop.setFocusable(false);
                binding.edtShopKeeperNo2Updateshop.setFocusable(false);
                binding.edtShopGstnoUpdateshop.setFocusable(false);

                binding.spnShopCategoryUpdateshop.setEnabled(false);
                binding.spnShopInvoiceTypeUpdateshop.setEnabled(false);

                binding.btnVerifyGstnoUpdateshop.setEnabled(false);

                //binding.imgEditShop.setVisibility(View.VISIBLE);
                binding.btnUpdateShop.setVisibility(View.GONE);*/
                //new getShopDetailTask().execute();

                finish();

            }
        });

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }


    //========================code for update bit detail starts =================================

    public class deleteShopTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "delete_shop_url=>" + delete_shop_url);

            HttpHandler httpHandler = new HttpHandler();
            delete_shop_response = httpHandler.makeServiceCall(delete_shop_url);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "delete_shop_response=>" + delete_shop_response);

                if (delete_shop_response.contains("Deleted Successfully"))
                    showDeleteDialog();
                else
                    Toast.makeText(UpdateShopActivity.this, commanSuchnaList.getArrayList().get(13) + "", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }
    }

    //========================code for update bit detail ends ===================================


    public void showWarningDialog(final String shopId) {

        builder = new AlertDialog.Builder(UpdateShopActivity.this);
        builder.setMessage(commanSuchnaList.getArrayList().get(2) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(3) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                delete_shop_url = getString(R.string.Base_URL) + getString(R.string.delete_shop_url) + shopId;
                //Log.i(TAG, "delete_shop_url=>" + delete_shop_url);
                new deleteShopTask().execute(delete_shop_url);

                SharedPreferences.Editor editor = sp_deleteshop.edit();
                editor.putBoolean("isShopDelete", true);
                editor.apply();

                SharedPreferences.Editor editor1 = sp_update.edit();
                editor1.putBoolean("isUpdate", true);
                editor1.apply();

            }
        });

        builder.setNegativeButton(commanSuchnaList.getArrayList().get(4) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ad.dismiss();
            }
        });


        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#056106"));
        //ad_info.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }


    public void showDeleteDialog() {

        builder = new AlertDialog.Builder(UpdateShopActivity.this);
        builder.setMessage(commanSuchnaList.getArrayList().get(5) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanSuchnaList.getArrayList().get(1) + "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //startActivity(new Intent(UpdateShopActivity.this, ShopListActivity.class));
                finish();

            }
        });

        ad = builder.create();
        ad.show();

        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }


    public void setLanguage(String key) {

        Language language = new Language(key, UpdateShopActivity.this, setLang(key));
        Language.CommanList commanList = language.getData();
        if (setLang(key).size() > 0) {
            if (commanList.getArrayList().size() > 0 && commanList.getArrayList() != null) {
                binding.tvScreenHeadingEditshop.setText("" + commanList.getArrayList().get(0));
                binding.tvShopNameTitleEditshop.setText("" + commanList.getArrayList().get(1));
                binding.edtShopKeeperNameUpdateshop.setHint("" + commanList.getArrayList().get(2));
                binding.tvAddressTitleEditshop.setText("" + commanList.getArrayList().get(3));
                binding.edtShopAddressUpdateshop.setHint("" + commanList.getArrayList().get(4));
                binding.tvContactPersonNameTitleEditshop.setText("" + commanList.getArrayList().get(5));
                binding.edtShopKeeperNameUpdateshop.setHint("" + commanList.getArrayList().get(6));
                binding.tvContact1TitleEditshop.setText("" + commanList.getArrayList().get(7));
                binding.edtShopKeeperNo1Updateshop.setHint("" + commanList.getArrayList().get(8));
                binding.tvContact2TitleEditshop.setText("" + commanList.getArrayList().get(9));
                binding.edtShopKeeperNo2Updateshop.setHint("" + commanList.getArrayList().get(10));
                binding.tvShopcategoryTitleEditshop.setText("" + commanList.getArrayList().get(11));
                binding.tvInvoicetypeTitleEditshop.setText("" + commanList.getArrayList().get(12));
                binding.tvGstnoTitleEditshop.setText("" + commanList.getArrayList().get(13));
                binding.edtShopGstnoUpdateshop.setHint("" + commanList.getArrayList().get(14));
                binding.btnVerifyGstnoUpdateshop.setText("" + commanList.getArrayList().get(15));
                binding.btnUpdateShop.setText("" + commanList.getArrayList().get(16));
                binding.btnDeleteShop.setText("" + commanList.getArrayList().get(17));
            }
        }
    }

    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "11");

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
        Cursor cur = db.viewMultiLangSuchna("11");

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

                builder = new AlertDialog.Builder(UpdateShopActivity.this);
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
