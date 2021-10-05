package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class DeliverySummeryPOJO {

    String dist_name, dist_number, order_date, total_orders, order_amt, delivery_order_amt, collection_amt, order_time;
    ArrayList<DeliverySummeryDetailPOJO> arrayList_detail;

    public DeliverySummeryPOJO(String dist_name, String dist_number, String order_date, String total_orders, String order_amt, String delivery_order_amt, String collection_amt, String order_time, ArrayList<DeliverySummeryDetailPOJO> arrayList_detail) {
        this.dist_name = dist_name;
        this.dist_number = dist_number;
        this.order_date = order_date;
        this.total_orders = total_orders;
        this.order_amt = order_amt;
        this.delivery_order_amt = delivery_order_amt;
        this.collection_amt = collection_amt;
        this.order_time = order_time;
        this.arrayList_detail = arrayList_detail;
    }

    public String getDist_name() {
        return dist_name;
    }

    public void setDist_name(String dist_name) {
        this.dist_name = dist_name;
    }

    public String getDist_number() {
        return dist_number;
    }

    public void setDist_number(String dist_number) {
        this.dist_number = dist_number;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getTotal_orders() {
        return total_orders;
    }

    public void setTotal_orders(String total_orders) {
        this.total_orders = total_orders;
    }

    public String getOrder_amt() {
        return order_amt;
    }

    public void setOrder_amt(String order_amt) {
        this.order_amt = order_amt;
    }

    public String getDelivery_order_amt() {
        return delivery_order_amt;
    }

    public void setDelivery_order_amt(String delivery_order_amt) {
        this.delivery_order_amt = delivery_order_amt;
    }

    public String getCollection_amt() {
        return collection_amt;
    }

    public void setCollection_amt(String collection_amt) {
        this.collection_amt = collection_amt;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public ArrayList<DeliverySummeryDetailPOJO> getArrayList_detail() {
        return arrayList_detail;
    }

    public void setArrayList_detail(ArrayList<DeliverySummeryDetailPOJO> arrayList_detail) {
        this.arrayList_detail = arrayList_detail;
    }
}
