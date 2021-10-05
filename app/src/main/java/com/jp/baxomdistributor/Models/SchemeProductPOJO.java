package com.jp.baxomdistributor.Models;

public class SchemeProductPOJO {

    String product_id,product_title,product_qty;

    public SchemeProductPOJO(String product_id, String product_title, String product_qty) {
        this.product_id = product_id;
        this.product_title = product_title;
        this.product_qty = product_qty;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(String product_qty) {
        this.product_qty = product_qty;
    }
}
