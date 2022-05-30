package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 10-05-2022
 */
public class OrderAgainstSalesQtyModel {

    double scheme_qty, replace_qty, shortage_qty;

    public OrderAgainstSalesQtyModel(double scheme_qty, double replace_qty, double shortage_qty) {
        this.scheme_qty = scheme_qty;
        this.replace_qty = replace_qty;
        this.shortage_qty = shortage_qty;
    }

    public double getScheme_qty() {
        return scheme_qty;
    }

    public void setScheme_qty(double scheme_qty) {
        this.scheme_qty = scheme_qty;
    }

    public double getReplace_qty() {
        return replace_qty;
    }

    public void setReplace_qty(double replace_qty) {
        this.replace_qty = replace_qty;
    }

    public double getShortage_qty() {
        return shortage_qty;
    }

    public void setShortage_qty(double shortage_qty) {
        this.shortage_qty = shortage_qty;
    }
}
