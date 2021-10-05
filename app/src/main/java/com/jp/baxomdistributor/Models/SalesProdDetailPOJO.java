package com.jp.baxomdistributor.Models;

public class SalesProdDetailPOJO {

    String product_id,title,product_qty,product_price,product_qty_del,prod_unit,photo;

    public SalesProdDetailPOJO(String product_id, String title, String product_qty, String product_price, String product_qty_del, String prod_unit, String photo) {
        this.product_id = product_id;
        this.title = title;
        this.product_qty = product_qty;
        this.product_price = product_price;
        this.product_qty_del = product_qty_del;
        this.prod_unit = prod_unit;
        this.photo = photo;
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
}
