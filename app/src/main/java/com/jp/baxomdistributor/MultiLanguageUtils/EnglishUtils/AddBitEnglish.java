package com.jp.baxomdistributor.MultiLanguageUtils.EnglishUtils;

import android.util.Log;

import java.util.ArrayList;

public class AddBitEnglish<T> {
   /* public static String NAME="Name";
    public static String SERNAME="Sername";*/


    public String addBit_titlebar_heading;
    public String addBit_bit_name_title;
    public String addBit_bit_name_hint;
    public String addBit_bit_desc_title;
    public String addBit_bit_desc_hint;
    public String addBit_bit_target_title;
    public String addBit_bit_target_hint;
    public String addBit_distributor_txt_title;
    public String addBit_btn_submit_title;

    ArrayList<String> arrayList_eng;

    public AddBitEnglish(ArrayList<String> arrayList_eng) {
        this.arrayList_eng = arrayList_eng;

        Log.i("Gujarati", "Count==>" + arrayList_eng.size());

        addBit_titlebar_heading = arrayList_eng.get(0);
        addBit_bit_name_title = arrayList_eng.get(1);
        addBit_bit_name_hint = arrayList_eng.get(2);
        addBit_bit_desc_title = arrayList_eng.get(3);
        addBit_bit_desc_hint = arrayList_eng.get(4);
        addBit_bit_target_title = arrayList_eng.get(5);
        addBit_bit_target_hint = arrayList_eng.get(6);
        addBit_distributor_txt_title = arrayList_eng.get(7);
        addBit_btn_submit_title = arrayList_eng.get(8);
    }


}
