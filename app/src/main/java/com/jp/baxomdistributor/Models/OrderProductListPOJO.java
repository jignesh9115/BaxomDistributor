package com.jp.baxomdistributor.Models;

public class OrderProductListPOJO {

    String p_id, p_qty, p_price, p_purchase_price, p_title, p_title_hindi, p_prod_unit, p_short_desc, p_long_desc, p_gst, p_dispatch_code;


    public OrderProductListPOJO(String p_id, String p_qty, String p_price, String p_purchase_price, String p_title, String p_title_hindi, String p_prod_unit, String p_short_desc, String p_long_desc, String p_gst, String p_dispatch_code) {
        this.p_id = p_id;
        this.p_qty = p_qty;
        this.p_price = p_price;
        this.p_purchase_price = p_purchase_price;
        this.p_title = p_title;
        this.p_title_hindi = p_title_hindi;
        this.p_prod_unit = p_prod_unit;
        this.p_short_desc = p_short_desc;
        this.p_long_desc = p_long_desc;
        this.p_gst = p_gst;
        this.p_dispatch_code = p_dispatch_code;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getP_qty() {
        return p_qty;
    }

    public void setP_qty(String p_qty) {
        this.p_qty = p_qty;
    }

    public String getP_price() {
        return p_price;
    }

    public void setP_price(String p_price) {
        this.p_price = p_price;
    }

    public String getP_purchase_price() {
        return p_purchase_price;
    }

    public void setP_purchase_price(String p_purchase_price) {
        this.p_purchase_price = p_purchase_price;
    }

    public String getP_title() {
        return p_title;
    }

    public void setP_title(String p_title) {
        this.p_title = p_title;
    }

    public String getP_title_hindi() {
        return p_title_hindi;
    }

    public void setP_title_hindi(String p_title_hindi) {
        this.p_title_hindi = p_title_hindi;
    }

    public String getP_prod_unit() {
        return p_prod_unit;
    }

    public void setP_prod_unit(String p_prod_unit) {
        this.p_prod_unit = p_prod_unit;
    }

    public String getP_short_desc() {
        return p_short_desc;
    }

    public void setP_short_desc(String p_short_desc) {
        this.p_short_desc = p_short_desc;
    }

    public String getP_long_desc() {
        return p_long_desc;
    }

    public void setP_long_desc(String p_long_desc) {
        this.p_long_desc = p_long_desc;
    }

    public String getP_gst() {
        return p_gst;
    }

    public void setP_gst(String p_gst) {
        this.p_gst = p_gst;
    }

    public String getP_dispatch_code() {
        return p_dispatch_code;
    }

    public void setP_dispatch_code(String p_dispatch_code) {
        this.p_dispatch_code = p_dispatch_code;
    }
}
