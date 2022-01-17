package com.jp.baxomdistributor.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.datatransport.backend.cct.BuildConfig;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.Api;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.Utils.PrefManager;
import com.jp.baxomdistributor.databinding.ActivityLoginBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    String url = "", response = "", username = "", password = "", enterusername = "", enterpassword = "";

    ProgressDialog pdialog;

    SharedPreferences sp, sp_distributor_detail, sp_login, sp_update_data, sp_download_manager, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Language.CommanList commanSuchnaList, commanupdateSuchnaList;
    Database db;


    String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA};


    AlertDialog ad, ad_net_connection;
    AlertDialog.Builder builder;

    int count = 0;
    String versionName = BuildConfig.VERSION_NAME;
    PrefManager prefManager;
    private IntentFilter minIntentFilter;

    Retrofit retrofit = null;
    Api api;

    String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.Base_URL))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

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
        if (!AskPermissions(LoginActivity.this, permissionsRequired)) {
            ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, 1);
        }
        prefManager = new PrefManager(LoginActivity.this, "salesman_detail");
        sp = getSharedPreferences("salesman_detail", Context.MODE_PRIVATE);
        sp_update_data = getSharedPreferences("update_data", Context.MODE_PRIVATE);
        sp_distributor_detail = getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);
        sp_login = getSharedPreferences("login_detail", Context.MODE_PRIVATE);
        sp_download_manager = getSharedPreferences("download_manager", Context.MODE_PRIVATE);

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        setLanguage(sp_multi_lang.getString("lang", ""));

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                LoginActivity.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language.getData();

        Language language1 = new Language(sp_multi_lang.getString("lang", ""),
                LoginActivity.this, setLangSuchnaUpdateDialog(sp_multi_lang.getString("lang", "")));
        commanupdateSuchnaList = language1.getData();

        binding.btnLogin.setOnClickListener(v -> {

            //showDialog();

            if (!AskPermissions(LoginActivity.this, permissionsRequired)) {
                ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, 1);
            } else {

                if (binding.edtUsername.getText().toString().length() > 0 || binding.edtPassword.getText().toString().length() > 0) {

                    enterusername = binding.edtUsername.getText().toString().trim();
                    enterpassword = binding.edtPassword.getText().toString().trim();
                    new getusernamepassTask().execute();

                } else {
                    Toast.makeText(LoginActivity.this, commanSuchnaList.getArrayList().get(0) + "", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //http://projects.drbbagpks.org/Baxom/API/user_login.php?login_username=divyesh&login_password=divyesh

    }


    //============================get username and password Task start==========================

    public class getusernamepassTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = new ProgressDialog(LoginActivity.this);
            pdialog.setMessage(commanSuchnaList.getArrayList().get(3) + "");
            pdialog.setCancelable(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            url = getString(R.string.Base_URL) +
                    "user_login.php?login_username=" + enterusername +
                    "&login_password=" + enterpassword;
            Log.i(TAG, "Login url=>" + url);

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Toast.makeText(LoginActivity.this, "response=>" + response, Toast.LENGTH_SHORT).show();
            try {
                Log.i(TAG, "response=>" + response);

                if (response.equalsIgnoreCase("null")) {
                    Toast.makeText(LoginActivity.this, commanSuchnaList.getArrayList().get(1) + "", Toast.LENGTH_SHORT).show();
                }

                getdata();

                if (pdialog.isShowing())
                    pdialog.dismiss();
            } catch (Exception e) {
                Log.i("TAG", e.getMessage());
            }
        }
    }

    public void getdata() {

        try {
            JSONObject jsonObject = new JSONObject(response + "");


            if (jsonObject.getString("data").equalsIgnoreCase("false")) {

                Toast.makeText(this, commanSuchnaList.getArrayList().get(2) + "", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Invalid Username/Password");

            } else {

                Log.i("Login", "Successfully...");

                JSONObject object = jsonObject.getJSONObject("data");

                JSONObject authrights_object = object.getJSONObject("authrights");

                if (jsonObject.getString("Saleslogin").equalsIgnoreCase("false")) {

                    SharedPreferences.Editor editor = sp_distributor_detail.edit();
                    editor.putString("distributor_id", object.getString("distributor_id"));
                    editor.putString("name", object.getString("name"));
                    editor.putString("address", object.getString("address"));
                    editor.putString("email_address", object.getString("email_address"));
                    editor.putString("parent_id", object.getString("parent_id"));
                    editor.putString("last_week_start", jsonObject.getString("last_week_start"));
                    editor.putString("last_week_end", jsonObject.getString("last_week_end"));
                    editor.apply();

                    SharedPreferences.Editor editor1 = sp_login.edit();
                    editor1.putString("username", object.getString("username"));
                    editor1.putString("password", object.getString("password"));
                    editor1.putString("login_person", "distributor");
                    editor1.putString("issubmit", authrights_object.getString("issubmit"));
                    editor1.putString("isverify", authrights_object.getString("isverify"));
                    editor1.putString("isauthentication", authrights_object.getString("isauthentication"));
                    editor1.putString("ispacking", authrights_object.getString("ispacking"));
                    editor1.putString("isshipping", authrights_object.getString("isshipping"));
                    editor1.putString("isshipped", authrights_object.getString("isshipped"));
                    editor1.putString("isupdateshop", authrights_object.getString("isupdateshop"));
                    editor1.putString("recess_time", jsonObject.getString("recess_time"));
                    JSONObject check_lang_update_object = jsonObject.getJSONObject("check_lang_update");
                    editor1.putString("tot_suchna", check_lang_update_object.getString("tot_suchna"));
                    editor1.putString("tot_lang", check_lang_update_object.getString("tot_lang"));
                    editor1.apply();

                    JSONObject last_update_object = jsonObject.getJSONObject("last_update");

                    SharedPreferences.Editor editor2 = sp_update_data.edit();

                    if (!last_update_object.getString("last_prod_update")
                            .equalsIgnoreCase(sp_update_data.getString("last_prod_update", ""))) {
                        editor2.putString("last_prod_update", last_update_object.getString("last_prod_update"));
                        editor2.putBoolean("isProdUpdate", true);
                        Log.i("LoginActivity", "last_prod_update");
                    } else {
                        editor2.putBoolean("isProdUpdate", false);
                    }

                    if (!last_update_object.getString("last_suchna_update")
                            .equalsIgnoreCase(sp_update_data.getString("last_suchana_update", ""))) {
                        editor2.putString("last_suchana_update", last_update_object.getString("last_suchna_update"));
                        editor2.putBoolean("isSuchanaUpdate", true);
                        Log.i("LoginActivity", "last_suchna_update");
                    } else {
                        editor2.putBoolean("isSuchanaUpdate", false);
                    }

                    if (!last_update_object.getString("last_multi_lang_update")
                            .equalsIgnoreCase(sp_update_data.getString("last_multi_lang_update", ""))) {
                        editor2.putString("last_multi_lang_update", last_update_object.getString("last_multi_lang_update"));
                        editor2.putBoolean("isMulti_lang_update", true);
                        Log.i("LoginActivity", "last_multi_lang_update");
                    } else {
                        editor2.putBoolean("isMulti_lang_update", false);
                    }

                    if (Integer.parseInt(versionName.replace(".", ""))
                            < Integer.parseInt(jsonObject.getString("avilable_version").replace(".", "")))
                        editor2.putString("is_avilable_version", "yes");
                    else
                        editor2.putString("is_avilable_version", "no");

                    editor2.apply();

                    //Toast.makeText(this, "Distributor", Toast.LENGTH_SHORT).show();

                    /*if (Integer.parseInt(versionName.replace(".", "")) < Integer.parseInt(jsonObject.getString("version").replace(".", ""))) {

                        showDialog();

                    } else {*/

                    if (sp_download_manager.getString("download_manager", "").equalsIgnoreCase("")) {

                        SharedPreferences.Editor editor3 = sp_download_manager.edit();
                        editor3.putString("download_manager", "download done");
                        editor3.apply();

                        Intent intent = new Intent(LoginActivity.this, RefreshDataActivity.class);
                        intent.putExtra("action", "Distributor");
                        startActivity(intent);
                        finish();

                    } else {

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                } else
                    Toast.makeText(this, commanSuchnaList.getArrayList().get(2) + "", Toast.LENGTH_LONG).show();
            }
            //}

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //============================get username and password Task end============================


    public void showDialog() {


        builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(commanupdateSuchnaList.getArrayList().get(0) + "");
        builder.setMessage(commanupdateSuchnaList.getArrayList().get(1) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanupdateSuchnaList.getArrayList().get(2) + "", (dialog, which) -> {
            //finish();

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=baxom&c=apps")));
                finish();
            } catch (Exception e) {
            }

        });
        builder.setNegativeButton(commanupdateSuchnaList.getArrayList().get(5) + "", (dialog, which) -> finish());
        ad = builder.create();
        ad.show();

        ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.colorwhite));
        ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorwhite));

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

    public void setLanguage(String key) {

        Language language = new Language(key, LoginActivity.this,
                setLang(key));
        Language.CommanList commanList = language.getData();
        if (setLang(key).size() > 0) {
            if (commanList.getArrayList() != null && commanList.getArrayList().size() > 0) {
                binding.edtUsername.setHint("" + commanList.getArrayList().get(0));
                binding.edtPassword.setHint("" + commanList.getArrayList().get(1));
                binding.btnLogin.setText("" + commanList.getArrayList().get(2));
            }
        }
    }

    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "6");

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
        Cursor cur = db.viewMultiLangSuchna("6");

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

    public ArrayList<String> setLangSuchnaUpdateDialog(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewMultiLangSuchna("52");

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

                builder = new AlertDialog.Builder(LoginActivity.this);
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

    public void updateIMEIno(String device_id) {

        Call<String> call = api.updateIMEIno(sp.getString("salesman_id", ""), device_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "update_imeino_req...>" + call.request());
                Log.i(TAG, "update_imeino_res...>" + response.body());
                if (response.body() != null && response.body().contains("Updated Successfully"))
                    Log.i(TAG, "update imeino successfully");
                else
                    Log.i(TAG, "something went wrong");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG, "onFailure...>" + t);
            }
        });

    }

    public void showWarningDialog(String msg) {

        builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(commanupdateSuchnaList.getArrayList().get(6) + "");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(commanupdateSuchnaList.getArrayList().get(8) + "", (dialog, which) -> {
            finish();

        });
        ad = builder.create();
        ad.show();

        //ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        //ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorwhite));

    }

}