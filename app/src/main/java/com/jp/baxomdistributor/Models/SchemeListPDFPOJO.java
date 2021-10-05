package com.jp.baxomdistributor.Models;

public class SchemeListPDFPOJO {

    String scheme_id,scheme_name,scheme_qty,result_product_qty,result_product_price;

    public SchemeListPDFPOJO(String scheme_id, String scheme_name, String scheme_qty, String result_product_qty, String result_product_price) {
        this.scheme_id = scheme_id;
        this.scheme_name = scheme_name;
        this.scheme_qty = scheme_qty;
        this.result_product_qty = result_product_qty;
        this.result_product_price = result_product_price;
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

    public String getScheme_qty() {
        return scheme_qty;
    }

    public void setScheme_qty(String scheme_qty) {
        this.scheme_qty = scheme_qty;
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
}
