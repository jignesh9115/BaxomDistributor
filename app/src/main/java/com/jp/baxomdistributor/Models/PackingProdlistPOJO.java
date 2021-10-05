package com.jp.baxomdistributor.Models;

public class PackingProdlistPOJO {

    String product_id,dispatch_code,prod_name,prod_name_hindi,product_qty,purchase_rate,basic_rate,gst,total_b4_gst;

    public PackingProdlistPOJO(String product_id, String dispatch_code, String prod_name, String prod_name_hindi, String product_qty, String purchase_rate, String basic_rate, String gst, String total_b4_gst) {
        this.product_id = product_id;
        this.dispatch_code = dispatch_code;
        this.prod_name = prod_name;
        this.prod_name_hindi = prod_name_hindi;
        this.product_qty = product_qty;
        this.purchase_rate = purchase_rate;
        this.basic_rate = basic_rate;
        this.gst = gst;
        this.total_b4_gst = total_b4_gst;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getDispatch_code() {
        return dispatch_code;
    }

    public void setDispatch_code(String dispatch_code) {
        this.dispatch_code = dispatch_code;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_name_hindi() {
        return prod_name_hindi;
    }

    public void setProd_name_hindi(String prod_name_hindi) {
        this.prod_name_hindi = prod_name_hindi;
    }

    public String getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(String product_qty) {
        this.product_qty = product_qty;
    }

    public String getBasic_rate() {
        return basic_rate;
    }

    public void setBasic_rate(String basic_rate) {
        this.basic_rate = basic_rate;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getPurchase_rate() {
        return purchase_rate;
    }

    public void setPurchase_rate(String purchase_rate) {
        this.purchase_rate = purchase_rate;
    }

    public String getTotal_b4_gst() {
        return total_b4_gst;
    }

    public void setTotal_b4_gst(String total_b4_gst) {
        this.total_b4_gst = total_b4_gst;
    }
}
