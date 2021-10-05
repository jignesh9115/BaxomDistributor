package com.jp.baxomdistributor.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database {

    public static final String DATABASE_NAME = "baxomlocaldb";
    public static final int DATABSE_VERSION = 2;

    public static final String TABLE_NAME_SHOP_TABLE = "shop_table";
    public static final String SHOP_ID = "shop_id";
    public static final String SALESMAN_ID = "salesman_id";
    public static final String BIT_ID = "bit_id";
    public static final String DIST_ID = "dist_id";
    public static final String TITLE = "title";
    public static final String ADDRESS = "address";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ENTRY_DATE = "entry_date";
    public static final String SHOP_KEEPER_NAME = "shop_keeper_name";
    public static final String SHOP_KEEPER_NO1 = "shop_keeper_no1";
    public static final String SHOP_KEEPER_NO2 = "shop_keeper_no2";
    public static final String INVOICE_TYPE = "invoice_type";
    public static final String GST_NO = "gst_no";
    public static final String CATEGORY_TYPE = "category_type";
    public static final String IMAGE_PATH = "image_path";
    public static final String TIMESTAMP = "timestamp";
    public static final String SHOP_STATUS = "shop_status";
    public static final String CREATE_SHOP_TABLE = "CREATE TABLE shop_table (\n" +
            " shop_id INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
            " salesman_id TEXT ,\n" +
            " bit_id TEXT ,\n" +
            " def_dist TEXT ,\n" +
            " title TEXT ,\n" +
            " address TEXT ,\n" +
            " latitude TEXT ,\n" +
            " longitude TEXT ,\n" +
            " entry_date TEXT ,\n" +
            " shop_keeper_name TEXT ,\n" +
            " shop_keeper_no1 TEXT ,\n" +
            " shop_keeper_no2 TEXT ,\n" +
            " invoice_type TEXT ,\n" +
            " gst_no TEXT ,\n" +
            " category_type TEXT ,\n" +
            " image_path TEXT ,\n" +
            " timestamp TEXT ,\n" +
            " shop_status TEXT \n" + ")";


    public static final String TABLE_NAME_DAILYSALES_URL_TABLE = "dailysales_url_table";
    public static final String DAILYSALES_URL_ID = "dailysales_url_id";
    public static final String DAILYSALES_URL = "dailysales_url";
    public static final String DAILYSALES_URL_STATUS = "dailysales_url_status";
    public static final String CREATE_DAILYSALES_URL_TABLE = "CREATE TABLE dailysales_url_table (\n" +
            " dailysales_url_id INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
            " dailysales_url TEXT ,\n" +
            " salesman_id TEXT ,\n" +
            " bit_id TEXT ,\n" +
            " dist_id TEXT ,\n" +
            " shop_id TEXT ,\n" +
            " timestamp TEXT ,\n" +
            " dailysales_url_status TEXT \n" + ")";


    public static final String TABLE_NAME_DAILYSALES_TABLE = "dailysales_table";
    public static final String DAILYSALES_ID = "dailysales_id";
    public static final String TOTAL_RS = "total_rs";
    public static final String TOTAL_LINE = "total_line";
    public static final String DAILY_STATUS = "daily_status";
    public static final String LATITUDE_O = "latitude_o";
    public static final String LONGITUDE_O = "longitude_o";
    public static final String NON_ORDER_REASON = "non_order_reason";
    public static final String TOTAL_DISCOUNT = "total_discount";
    public static final String PREV_ORDER_TIME = "prev_order_time";
    public static final String TIME_DIFFERENT = "time_different";
    public static final String CREATE_DAILYSALES_TABLE = "CREATE TABLE dailysales_table (\n" +
            " dailysales_id INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
            " shop_id TEXT ,\n" +
            " salesman_id TEXT ,\n" +
            " bit_id TEXT ,\n" +
            " dist_id TEXT ,\n" +
            " total_rs TEXT ,\n" +
            " total_line TEXT ,\n" +
            " daily_status TEXT ,\n" +
            " entry_date TEXT ,\n" +
            " latitude_o TEXT ,\n" +
            " longitude_o TEXT ,\n" +
            " non_order_reason TEXT ,\n" +
            " total_discount TEXT ,\n" +
            " prev_order_time TEXT ,\n" +
            " timestamp TEXT \n" + ")";


    public static final String TABLE_NAME_DAILYSALES_PROD_TABLE = "dailysales_prod_table";
    public static final String DAILYSALES_PROD_ID = "dailysales_prod_id";
    public static final String DAILYSALES_SALES_ID = "daily_sales_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_QTY = "product_qty";
    public static final String PRODUCT_PRICE = "product_price";
    public static final String CREATE_DAILYSALES_PROD = "CREATE TABLE dailysales_prod_table (\n" +
            " dailysales_prod_id INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
            " daily_sales_id TEXT ,\n" +
            " product_id TEXT ,\n" +
            " product_qty TEXT ,\n" +
            " product_price TEXT \n" + ")";


    public static final String TABLE_NAME_DAILYSALES_SHEME_TABLE = "dailysales_scheme_table";
    public static final String DAILYSALES_SHEMES_ID = "dailysales_schemes_id";
    public static final String SHEMES_ID = "scheme_id";
    public static final String SHEMES_NAME_SORT = "scheme_name_sort";
    public static final String SHEMES_NAME_LONG = "scheme_name_long";
    public static final String SHEMES_TYPE_ID = "scheme_type_id";
    public static final String SHEMES_TYPE_NAME = "scheme_type_name";
    public static final String SHEMES_IMAGE = "scheme_image";
    public static final String SHEMES_QTY = "scheme_qty";
    public static final String SHEMES_QTY_DEL = "scheme_qty_del";
    public static final String RESULT_PRODUCT_ID = "result_product_id";
    public static final String RESULT_PRODUCT_QTY = "result_product_qty";
    public static final String RESULT_PRODUCT_PRICE = "result_product_price";
    public static final String RESULT_PRODUCT_TOTAL_PRICE = "result_product_total_price";
    public static final String RESULT_PRODUCT_IMAGE = "result_product_image";
    public static final String IS_HALF_SCHEME = "is_half_scheme";
    public static final String CREATE_DAILYSALES_SHEME_TABLE = "CREATE TABLE dailysales_scheme_table (\n" +
            " dailysales_schemes_id  INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
            " dailysales_id TEXT ,\n" +
            " scheme_id TEXT ,\n" +
            " scheme_name_sort TEXT ,\n" +
            " scheme_name_long TEXT ,\n" +
            " scheme_type_id TEXT ,\n" +
            " scheme_type_name TEXT ,\n" +
            " scheme_image TEXT ,\n" +
            " scheme_qty TEXT ,\n" +
            " scheme_qty_del TEXT ,\n" +
            " result_product_id TEXT ,\n" +
            " result_product_qty TEXT ,\n" +
            " result_product_price TEXT ,\n" +
            " result_product_total_price TEXT ,\n" +
            " result_product_image TEXT ,\n" +
            " is_half_scheme TEXT \n" + ")";


    public static final String TABLE_NAME_MULTI_LANGUAGE_TABLE = "multi_langauge_table";
    public static final String MULTI_LANG_ID = "multi_lang_id";
    public static final String SCREEN_ID = "screen_id";
    public static final String SCREEN_NAME = "screen_name";
    public static final String LANG_ENG = "lang_eng";
    public static final String LANG_GUJ = "lang_guj";
    public static final String LANG_HINDI = "lang_hindi";
    public static final String CREATE_MULTI_LANGUAGE_TABLE = "CREATE TABLE multi_langauge_table (\n" +
            " multi_lang_id  INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
            " screen_id TEXT ,\n" +
            " screen_name TEXT ,\n" +
            " lang_eng TEXT ,\n" +
            " lang_guj TEXT ,\n" +
            " lang_hindi TEXT \n" + ")";

    public static final String TABLE_NAME_MULTI_LANGUAGE_SUCHNA_TABLE = "multi_langauge_suchna_table";
    public static final String MULTI_LANG_SUCHNA_ID = "multi_lang_suchna_id";
    public static final String SUCHNA_TITLE = "suchna_title";
    public static final String SUCHNA_DESC_ENG = "suchna_desc_eng";
    public static final String SUCHNA_DESC_GUJ = "suchna_desc_guj";
    public static final String SUCHNA_DESC_HINDI = "suchna_desc_hindi";
    public static final String CREATE_MULTI_LANGUAGE_SUCHNA_TABLE = "CREATE TABLE multi_langauge_suchna_table (\n" +
            " multi_lang_suchna_id  INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
            " screen_id TEXT ,\n" +
            " screen_name TEXT ,\n" +
            " suchna_title TEXT ,\n" +
            " suchna_desc_eng TEXT ,\n" +
            " suchna_desc_guj TEXT ,\n" +
            " suchna_desc_hindi TEXT ,\n" +
            " timestamp TEXT \n" + ")";

    public static final String TABLE_NAME_TEMP_SALESMAN_RIGHTS_TABLE = "temp_salesman_rights_table";
    public static final String TEMP_SALESMAN_RIGHT_ID = "temp_salesman_right_id";
    public static final String PARENT_ID = "parent_id";
    public static final String RIGHT_NAME = "right_name";
    public static final String RIGHT_EXP_DATE = "right_exp_date";
    public static final String RIGHT_EXP_DATE_DMY = "right_exp_date_dmy";
    public static final String CREATE_TEMP_SALESMAN_RIGHTS_TABLE = "CREATE TABLE temp_salesman_rights_table (\n" +
            " temp_salesman_right_id  INTEGER ,\n" +
            " parent_id TEXT ,\n" +
            " salesman_id TEXT ,\n" +
            " right_name TEXT ,\n" +
            " entry_date TEXT ,\n" +
            " right_exp_date TEXT ,\n" +
            " right_exp_date_dmy TEXT \n" + ")";


    private Context context;
    SQLiteDatabase db;    // manipulation with database
    DatabaseHelper dbhelper;

    public Database(Context ctx) {
        // TODO Auto-generated constructor stub
        this.context = ctx;
        dbhelper = new DatabaseHelper(ctx);
    }


    private class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABSE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SHOP_TABLE);
            db.execSQL(CREATE_DAILYSALES_URL_TABLE);
            db.execSQL(CREATE_DAILYSALES_TABLE);
            db.execSQL(CREATE_DAILYSALES_PROD);
            db.execSQL(CREATE_DAILYSALES_SHEME_TABLE);
            db.execSQL(CREATE_MULTI_LANGUAGE_TABLE);
            db.execSQL(CREATE_MULTI_LANGUAGE_SUCHNA_TABLE);
            db.execSQL(CREATE_TEMP_SALESMAN_RIGHTS_TABLE);
            Log.i("Database", "Table is creted...");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /*db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SHOP_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DAILYSALES_URL_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DAILYSALES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DAILYSALES_PROD_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DAILYSALES_SHEME_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MULTI_LANGUAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MULTI_LANGUAGE_SUCHNA_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TEMP_SALESMAN_RIGHTS_TABLE);*/
            //onCreate(db);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TEMP_SALESMAN_RIGHTS_TABLE);
            db.execSQL(CREATE_TEMP_SALESMAN_RIGHTS_TABLE);
            Log.i("Database", "onUpgrade..");
            Log.i("Database", "Database Scheme Upgrade");
        }
    }

    public Database open() throws SQLException {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbhelper.close();
    }


    public long addShop(String image_path, String salesman_id, String title, String address, String def_dist, String latitude, String longitude, String bit_id, String shop_keeper_name,
                        String shop_keeper_no1, String shop_keeper_no2, String invoice_type, String gst_no, String category_type, String status, String timestamp) {

        ContentValues values = new ContentValues();
        values.put("image_path", image_path);
        values.put("salesman_id", salesman_id);
        values.put("title", title);
        values.put("address", address);
        values.put("def_dist", def_dist);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("bit_id", bit_id);
        values.put("shop_keeper_name", shop_keeper_name);
        values.put("shop_keeper_no1", shop_keeper_no1);
        values.put("shop_keeper_no2", shop_keeper_no2);
        values.put("invoice_type", invoice_type);
        values.put("gst_no", gst_no);
        values.put("category_type", category_type);
        values.put("shop_status", status);
        values.put("timestamp", timestamp);
        Log.i("Database", "query inserted==>" + image_path);

        return db.insert(TABLE_NAME_SHOP_TABLE, null, values);

    }

    public Cursor viewAllShop() {

        String query = "select * from " + TABLE_NAME_SHOP_TABLE;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public Cursor viewShopByBitId(String bitId) {

        String query = "select * from " + TABLE_NAME_SHOP_TABLE + " WHERE bit_id=" + bitId;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }


    public void updateShopStatus(String status, String shop_id) {

        ContentValues values = new ContentValues();
        values.put(SHOP_STATUS, status);

        db.update(TABLE_NAME_SHOP_TABLE, values, SHOP_ID + " = ?", new String[]{shop_id});
        //Log.i("Database", "delete query==>" + query_id);
    }


    public void updateShopTimestamp(String timestamp, String shop_id) {

        ContentValues values = new ContentValues();
        values.put("timestamp", timestamp);

        db.update(TABLE_NAME_SHOP_TABLE, values, SHOP_ID + " = ?", new String[]{shop_id});
        //Log.i("Database", "delete query==>" + query_id);
    }

    public void deleteShop(String shop_id) {
        db.delete(TABLE_NAME_SHOP_TABLE, SHOP_ID + " = ?", new String[]{shop_id});
        //Log.i("Database", "delete query==>" + query_id);
    }


    public long addDailysalesUrl(String dailysales_url, String salesman_id, String bit_id, String dist_id, String shop_id, String timestamp, String dailysales_url_status) {

        ContentValues values = new ContentValues();
        values.put("dailysales_url", dailysales_url);
        values.put("salesman_id", salesman_id);
        values.put("bit_id", bit_id);
        values.put("dist_id", dist_id);
        values.put("shop_id", shop_id);
        values.put("timestamp", timestamp);
        values.put("dailysales_url_status", dailysales_url_status);
        return db.insert(TABLE_NAME_DAILYSALES_URL_TABLE, null, values);
    }

    public Cursor viewAllOrderUrls() {
        String query = "select * from " + TABLE_NAME_DAILYSALES_URL_TABLE;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public void updateOrderUrlStatus(String status, String url_id) {

        ContentValues values = new ContentValues();
        values.put(DAILYSALES_URL_STATUS, status);

        db.update(TABLE_NAME_DAILYSALES_URL_TABLE, values, DAILYSALES_URL_ID + " = ?", new String[]{url_id});
        //Log.i("Database", "delete query==>" + query_id);
    }

    public void updateOrderUrltimestamp(String timestamp, String url_id) {

        ContentValues values = new ContentValues();
        values.put("timestamp", timestamp);

        db.update(TABLE_NAME_DAILYSALES_URL_TABLE, values, DAILYSALES_URL_ID + " = ?", new String[]{url_id});
        //Log.i("Database", "delete query==>" + query_id);
    }

    public void deleteOrderUrl(String dailysales_url_id) {
        db.delete(TABLE_NAME_DAILYSALES_URL_TABLE, DAILYSALES_URL_ID + " = ?", new String[]{dailysales_url_id});
        Log.i("Database", "delete dailysales_url_id==>" + dailysales_url_id);
    }


    public long addDailySales(String shop_id, String salesman_id, String bit_id, String dist_id, String total_rs, String total_line, String daily_status, String entry_date, String latitude_o,
                              String longitude_o, String non_order_reason, String total_discount, String prev_order_time, String timestamp) {

        ContentValues values = new ContentValues();
        values.put("shop_id", shop_id);
        values.put("salesman_id", salesman_id);
        values.put("bit_id", bit_id);
        values.put("dist_id", dist_id);
        values.put("total_rs", total_rs);
        values.put("total_line", total_line);
        values.put("daily_status", daily_status);
        values.put("entry_date", entry_date);
        values.put("latitude_o", latitude_o);
        values.put("longitude_o", longitude_o);
        values.put("non_order_reason", non_order_reason);
        values.put("total_discount", total_discount);
        values.put("prev_order_time", prev_order_time);
        values.put("timestamp", timestamp);

        return db.insert(TABLE_NAME_DAILYSALES_TABLE, null, values);
    }

    public Cursor viewAllOrders() {

        String query = "select * from " + TABLE_NAME_DAILYSALES_TABLE;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public Cursor viewShopByOrder(String dailysales_id) {

        String query = "select * from " + TABLE_NAME_DAILYSALES_TABLE + " WHERE dailysales_id=" + dailysales_id;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public void deleteOrder(String dailysales_id) {
        db.delete(TABLE_NAME_DAILYSALES_TABLE, DAILYSALES_ID + " = ?", new String[]{dailysales_id});
        //Log.i("Database", "delete query==>" + query_id);
    }


    public long addDailysalesProd(String daily_sales_id, String product_id, String product_qty, String product_price) {

        ContentValues values = new ContentValues();
        values.put("daily_sales_id", daily_sales_id);
        values.put("product_id", product_id);
        values.put("product_qty", product_qty);
        values.put("product_price", product_price);

        return db.insert(TABLE_NAME_DAILYSALES_PROD_TABLE, null, values);
    }


    public Cursor viewAllOrderProd() {

        String query = "select * from " + TABLE_NAME_DAILYSALES_PROD_TABLE;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public Cursor viewShopByOrderProd(String dailysales_prod_id) {

        String query = "select * from " + TABLE_NAME_DAILYSALES_PROD_TABLE + " WHERE dailysales_prod_id=" + dailysales_prod_id;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public void deleteOrderProd(String dailysales_prod_id) {
        db.delete(TABLE_NAME_DAILYSALES_TABLE, DAILYSALES_PROD_ID + " = ?", new String[]{dailysales_prod_id});
        //Log.i("Database", "delete query==>" + query_id);
    }


    public long addDailysalesSchemes(String daily_sales_id, String scheme_id, String scheme_name_sort, String scheme_name_long, String scheme_type_id, String scheme_type_name,
                                     String scheme_image, String scheme_qty, String scheme_qty_del, String result_product_id, String result_product_qty, String result_product_price,
                                     String result_product_total_price, String result_product_image, String is_half_scheme) {

        ContentValues values = new ContentValues();
        values.put("dailysales_id", daily_sales_id);
        values.put("scheme_id", scheme_id);
        values.put("scheme_name_sort", scheme_name_sort);
        values.put("scheme_name_long", scheme_name_long);
        values.put("scheme_type_id", scheme_type_id);
        values.put("scheme_type_name", scheme_type_name);
        values.put("scheme_image", scheme_image);
        values.put("scheme_qty", scheme_qty);
        values.put("scheme_qty_del", scheme_qty_del);
        values.put("result_product_id", result_product_id);
        values.put("result_product_qty", result_product_qty);
        values.put("result_product_price", result_product_price);
        values.put("result_product_total_price", result_product_total_price);
        values.put("result_product_image", result_product_image);
        values.put("is_half_scheme", is_half_scheme);

        return db.insert(TABLE_NAME_DAILYSALES_SHEME_TABLE, null, values);
    }


    public Cursor viewAllOrderScheme() {

        String query = "select * from " + TABLE_NAME_DAILYSALES_SHEME_TABLE;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public Cursor viewShopByOrderScheme(String dailysales_schemes_id) {

        String query = "select * from " + TABLE_NAME_DAILYSALES_SHEME_TABLE + " WHERE dailysales_schemes_id=" + dailysales_schemes_id;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public void deleteOrderScheme(String dailysales_schemes_id) {
        db.delete(TABLE_NAME_DAILYSALES_SHEME_TABLE, DAILYSALES_SHEMES_ID + " = ?", new String[]{dailysales_schemes_id});
        //Log.i("Database", "delete query==>" + query_id);
    }


    public long addMultiLanguage(String screen_id, String screen_name, String lang_eng, String lang_guj, String lang_hindi) {
        ContentValues values = new ContentValues();
        values.put(SCREEN_ID, screen_id);
        values.put(SCREEN_NAME, screen_name);
        values.put(LANG_ENG, lang_eng);
        values.put(LANG_GUJ, lang_guj);
        values.put(LANG_HINDI, lang_hindi);

        return db.insert(TABLE_NAME_MULTI_LANGUAGE_TABLE, null, values);
    }

    public Cursor viewLanguage(String lang, String screenId) {

        String query = "select * from " + TABLE_NAME_MULTI_LANGUAGE_TABLE;
        /*if (lang.equalsIgnoreCase("ENG")) {
            query = "select * from " + TABLE_NAME_MULTI_LANGUAGE_TABLE;
        } else if (lang.equalsIgnoreCase("GUJ")) {
            //query = "select " + LANG_GUJ + " from " + TABLE_NAME_MULTI_LANGUAGE_TABLE;
            query = "select * from " + TABLE_NAME_MULTI_LANGUAGE_TABLE;
        } else if (lang.equalsIgnoreCase("HINDI")) {
            query = "select * from " + TABLE_NAME_MULTI_LANGUAGE_TABLE;
        }*/

        Cursor cur = db.rawQuery(query + " WHERE " + SCREEN_ID + "=" + screenId, null);
        //Log.i("Database", "query=>" + query);

        return cur;
    }

    public Cursor viewAllLanguage() {
        String query = "select * from " + TABLE_NAME_MULTI_LANGUAGE_TABLE;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public void deleteMultiLang() {
        db.delete(TABLE_NAME_MULTI_LANGUAGE_TABLE, null, null);
        //Log.i("Database", "delete query==>" + query_id);
    }


    public long addMultiSuchna(String screen_id, String screen_name, String suchna_title, String suchna_desc_eng, String suchna_desc_guj, String suchna_desc_hindi, String timestamp) {

        ContentValues values = new ContentValues();
        values.put(SCREEN_ID, screen_id);
        values.put(SCREEN_NAME, screen_name);
        values.put(SUCHNA_TITLE, suchna_title);
        values.put(SUCHNA_DESC_ENG, suchna_desc_eng);
        values.put(SUCHNA_DESC_GUJ, suchna_desc_guj);
        values.put(SUCHNA_DESC_HINDI, suchna_desc_hindi);
        values.put(TIMESTAMP, timestamp);

        return db.insert(TABLE_NAME_MULTI_LANGUAGE_SUCHNA_TABLE, null, values);

    }


    public Cursor viewMultiLangSuchna(String screenId) {

        String query = "select * from " + TABLE_NAME_MULTI_LANGUAGE_SUCHNA_TABLE;
        Cursor cur = db.rawQuery(query + " WHERE " + SCREEN_ID + "=" + screenId, null);
        //Log.i("Database", "query=>" + query);
        return cur;
    }

    public Cursor viewMultiLangSuchna() {
        String query = "select * from " + TABLE_NAME_MULTI_LANGUAGE_SUCHNA_TABLE;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public void deleteMultiLangSuchna() {
        db.delete(TABLE_NAME_MULTI_LANGUAGE_SUCHNA_TABLE, null, null);
        //Log.i("Database", "delete query==>" + query_id);
    }

    public long addTempSalesRights(String temp_salesman_right_id, String parent_id, String salesman_id, String right_name, String entry_date, String right_exp_date, String right_exp_date_dmy) {

        ContentValues values = new ContentValues();
        values.put(TEMP_SALESMAN_RIGHT_ID, temp_salesman_right_id);
        values.put(PARENT_ID, parent_id);
        values.put(SALESMAN_ID, salesman_id);
        values.put(RIGHT_NAME, right_name);
        values.put(ENTRY_DATE, entry_date);
        values.put(RIGHT_EXP_DATE, right_exp_date);
        values.put(RIGHT_EXP_DATE_DMY, right_exp_date_dmy);

        return db.insert(TABLE_NAME_TEMP_SALESMAN_RIGHTS_TABLE, null, values);

    }


    public Cursor viewAllTempSalesRights() {

        String query = "select * from " + TABLE_NAME_TEMP_SALESMAN_RIGHTS_TABLE;
        Cursor cur = db.rawQuery(query, null);
        //Log.i("Database", "query=>" + query);
        return cur;
    }

    public void deleteAllTempSalesRights() {
        db.delete(TABLE_NAME_TEMP_SALESMAN_RIGHTS_TABLE, null, null);
        //Log.i("Database", "delete query==>" + query_id);
    }

    public void deleteTempSalesRights(String dailysales_schemes_id) {
        db.delete(TABLE_NAME_TEMP_SALESMAN_RIGHTS_TABLE, TEMP_SALESMAN_RIGHT_ID + " = ?", new String[]{dailysales_schemes_id});
    }

}
