package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 04-05-2022
 */
public class OrderAgainstSalesOrderModel {

    String prod_id, prod_name, prod_unit, prod_gst, prod_hsn_code, prod_ptr_rs, prod_biz_rs, prod_stock, prod_day_sale, prod_order_qty;

    public OrderAgainstSalesOrderModel(String prod_id, String prod_name, String prod_unit, String prod_gst, String prod_hsn_code, String prod_ptr_rs, String prod_biz_rs, String prod_stock, String prod_day_sale, String prod_order_qty) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_unit = prod_unit;
        this.prod_gst = prod_gst;
        this.prod_hsn_code = prod_hsn_code;
        this.prod_ptr_rs = prod_ptr_rs;
        this.prod_biz_rs = prod_biz_rs;
        this.prod_stock = prod_stock;
        this.prod_day_sale = prod_day_sale;
        this.prod_order_qty = prod_order_qty;
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_unit() {
        return prod_unit;
    }

    public void setProd_unit(String prod_unit) {
        this.prod_unit = prod_unit;
    }

    public String getProd_gst() {
        return prod_gst;
    }

    public void setProd_gst(String prod_gst) {
        this.prod_gst = prod_gst;
    }

    public String getProd_hsn_code() {
        return prod_hsn_code;
    }

    public void setProd_hsn_code(String prod_hsn_code) {
        this.prod_hsn_code = prod_hsn_code;
    }

    public String getProd_ptr_rs() {
        return prod_ptr_rs;
    }

    public void setProd_ptr_rs(String prod_ptr_rs) {
        this.prod_ptr_rs = prod_ptr_rs;
    }

    public String getProd_biz_rs() {
        return prod_biz_rs;
    }

    public void setProd_biz_rs(String prod_biz_rs) {
        this.prod_biz_rs = prod_biz_rs;
    }

    public String getProd_stock() {
        return prod_stock;
    }

    public void setProd_stock(String prod_stock) {
        this.prod_stock = prod_stock;
    }

    public String getProd_day_sale() {
        return prod_day_sale;
    }

    public void setProd_day_sale(String prod_day_sale) {
        this.prod_day_sale = prod_day_sale;
    }

    public String getProd_order_qty() {
        return prod_order_qty;
    }

    public void setProd_order_qty(String prod_order_qty) {
        this.prod_order_qty = prod_order_qty;
    }

}
