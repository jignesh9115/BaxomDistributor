package com.jp.baxomdistributor.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.jp.baxomdistributor.Models.LocationBysalesIdsPOJO;
import com.jp.baxomdistributor.Models.LocationDataPOJO;
import com.jp.baxomdistributor.Models.ViewLocationPOJO;
import com.jp.baxomdistributor.MultiLanguageUtils.Language;
import com.jp.baxomdistributor.R;
import com.jp.baxomdistributor.Services.NetConnetionService;
import com.jp.baxomdistributor.Utils.ConstantVariables;
import com.jp.baxomdistributor.Utils.Database;
import com.jp.baxomdistributor.Utils.GDateTime;
import com.jp.baxomdistributor.Utils.HttpHandler;
import com.jp.baxomdistributor.databinding.ActivityMapsBinding;
import com.jp.baxomdistributor.databinding.DialogNetworkErrorBinding;
import com.jp.baxomdistributor.databinding.DialogShopDetailMapBinding;
import com.jp.baxomdistributor.databinding.EntityCustomeMarkerBinding;
import com.jp.baxomdistributor.databinding.EntityCustomeMarkerSalesTeamBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    ActivityMapsBinding binding;
    String TAG = getClass().getSimpleName();

    private GoogleMap mMap;

    String url = "", response = "", shop_lat, shop_long, shop_distance, order_lat, order_long;
    ArrayList<ViewLocationPOJO> arrayList_mapdata;
    ArrayList<String> arrayList_shop_name_for_pos;
    ViewLocationPOJO viewLocationPOJO;

    PolylineOptions polylineOptions;
    PolygonOptions polygonOptions;
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();

    double shop_latitude, shop_longitude;

    String order_date = "", salesman_id = "", salesman_Ids = "", view_shop_location_url = "", view_shop_location_response = "",
            bit_id = "", map_action = "", bit_salesman_id = "", jArray = "";

    ArrayList<LocationBysalesIdsPOJO> arrayList_salesman_location;
    LocationBysalesIdsPOJO locationBysalesIdsPOJO;

    ArrayList<LocationDataPOJO> arrayList_location_data;
    LocationDataPOJO locationDataPOJO;

    SharedPreferences sp, sp_multi_lang;
    ArrayList<String> arrayList_lang_desc;
    Database db;
    Language.CommanList commanSuchnaList;

    DatePickerDialog dp;
    GDateTime gDateTime = new GDateTime();
    Calendar cal;
    int m, y, d;

    int mode = 0;
    private boolean not_first_time_showing_info_window = false;

    AlertDialog ad, ad_net_connection;
    AlertDialog.Builder builder;

    ProgressDialog pdialog;
    Language.CommanList commanList;
    private IntentFilter minIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
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
        sp = getSharedPreferences("salesman_detail", Context.MODE_PRIVATE);
        salesman_id = sp.getString("salesman_id", "");

        sp_multi_lang = getSharedPreferences("Language", Context.MODE_PRIVATE);
        db = new Database(getApplicationContext());

        Language language = new Language(sp_multi_lang.getString("lang", ""), MapsActivity.this,
                setLang(sp_multi_lang.getString("lang", "")));
        commanList = language.getData();


        Language language1 = new Language(sp_multi_lang.getString("lang", ""),
                MapsActivity.this, setLangSuchna(sp_multi_lang.getString("lang", "")));
        commanSuchnaList = language1.getData();

        binding.tvTitleMap.setText("" + commanList.getArrayList().get(0));
        binding.tvSelectDateTitleMap.setText("" + commanList.getArrayList().get(1));


        binding.tvSalesmanNameMap.setText("" + sp.getString("salesman", ""));

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            //shop_title = bundle.getString("shop_title");
            //shop_latitude = Double.parseDouble(bundle.getString("shop_latitude"));
            //shop_longitude = Double.parseDouble(bundle.getString("shop_longitude"));

            map_action = bundle.getString("map_action", "");
            salesman_Ids = bundle.getString("salesman_Ids", "");
            bit_salesman_id = bundle.getString("salesman_id", "");
            order_date = bundle.getString("order_date", "");
            bit_id = bundle.getString("bit_id", "");
            shop_lat = bundle.getString("shop_lat", "");
            shop_long = bundle.getString("shop_long", "");
            shop_distance = bundle.getString("shop_distance", "");
            order_lat = bundle.getString("order_lat", "");
            order_long = bundle.getString("order_long", "");
            jArray = bundle.getString("jArray", "");

            binding.tvLocationDate.setText("" + bundle.getString("selected_date", ""));
            Log.i(TAG, "map_action==>" + map_action);
            Log.i(TAG, "salesman_Ids==>" + salesman_Ids);
            Log.i(TAG, "order_date==>" + order_date);
            Log.i(TAG, "bit_id==>" + bit_id);
            Log.i(TAG, "selected_date==>" + bundle.getString("selected_date", ""));


            /*if (map_action.equalsIgnoreCase("SALESTEAM")) {

                url = getString(R.string.Base_URL) + "viewlocationBysalemanId.php?salesman_id=" + salesman_Ids +
                        "&locationBydate=" + gDateTime.dmyToymd(bundle.getString("selected_date", ""));
                new getlocationbysalesIdsTask().execute(url);
            }*/

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.imgBackMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //binding.tvLocationDate.setText("" + gDateTime.getDatedmy());

        binding.tvLocationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                y = Integer.parseInt(gDateTime.getYear());
                m = Integer.parseInt(gDateTime.getMonth()) - 1;
                d = Integer.parseInt(gDateTime.getDay());

                dp = new DatePickerDialog(MapsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cal = Calendar.getInstance();
                        cal.set(year, month, dayOfMonth);
                        DateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        binding.tvLocationDate.setText("" + dff.format(cal.getTime()));

                        //url = "http://192.168.43.151/Baxom/API/viewlocationBysalemanId.php?salesman_id=31,2,3,4&locationBydate=" + df.format(cal.getTime());
                        url = getString(R.string.Base_URL) + "viewlocationBysalemanId.php?salesman_id=" + salesman_Ids +
                                "&locationBydate=" + df.format(cal.getTime());
                        new getlocationbysalesIdsTask().execute(url);

                    }
                }, y, m, d);
                dp.show();

            }
        });


        binding.imgChangeViewMap.setBackgroundResource(R.drawable.satelite_mode);
        binding.imgChangeViewMap.setTag(R.drawable.satelite_mode);

        binding.imgChangeViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mode == 0 && binding.imgChangeViewMap.getTag().equals(R.drawable.satelite_mode)) {

                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mode = 1;
                    binding.imgChangeViewMap.setBackgroundResource(R.drawable.roadmap_mode);
                    binding.imgChangeViewMap.setTag(R.drawable.roadmap_mode);

                } else if (mode == 1 && binding.imgChangeViewMap.getTag().equals(R.drawable.roadmap_mode)) {

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mode = 0;
                    binding.imgChangeViewMap.setBackgroundResource(R.drawable.satelite_mode);
                    binding.imgChangeViewMap.setTag(R.drawable.satelite_mode);
                }

            }
        });


       /* binding.cbShopPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!salesman_Ids.isEmpty() && order_date.isEmpty()) {

                    binding.imgBackMap.setVisibility(View.VISIBLE);
                    binding.llDateMap.setVisibility(View.VISIBLE);

                    //salesman my salesman team list screen
                    url = getString(R.string.Base_URL) + "viewlocationBysalemanId.php?salesman_id=" + salesman_Ids + "&locationBydate=" + gDateTime.getDateymd();
                    new getlocationbysalesIdsTask().execute(url);

                } else if (!order_date.isEmpty() && !salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getShopLocationsTask().execute();

                } else if (!bit_id.isEmpty() && order_date.isEmpty() && salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getBitWiseShopListTask().execute();

                } else {

                    //salesman my shop location screen
                    new getLocationDataTask().execute();

                }

            }
        });


        binding.cbShopName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!salesman_Ids.isEmpty() && order_date.isEmpty()) {

                    binding.imgBackMap.setVisibility(View.VISIBLE);
                    binding.llDateMap.setVisibility(View.VISIBLE);

                    //salesman my salesman team list screen
                    url = getString(R.string.Base_URL) + "viewlocationBysalemanId.php?salesman_id=" + salesman_Ids + "&locationBydate=" + gDateTime.getDateymd();
                    new getlocationbysalesIdsTask().execute(url);

                } else if (!order_date.isEmpty() && !salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getShopLocationsTask().execute();

                } else if (!bit_id.isEmpty() && order_date.isEmpty() && salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getBitWiseShopListTask().execute();

                } else {

                    //salesman my shop location screen
                    new getLocationDataTask().execute();

                }

            }
        });

        binding.cbSalesmanName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!salesman_Ids.isEmpty() && order_date.isEmpty()) {

                    binding.imgBackMap.setVisibility(View.VISIBLE);
                    binding.llDateMap.setVisibility(View.VISIBLE);

                    //salesman my salesman team list screen
                    url = getString(R.string.Base_URL) + "viewlocationBysalemanId.php?salesman_id=" + salesman_Ids + "&locationBydate=" + gDateTime.getDateymd();
                    new getlocationbysalesIdsTask().execute(url);

                } else if (!order_date.isEmpty() && !salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getShopLocationsTask().execute();

                } else if (!bit_id.isEmpty() && order_date.isEmpty() && salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getBitWiseShopListTask().execute();

                } else {

                    //salesman my shop location screen
                    new getLocationDataTask().execute();

                }

            }
        });

        binding.cbShopAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!salesman_Ids.isEmpty() && order_date.isEmpty()) {

                    binding.imgBackMap.setVisibility(View.VISIBLE);
                    binding.llDateMap.setVisibility(View.VISIBLE);

                    //salesman my salesman team list screen
                    url = getString(R.string.Base_URL) + "viewlocationBysalemanId.php?salesman_id=" + salesman_Ids + "&locationBydate=" + gDateTime.getDateymd();
                    new getlocationbysalesIdsTask().execute(url);

                } else if (!order_date.isEmpty() && !salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getShopLocationsTask().execute();

                } else if (!bit_id.isEmpty() && order_date.isEmpty() && salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getBitWiseShopListTask().execute();

                } else {

                    //salesman my shop location screen
                    new getLocationDataTask().execute();

                }

            }
        });


        binding.cbOrderLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!salesman_Ids.isEmpty() && order_date.isEmpty()) {

                    binding.imgBackMap.setVisibility(View.VISIBLE);
                    binding.llDateMap.setVisibility(View.VISIBLE);

                    //salesman my salesman team list screen
                    url = getString(R.string.Base_URL) + "viewlocationBysalemanId.php?salesman_id=" + salesman_Ids + "&locationBydate=" + gDateTime.getDateymd();
                    new getlocationbysalesIdsTask().execute(url);

                } else if (!order_date.isEmpty() && !salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getShopLocationsTask().execute();

                } else if (!bit_id.isEmpty() && order_date.isEmpty() && salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getBitWiseShopListTask().execute();

                } else {

                    //salesman my shop location screen
                    new getLocationDataTask().execute();

                }

            }
        });


        binding.cbBitName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!salesman_Ids.isEmpty() && order_date.isEmpty()) {

                    binding.imgBackMap.setVisibility(View.VISIBLE);
                    binding.llDateMap.setVisibility(View.VISIBLE);

                    //salesman my salesman team list screen
                    url = getString(R.string.Base_URL) + "viewlocationBysalemanId.php?salesman_id=" + salesman_Ids + "&locationBydate=" + gDateTime.getDateymd();
                    new getlocationbysalesIdsTask().execute(url);

                } else if (!order_date.isEmpty() && !salesman_Ids.isEmpty()) {

                    //distributor undelivered order screen
                    new getShopLocationsTask().execute();

                } else if (!bit_id.isEmpty() && order_date.isEmpty() && salesman_Ids.isEmpty()) {

                    binding.cbBitName.setVisibility(View.VISIBLE);

                    //distributor undelivered order screen
                    new getBitWiseShopListTask().execute();

                } else {

                    //salesman my shop location screen
                    new getLocationDataTask().execute();

                }

            }
        });*/


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng1));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng2));

        //mMap.setOnInfoWindowClickListener(this);

        if (!map_action.equalsIgnoreCase("SALESTEAM"))
            mMap.setOnMarkerClickListener(this);

        if (!salesman_Ids.isEmpty() && order_date.isEmpty()) {

            binding.imgBackMap.setVisibility(View.VISIBLE);
            binding.llDateMap.setVisibility(View.VISIBLE);

            binding.llChecckboxFilter.setVisibility(View.GONE);

            pdialog = new ProgressDialog(MapsActivity.this);
            pdialog.setTitle(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setMessage(commanSuchnaList.getArrayList().get(1) + "");
            pdialog.setCancelable(false);
            pdialog.show();

            //salesman my salesman team list screen
            url = getString(R.string.Base_URL) + "viewlocationBysalemanId.php?salesman_id=" + salesman_Ids +
                    "&locationBydate=" + gDateTime.dmyToymd(binding.tvLocationDate.getText().toString().trim());
            new getlocationbysalesIdsTask().execute(url);

        } else if (!order_date.isEmpty() && !salesman_Ids.isEmpty()) {
            pdialog = new ProgressDialog(MapsActivity.this);
            pdialog.setTitle(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setMessage(commanSuchnaList.getArrayList().get(1) + "");
            pdialog.setCancelable(false);
            pdialog.show();
            //distributor undelivered order screen
            new getShopLocationsTask().execute();

        } else if (!bit_id.isEmpty() && order_date.isEmpty() && salesman_Ids.isEmpty()) {

            pdialog = new ProgressDialog(MapsActivity.this);
            pdialog.setTitle(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setMessage(commanSuchnaList.getArrayList().get(1) + "");
            pdialog.setCancelable(false);
            pdialog.show();
            //distributor undelivered order screen
            new getBitWiseShopListTask().execute();

        } else if (map_action.equalsIgnoreCase("SHOP_DISTANCE")) {


            LatLng latlng = new LatLng(Double.parseDouble(shop_lat), Double.parseDouble(shop_long));
            LatLng latlng1 = new LatLng(Double.parseDouble(order_lat), Double.parseDouble(order_long));

            latLngArrayList.add(new LatLng(Double.parseDouble(shop_lat), Double.parseDouble(shop_long)));
            latLngArrayList.add(new LatLng(Double.parseDouble(order_lat), Double.parseDouble(order_long)));

            mMap.addMarker(new MarkerOptions().position(latlng).title("Shop Location ")
                    .snippet(shop_distance + " m From Order Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_home_pin)));

            mMap.addMarker(new MarkerOptions().position(latlng1).title("Order Location")
                    .snippet(shop_distance + " m From Shop Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_order_pin)));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

            polylineOptions = new PolylineOptions().width(10).color(Color.parseColor("#3498DB")).geodesic(true)
                    .addAll(latLngArrayList).clickable(true);

            Polyline polyline = mMap.addPolyline(polylineOptions);
            polyline.setTag("A");
            //stylePolyline(polyline);


        } else if (map_action.equalsIgnoreCase("multiple_date")) {

            pdialog = new ProgressDialog(MapsActivity.this);
            pdialog.setTitle(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setMessage(commanSuchnaList.getArrayList().get(1) + "");
            pdialog.setCancelable(false);
            pdialog.show();
            //distributor undelivered order screen
            new getShopLocationsTask().execute();

        } else {

            pdialog = new ProgressDialog(MapsActivity.this);
            pdialog.setTitle(commanSuchnaList.getArrayList().get(0) + "");
            pdialog.setMessage(commanSuchnaList.getArrayList().get(1) + "");
            pdialog.setCancelable(false);
            pdialog.show();

            binding.llCbSalesman.setVisibility(View.GONE);
            //salesman my shop location screen
            new getLocationDataTask().execute();

        }



        /*   polylineOptions = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true)
                .addAll(latLngArrayList).clickable(true);

        Polyline polyline = mMap.addPolyline(polylineOptions);
        polyline.setTag("A");
        stylePolyline(polyline);


        polygonOptions =new PolygonOptions()
                .addAll(latLngArrayList).clickable(true);

        Polygon polygon1 = mMap.addPolygon(polygonOptions);
        polygon1.setTag("alpha");
        stylePolygon(polygon1);*/

        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));

        if (mMap != null) {

            /*mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View view = null;
                    //if (salesman_Ids.equalsIgnoreCase("") || salesman_Ids.isEmpty()) {

                    int pos = arrayList_shop_name_for_pos.indexOf(marker.getTitle());
                    Log.i(TAG, "marker pos===>" + pos);
                    Log.i(TAG, "Shop_image===>" + arrayList_mapdata.get(pos).getShop_image());

                    EntityInfoWindowBinding infoWindowBinding = EntityInfoWindowBinding.inflate(getLayoutInflater());
                    view = infoWindowBinding.getRoot();

                    if (salesman_id.isEmpty() || salesman_id.equalsIgnoreCase(""))
                        infoWindowBinding.tvShopNo.setVisibility(View.VISIBLE);
                    else
                        infoWindowBinding.tvShopNo.setVisibility(View.GONE);

                    infoWindowBinding.tvShopNo.setText("" + arrayList_mapdata.get(pos).getShop_no());

                    if (binding.cbShopPhoto.isChecked())
                        infoWindowBinding.imgShopImageInfowindow.setVisibility(View.VISIBLE);
                    else
                        infoWindowBinding.imgShopImageInfowindow.setVisibility(View.GONE);

                    if (binding.cbShopName.isChecked())
                        infoWindowBinding.tvShopNameInfowindow.setVisibility(View.VISIBLE);
                    else
                        infoWindowBinding.tvShopNameInfowindow.setVisibility(View.GONE);

                    if (binding.cbSalesmanName.isChecked()) {

                        if (!arrayList_mapdata.get(pos).getSalesman_name().isEmpty())
                            infoWindowBinding.tvSalesmanNameInfowindow.setVisibility(View.VISIBLE);
                        else
                            infoWindowBinding.tvSalesmanNameInfowindow.setVisibility(View.GONE);
                    } else
                        infoWindowBinding.tvSalesmanNameInfowindow.setVisibility(View.GONE);

                    if (binding.cbShopAddress.isChecked())
                        infoWindowBinding.tvOrderRsInfowindow.setVisibility(View.VISIBLE);
                    else
                        infoWindowBinding.tvOrderRsInfowindow.setVisibility(View.GONE);


                    if (binding.cbOrderLine.isChecked())
                        infoWindowBinding.tvOrderLineInfowindow.setVisibility(View.VISIBLE);
                    else
                        infoWindowBinding.tvOrderLineInfowindow.setVisibility(View.GONE);


                    not_first_time_showing_info_window = false;

                    try {

                        if (!arrayList_mapdata.get(pos).getShop_image().isEmpty()) {

                            if (not_first_time_showing_info_window) {
                                Picasso.get().load(arrayList_mapdata.get(pos).getShop_image()).into(infoWindowBinding.imgShopImageInfowindow);
                            } else {
                                not_first_time_showing_info_window = true;
                                Picasso.get().load(arrayList_mapdata.get(pos).getShop_image()).into(infoWindowBinding.imgShopImageInfowindow, new InfoWindowRefresher(marker));
                            }

                        }

                        infoWindowBinding.tvShopNameInfowindow.setText("" + arrayList_mapdata.get(pos).getShop_name());

                        if (!bit_id.isEmpty()) {

                            if (binding.cbBitName.isChecked())
                                infoWindowBinding.tvSalesmanNameInfowindow.setText("" + arrayList_mapdata.get(pos).getSalesman_name()
                                        + "\nBIT: " + arrayList_mapdata.get(pos).getAddress());
                            else
                                infoWindowBinding.tvSalesmanNameInfowindow.setText("" + arrayList_mapdata.get(pos).getSalesman_name());
                        } else {
                            infoWindowBinding.tvSalesmanNameInfowindow.setText("" + arrayList_mapdata.get(pos).getSalesman_name());
                        }


                        //if (bit_id.isEmpty()) {
                        infoWindowBinding.tvOrderRsInfowindow.setText("₹ " + (int) Double.parseDouble(arrayList_mapdata.get(pos).getLast_order_rs()));
                        infoWindowBinding.tvOrderLineInfowindow.setText("LINE: " + (int) Double.parseDouble(arrayList_mapdata.get(pos).getLast_order_line()));
                        *//*} else {

                            infoWindowBinding.tvOrderRsInfowindow.setText("address:" + arrayList_mapdata.get(pos).getAddress());
                            infoWindowBinding.tvOrderLineInfowindow.setText("BIT: " + arrayList_mapdata.get(pos).getLast_order_line());
                        }*//*


                    } catch (Exception e) {
                    }


                    //}

                    return view;
                }

            });*/

        }


        //new getLocationDataTask().execute();


    }

    public void load_Map_Markers() {

        arrayList_mapdata = new ArrayList<>();
        /*arrayList_mapdata.add(new ViewLocationPOJO("1", "jignesh", "9727612383", 22.304782, 70.802457));
        arrayList_mapdata.add(new ViewLocationPOJO("2", "jaydeep", "1234567890", 22.304726, 70.801585));
        arrayList_mapdata.add(new ViewLocationPOJO("3", "Prince", "1234567890", 22.303673, 70.803360));
        arrayList_mapdata.add(new ViewLocationPOJO("4", "Milan", "1234567890", 22.302992, 70.802215));
        arrayList_mapdata.add(new ViewLocationPOJO("5", "Priyesh", "1234567890", 22.305119, 70.802579));*/


        for (int i = 0; i < arrayList_mapdata.size(); i++) {

            LatLng latlng = new LatLng(arrayList_mapdata.get(i).getLatitude(), arrayList_mapdata.get(i).getLongitude());
            String st = arrayList_mapdata.get(i).getSalesman_name();
            mMap.addMarker(new MarkerOptions().position(latlng).title(st).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

            latLngArrayList.add(latlng);

            Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add(new LatLng(22.304782, 70.802457),
                            new LatLng(22.304726, 70.801585),
                            new LatLng(22.303673, 70.803360),
                            new LatLng(22.302992, 70.802215),
                            new LatLng(22.305119, 70.802579)));
            polyline1.setTag("A");
            stylePolyline(polyline1);

            Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
                    .clickable(true)
                    .add(new LatLng(22.304782, 70.802457),
                            new LatLng(22.304726, 70.801585),
                            new LatLng(22.303673, 70.803360),
                            new LatLng(22.302992, 70.802215),
                            new LatLng(22.305119, 70.802579)));
            // Store a data object with the polygon, used here to indicate an arbitrary type.
            polygon1.setTag("alpha");
            // [END maps_poly_activity_add_polygon]
            // Style the polygon.
            stylePolygon(polygon1);


            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            // Set listeners for click events.
            mMap.setOnPolylineClickListener(this);
            mMap.setOnPolygonClickListener(this);
        }

    }

    @Override
    public void onPolygonClick(Polygon polygon) {

        int color = polygon.getStrokeColor() ^ 0x00ffffff;
        polygon.setStrokeColor(color);
        color = polygon.getFillColor() ^ 0x00ffffff;
        polygon.setFillColor(color);

        //Toast.makeText(this, "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    // [START maps_poly_activity_on_polyline_click]
    static final int PATTERN_GAP_LENGTH_PX = 20;
    static final PatternItem DOT = new Dot();
    static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    @Override
    public void onPolylineClick(Polyline polyline) {

        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        //Toast.makeText(this, "Route type " + polyline.getTag().toString(),Toast.LENGTH_SHORT).show();

    }


    // [START maps_poly_activity_style_polyline]
    //private static final int COLOR_BLACK_ARGB = 0xff000000;


    private static final int COLOR_BLACK_ARGB = 0xff3498DB;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    /**
     * Styles the polyline, based on type.
     *
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.location_marker), 5));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }


        Random random = new Random();
        int color_pos = random.nextInt(5);

        //polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }
    // [END maps_poly_activity_style_polyline]


    // [START maps_poly_activity_style_polygon]
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYGON_STROKE_WIDTH_PX = 10;
    private static final int PATTERN_DASH_LENGTH_PX = 50;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    /**
     * Styles the polygon, based on type.
     *
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    // [END maps_poly_activity_style_polygon]


    //http://localhost/locationtrackerDemo/location_data_get_by_user_id.php?user_id=1


    //=================get location data by user task starts=====================

    public class getLocationDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //url = getString(R.string.Base_URL) + getString(R.string.view_location_url);
            url = getString(R.string.Base_URL) + getString(R.string.shoplists_by_salesmanbit_id_for_maps_url) + salesman_id;

            Log.i("view Location url=>", url + "");

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i("viewLocation response=>", response + "");
                //Toast.makeText(MapsActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                getLocationData();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }
    }

    private void getLocationData() {

        try {

            JSONObject jsonObject = new JSONObject(response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            if (jsonArray.length() > 0) {

                arrayList_mapdata = new ArrayList<>();
                arrayList_shop_name_for_pos = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject data_object = jsonArray.getJSONObject(i);

                    arrayList_shop_name_for_pos.add(data_object.getString("shop_id"));

                    viewLocationPOJO = new ViewLocationPOJO("",
                            sp.getString("salesman", ""),
                            data_object.getString("shop_id"),
                            data_object.getString("title"),
                            data_object.getString("photo"),
                            data_object.getString("total_rs"),
                            data_object.getString("total_line"),
                            data_object.getString("address"),
                            "",
                            data_object.getDouble("latitude"),
                            data_object.getDouble("longitude"));

                    arrayList_mapdata.add(viewLocationPOJO);

                    LatLng latlng = new LatLng(data_object.getDouble("latitude"), data_object.getDouble("longitude"));
                    latLngArrayList.add(latlng);

                    /*String st = data_object.getString("title") + "";
                    mMap.addMarker(new MarkerOptions().position(latlng).title(st).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));*/


                    Bitmap bitmap = createUserBitmap();
                    if (bitmap != null) {
                        String st = data_object.getString("shop_id");
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latlng)
                                //.title(st)
                                //.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView("",
                                        data_object.getString("title"),
                                        sp.getString("salesman", ""),
                                        data_object.getString("total_rs"),
                                        data_object.getString("total_line")))));
                        marker.setTag(data_object.getString("shop_id"));
                                /*.snippet(data_object.getString("title")
                                        + "((₹ " + data_object.getString("total_rs")
                                        + ")(LINE:" + data_object.getString("total_line") + "))"));*/
                        //marker.showInfoWindow();

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                    }

                   /* Bitmap bitmap = createUserBitmap();
                    if (bitmap != null) {
                        options.title("Ketan Ramani");
                        options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        options.anchor(0.5f, 0.907f);
                        marker = mMap.addMarker(options);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    }*/


                }


                if (pdialog.isShowing())
                    pdialog.dismiss();


                /* for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject dataobject = jsonArray.getJSONObject(i);

                //Toast.makeText(this, ""+ dataobject.getString("salesman")+"\n"+ dataobject.getString("salesman_id"), Toast.LENGTH_LONG).show();

                JSONArray locationDataArray = dataobject.getJSONArray("locData");

                for (int j = 0; j < locationDataArray.length(); j++) {

                    JSONObject locationDataobject = locationDataArray.getJSONObject(j);

                    LatLng latlng = new LatLng(locationDataobject.getDouble("latitude"), locationDataobject.getDouble("latitude"));
                    String st =  dataobject.getString("title")+"";
                    mMap.addMarker(new MarkerOptions().position(latlng).title(st).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));

                    latLngArrayList.add(latlng);
                }*/

                /*polylineOptions = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true)
                        .addAll(latLngArrayList).clickable(true);

                Polyline polyline = mMap.addPolyline(polylineOptions);
                polyline.setTag("A");
                stylePolyline(polyline);


                polygonOptions =new PolygonOptions()
                        .addAll(latLngArrayList).clickable(true);

                Polygon polygon1 = mMap.addPolygon(polygonOptions);
                polygon1.setTag("alpha");
                stylePolygon(polygon1);*/

                /*  for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);
                viewLocationPOJO = new ViewLocationPOJO(object.getString("user_id"),
                        object.getString("username"),
                        object.getString("mobileno"),
                        object.getDouble("latitude"),
                        object.getDouble("longitude"));

                LatLng latlng = new LatLng(object.getDouble("latitude"), object.getDouble("longitude"));
                String st = object.getString("username") + " " + object.getString("mobileno");
                mMap.addMarker(new MarkerOptions().position(latlng).title(st).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            }*/

            } else {

                if (pdialog.isShowing())
                    pdialog.dismiss();

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(22.304782, 70.802457)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //=================get location data by user task ends=======================


    //==================get salesman's location data code starts=================

    public class getlocationbysalesIdsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.i(TAG, "salesmanLocation url=>" + url + "");

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "salesmanLocation res=>" + response + "");
                getlocationbysalesIds();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }
    }

    private void getlocationbysalesIds() {

        int color_pos = 0;
        boolean isLocationAvailable = false;

        arrayList_salesman_location = new ArrayList<>();
        latLngArrayList = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(response + "");
            JSONArray data_array = jsonObject.getJSONArray("data");

            mMap.clear();

            if (data_array.length() > 0) {

                isLocationAvailable = false;

                for (int i = 0; i < data_array.length(); i++) {

                    JSONObject data_object = data_array.getJSONObject(i);

                    JSONArray locData_array = data_object.getJSONArray("locData");

                    arrayList_location_data = new ArrayList<>();

                    latLngArrayList.clear();

                    for (int j = 0; j < locData_array.length(); j++) {

                        isLocationAvailable = true;

                        JSONObject locData_object = locData_array.getJSONObject(j);

                        locationDataPOJO = new LocationDataPOJO(locData_object.getString("lat"),
                                locData_object.getString("lang"),
                                locData_object.getString("datetimeval"));

                        LatLng latlng = new LatLng(locData_object.getDouble("lat"), locData_object.getDouble("lang"));

                        /* if (j == 0) {

                         *//* String st = data_object.getString("salesman");
                        mMap.addMarker(new MarkerOptions().position(latlng).title(st).icon(BitmapDescriptorFactory.fromResource(R.drawable.start_cap)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));*//*

                        } else if (j == locData_array.length() - 1) {

                            String st = data_object.getString("salesman");
                            mMap.addMarker(new MarkerOptions().position(latlng).title(st));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

                        } else {

                            String st = data_object.getString("salesman");
                            mMap.addMarker(new MarkerOptions().position(latlng)
                                    //.title(st)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromViewSalesman((j + 1) + "",
                                            data_object.getString("salesman"),
                                            locData_object.getString("datetimeval")))));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                        }*/

                        mMap.addMarker(new MarkerOptions().position(latlng)
                                //.title(st)
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromViewSalesman((j + 1) + "",
                                        data_object.getString("salesman"),
                                        locData_object.getString("datetimeval")))));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));


                        /*Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
                        animation.setDuration(2000);
                        binding.imgLogo.startAnimation(animation);*/


                        latLngArrayList.add(latlng);

                        Random random = new Random();
                        color_pos = random.nextInt(5);

                        arrayList_location_data.add(locationDataPOJO);
                    }

                    locationBysalesIdsPOJO = new LocationBysalesIdsPOJO(data_object.getString("salesman"),
                            data_object.getString("salesman_id"),
                            arrayList_location_data);

                    arrayList_salesman_location.add(locationBysalesIdsPOJO);

                   /* if (latLngArrayList.size() > 0) {

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngArrayList.get(i)));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));

                    } else {

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(22.304782, 70.802457)));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));

                    }*/

                    //polylineOptions = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true)

                    polylineOptions = new PolylineOptions().width(13)
                            .addAll(latLngArrayList).clickable(true);

                    Polyline polyline = mMap.addPolyline(polylineOptions);
                    polyline.setClickable(true);
                    polyline.setStartCap(new CustomCap(
                            BitmapDescriptorFactory.fromResource(R.drawable.start_cap), 20));
                   /* polyline.setEndCap(new CustomCap(
                            BitmapDescriptorFactory.fromResource(R.drawable.location_marker), 20));*/


                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    polyline.setColor(color);

                    //polyline.setTag("A");
                    //stylePolyline(polyline);

               /* polygonOptions =new PolygonOptions()
                        .addAll(latLngArrayList).clickable(true);

                Polygon polygon1 = mMap.addPolygon(polygonOptions);
                polygon1.setTag("alpha");
                stylePolygon(polygon1);*/

                }

                if (!isLocationAvailable) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(22.304782, 70.802457)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                }

                if (pdialog.isShowing())
                    pdialog.dismiss();


            } else {

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(22.304782, 70.802457)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));

                if (pdialog.isShowing())
                    pdialog.dismiss();

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //==================get salesman's location data code ends===================


    //===========================view shop locations task starts =================================

    public class getShopLocationsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (map_action.equalsIgnoreCase("multiple_date"))
                view_shop_location_url = getString(R.string.Base_URL) + getString(R.string.view_undelivered_order_shoplist_map_group_dates_url) + jArray;
            else
                view_shop_location_url = getString(R.string.Base_URL) + getString(R.string.view_undelivered_order_shoplist_map_url) + salesman_Ids + "&order_date=" + order_date;

            Log.i(TAG, "view_shop_location_url=>" + view_shop_location_url);

            HttpHandler httpHandler = new HttpHandler();
            view_shop_location_response = httpHandler.makeServiceCall(view_shop_location_url.replace("%20", ""));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "view_shop_location_res=>" + view_shop_location_response);
                getShopLocations();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }
    }

    private void getShopLocations() {

        try {

            JSONObject jsonObject = new JSONObject(view_shop_location_response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            mMap.clear();

            latLngArrayList = new ArrayList<>();

            if (jsonArray.length() > 0) {

                arrayList_mapdata = new ArrayList<>();
                arrayList_shop_name_for_pos = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject data_object = jsonArray.getJSONObject(i);

                    arrayList_shop_name_for_pos.add(data_object.getString("shop_id"));

                    viewLocationPOJO = new ViewLocationPOJO(data_object.getString("salesman_id"),
                            data_object.getString("salesman_name"),
                            data_object.getString("shop_id"),
                            data_object.getString("shop_name"),
                            data_object.getString("shop_image"),
                            data_object.getString("last_order_rs"),
                            data_object.getString("last_order_line"),
                            data_object.getString("address"),
                            data_object.getString("shop_order"),
                            data_object.getDouble("latitude"),
                            data_object.getDouble("longitude"));

                    arrayList_mapdata.add(viewLocationPOJO);

                    LatLng latlng = new LatLng(data_object.getDouble("latitude"), data_object.getDouble("longitude"));
                    latLngArrayList.add(latlng);

                    Bitmap bitmap = createUserBitmap();
                    if (i == jsonArray.length() - 1) {

                       /* String st = data_object.getString("shop_id");
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title(st).snippet(data_object.getString("shop_name")
                                + "((₹ " + data_object.getString("last_order_rs")
                                + ")(LINE:" + data_object.getString("last_order_line") + "))"));*/


                        if (bitmap != null) {
                            String st = data_object.getString("shop_id");
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latlng)
                                    //.title(st)
                                    //.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(data_object.getString("shop_order"),
                                            data_object.getString("shop_name"),
                                            data_object.getString("salesman_name"),
                                            data_object.getString("last_order_rs"),
                                            data_object.getString("last_order_line")))));
                                    /*.snippet(data_object.getString("shop_name")
                                            + "((₹ " + data_object.getString("total_rs")
                                            + ")(LINE:" + data_object.getString("total_line") + "))"));*/

                            marker.setTag(data_object.getString("shop_id"));

                        }
                        //marker.showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

                    } else {

                     /*   String st = data_object.getString("shop_id");
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latlng)
                                .title(st)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                                .snippet(data_object.getString("shop_name")
                                        + "((₹ " + data_object.getString("last_order_rs")
                                        + ")(LINE:" + data_object.getString("last_order_line") + "))"));*/

                        if (bitmap != null) {
                            String st = data_object.getString("shop_id");
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latlng)
                                    //.title(st)
                                    //.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(data_object.getString("shop_order"),
                                            data_object.getString("shop_name"),
                                            data_object.getString("salesman_name"),
                                            data_object.getString("last_order_rs"),
                                            data_object.getString("last_order_line")))));
                                 /*   .snippet(data_object.getString("shop_name")
                                            + "((₹ " + data_object.getString("total_rs")
                                            + ")(LINE:" + data_object.getString("total_line") + "))"));*/
                            marker.setTag(data_object.getString("shop_id"));
                        }


                        //marker.showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                    }

                }


                if (pdialog.isShowing())
                    pdialog.dismiss();


              /*  polylineOptions = new PolylineOptions().width(13)
                        .addAll(latLngArrayList).clickable(true);

                Polyline polyline = mMap.addPolyline(polylineOptions);
                polyline.setClickable(true);
                polyline.setStartCap(new CustomCap(
                        BitmapDescriptorFactory.fromResource(R.drawable.start_cap), 20));*/


            } else {

                if (pdialog.isShowing())
                    pdialog.dismiss();

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(22.304782, 70.802457)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //===========================view shop locations task ends ===================================


    //===========================view shop locations for particular BIT task starts ===================================

    public class getBitWiseShopListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            url = getString(R.string.Base_URL) + getString(R.string.view_bitwise_shoplist_location_url) + bit_id +
                    "&salesman_id=" + bit_salesman_id;
            Log.i(TAG, "bit_wise_shop_url==>" + url);

            HttpHandler httpHandler = new HttpHandler();
            response = httpHandler.makeServiceCall(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.i(TAG, "bit_wise_shop_res" + response);
                getBitWiseShopList();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }
    }

    private void getBitWiseShopList() {

        try {

            JSONObject jsonObject = new JSONObject(response + "");
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            mMap.clear();

            if (jsonArray.length() > 0) {

                arrayList_mapdata = new ArrayList<>();
                arrayList_shop_name_for_pos = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject data_object = jsonArray.getJSONObject(i);

                    Log.i(TAG, "inside for......" + i);

                    arrayList_shop_name_for_pos.add(data_object.getString("shop_id"));

                    viewLocationPOJO = new ViewLocationPOJO("",
                            data_object.getString("salesman_name"),
                            data_object.getString("shop_id"),
                            data_object.getString("shop_name"),
                            data_object.getString("shop_image"),
                            data_object.getString("total_rs"),
                            data_object.getString("total_line"),
                            data_object.getString("bit_name"),
                            data_object.getString("shop_order"),
                            data_object.getDouble("latitude"),
                            data_object.getDouble("longitude"));

                    arrayList_mapdata.add(viewLocationPOJO);

                    LatLng latlng = new LatLng(data_object.getDouble("latitude"), data_object.getDouble("longitude"));
                    latLngArrayList.add(latlng);


                  /*  String st = data_object.getString("shop_id");
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latlng)
                            .title(st)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));*/

                    Bitmap bitmap = createUserBitmap();
                    if (bitmap != null) {
                        String st = data_object.getString("shop_id");
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latlng)
                                //.title(st)
                                //.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(data_object.getString("shop_order"),
                                        data_object.getString("shop_name"),
                                        data_object.getString("salesman_name"),
                                        data_object.getString("total_rs"),
                                        data_object.getString("total_line")))));
                                /*.snippet(data_object.getString("shop_name")
                                        + "((₹ " + data_object.getString("total_rs")
                                        + ")(LINE:" + data_object.getString("total_line") + "))"));*/

                        marker.setTag(data_object.getString("shop_id"));
                    }

                    //marker.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));


                }

                if (pdialog.isShowing())
                    pdialog.dismiss();


            } else {

                if (pdialog.isShowing())
                    pdialog.dismiss();

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(22.304782, 70.802457)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //===========================view shop locations for particular BIT task ends =====================================

    @Override
    public void onInfoWindowClick(Marker marker) {

        int pos = arrayList_shop_name_for_pos.indexOf(marker.getTitle());
        Log.i(TAG, "salesman_id===>" + salesman_id);
        Log.i(TAG, "marker pos===>" + pos);
        Log.i(TAG, "Shop_image===>" + arrayList_mapdata.get(pos).getShop_image());

        DialogShopDetailMapBinding binding1;
        builder = new AlertDialog.Builder(MapsActivity.this, R.style.AlertDialogTheme);
        binding1 = DialogShopDetailMapBinding.inflate(LayoutInflater.from(MapsActivity.this));
        builder.setView(binding1.getRoot());

        binding1.tvShopNameAddressMapDetail.setText("Shop Name : " + arrayList_mapdata.get(pos).getShop_name());
        binding1.tvShopAddressMapDetail.setText("Shop Address : " + arrayList_mapdata.get(pos).getAddress());

        Picasso.get().load(arrayList_mapdata.get(pos).getShop_image())
                .into(binding1.imgShopImageMapDetail);

        binding1.btnOkShopDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });


        ad = builder.create();
        ad.show();

        Window window = ad.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        //layoutParams.height = 1600;
        window.setAttributes(layoutParams);

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if (!map_action.equalsIgnoreCase("SALESTEAM") && !map_action.equalsIgnoreCase("SHOP_DISTANCE")) {

            Log.i(TAG, "Shop_id====>" + marker.getTag());

            int pos = arrayList_shop_name_for_pos.indexOf(marker.getTag());
            Log.i(TAG, "salesman_id===>" + salesman_id);
            Log.i(TAG, "marker pos===>" + pos);
            Log.i(TAG, "Shop_image===>" + arrayList_mapdata.get(pos).getShop_image());

            DialogShopDetailMapBinding binding1;
            builder = new AlertDialog.Builder(MapsActivity.this);
            binding1 = DialogShopDetailMapBinding.inflate(LayoutInflater.from(MapsActivity.this));
            builder.setView(binding1.getRoot());

            binding1.tvOrderRsAddressMapDetail.setText("₹ " + arrayList_mapdata.get(pos).getLast_order_rs());
            binding1.tvOrderLineAddressMapDetail.setText(commanList.getArrayList().get(2) + " " + arrayList_mapdata.get(pos).getLast_order_line());
            binding1.tvShopNameAddressMapDetail.setText(commanList.getArrayList().get(3) + " " + arrayList_mapdata.get(pos).getShop_name());
            binding1.tvSalesmanNameAddressMapDetail.setText(commanList.getArrayList().get(4) + " " + arrayList_mapdata.get(pos).getSalesman_name());
            binding1.tvShopAddressMapDetail.setText(commanList.getArrayList().get(5) + " " + arrayList_mapdata.get(pos).getAddress());

            if (!arrayList_mapdata.get(pos).getShop_image().isEmpty())
                Picasso.get().load(arrayList_mapdata.get(pos).getShop_image())
                        .into(binding1.imgShopImageMapDetail);

            binding1.btnOkShopDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad.dismiss();
                }
            });


            ad = builder.create();
            ad.show();
/*
        Window window = ad.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        //layoutParams.height = 1600;
        window.setAttributes(layoutParams);*/

        }
        return false;
    }


    private Bitmap createUserBitmap() {

        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(62, 76, Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getResources().getDrawable(R.drawable.custome_marker_down_arrow);
            drawable.setBounds(0, 0, 62, 76);
            //drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_bug);
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                //float scale = dp(52) / (float) bitmap.getWidth();
                float scale = 52 / (float) bitmap.getWidth();
                //matrix.postTranslate(dp(5), dp(5));
                matrix.postTranslate(5, 5);
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                //bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                bitmapRect.set(5, 5, 52 + 5, 52 + 5);
                //canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
                canvas.drawRoundRect(bitmapRect, 26, 26, roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }


    private Bitmap getMarkerBitmapFromView(String shop_no, String shop_name, String salesman_name, String total_rs, String total_line) {

        EntityCustomeMarkerBinding markerBinding;

        markerBinding = EntityCustomeMarkerBinding.inflate(getLayoutInflater());
        //View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.entity_custome_marker, null);
        View customMarkerView = markerBinding.getRoot();
        //ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.img_sales_order_product_photo);
        //markerImageView.setImageResource(resId);

        if (!shop_no.isEmpty())
            markerBinding.tvShopNo.setText("" + shop_no);
        else
            markerBinding.tvShopNo.setVisibility(View.GONE);

        if (!shop_name.isEmpty())
            markerBinding.tvShopNameInfowindow.setText("" + shop_name);
        else
            markerBinding.tvShopNameInfowindow.setVisibility(View.GONE);

        if (!salesman_name.isEmpty())
            markerBinding.tvSalesmanNameInfowindow.setText("" + salesman_name);
        else
            markerBinding.tvSalesmanNameInfowindow.setVisibility(View.GONE);


        if (!total_rs.isEmpty())
            markerBinding.tvOrderRsInfowindow.setText("₹ " + (int) Double.parseDouble(total_rs));
        else
            markerBinding.tvOrderRsInfowindow.setVisibility(View.GONE);

        if (!total_line.isEmpty())
            markerBinding.tvOrderLineInfowindow.setText(commanList.getArrayList().get(2) + " " + (int) Double.parseDouble(total_line));
        else
            markerBinding.tvOrderLineInfowindow.setVisibility(View.GONE);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


    private Bitmap getMarkerBitmapFromViewSalesman(String salesman_no, String salesman_name, String time) {

        EntityCustomeMarkerSalesTeamBinding markerBinding;

        markerBinding = EntityCustomeMarkerSalesTeamBinding.inflate(getLayoutInflater());
        //View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.entity_custome_marker, null);
        View customMarkerView = markerBinding.getRoot();
        //ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.img_sales_order_product_photo);
        //markerImageView.setImageResource(resId);

        if (!salesman_no.isEmpty())
            markerBinding.tvSalesmanNoMarker.setText("" + salesman_no);
        else
            markerBinding.tvSalesmanNoMarker.setVisibility(View.GONE);


        if (!salesman_name.isEmpty())
            markerBinding.tvSalesmanNameMarker.setText("" + salesman_name);
        else
            markerBinding.tvSalesmanNameMarker.setVisibility(View.GONE);

        if (!time.isEmpty())
            markerBinding.tvLocationTimeMarker.setText("" + time);
        else
            markerBinding.tvLocationTimeMarker.setVisibility(View.GONE);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }


    public ArrayList<String> setLang(String key) {

        arrayList_lang_desc = new ArrayList<>();
        db.open();
        Cursor cur = db.viewLanguage(key, "7");

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
        Cursor cur = db.viewMultiLangSuchna("7");

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

                builder = new AlertDialog.Builder(MapsActivity.this);
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
