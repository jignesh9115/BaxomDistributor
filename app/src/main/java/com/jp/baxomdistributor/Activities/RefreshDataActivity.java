package com.jp.baxomdistributor.Activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.ActivityRefreshDataBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;
import com.jp.baxomdistributor.databinding.LayoutBotttomSheetBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RefreshDataActivity extends AppCompatActivity {

    ActivityRefreshDataBinding binding;

    String TAG = getClass().getSimpleName();
    String url = "", response = "", multi_lang_url = "", multi_lang_response = "", suchana_list_url = "", suchana_list_response = "";
    String name = "", total_photos = "", action = "";

    int isdismissDialog = 0;
    ArrayList<Long> reaquestIds_list = new ArrayList<>();

    boolean showLangDialog = false;
    SharedPreferences sp, sp_update_data, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanSuchnaList;
    LayoutBotttomSheetBinding binding1;

    BottomSheetDialog bottomSheetDialog;

    AlertDialog ad, ad_net_connection;
    AlertDialog.Builder builder;

    File mydir;
    private IntentFilter minIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRefreshDataBinding.inflate(getLayoutInflater());
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

        sp_update_data = getSharedPreferences("update_data", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mydir = new File(Environment.getExternalStorageDirectory(), "Pictures/Baxom Health Care/Product Photos");

        Log.i(TAG, "isProdUpdate=>" + sp_update_data.getBoolean("isProdUpdate", false));
        Log.i(TAG, "isSuchanaUpdate=>" + sp_update_data.getBoolean("isSuchanaUpdate", false));
        Log.i(TAG, "isMulti_lang_update=>" + sp_update_data.getBoolean("isMulti_lang_update", false));

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        if (checkLangTable() > 0)
            setLanguage(sp_multi_lang.getString("lang", ""));

        Language language = new Language(sp_multi_lang.getString("lang", ""),
                RefreshDataActivity.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language.getData();


        Bundle bundle = getIntent().getExtras();
        action = bundle.getString("action", "");

        Log.i(TAG, "action==>" + action);


        if (action.equalsIgnoreCase("Update_Lang")) {

            Log.i(TAG, "<...inside update language action....>");
            binding.btnUpdateLang.setEnabled(false);
            showLangDialog = true;
            db.open();
            db.deleteMultiLang();
            db.close();
            new getmultilangTask().execute();

        } else if (action.equalsIgnoreCase("Update_Suchana")) {
            Log.i(TAG, "<...inside update suchana action....>");
            binding.btnUpdateSuchana.setEnabled(false);

            db.open();
            db.deleteMultiLangSuchna();
            db.close();
            new SuchanaListTask().execute();

            isdismissDialog = 0;
            showCompleteDialog();

        } else if (action.equalsIgnoreCase("Update_Lang_Suchana")) {
            Log.i(TAG, "<...inside update language & suchana action....>");
            binding.btnUpdateLang.setEnabled(false);
            binding.btnUpdateSuchana.setEnabled(false);
            showLangDialog = true;
            db.open();
            db.deleteMultiLang();
            db.deleteMultiLangSuchna();
            db.close();
            new getmultilangTask().execute();
            new SuchanaListTask().execute();

            /*isdismissDialog = 0;
            showCompleteDialog();*/
        }


        if (action.equalsIgnoreCase("Distributor")) {

            binding.btnUpdatePhotos.setVisibility(View.GONE);

            if (sp_update_data.getBoolean("isMulti_lang_update", false)) {

                SharedPreferences.Editor editor = sp_update_data.edit();
                editor.putBoolean("isMulti_lang_update", false);
                editor.apply();

                binding.btnUpdateLang.setEnabled(false);
                showLangDialog = true;

                db.open();
                db.deleteMultiLang();
                db.close();
                new getmultilangTask().execute();
            }

            if (sp_update_data.getBoolean("isSuchanaUpdate", false)) {

                SharedPreferences.Editor editor = sp_update_data.edit();
                editor.putBoolean("isSuchanaUpdate", false);
                editor.apply();

                binding.btnUpdateSuchana.setEnabled(false);

                showLangDialog = true;
                db.open();
                db.deleteMultiLangSuchna();
                db.close();
                new SuchanaListTask().execute();
            }


        } else {

            if (sp_update_data.getBoolean("isProdUpdate", false)) {

                SharedPreferences.Editor editor = sp_update_data.edit();
                editor.putBoolean("isProdUpdate", false);
                editor.apply();

                deleteRecursive(mydir);
                Log.i(TAG, "olddir=>" + mydir + " is deleted=>" + new File(String.valueOf(mydir)).delete());

                if (!mydir.exists()) {
                    mydir.mkdirs();
                    new downloadProductPhotosTask().execute();
                }

                binding.btnUpdatePhotos.setEnabled(false);

            } else if (!mydir.exists()) {

                deleteRecursive(mydir);

                if (!mydir.exists()) {
                    mydir.mkdirs();
                    new downloadProductPhotosTask().execute();
                }

                binding.btnUpdatePhotos.setEnabled(false);

            }

            if (sp_update_data.getBoolean("isSuchanaUpdate", false)) {

                SharedPreferences.Editor editor = sp_update_data.edit();
                editor.putBoolean("isSuchanaUpdate", false);
                editor.apply();

                binding.btnUpdateSuchana.setEnabled(false);

                showLangDialog = true;
                db.open();
                db.deleteMultiLangSuchna();
                db.close();
                new SuchanaListTask().execute();
            }

            if (sp_update_data.getBoolean("isMulti_lang_update", false)) {

                SharedPreferences.Editor editor = sp_update_data.edit();
                editor.putBoolean("isMulti_lang_update", false);
                editor.apply();

                binding.btnUpdateLang.setEnabled(false);
                showLangDialog = true;
                db.open();
                db.deleteMultiLang();
                db.close();
                new getmultilangTask().execute();
            }
        }


        binding.btnUpdatePhotos.setOnClickListener(v -> {

            binding.btnUpdatePhotos.setEnabled(false);

            deleteRecursive(mydir);
            Log.i(TAG, "olddir=>" + mydir + " is deleted=>" + new File(String.valueOf(mydir)).delete());

            if (!mydir.exists()) {
                mydir.mkdirs();
                new downloadProductPhotosTask().execute();
            }
        });


        binding.btnUpdateSuchana.setOnClickListener(v -> {

            binding.btnUpdateSuchana.setEnabled(false);

            db.open();
            db.deleteMultiLangSuchna();
            db.close();
            new SuchanaListTask().execute();

            isdismissDialog = 0;
            showCompleteDialog();

        });


        binding.btnUpdateLang.setOnClickListener(v -> {


            binding.btnUpdateLang.setEnabled(false);
            showLangDialog = true;
            db.open();
            db.deleteMultiLang();
            db.close();
            new getmultilangTask().execute();

        });


        binding.btnChangeLang.setOnClickListener(v -> changeLanguage());

        binding.imgBackRefreshdata.setOnClickListener(v -> {
            startActivity(new Intent(RefreshDataActivity.this, MainActivity.class));
            finish();
        });
    }


    public class downloadProductPhotosTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           /* pdialog = new ProgressDialog(getActivity());
            pdialog.setTitle("Loading...");
            pdialog.setMessage("Downloading");
            pdialog.setCancelable(false);
            pdialog.show();*/

            SharedPreferences.Editor editor = sp_update_data.edit();
            editor.putBoolean("isDownloading", true);
            editor.apply();

            binding.btnChangeLang.setEnabled(false);
            binding.cardDownloadingPhotos.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... voids) {

            url = getString(R.string.Base_URL) + "productlisting.php";
            //url = "http://192.168.43.193/Baxom/API/productlisting.php";
            Log.i(TAG, "load_photos_url=>" + url);

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "load_photos_res=>" + response);
                getProductPhotos();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

           /* new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pdialog.setMessage("Please wait until complete downloads");
                }
            }, 15000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (pdialog.isShowing()) {
                        pdialog.dismiss();
                    }
                }
            }, 30000);*/

        }
    }

    private void getProductPhotos() {

        try {

            JSONObject jsonObject = new JSONObject(response + "");
            JSONObject data_object = jsonObject.getJSONObject("data");

            JSONArray prod_detail_array = data_object.getJSONArray("prod_detail");

            total_photos = String.valueOf(prod_detail_array.length());

            reaquestIds_list = new ArrayList<>();

            for (int i = 0; i < prod_detail_array.length(); i++) {

                JSONObject prod_detail_object = prod_detail_array.getJSONObject(i);

                String[] parts = prod_detail_object.getString("name").split("\\."); // escape .
                name = parts[0];

                //Log.i(TAG, "name=>" + name);

                //File mydir = new File(Environment.getExternalStorageDirectory(), "/Baxom Distribution/Products-" + gDateTime.getDateymd() + "/");
                File mydir = new File(Environment.DIRECTORY_PICTURES, "/Baxom Distribution/Product Photos");

                if (!mydir.exists()) {
                    mydir.mkdirs();
                }


                //Log.i(TAG, "prod_photo_name=>" + prod_detail_object.getString("name"));

                /*File prodImageFile = new File(Environment.getExternalStorageDirectory(), "Pictures/Baxom Health Care/Product Photos/" + prod_detail_object.getString("name"));
                //Log.i(TAG, "prodImageFile==>" + "name");
                Log.i(TAG, "isExit==>" + prodImageFile.exists());

                if (!prodImageFile.exists()) {*/

                if (!prod_detail_object.getString("photo").equalsIgnoreCase("")) {

                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri downloadUri = Uri.parse(prod_detail_object.getString("photo"));
                    DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
                    String date = dateFormat.format(new Date());

                    request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            //.setAllowedOverRoaming(false)
                            //.setTitle("Downloading...")
                            //.setAllowedOverMetered(true)
                            //.setAllowedOverRoaming(true)
                            //.setTitle("Downloading Images-" + name)
                            .setVisibleInDownloadsUi(true)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "/Baxom Distribution/Product Photos/" + name + ".png");

                    reaquestIds_list.add(manager.enqueue(request));

                }
                //}

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {

            // get the refid from the download manager
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            //Log.i(TAG, "inside BroadcastReceiver==>" + referenceId);
            reaquestIds_list.remove(referenceId);
            //Log.i(TAG, "reaquestIds_list==>" + reaquestIds_list.size());

            binding.tvRemaingPhotos.setText(reaquestIds_list.size() + "/" + total_photos);

            if (reaquestIds_list.size() == 0) {

                SharedPreferences.Editor editor = sp_update_data.edit();
                editor.putBoolean("isDownloading", false);
                editor.apply();

                binding.btnChangeLang.setEnabled(true);
                binding.btnUpdatePhotos.setVisibility(View.GONE);
                binding.cardDownloadingPhotos.setVisibility(View.GONE);
                binding.llCompletePhotos.setVisibility(View.VISIBLE);
                showCompleteDialog();

            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onComplete);
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing())
            bottomSheetDialog.dismiss();

    }

    public void showCompleteDialog() {

        if (isdismissDialog == 0) {
            isdismissDialog = 1;
            builder = new AlertDialog.Builder(RefreshDataActivity.this);
            builder.setTitle(commanSuchnaList.getArrayList().get(0) + "");
            builder.setMessage(commanSuchnaList.getArrayList().get(1) + "");
            builder.setCancelable(false);
            builder.setPositiveButton(commanSuchnaList.getArrayList().get(2) + "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ad.dismiss();

                   /* if (action.equalsIgnoreCase("Distributor")) {

                        startActivity(new Intent(RefreshDataActivity.this, DistributorActivity.class));
                        finish();

                    } else {

                        startActivity(new Intent(RefreshDataActivity.this, MainActivity.class));
                        finish();
                    }*/

                }
            });

            ad = builder.create();
            ad.show();

            ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

    }


    //==============================delete folder with content code starts =========================

    //first of delete all content than delete folder

    void deleteRecursive(File fileOrDirectory) {
        try {

            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);
            fileOrDirectory.delete();

        } catch (Exception e) {
            e.getMessage();
        }
    }

    //==============================delete folder with content code ends ===========================


    //================================Load all suchana task starts ======================

    public class SuchanaListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.cardDownloadingSuchana.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            suchana_list_url = getString(R.string.Base_URL) + getString(R.string.get_suchna_list_url);

            Log.i(TAG, "suchana_list_url=>" + suchana_list_url);

            HttpHandler httpHandler = new HttpHandler();
            suchana_list_response = httpHandler.makeServiceCall(suchana_list_url);

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "suchana_list_response=>" + suchana_list_response);
                getSuchanaList();

                if (showLangDialog)
                    showCompleteDialog();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }

    private void getSuchanaList() {

        try {

            JSONObject object = new JSONObject(suchana_list_response + "");
            JSONArray jsonArray = object.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject data_object = jsonArray.getJSONObject(i);

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

            binding.btnUpdateSuchana.setVisibility(View.GONE);
            binding.cardDownloadingSuchana.setVisibility(View.GONE);
            binding.llCompleteSuchana.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //================================Load all suchana task ends ========================


    //================================Load all multi language task starts ========================

    public class getmultilangTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.cardDownloadingLang.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            multi_lang_url = getString(R.string.Base_URL) + getString(R.string.view_all_multi_language_url);
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

                if (showLangDialog)
                    showCompleteDialog();
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

            binding.btnUpdateLang.setVisibility(View.GONE);
            binding.cardDownloadingLang.setVisibility(View.GONE);
            binding.llCompleteLang.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //================================Load all multi language task ends ==========================

    public void setLanguage(String key) {

        Language language = new Language(key, RefreshDataActivity.this, setLang(key));
        Language.CommanList commanList = language.getData();
        if (setLang(key).size() > 0) {
            if (commanList.getArrayList() != null && commanList.getArrayList().size() > 0) {
                binding.tvDownloadingphotosTitleDm.setText("" + commanList.getArrayList().get(0));
                binding.tvPhotosdownloadcompleteTitleDm.setText("" + commanList.getArrayList().get(1));
                binding.btnUpdatePhotos.setText("" + commanList.getArrayList().get(2));
                binding.tvDownloadingsuchanaTitleDm.setText("" + commanList.getArrayList().get(3));
                binding.tvSuchanadownloadcompleteTitleDm.setText("" + commanList.getArrayList().get(4));
                binding.btnUpdateSuchana.setText("" + commanList.getArrayList().get(5));
                binding.btnUpdateLang.setText("" + commanList.getArrayList().get(6));
                binding.tvScreenHeadingDownmanager.setText("" + commanList.getArrayList().get(7));
                binding.tvDownloadinglangTitleDm.setText("" + commanList.getArrayList().get(8));
                binding.tvLangdownloadcompleteTitleDm.setText("" + commanList.getArrayList().get(9));
                binding.btnChangeLang.setText("" + commanList.getArrayList().get(10));
            }
        }
    }

    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "41");

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

    public int checkLangTable() {
        int count = 0;

        db.open();
        Cursor cur = db.viewLanguage("ENG", "41");
        count = cur.getCount();
        db.close();

        return count;
    }


    public void changeLanguage() {


        bottomSheetDialog = new BottomSheetDialog(RefreshDataActivity.this, R.style.BottomSheetDialogTheme);

        binding1 = LayoutBotttomSheetBinding.inflate(LayoutInflater.from(RefreshDataActivity.this));
        View view = binding1.getRoot();

        if (sp_multi_lang.getString("lang", "").equalsIgnoreCase("ENG"))
            binding1.rbEng.setChecked(true);
        else if (sp_multi_lang.getString("lang", "").equalsIgnoreCase("GUJ"))
            binding1.rbGuj.setChecked(true);
        else if (sp_multi_lang.getString("lang", "").equalsIgnoreCase("HINDI"))
            binding1.rbHindi.setChecked(true);

        Language language = new Language(sp_multi_lang.getString("lang", ""), RefreshDataActivity.this, setLangBottomSheet(sp_multi_lang.getString("lang", "")));
        Language.CommanList commanList = language.getData();
        if (commanList.getArrayList() != null && commanList.getArrayList().size() > 0) {
            binding1.tvChangeLangbottomsheetTitle.setText("" + commanList.getArrayList().get(0));
            binding1.rbEng.setText("" + commanList.getArrayList().get(1));
            binding1.rbGuj.setText("" + commanList.getArrayList().get(2));
            binding1.rbHindi.setText("" + commanList.getArrayList().get(3));
            binding1.btnChangeLang.setText("" + commanList.getArrayList().get(4));
        }

        binding1.btnChangeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding1.rbEng.isChecked()) {

                    SharedPreferences.Editor editor1 = sp_multi_lang.edit();
                    editor1.putString("lang", "ENG");
                    editor1.apply();

                } else if (binding1.rbGuj.isChecked()) {

                    SharedPreferences.Editor editor1 = sp_multi_lang.edit();
                    editor1.putString("lang", "GUJ");
                    editor1.apply();

                } else if (binding1.rbHindi.isChecked()) {

                    SharedPreferences.Editor editor1 = sp_multi_lang.edit();
                    editor1.putString("lang", "HINDI");
                    editor1.apply();

                }

                if (action.equalsIgnoreCase("Distributor")) {

                    startActivity(new Intent(RefreshDataActivity.this, MainActivity.class));
                    finish();

                } /*else {

                    startActivity(new Intent(RefreshDataActivity.this, MainActivity.class));
                    finish();
                }*/

            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }

    public ArrayList<String> setLangBottomSheet(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "42");

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
        Cursor cur = db.viewMultiLangSuchna("41");

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
    public void onBackPressed() {
        super.onBackPressed();

        if (action.equalsIgnoreCase("Distributor")) {

            startActivity(new Intent(RefreshDataActivity.this, MainActivity.class));
            finish();

        }/* else {

            startActivity(new Intent(RefreshDataActivity.this, MainActivity.class));
            finish();
        }*/
    }

    public void connctionDialog() {

        try {
            if (ad_net_connection == null) {

                DialogNetworkErrorBinding neterrorBinding;

                builder = new AlertDialog.Builder(RefreshDataActivity.this);
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