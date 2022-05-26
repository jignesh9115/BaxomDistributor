package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class SchemeListPOJO {

    String scheme_id, scheme_name, scheme_long_name, scheme_type_id, scheme_type_name, scheme_image,
            scheme_qty, scheme_qty_del, scheme_order_price,
            scheme_image_sort, scheme_price, scheme_price_purchase_rs, scheme_price_biz_rs, scheme_price_mrp_rs, result_product_id,
            result_product_name, result_product_qty, result_product_price, result_product_price_purchase_rs, result_product_price_biz_rs,
            result_product_price_mrp_rs, total_result_prod_value, total_result_prod_value_purchase_rs, total_result_prod_value_biz_rs,
            total_result_prod_value_mrp_rs, pricelist_id, result_product_photo, result_product_photo_sort, pricelist_name, is_scheme_half,
            status, curr_order_qty;

    ArrayList<SchemeProductPOJO> arrayList;


    public SchemeListPOJO(String scheme_id, String scheme_name, String scheme_long_name, String scheme_type_id, String scheme_type_name, String scheme_image, String scheme_image_sort, String scheme_price, String result_product_id, String result_product_qty, String result_product_price, String total_result_prod_value, String pricelist_id, String result_product_photo, String result_product_photo_sort, String pricelist_name, String is_scheme_half, String status, ArrayList<SchemeProductPOJO> arrayList) {
        this.scheme_id = scheme_id;
        this.scheme_name = scheme_name;
        this.scheme_long_name = scheme_long_name;
        this.scheme_type_id = scheme_type_id;
        this.scheme_type_name = scheme_type_name;
        this.scheme_image = scheme_image;
        this.scheme_image_sort = scheme_image_sort;
        this.scheme_price = scheme_price;
        this.result_product_id = result_product_id;
        this.result_product_qty = result_product_qty;
        this.result_product_price = result_product_price;
        this.total_result_prod_value = total_result_prod_value;
        this.pricelist_id = pricelist_id;
        this.result_product_photo = result_product_photo;
        this.result_product_photo_sort = result_product_photo_sort;
        this.pricelist_name = pricelist_name;
        this.is_scheme_half = is_scheme_half;
        this.status = status;
        this.arrayList = arrayList;
    }

    public SchemeListPOJO(String scheme_id, String scheme_name, String scheme_long_name, String scheme_type_id, String scheme_type_name, String scheme_image, String scheme_qty, String scheme_qty_del, String scheme_order_price, String scheme_image_sort, String scheme_price, String result_product_id, String result_product_qty, String result_product_price, String total_result_prod_value, String pricelist_id, String result_product_photo, String result_product_photo_sort, String pricelist_name, String is_scheme_half, String status, ArrayList<SchemeProductPOJO> arrayList) {
        this.scheme_id = scheme_id;
        this.scheme_name = scheme_name;
        this.scheme_long_name = scheme_long_name;
        this.scheme_type_id = scheme_type_id;
        this.scheme_type_name = scheme_type_name;
        this.scheme_image = scheme_image;
        this.scheme_qty = scheme_qty;
        this.scheme_qty_del = scheme_qty_del;
        this.scheme_order_price = scheme_order_price;
        this.scheme_image_sort = scheme_image_sort;
        this.scheme_price = scheme_price;
        this.result_product_id = result_product_id;
        this.result_product_qty = result_product_qty;
        this.result_product_price = result_product_price;
        this.total_result_prod_value = total_result_prod_value;
        this.pricelist_id = pricelist_id;
        this.result_product_photo = result_product_photo;
        this.result_product_photo_sort = result_product_photo_sort;
        this.pricelist_name = pricelist_name;
        this.is_scheme_half = is_scheme_half;
        this.status = status;
        this.arrayList = arrayList;
    }

    public SchemeListPOJO(String scheme_id, String scheme_name, String scheme_long_name, String scheme_type_id, String scheme_type_name, String scheme_image, String scheme_image_sort, String scheme_price, String result_product_id, String result_product_qty, String result_product_price, String total_result_prod_value, String pricelist_id, String result_product_photo, String result_product_photo_sort, String pricelist_name, String is_scheme_half, String status, String curr_order_qty, ArrayList<SchemeProductPOJO> arrayList) {
        this.scheme_id = scheme_id;
        this.scheme_name = scheme_name;
        this.scheme_long_name = scheme_long_name;
        this.scheme_type_id = scheme_type_id;
        this.scheme_type_name = scheme_type_name;
        this.scheme_image = scheme_image;
        this.scheme_image_sort = scheme_image_sort;
        this.scheme_price = scheme_price;
        this.result_product_id = result_product_id;
        this.result_product_qty = result_product_qty;
        this.result_product_price = result_product_price;
        this.total_result_prod_value = total_result_prod_value;
        this.pricelist_id = pricelist_id;
        this.result_product_photo = result_product_photo;
        this.result_product_photo_sort = result_product_photo_sort;
        this.pricelist_name = pricelist_name;
        this.is_scheme_half = is_scheme_half;
        this.status = status;
        this.curr_order_qty = curr_order_qty;
        this.arrayList = arrayList;
    }

    public SchemeListPOJO(String scheme_id, String scheme_name, String scheme_long_name, String scheme_type_id, String scheme_type_name, String scheme_image, String scheme_image_sort, String scheme_price, String scheme_price_purchase_rs, String scheme_price_biz_rs, String scheme_price_mrp_rs, String result_product_id, String result_product_name, String result_product_qty, String result_product_price, String result_product_price_purchase_rs, String result_product_price_biz_rs, String result_product_price_mrp_rs, String total_result_prod_value, String total_result_prod_value_purchase_rs, String total_result_prod_value_biz_rs, String total_result_prod_value_mrp_rs, String pricelist_id, String result_product_photo, String result_product_photo_sort, String pricelist_name, String is_scheme_half, String status, String curr_order_qty, ArrayList<SchemeProductPOJO> arrayList) {
        this.scheme_id = scheme_id;
        this.scheme_name = scheme_name;
        this.scheme_long_name = scheme_long_name;
        this.scheme_type_id = scheme_type_id;
        this.scheme_type_name = scheme_type_name;
        this.scheme_image = scheme_image;
        this.scheme_image_sort = scheme_image_sort;
        this.scheme_price = scheme_price;
        this.scheme_price_purchase_rs = scheme_price_purchase_rs;
        this.scheme_price_biz_rs = scheme_price_biz_rs;
        this.scheme_price_mrp_rs = scheme_price_mrp_rs;
        this.result_product_id = result_product_id;
        this.result_product_name = result_product_name;
        this.result_product_qty = result_product_qty;
        this.result_product_price = result_product_price;
        this.result_product_price_purchase_rs = result_product_price_purchase_rs;
        this.result_product_price_biz_rs = result_product_price_biz_rs;
        this.result_product_price_mrp_rs = result_product_price_mrp_rs;
        this.total_result_prod_value = total_result_prod_value;
        this.total_result_prod_value_purchase_rs = total_result_prod_value_purchase_rs;
        this.total_result_prod_value_biz_rs = total_result_prod_value_biz_rs;
        this.total_result_prod_value_mrp_rs = total_result_prod_value_mrp_rs;
        this.pricelist_id = pricelist_id;
        this.result_product_photo = result_product_photo;
        this.result_product_photo_sort = result_product_photo_sort;
        this.pricelist_name = pricelist_name;
        this.is_scheme_half = is_scheme_half;
        this.status = status;
        this.curr_order_qty = curr_order_qty;
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

    public String getScheme_long_name() {
        return scheme_long_name;
    }

    public void setScheme_long_name(String scheme_long_name) {
        this.scheme_long_name = scheme_long_name;
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

    public String getScheme_order_price() {
        return scheme_order_price;
    }

    public void setScheme_order_price(String scheme_order_price) {
        this.scheme_order_price = scheme_order_price;
    }

    public String getScheme_image_sort() {
        return scheme_image_sort;
    }

    public void setScheme_image_sort(String scheme_image_sort) {
        this.scheme_image_sort = scheme_image_sort;
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

    public String getPricelist_id() {
        return pricelist_id;
    }

    public void setPricelist_id(String pricelist_id) {
        this.pricelist_id = pricelist_id;
    }

    public String getResult_product_photo() {
        return result_product_photo;
    }

    public void setResult_product_photo(String result_product_photo) {
        this.result_product_photo = result_product_photo;
    }

    public String getResult_product_photo_sort() {
        return result_product_photo_sort;
    }

    public void setResult_product_photo_sort(String result_product_photo_sort) {
        this.result_product_photo_sort = result_product_photo_sort;
    }

    public String getPricelist_name() {
        return pricelist_name;
    }

    public void setPricelist_name(String pricelist_name) {
        this.pricelist_name = pricelist_name;
    }

    public String getIs_scheme_half() {
        return is_scheme_half;
    }

    public void setIs_scheme_half(String is_scheme_half) {
        this.is_scheme_half = is_scheme_half;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurr_order_qty() {
        return curr_order_qty;
    }

    public void setCurr_order_qty(String curr_order_qty) {
        this.curr_order_qty = curr_order_qty;
    }

    public ArrayList<SchemeProductPOJO> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<SchemeProductPOJO> arrayList) {
        this.arrayList = arrayList;
    }

    public String getScheme_price_purchase_rs() {
        return scheme_price_purchase_rs;
    }

    public void setScheme_price_purchase_rs(String scheme_price_purchase_rs) {
        this.scheme_price_purchase_rs = scheme_price_purchase_rs;
    }

    public String getScheme_price_biz_rs() {
        return scheme_price_biz_rs;
    }

    public void setScheme_price_biz_rs(String scheme_price_biz_rs) {
        this.scheme_price_biz_rs = scheme_price_biz_rs;
    }

    public String getScheme_price_mrp_rs() {
        return scheme_price_mrp_rs;
    }

    public void setScheme_price_mrp_rs(String scheme_price_mrp_rs) {
        this.scheme_price_mrp_rs = scheme_price_mrp_rs;
    }

    public String getResult_product_name() {
        return result_product_name;
    }

    public void setResult_product_name(String result_product_name) {
        this.result_product_name = result_product_name;
    }

    public String getResult_product_price_purchase_rs() {
        return result_product_price_purchase_rs;
    }

    public void setResult_product_price_purchase_rs(String result_product_price_purchase_rs) {
        this.result_product_price_purchase_rs = result_product_price_purchase_rs;
    }

    public String getResult_product_price_biz_rs() {
        return result_product_price_biz_rs;
    }

    public void setResult_product_price_biz_rs(String result_product_price_biz_rs) {
        this.result_product_price_biz_rs = result_product_price_biz_rs;
    }

    public String getResult_product_price_mrp_rs() {
        return result_product_price_mrp_rs;
    }

    public void setResult_product_price_mrp_rs(String result_product_price_mrp_rs) {
        this.result_product_price_mrp_rs = result_product_price_mrp_rs;
    }

    public String getTotal_result_prod_value_purchase_rs() {
        return total_result_prod_value_purchase_rs;
    }

    public void setTotal_result_prod_value_purchase_rs(String total_result_prod_value_purchase_rs) {
        this.total_result_prod_value_purchase_rs = total_result_prod_value_purchase_rs;
    }

    public String getTotal_result_prod_value_biz_rs() {
        return total_result_prod_value_biz_rs;
    }

    public void setTotal_result_prod_value_biz_rs(String total_result_prod_value_biz_rs) {
        this.total_result_prod_value_biz_rs = total_result_prod_value_biz_rs;
    }

    public String getTotal_result_prod_value_mrp_rs() {
        return total_result_prod_value_mrp_rs;
    }

    public void setTotal_result_prod_value_mrp_rs(String total_result_prod_value_mrp_rs) {
        this.total_result_prod_value_mrp_rs = total_result_prod_value_mrp_rs;
    }
}
