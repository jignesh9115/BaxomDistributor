package com.jp.baxomdistributor.Activities;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.FileUtils;
import com.jp.baxomdistributor.databinding.ActivityMainBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;
import com.jp.baxomdistributor.databinding.LayoutBotttomSheetBinding;
import com.jp.baxomdistributor.ui.delivered_sales_orders.DeliveredSalesOrdersFragment;
import com.jp.baxomdistributor.ui.home.HomeFragment;
import com.jp.baxomdistributor.ui.my_stock_statement.MyStockStatementFragment;
import com.jp.baxomdistributor.ui.mypurchaseorder.MyPurchaseOrderFragment;
import com.jp.baxomdistributor.ui.undelivered_sales_orders.UndeliveredSalesOrdersFragment;
import com.jp.baxomdistributor.ui.undeliveredordersnew.UndeliveredSalesOrdersNewFragment;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    Toolbar toolbar;
    LayoutBotttomSheetBinding binding1;

    String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA};


    SharedPreferences sp, sp_distributor_detail, sp_login, sp_multi_lang;
    Database db;
    ArrayList<String> arrayList_lang_desc;
    Menu drawer_menu;
    Language.CommanList commanList;

    BottomSheetDialog bottomSheetDialog;

    AppUpdateManager appUpdateManager;
    private static final int REQUEST_UPDATE_APP = 1001;
    AlertDialog ad_net_connection;
    AlertDialog.Builder builder;
    private IntentFilter minIntentFilter;
    NetConnetionService mservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_undelivered_sales_orders,
                R.id.nav_undelivered_sales_orders_new,
                R.id.nav_delivered_sales_orders,
                //R.id.nav_my_sales_orders_dist,
                //R.id.nav_delivery_fail_sales_orders,
                //R.id.nav_my_pending_purchase_orders,
                //R.id.nav_add_new_purchase_orders,
                R.id.nav_my_purchase_order,
                //R.id.nav_my_fullfilled_purchase_orders,
                R.id.nav_my_stock_statement,
                R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //==============code for check quick response starts==========
        minIntentFilter = new IntentFilter();
        minIntentFilter.addAction(ConstantVariables.BroadcastStringForAction);

        Intent serviceIntent = new Intent(this, NetConnetionService.class);
        if (SDK_INT >= Build.VERSION_CODES.O) {
            //startForegroundService(this,serviceIntent);
            ContextCompat.startForegroundService(this, serviceIntent);
        } else {
            startService(serviceIntent);
        }


        if (!isOnline(getApplicationContext()))
            connctionDialog();
        else {

            if (ad_net_connection != null && ad_net_connection.isShowing()) {
                ad_net_connection.dismiss();
                ad_net_connection = null;
            }
        }
        //==============code for check quick response ends============

        sp = getSharedPreferences("salesman_detail", Context.MODE_PRIVATE);
        sp_distributor_detail = getSharedPreferences("distributor_detail", Context.MODE_PRIVATE);
        sp_login = getSharedPreferences("login_detail", Context.MODE_PRIVATE);

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());
        updateApp();

        drawer_menu = navigationView.getMenu();

        try {

            Language language = new Language(sp_multi_lang.getString("lang", ""),
                    MainActivity.this, setLang(sp_multi_lang.getString("lang", "")));
            commanList = language.getData();

            drawer_menu.findItem(R.id.nav_home).setTitle("" + commanList.getArrayList().get(0));
            drawer_menu.findItem(R.id.nav_undelivered_sales_orders).setTitle("" + commanList.getArrayList().get(1));
            drawer_menu.findItem(R.id.nav_undelivered_sales_orders_new).setTitle("" + commanList.getArrayList().get(12));
            drawer_menu.findItem(R.id.nav_delivered_sales_orders).setTitle("" + commanList.getArrayList().get(2));
            //drawer_menu.findItem(R.id.nav_my_sales_orders_dist).setTitle("" + commanList.getArrayList().get(11));
            //drawer_menu.findItem(R.id.nav_delivery_fail_sales_orders).setTitle("" + commanList.getArrayList().get(3));
            //drawer_menu.findItem(R.id.nav_my_pending_purchase_orders).setTitle("" + commanList.getArrayList().get(4));
            //drawer_menu.findItem(R.id.nav_add_new_purchase_orders).setTitle("" + commanList.getArrayList().get(5));
            drawer_menu.findItem(R.id.nav_my_purchase_order).setTitle("" + commanList.getArrayList().get(6));
            //drawer_menu.findItem(R.id.nav_my_fullfilled_purchase_orders).setTitle("" + commanList.getArrayList().get(7));
            drawer_menu.findItem(R.id.nav_my_stock_statement).setTitle("" + commanList.getArrayList().get(8));
            drawer_menu.findItem(R.id.nav_logout).setTitle("" + commanList.getArrayList().get(9));
            drawer_menu.findItem(R.id.nav_change_lang).setTitle("" + commanList.getArrayList().get(10));

            binding.appBarMain.toolbar.setTitle(commanList.getArrayList().get(0) + "");

            /*if (sp_distributor_detail.getString("parent_id", "").equalsIgnoreCase("0"))
                drawer_menu.findItem(R.id.nav_my_sales_orders_dist).setVisible(false);*/


            navigationView.setNavigationItemSelectedListener(item -> {

                if (item.getItemId() == R.id.nav_home) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_main, new HomeFragment()).commit();
                    binding.appBarMain.toolbar.setTitle(commanList.getArrayList().get(0) + "");
                    drawer.close();

                } else if (item.getItemId() == R.id.nav_undelivered_sales_orders) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_main, new UndeliveredSalesOrdersFragment()).commit();
                    binding.appBarMain.toolbar.setTitle(commanList.getArrayList().get(1) + "");
                    drawer.close();

                } else if (item.getItemId() == R.id.nav_undelivered_sales_orders_new) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_main, new UndeliveredSalesOrdersNewFragment()).commit();
                    binding.appBarMain.toolbar.setTitle("" + commanList.getArrayList().get(12));
                    drawer.close();

                } else if (item.getItemId() == R.id.nav_delivered_sales_orders) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_main, new DeliveredSalesOrdersFragment()).commit();
                    binding.appBarMain.toolbar.setTitle(commanList.getArrayList().get(2) + "");
                    drawer.close();

                }/* else if (item.getItemId() == R.id.nav_my_sales_orders_dist) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_main, new MySalesOrderDistFragment()).commit();
                    binding.appBarMain.toolbar.setTitle(commanList.getArrayList().get(11) + "");
                    drawer.close();

                }*/ else if (item.getItemId() == R.id.nav_my_purchase_order) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_main, new MyPurchaseOrderFragment()).commit();
                    binding.appBarMain.toolbar.setTitle(commanList.getArrayList().get(6) + "");
                    drawer.close();

                } else if (item.getItemId() == R.id.nav_my_stock_statement) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_main, new MyStockStatementFragment()).commit();
                    binding.appBarMain.toolbar.setTitle(commanList.getArrayList().get(8) + "");
                    drawer.close();

                }


                return false;
            });

        } catch (Exception e) {
            e.getMessage();
        }

        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }

        if (!AskPermissions(MainActivity.this, permissionsRequired)) {
            ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, 1);
        } else {
            MakeDirectory();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logoutdistributor(MenuItem item) {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();

        SharedPreferences.Editor editor1 = sp_distributor_detail.edit();
        editor1.clear();
        editor1.apply();

        SharedPreferences.Editor editor2 = sp_login.edit();
        editor2.clear();
        editor2.apply();

    }

    public void changeLanguage(MenuItem item) {


        bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialogTheme);

        binding1 = LayoutBotttomSheetBinding.inflate(LayoutInflater.from(MainActivity.this));
        View view = binding1.getRoot();

        if (sp_multi_lang.getString("lang", "").equalsIgnoreCase("ENG"))
            binding1.rbEng.setChecked(true);
        else if (sp_multi_lang.getString("lang", "").equalsIgnoreCase("GUJ"))
            binding1.rbGuj.setChecked(true);
        else if (sp_multi_lang.getString("lang", "").equalsIgnoreCase("HINDI"))
            binding1.rbHindi.setChecked(true);

        Language language = new Language(sp_multi_lang.getString("lang", ""), MainActivity.this, setLangBottomSheet(sp_multi_lang.getString("lang", "")));
        Language.CommanList commanList = language.getData();

        binding1.tvChangeLangbottomsheetTitle.setText("" + commanList.getArrayList().get(0));
        binding1.rbEng.setText("" + commanList.getArrayList().get(1));
        binding1.rbGuj.setText("" + commanList.getArrayList().get(2));
        binding1.rbHindi.setText("" + commanList.getArrayList().get(3));
        binding1.btnChangeLang.setText("" + commanList.getArrayList().get(4));


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

                Intent intent = getIntent();
                finishAffinity();
                startActivity(intent);

            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
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

    //================================make directory  code start ============================

    public void MakeDirectory() {

        //File file = new File(Environment.getExternalStorageDirectory() + "/Baxom Distribution");
        File file = new File(FileUtils.MAIN_FOLDER_PATH);
        if (!file.exists()) {

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(FileUtils.MAIN_FOLDER_PATH);
            myDir.mkdirs();

            Log.i("directory===>", "making successfuly");
        } else {

            Log.i("Directory is", "Exist");

        }


        //File file1 = new File(Environment.getExternalStorageDirectory() + "/Baxom Distribution/" + "Order List");
        File file1 = new File(FileUtils.ORDER_PDF_FOLDER_PATH);
        if (!file1.exists()) {

            String root = Environment.getExternalStorageDirectory().toString();
            //File myDir = new File(root + "/Baxom Distribution/" + "Order List");
            File myDir = new File(FileUtils.ORDER_PDF_FOLDER_PATH);
            myDir.mkdirs();

            Log.i("directory===>", "making successfuly");

        } else {

            Log.i("Directory is", "Exist");

        }

    }

    //================================make directory  code ends ============================

    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "51");

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

    public ArrayList<String> setLangActionMenu(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "40");

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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing())
            bottomSheetDialog.dismiss();

    }

    private void updateApp() {

        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            MainActivity.this,
                            // Include a request code to later monitor this update request.
                            REQUEST_UPDATE_APP);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UPDATE_APP) {

            Toast.makeText(this, "Start Downloading...", Toast.LENGTH_SHORT).show();

            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Downloads Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void connctionDialog() {

        try {
            if (ad_net_connection == null) {

                DialogNetworkErrorBinding neterrorBinding;

                builder = new AlertDialog.Builder(MainActivity.this);
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


                boolean isdiff_suchana = false, isdiff_lang = false;
                db.open();
                Cursor tot_local_suchana = db.viewMultiLangSuchna();
                Cursor tot_local_lang = db.viewAllLanguage();
                if (!sp_login.getString("tot_suchna", "")
                        .equalsIgnoreCase(String.valueOf(tot_local_suchana.getCount())))
                    isdiff_suchana = true;

                if (!sp_login.getString("tot_lang", "")
                        .equalsIgnoreCase(String.valueOf(tot_local_lang.getCount())))
                    isdiff_lang = true;

                db.close();

                if (isdiff_lang) {

                    Intent intent1 = new Intent(MainActivity.this, RefreshDataActivity.class);
                    intent1.putExtra("action", "Update_Lang");
                    startActivity(intent1);

                } else if (isdiff_suchana) {

                    Intent intent1 = new Intent(MainActivity.this, RefreshDataActivity.class);
                    intent1.putExtra("action", "Update_Suchana");
                    startActivity(intent1);

                } else if (isdiff_suchana && isdiff_lang) {

                    Intent intent1 = new Intent(MainActivity.this, RefreshDataActivity.class);
                    intent1.putExtra("action", "Update_Lang_Suchana");
                    startActivity(intent1);

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
        updateApp();
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }
        MakeDirectory();
    }
}