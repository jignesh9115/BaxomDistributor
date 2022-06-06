package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 13-04-2022
 */
public class DistributorSchemeStockModel {

    String dist_scheme_stock_id, dist_id, scheme_id, scheme_name, scheme_qty, scheme_update_qty, entry_date;

    public DistributorSchemeStockModel(String dist_scheme_stock_id, String dist_id, String scheme_id, String scheme_name, String scheme_qty, String scheme_update_qty, String entry_date) {
        this.dist_scheme_stock_id = dist_scheme_stock_id;
        this.dist_id = dist_id;
        this.scheme_id = scheme_id;
        this.scheme_name = scheme_name;
        this.scheme_qty = scheme_qty;
        this.scheme_update_qty = scheme_update_qty;
        this.entry_date = entry_date;
    }

    public String getDist_scheme_stock_id() {
        return dist_scheme_stock_id;
    }

    public void setDist_scheme_stock_id(String dist_scheme_stock_id) {
        this.dist_scheme_stock_id = dist_scheme_stock_id;
    }

    public String getDist_id() {
        return dist_id;
    }

    public void setDist_id(String dist_id) {
        this.dist_id = dist_id;
    }

    public String getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(String scheme_id) {
        this.scheme_id = scheme_id;
    }

    public String getScheme_name() {
        return scheme_name;
    }

    public void setScheme_name(String scheme_name) {
        this.scheme_name = scheme_name;
    }

    public String getScheme_qty() {
        return scheme_qty;
    }

    public void setScheme_qty(String scheme_qty) {
        this.scheme_qty = scheme_qty;
    }

    public String getScheme_update_qty() {
        return scheme_update_qty;
    }

    public void setScheme_update_qty(String scheme_update_qty) {
        this.scheme_update_qty = scheme_update_qty;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }
}
