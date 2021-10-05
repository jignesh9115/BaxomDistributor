package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class ViewSchemesOrderPOJO {

    String scheme_id,scheme_name_sort,scheme_name_long,scheme_type_id,scheme_type_name,scheme_image,scheme_qty,scheme_qty_del,scheme_price,result_product_id,result_product_qty,result_product_price,total_result_prod_value,result_product_image,is_scheme_half;

    ArrayList<SchemeProductPOJO> arrayList;

    public ViewSchemesOrderPOJO(String scheme_id, String scheme_name_sort, String scheme_name_long, String scheme_type_id, String scheme_type_name, String scheme_image, String scheme_qty, String scheme_qty_del, String scheme_price, String result_product_id, String result_product_qty, String result_product_price, String total_result_prod_value, String result_product_image, String is_scheme_half, ArrayList<SchemeProductPOJO> arrayList) {
        this.scheme_id = scheme_id;
        this.scheme_name_sort = scheme_name_sort;
        this.scheme_name_long = scheme_name_long;
        this.scheme_type_id = scheme_type_id;
        this.scheme_type_name = scheme_type_name;
        this.scheme_image = scheme_image;
        this.scheme_qty = scheme_qty;
        this.scheme_qty_del = scheme_qty_del;
        this.scheme_price = scheme_price;
        this.result_product_id = result_product_id;
        this.result_product_qty = result_product_qty;
        this.result_product_price = result_product_price;
        this.total_result_prod_value = total_result_prod_value;
        this.result_product_image = result_product_image;
        this.is_scheme_half = is_scheme_half;
        this.arrayList = arrayList;
    }

    public String getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(String scheme_id) {
        this.scheme_id = scheme_id;
    }

    public String getScheme_name_sort() {
        return scheme_name_sort;
    }

    public void setScheme_name_sort(String scheme_name_sort) {
        this.scheme_name_sort = scheme_name_sort;
    }

    public String getScheme_name_long() {
        return scheme_name_long;
    }

    public void setScheme_name_long(String scheme_name_long) {
        this.scheme_name_long = scheme_name_long;
    }

    public String getScheme_type_id() {
        return scheme_type_id;
    }

    public void setScheme_type_id(String scheme_type_id) {
        this.scheme_type_id = scheme_type_id;
    }

    public String getScheme_type_name() {
        return scheme_type_name;
    }

    public void setScheme_type_name(String scheme_type_name) {
        this.scheme_type_name = scheme_type_name;
    }

    public String getScheme_image() {
        return scheme_image;
    }

    public void setScheme_image(String scheme_image) {
        this.scheme_image = scheme_image;
    }

    public String getScheme_qty() {
        return scheme_qty;
    }

    public void setScheme_qty(String scheme_qty) {
        this.scheme_qty = scheme_qty;
    }

    public String getScheme_qty_del() {
        return scheme_qty_del;
    }

    public void setScheme_qty_del(String scheme_qty_del) {
        this.scheme_qty_del = scheme_qty_del;
    }

    public String getScheme_price() {
        return scheme_price;
    }

    public void setScheme_price(String scheme_price) {
        this.scheme_price = scheme_price;
    }

    public String getResult_product_id() {
        return result_product_id;
    }

    public void setResult_product_id(String result_product_id) {
        this.result_product_id = result_product_id;
    }

    public String getResult_product_qty() {
        return result_product_qty;
    }

    public void setResult_product_qty(String result_product_qty) {
        this.result_product_qty = result_product_qty;
    }

    public String getResult_product_price() {
        return result_product_price;
    }

    public void setResult_product_price(String result_product_price) {
        this.result_product_price = result_product_price;
    }

    public String getTotal_result_prod_value() {
        return total_result_prod_value;
    }

    public void setTotal_result_prod_value(String total_result_prod_value) {
        this.total_result_prod_value = total_result_prod_value;
    }

    public String getResult_product_image() {
        return result_product_image;
    }

    public void setResult_product_image(String result_product_image) {
        this.result_product_image = result_product_image;
    }

    public String getIs_scheme_half() {
        return is_scheme_half;
    }

    public void setIs_scheme_half(String is_scheme_half) {
        this.is_scheme_half = is_scheme_half;
    }

    public ArrayList<SchemeProductPOJO> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<SchemeProductPOJO> arrayList) {
        this.arrayList = arrayList;
    }
}
