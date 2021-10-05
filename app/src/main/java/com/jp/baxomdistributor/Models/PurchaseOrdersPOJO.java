package com.jp.baxomdistributor.Models;

public class PurchaseOrdersPOJO {

    String purchase_id,purchase_order_status,order_date,shipping_date,order_amount,LR_symbol,no_of_article,billing_amount,transporter_name,transporter_contact1,transporter_contact2,dist_id,dist_name,dist_phone,dist_town;

    public PurchaseOrdersPOJO(String purchase_id, String purchase_order_status, String order_date, String shipping_date, String order_amount, String LR_symbol, String no_of_article, String billing_amount, String transporter_name, String transporter_contact1, String transporter_contact2, String dist_id, String dist_name, String dist_phone, String dist_town) {
        this.purchase_id = purchase_id;
        this.purchase_order_status = purchase_order_status;
        this.order_date = order_date;
        this.shipping_date = shipping_date;
        this.order_amount = order_amount;
        this.LR_symbol = LR_symbol;
        this.no_of_article = no_of_article;
        this.billing_amount = billing_amount;
        this.transporter_name = transporter_name;
        this.transporter_contact1 = transporter_contact1;
        this.transporter_contact2 = transporter_contact2;
        this.dist_id = dist_id;
        this.dist_name = dist_name;
        this.dist_phone = dist_phone;
        this.dist_town = dist_town;
    }

    public String getPurchase_id() {
        return purchase_id;
    }

    public void setPurchase_id(String purchase_id) {
        this.purchase_id = purchase_id;
    }

    public String getPurchase_order_status() {
        return purchase_order_status;
    }

    public void setPurchase_order_status(String purchase_order_status) {
        this.purchase_order_status = purchase_order_status;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getShipping_date() {
        return shipping_date;
    }

    public void setShipping_date(String shipping_date) {
        this.shipping_date = shipping_date;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public String getLR_symbol() {
        return LR_symbol;
    }

    public void setLR_symbol(String LR_symbol) {
        this.LR_symbol = LR_symbol;
    }

    public String getNo_of_article() {
        return no_of_article;
    }

    public void setNo_of_article(String no_of_article) {
        this.no_of_article = no_of_article;
    }

    public String getBilling_amount() {
        return billing_amount;
    }

    public void setBilling_amount(String billing_amount) {
        this.billing_amount = billing_amount;
    }

    public String getTransporter_name() {
        return transporter_name;
    }

    public void setTransporter_name(String transporter_name) {
        this.transporter_name = transporter_name;
    }

    public String getTransporter_contact1() {
        return transporter_contact1;
    }

    public void setTransporter_contact1(String transporter_contact1) {
        this.transporter_contact1 = transporter_contact1;
    }

    public String getTransporter_contact2() {
        return transporter_contact2;
    }

    public void setTransporter_contact2(String transporter_contact2) {
        this.transporter_contact2 = transporter_contact2;
    }

    public String getDist_id() {
        return dist_id;
    }

    public void setDist_id(String dist_id) {
        this.dist_id = dist_id;
    }

    public String getDist_name() {
        return dist_name;
    }

    public void setDist_name(String dist_name) {
        this.dist_name = dist_name;
    }

    public String getDist_phone() {
        return dist_phone;
    }

    public void setDist_phone(String dist_phone) {
        this.dist_phone = dist_phone;
    }

    public String getDist_town() {
        return dist_town;
    }

    public void setDist_town(String dist_town) {
        this.dist_town = dist_town;
    }
}
