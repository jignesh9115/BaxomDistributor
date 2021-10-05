package com.jp.baxomdistributor.MultiLanguageUtils.GujaratiUtils;


import android.util.Log;

import java.util.ArrayList;

public class AddShopGujarati<T> {

    ArrayList<String> arrayList_guj;

    public String addShop_titlebar_heading;
    public String addShop_shop_name_title;
    public String addShop_shop_name_hint;
    public String addShop_shop_address_title;
    public String addShop_shop_address_hint;
    public String addShop_contact_person_name_title;
    public String addShop_contact_person_name_hint;
    public String addShop_contact1_title;
    public String addShop_contact1_hint;
    public String addShop_contact2_title;
    public String addShop_contact2_hint;
    public String addShop_shop_category_title;
    public String addShop_invoice_type_title;
    public String addShop_gstno_title;
    public String addShop_gstno_hint;
    public String addShop_btn_verifygst_title;
    public String addShop_btn_submit_title;

    public AddShopGujarati(ArrayList<String> arrayList_guj1) {
        this.arrayList_guj = arrayList_guj1;

        Log.i("Gujarati", "Count==>" + arrayList_guj.size());

        addShop_titlebar_heading = arrayList_guj.get(0);
        addShop_shop_name_title = arrayList_guj.get(1);
        addShop_shop_name_hint = arrayList_guj.get(2);
        addShop_shop_address_title = arrayList_guj.get(3);
        addShop_shop_address_hint = arrayList_guj.get(4);
        addShop_contact_person_name_title = arrayList_guj.get(5);
        addShop_contact_person_name_hint = arrayList_guj.get(6);
        addShop_contact1_title = arrayList_guj.get(7);
        addShop_contact1_hint = arrayList_guj.get(8);
        addShop_contact2_title = arrayList_guj.get(9);
        addShop_contact2_hint = arrayList_guj.get(10);
        addShop_shop_category_title = arrayList_guj.get(11);
        addShop_invoice_type_title = arrayList_guj.get(12);
        addShop_gstno_title = arrayList_guj.get(13);
        addShop_gstno_hint = arrayList_guj.get(14);
        addShop_btn_verifygst_title = arrayList_guj.get(15);
        addShop_btn_submit_title = arrayList_guj.get(16);

    }


}
