package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class AddPurchaseOrderProductsPOJO {

    String product_id, title, title_hindi, short_desc, long_desc, gst, dispatch_code, purchase_rate, point, prod_unit, photo,
            product_qty, product_qty_del, product_price, stock_qty, pending, stock_in_days;
    boolean isSelect = false;
    int start = 1, min_pos = 0;
    double p_price = 0, tot_order_price = 0;
    ArrayList<Double> arrayList_minvalue;

    public AddPurchaseOrderProductsPOJO(String product_id, String title, String title_hindi, String short_desc, String long_desc, String gst, String dispatch_code, String purchase_rate, String point, String prod_unit, String photo, String product_qty, String product_qty_del, String product_price, String stock_qty, String pending, String stock_in_days, ArrayList<Double> arrayList_minvalue) {
        this.product_id = product_id;
        this.title = title;
        this.title_hindi = title_hindi;
        this.short_desc = short_desc;
        this.long_desc = long_desc;
        this.gst = gst;
        this.dispatch_code = dispatch_code;
        this.purchase_rate = purchase_rate;
        this.point = point;
        this.prod_unit = prod_unit;
        this.photo = photo;
        this.product_qty = product_qty;
        this.product_qty_del = product_qty_del;
        this.product_price = product_price;
        this.stock_qty = stock_qty;
        this.pending = pending;
        this.stock_in_days = stock_in_days;
        this.arrayList_minvalue = arrayList_minvalue;
    }

    public AddPurchaseOrderProductsPOJO(String product_id, String title, String title_hindi, String short_desc, String long_desc, String gst, String dispatch_code, String purchase_rate, String point, String prod_unit, String photo, String product_qty, String product_qty_del, String product_price, String stock_qty, String pending, String stock_in_days, boolean isSelect, int start, int min_pos, double p_price, double tot_order_price, ArrayList<Double> arrayList_minvalue) {
        this.product_id = product_id;
        this.title = title;
        this.title_hindi = title_hindi;
        this.short_desc = short_desc;
        this.long_desc = long_desc;
        this.gst = gst;
        this.dispatch_code = dispatch_code;
        this.purchase_rate = purchase_rate;
        this.point = point;
        this.prod_unit = prod_unit;
        this.photo = photo;
        this.product_qty = product_qty;
        this.product_qty_del = product_qty_del;
        this.product_price = product_price;
        this.stock_qty = stock_qty;
        this.pending = pending;
        this.stock_in_days = stock_in_days;
        this.isSelect = isSelect;
        this.start = start;
        this.min_pos = min_pos;
        this.p_price = p_price;
        this.tot_order_price = tot_order_price;
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

    public String getTitle_hindi() {
        return title_hindi;
    }

    public void setTitle_hindi(String title_hindi) {
        this.title_hindi = title_hindi;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getLong_desc() {
        return long_desc;
    }

    public void setLong_desc(String long_desc) {
        this.long_desc = long_desc;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getDispatch_code() {
        return dispatch_code;
    }

    public void setDispatch_code(String dispatch_code) {
        this.dispatch_code = dispatch_code;
    }

    public String getPurchase_rate() {
        return purchase_rate;
    }

    public void setPurchase_rate(String purchase_rate) {
        this.purchase_rate = purchase_rate;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
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

    public String getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(String product_qty) {
        this.product_qty = product_qty;
    }

    public String getProduct_qty_del() {
        return product_qty_del;
    }

    public void setProduct_qty_del(String product_qty_del) {
        this.product_qty_del = product_qty_del;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getStock_qty() {
        return stock_qty;
    }

    public void setStock_qty(String stock_qty) {
        this.stock_qty = stock_qty;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public String getStock_in_days() {
        return stock_in_days;
    }

    public void setStock_in_days(String stock_in_days) {
        this.stock_in_days = stock_in_days;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getMin_pos() {
        return min_pos;
    }

    public void setMin_pos(int min_pos) {
        this.min_pos = min_pos;
    }

    public double getP_price() {
        return p_price;
    }

    public void setP_price(double p_price) {
        this.p_price = p_price;
    }

    public double getTot_order_price() {
        return tot_order_price;
    }

    public void setTot_order_price(double tot_order_price) {
        this.tot_order_price = tot_order_price;
    }

    public ArrayList<Double> getArrayList_minvalue() {
        return arrayList_minvalue;
    }

    public void setArrayList_minvalue(ArrayList<Double> arrayList_minvalue) {
        this.arrayList_minvalue = arrayList_minvalue;
    }


}
