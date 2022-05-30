package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 11-05-2022
 */
public class OrderAgainstSchemeProdQtyModel {

    String prod_id, prod_qty;

    public OrderAgainstSchemeProdQtyModel(String prod_id, String prod_qty) {
        this.prod_id = prod_id;
        this.prod_qty = prod_qty;
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_qty() {
        return prod_qty;
    }

    public void setProd_qty(String prod_qty) {
        this.prod_qty = prod_qty;
    }
}
