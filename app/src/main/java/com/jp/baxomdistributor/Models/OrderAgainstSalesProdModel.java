package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 29-04-2022
 */
public class OrderAgainstSalesProdModel {

    String prod_id, prod_name, prod_unit, prod_photo, order_qty, stock_qty, one_day_avg, scheme_qty, purchase_rate, sales_rate;

    ArrayList<Double> arrayList_minvalue;

    public OrderAgainstSalesProdModel(String prod_id, String prod_name, String prod_unit, String prod_photo, String order_qty, String stock_qty, String one_day_avg, String scheme_qty, String purchase_rate, String sales_rate, ArrayList<Double> arrayList_minvalue) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_unit = prod_unit;
        this.prod_photo = prod_photo;
        this.order_qty = order_qty;
        this.stock_qty = stock_qty;
        this.one_day_avg = one_day_avg;
        this.scheme_qty = scheme_qty;
        this.purchase_rate = purchase_rate;
        this.sales_rate = sales_rate;
        this.arrayList_minvalue = arrayList_minvalue;
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

    public String getProd_photo() {
        return prod_photo;
    }

    public void setProd_photo(String prod_photo) {
        this.prod_photo = prod_photo;
    }

    public String getOrder_qty() {
        return order_qty;
    }

    public void setOrder_qty(String order_qty) {
        this.order_qty = order_qty;
    }

    public String getStock_qty() {
        return stock_qty;
    }

    public void setStock_qty(String stock_qty) {
        this.stock_qty = stock_qty;
    }

    public String getOne_day_avg() {
        return one_day_avg;
    }

    public void setOne_day_avg(String one_day_avg) {
        this.one_day_avg = one_day_avg;
    }

    public String getScheme_qty() {
        return scheme_qty;
    }

    public void setScheme_qty(String scheme_qty) {
        this.scheme_qty = scheme_qty;
    }

    public String getPurchase_rate() {
        return purchase_rate;
    }

    public void setPurchase_rate(String purchase_rate) {
        this.purchase_rate = purchase_rate;
    }

    public String getSales_rate() {
        return sales_rate;
    }

    public void setSales_rate(String sales_rate) {
        this.sales_rate = sales_rate;
    }

    public ArrayList<Double> getArrayList_minvalue() {
        return arrayList_minvalue;
    }

    public void setArrayList_minvalue(ArrayList<Double> arrayList_minvalue) {
        this.arrayList_minvalue = arrayList_minvalue;
    }
}
