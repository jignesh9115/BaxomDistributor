package com.jp.baxomdistributor.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PrefManager {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;

    public PrefManager(Context context, String pref_name) {
        this.context = context;
        Log.i("PrefManager", "inside PrefManager initialize");
        sp = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
    }

    public void setPrefString(String key, String value) {
        editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getPrefString(String key) {
        return sp.getString(key, "");
    }

    public void setPrefInt(String key, int value) {
        editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();

    }

    public int getPrefInt(String key) {
        return sp.getInt(key, 0);
    }

    public void setPrefFloat(String key, float value) {
        editor = sp.edit();
        editor.putFloat(key, value);
        editor.apply();

    }

    public float getPrefFloat(String key) {
        return sp.getFloat(key, 0);
    }

    public void setPrefBoolean(String key, boolean value) {
        editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    public boolean getPrefBoolean(String key) {
        return sp.getBoolean(key, false);
    }

}

