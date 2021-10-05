package com.jp.baxomdistributor.Models;

public class UpdateSalesOrderProductPOJO {

    String p_id,p_qty,p_qty_del,p_price;

    public UpdateSalesOrderProductPOJO(String p_id, String p_qty, String p_qty_del, String p_price) {
        this.p_id = p_id;
        this.p_qty = p_qty;
        this.p_qty_del = p_qty_del;
        this.p_price = p_price;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getP_qty() {
        return p_qty;
    }

    public void setP_qty(String p_qty) {
        this.p_qty = p_qty;
    }

    public String getP_qty_del() {
        return p_qty_del;
    }

    public void setP_qty_del(String p_qty_del) {
        this.p_qty_del = p_qty_del;
    }

    public String getP_price() {
        return p_price;
    }

    public void setP_price(String p_price) {
        this.p_price = p_price;
    }
}
