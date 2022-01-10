package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 30-12-2021
 */
public class DeliveryOrderProdModel {

    String prod_id, prod_name, prod_img, prod_unit, prod_qty, prod_del_qty, prod_price, ptr_rs, tp_rs, mrp_rs, order_rs, delivery_rs, is_nline;
    ArrayList<Double> arrayList_minvalue;

    public DeliveryOrderProdModel(String prod_id, String prod_name, String prod_img, String prod_unit, String prod_qty, String prod_del_qty, String prod_price, String ptr_rs, String tp_rs, String mrp_rs, String order_rs, String delivery_rs, String is_nline, ArrayList<Double> arrayList_minvalue) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_img = prod_img;
        this.prod_unit = prod_unit;
        this.prod_qty = prod_qty;
        this.prod_del_qty = prod_del_qty;
        this.prod_price = prod_price;
        this.ptr_rs = ptr_rs;
        this.tp_rs = tp_rs;
        this.mrp_rs = mrp_rs;
        this.order_rs = order_rs;
        this.delivery_rs = delivery_rs;
        this.is_nline = is_nline;
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

    public String getProd_img() {
        return prod_img;
    }

    public void setProd_img(String prod_img) {
        this.prod_img = prod_img;
    }

    public String getProd_unit() {
        return prod_unit;
    }

    public void setProd_unit(String prod_unit) {
        this.prod_unit = prod_unit;
    }

    public String getProd_qty() {
        return prod_qty;
    }

    public void setProd_qty(String prod_qty) {
        this.prod_qty = prod_qty;
    }

    public String getProd_del_qty() {
        return prod_del_qty;
    }

    public void setProd_del_qty(String prod_del_qty) {
        this.prod_del_qty = prod_del_qty;
    }

    public String getProd_price() {
        return prod_price;
    }

    public void setProd_price(String prod_price) {
        this.prod_price = prod_price;
    }

    public String getPtr_rs() {
        return ptr_rs;
    }

    public void setPtr_rs(String ptr_rs) {
        this.ptr_rs = ptr_rs;
    }

    public String getTp_rs() {
        return tp_rs;
    }

    public void setTp_rs(String tp_rs) {
        this.tp_rs = tp_rs;
    }

    public String getMrp_rs() {
        return mrp_rs;
    }

    public void setMrp_rs(String mrp_rs) {
        this.mrp_rs = mrp_rs;
    }

    public String getOrder_rs() {
        return order_rs;
    }

    public void setOrder_rs(String order_rs) {
        this.order_rs = order_rs;
    }

    public String getDelivery_rs() {
        return delivery_rs;
    }

    public void setDelivery_rs(String delivery_rs) {
        this.delivery_rs = delivery_rs;
    }

    public String getIs_nline() {
        return is_nline;
    }

    public void setIs_nline(String is_nline) {
        this.is_nline = is_nline;
    }

    public ArrayList<Double> getArrayList_minvalue() {
        return arrayList_minvalue;
    }

    public void setArrayList_minvalue(ArrayList<Double> arrayList_minvalue) {
        this.arrayList_minvalue = arrayList_minvalue;
    }
}
