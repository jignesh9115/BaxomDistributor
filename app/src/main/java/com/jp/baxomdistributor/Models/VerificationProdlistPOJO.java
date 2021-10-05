package com.jp.baxomdistributor.Models;

public class VerificationProdlistPOJO {

    String product_id,dispatch_code,prod_name,prod_name_hindi,product_qty;

    public VerificationProdlistPOJO(String product_id, String dispatch_code, String prod_name, String prod_name_hindi, String product_qty) {
        this.product_id = product_id;
        this.dispatch_code = dispatch_code;
        this.prod_name = prod_name;
        this.prod_name_hindi = prod_name_hindi;
        this.product_qty = product_qty;
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
}
