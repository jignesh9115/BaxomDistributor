package com.jp.baxomdistributor.Models;

public class CartoonQtyPOJO {

    int p_id,qty;

    public CartoonQtyPOJO(int p_id, int qty) {
        this.p_id = p_id;
        this.qty = qty;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
