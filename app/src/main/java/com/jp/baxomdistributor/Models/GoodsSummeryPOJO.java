package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class GoodsSummeryPOJO {

    String order_date, total_orders, order_amt, delivery_order_amt, collection_amt, order_time;

    ArrayList<GoodsSummeryDetailsPOJO> arrayList_details;

    public GoodsSummeryPOJO(String order_date, String total_orders, String order_amt, String delivery_order_amt, String collection_amt, String order_time, ArrayList<GoodsSummeryDetailsPOJO> arrayList_details) {
        this.order_date = order_date;
        this.total_orders = total_orders;
        this.order_amt = order_amt;
        this.delivery_order_amt = delivery_order_amt;
        this.collection_amt = collection_amt;
        this.order_time = order_time;
        this.arrayList_details = arrayList_details;
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

    public ArrayList<GoodsSummeryDetailsPOJO> getArrayList_details() {
        return arrayList_details;
    }

    public void setArrayList_details(ArrayList<GoodsSummeryDetailsPOJO> arrayList_details) {
        this.arrayList_details = arrayList_details;
    }
}
