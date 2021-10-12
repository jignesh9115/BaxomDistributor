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
    public static final int DATABSE_VERSION = 1;


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
    public static final String TIMESTAMP = "timestamp";
    public static final String CREATE_MULTI_LANGUAGE_SUCHNA_TABLE = "CREATE TABLE multi_langauge_suchna_table (\n" +
            " multi_lang_suchna_id  INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
            " screen_id TEXT ,\n" +
            " screen_name TEXT ,\n" +
            " suchna_title TEXT ,\n" +
            " suchna_desc_eng TEXT ,\n" +
            " suchna_desc_guj TEXT ,\n" +
            " suchna_desc_hindi TEXT ,\n" +
            " timestamp TEXT \n" + ")";


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
            db.execSQL(CREATE_MULTI_LANGUAGE_TABLE);
            db.execSQL(CREATE_MULTI_LANGUAGE_SUCHNA_TABLE);
            Log.i("Database", "Table is creted...");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("Database", "onUpgrade..");
        }
    }

    public Database open() throws SQLException {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbhelper.close();
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
}
