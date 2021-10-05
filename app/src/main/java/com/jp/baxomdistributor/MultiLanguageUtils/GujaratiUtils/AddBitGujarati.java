package com.jp.baxomdistributor.MultiLanguageUtils.GujaratiUtils;


import android.util.Log;

import java.util.ArrayList;

public class AddBitGujarati<T> {

    ArrayList<String> arrayList_guj;

    public String addBit_titlebar_heading;
    public String addBit_bit_name_title;
    public String addBit_bit_name_hint;
    public String addBit_bit_desc_title;
    public String addBit_bit_desc_hint;
    public String addBit_bit_target_title;
    public String addBit_bit_target_hint;
    public String addBit_distributor_txt_title;
    public String addBit_btn_submit_title;

    public AddBitGujarati(ArrayList<String> arrayList_guj1) {
        this.arrayList_guj = arrayList_guj1;

        Log.i("Gujarati", "Count==>" + arrayList_guj.size());

        addBit_titlebar_heading = arrayList_guj.get(0);
        addBit_bit_name_title = arrayList_guj.get(1);
        addBit_bit_name_hint = arrayList_guj.get(2);
        addBit_bit_desc_title = arrayList_guj.get(3);
        addBit_bit_desc_hint = arrayList_guj.get(4);
        addBit_bit_target_title = arrayList_guj.get(5);
        addBit_bit_target_hint = arrayList_guj.get(6);
        addBit_distributor_txt_title = arrayList_guj.get(7);
        addBit_btn_submit_title = arrayList_guj.get(8);

    }


}
