package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 30-12-2021
 */
public class DeliveryOrderSchemeModel {

    String scheme_id, scheme_name, scheme_image, scheme_price, scheme_value, result_prod_image, scheme_qty, scheme_del_qty;
    ArrayList<SchemeProductPOJO> arrayList;

    public DeliveryOrderSchemeModel(String scheme_id, String scheme_name, String scheme_image, String scheme_price, String scheme_value, String result_prod_image, String scheme_qty, String scheme_del_qty, ArrayList<SchemeProductPOJO> arrayList) {
        this.scheme_id = scheme_id;
        this.scheme_name = scheme_name;
        this.scheme_image = scheme_image;
        this.scheme_price = scheme_price;
        this.scheme_value = scheme_value;
        this.result_prod_image = result_prod_image;
        this.scheme_qty = scheme_qty;
        this.scheme_del_qty = scheme_del_qty;
        this.arrayList = arrayList;
    }

    public String getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(String scheme_id) {
        this.scheme_id = scheme_id;
    }

    public String getScheme_name() {
        return scheme_name;
    }

    public void setScheme_name(String scheme_name) {
        this.scheme_name = scheme_name;
    }

    public String getScheme_image() {
        return scheme_image;
    }

    public void setScheme_image(String scheme_image) {
        this.scheme_image = scheme_image;
    }

    public String getScheme_price() {
        return scheme_price;
    }

    public void setScheme_price(String scheme_price) {
        this.scheme_price = scheme_price;
    }

    public String getScheme_value() {
        return scheme_value;
    }

    public void setScheme_value(String scheme_value) {
        this.scheme_value = scheme_value;
    }

    public String getResult_prod_image() {
        return result_prod_image;
    }

    public void setResult_prod_image(String result_prod_image) {
        this.result_prod_image = result_prod_image;
    }

    public String getScheme_qty() {
        return scheme_qty;
    }

    public void setScheme_qty(String scheme_qty) {
        this.scheme_qty = scheme_qty;
    }

    public String getScheme_del_qty() {
        return scheme_del_qty;
    }

    public void setScheme_del_qty(String scheme_del_qty) {
        this.scheme_del_qty = scheme_del_qty;
    }

    public ArrayList<SchemeProductPOJO> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<SchemeProductPOJO> arrayList) {
        this.arrayList = arrayList;
    }
}
