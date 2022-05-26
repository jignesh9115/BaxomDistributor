package com.jp.baxomdistributor.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Utils.Api;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.Utils.PrefManager;
import com.jp.baxomdistributor.databinding.ActivitySplashBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private static final int DIALOG_ERROR_CONNECTION = 1;

    ActivitySplashBinding binding;
    String TAG = getClass().getSimpleName();
    String url = "", response = "", multi_lang_url = "", multi_lang_response = "", username = "", password = "", enterusername = "", enterpassword = "";

    ProgressDialog pdialog;

    SharedPreferences sp, sp_distributor_detail, sp_login, sp_update_data, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;

    AlertDialog ad;
    AlertDialog.Builder builder;


    Database db;
    Language.CommanList commanList;

    private FirebaseAnalytics mFirebaseAnalytics;
    String versionName = BuildConfig.VERSION_NAME;
    PrefManager prefManager;

    Retrofit retrofit = null;
    Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.Base_URL))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        prefManager = new PrefManager(SplashActivity.this, "salesman_detail");
        sp = getSharedPreferences("salesman_detail", Context.MODE_PRIVATE);
        sp_update_data = getSharedPreferences("update_data", Context.MODE_PRIVATE);
        sp_distributor_detail = getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);
        sp_login = getSharedPreferences("login_detail", Context.MODE_PRIVATE);
        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);

        db = new Database(getApplicationContext());

        db.open();
        Cursor cur = db.viewAllLanguage();
        if (cur.getCount() == 0)
            new getmultilangTask().execute();
        db.close();


        binding.tvVersion.setText("App Version : " + BuildConfig.VERSION_NAME);

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                SplashActivity.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanList = language.getData();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        animation.setDuration(2000);
        binding.imgLogo.startAnimation(animation);

        if (!isOnline(SplashActivity.this)) {
            showDialog(DIALOG_ERROR_CONNECTION); // displaying the created
        } else {
            new Handler().postDelayed(() -> {

                if (sp_login.getString("username", "").length() > 0 && sp_login.getString("password", "").length() > 0) {

                    enterusername = sp_login.getString("username", "").trim();
                    enterpassword = sp_login.getString("password", "").trim();
                    new getusernamepassTask().execute();

                } else {

                    if (sp_multi_lang.getString("lang", "").equalsIgnoreCase("")) {

                        startActivity(new Intent(SplashActivity.this, ChooseLanguageActivity.class));
                        finish();

                    } else {

                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }

            }, 5000);
        }


        /*CaocConfig.Builder.create().backgroundMode(0).trackActivities(true).minTimeBetweenCrashesMs(1000)
                .restartActivity(SplashActivity.class).errorActivity(CustomErrorActivity.class).apply();*/

    }


    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        // check ni otherwise if connection not there it will thrown an exception..

        if (ni != null && ni.isConnected())
            return true;
        else
            return false;
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case DIALOG_ERROR_CONNECTION:
                Log.i("inside net error", " " + DIALOG_ERROR_CONNECTION);
                return new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("Error !")
                        .setMessage("No internet connection.")
                        .setNegativeButton("ok", (dialog, id1) -> finish())

                        .setOnKeyListener((arg0, keyCode, event) -> {
                            // TODO Auto-generated method stub
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                finish();
                            }
                            return true;
                        }).create();

            default:
                return null;
        }

    }


    //============================get username and password Task start==========================

    public class getusernamepassTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*pdialog = new ProgressDialog(SplashActivity.this);
            pdialog.setMessage("please wait...");
            pdialog.setCancelable(false);
            pdialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //url = "http://192.168.43.193/Baxom/API/user_login.php?login_username=" + enterusername + "&login_password=" + enterpassword;
            url = getString(R.string.Base_URL) +
                    "user_login.php?login_username=" + enterusername +
                    "&login_password=" + enterpassword;
            Log.i(TAG, "Login url=>" + url + "");

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Toast.makeText(LoginActivity.this, "response=>" + response, Toast.LENGTH_SHORT).show();
            try {

                Log.i(TAG, "response=>" + response + "");
                if (response.contains("null"))
                    Toast.makeText(SplashActivity.this, commanList.getArrayList().get(3) + "", Toast.LENGTH_SHORT).show();

                getdata();

            } catch (Exception e) {
                //Toast.makeText(SplashActivity.this, "network issue...", Toast.LENGTH_SHORT).show();
            }


         /*   if (pdialog.isShowing()) {
                pdialog.dismiss();
            }*/
        }
    }

    public void getdata() {

        try {
            JSONObject jsonObject = new JSONObject(response + "");

            if (jsonObject.getString("data").equalsIgnoreCase("false")) {

                //Toast.makeText(this, commanList.getArrayList().get(4) + "", Toast.LENGTH_LONG).show();
                showWarningDialog(commanList.getArrayList().get(4) + "");
                SharedPreferences.Editor editor3 = sp_login.edit();
                editor3.putString("username", "");
                editor3.putString("password", "");
                editor3.apply();
                Log.i(TAG, "Invalid Username/Password");

            } else {

                Log.i("Login", "Successfully...");

                JSONObject object = jsonObject.getJSONObject("data");

                JSONObject authrights_object = object.getJSONObject("authrights");

                if (jsonObject.getString("Saleslogin").equalsIgnoreCase("false")) {

                    //Toast.makeText(this, "Distributor", Toast.LENGTH_SHORT).show();

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
                    editor1.putString("is_add_scheme_qty_oas", authrights_object.getString("is_add_scheme_qty_oas"));
                    editor1.putString("is_add_replace_qty_oas", authrights_object.getString("is_add_replace_qty_oas"));
                    editor1.putString("is_add_shortage_qty_oas", authrights_object.getString("is_add_shortage_qty_oas"));
                    editor1.putString("is_add_scheme_detail_oas", authrights_object.getString("is_add_scheme_detail_oas"));
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

                    /*if (Integer.parseInt(versionName.replace(".", "")) < Integer.parseInt(jsonObject.getString("version").replace(".", ""))) {

                        showDialog();

                    } else {*/

                    if (sp_update_data.getBoolean("isProdUpdate", false)
                            || sp_update_data.getBoolean("isSuchanaUpdate", false)
                            || sp_update_data.getBoolean("isMulti_lang_update", false)) {

                        Intent intent = new Intent(SplashActivity.this, RefreshDataActivity.class);
                        intent.putExtra("action", "Distributor");
                        startActivity(intent);
                        finish();

                    } else {

                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            //}

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //============================get username and password Task end============================


    public void showDialog() {


        builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(commanList.getArrayList().get(0) + "");
        builder.setMessage(commanList.getArrayList().get(1) + "");
        builder.setCancelable(false);
        builder.setPositiveButton(commanList.getArrayList().get(2) + "", (dialog, which) -> {
            //finish();

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=baxom&c=apps")));
                finish();
            } catch (Exception e) {
            }

        });
        builder.setNegativeButton(commanList.getArrayList().get(5) + "", (dialog, which) -> finish());
        ad = builder.create();
        ad.show();

        ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.colorwhite));
        ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorwhite));

    }


    //================================Load all multi language task starts ========================

    public class getmultilangTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            multi_lang_url = getString(R.string.Base_URL) + getString(R.string.viewallmulti_language_by_screen_id_url);
            Log.i(TAG, "multi_lang_url=>" + multi_lang_url);

            HttpHandler httpHandler = new HttpHandler();
            multi_lang_response = httpHandler.makeServiceCall(multi_lang_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "multi_lang_res=>" + multi_lang_response);
                getmultilang();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getmultilang() {

        try {

            JSONObject jsonObject = new JSONObject(multi_lang_response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                db.open();
                db.addMultiLanguage(object.getString("screen_id"),
                        object.getString("screen_name"),
                        object.getString("lang_eng"),
                        object.getString("lang_guj"),
                        object.getString("lang_hindi"));
                db.close();

            }

            JSONArray suchna_dataArray = jsonObject.getJSONArray("suchna_data");

            for (int i = 0; i < suchna_dataArray.length(); i++) {

                JSONObject data_object = suchna_dataArray.getJSONObject(i);

                db.open();
                db.addMultiSuchna(data_object.getString("screen_id"),
                        data_object.getString("screen_name"),
                        data_object.getString("suchna_title"),
                        data_object.getString("suchna_desc_eng"),
                        data_object.getString("suchna_desc_guj"),
                        data_object.getString("suchna_desc_hindi"),
                        data_object.getString("timestamp"));
                db.close();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //================================Load all multi language task ends ==========================

    public ArrayList<String> setLangSuchna(String key) {

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

        builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(commanList.getArrayList().get(6) + "");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(commanList.getArrayList().get(8) + "", (dialog, which) -> {
            finish();

        });
        ad = builder.create();
        ad.show();

        //ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        //ad.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorwhite));

    }
}
