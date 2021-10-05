package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class SalesOrderProductPOJO {

    String product_id,title,point,product_qty,product_price,purchase_rate,product_qty_del,prod_unit,photo;
    ArrayList<Double> arrayList_minvalue;

    public SalesOrderProductPOJO(String product_id, String title, String point, String product_qty, String product_price, String purchase_rate, String product_qty_del, String prod_unit, String photo, ArrayList<Double> arrayList_minvalue) {
        this.product_id = product_id;
        this.title = title;
        this.point = point;
        this.product_qty = product_qty;
        this.product_price = product_price;
        this.purchase_rate = purchase_rate;
        this.product_qty_del = product_qty_del;
        this.prod_unit = prod_unit;
        this.photo = photo;
        this.arrayList_minvalue = arrayList_minvalue;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(String product_qty) {
        this.product_qty = product_qty;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getPurchase_rate() {
        return purchase_rate;
    }

    public void setPurchase_rate(String purchase_rate) {
        this.purchase_rate = purchase_rate;
    }

    public String getProduct_qty_del() {
        return product_qty_del;
    }

    public void setProduct_qty_del(String product_qty_del) {
        this.product_qty_del = product_qty_del;
    }

    public String getProd_unit() {
        return prod_unit;
    }

    public void setProd_unit(String prod_unit) {
        this.prod_unit = prod_unit;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<Double> getArrayList_minvalue() {
        return arrayList_minvalue;
    }

    public void setArrayList_minvalue(ArrayList<Double> arrayList_minvalue) {
        this.arrayList_minvalue = arrayList_minvalue;
    }
}
