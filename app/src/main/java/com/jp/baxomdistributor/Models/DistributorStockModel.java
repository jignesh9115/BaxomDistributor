package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 13-04-2022
 */
public class DistributorStockModel {

    String dist_stock_id, dist_id, prod_id, prod_name, prod_qty, prod_update_qty, avg_21days, stock_date, entry_date;

    public DistributorStockModel(String dist_stock_id, String dist_id, String prod_id, String prod_name, String prod_qty, String prod_update_qty, String avg_21days, String stock_date, String entry_date) {
        this.dist_stock_id = dist_stock_id;
        this.dist_id = dist_id;
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_qty = prod_qty;
        this.prod_update_qty = prod_update_qty;
        this.avg_21days = avg_21days;
        this.stock_date = stock_date;
        this.entry_date = entry_date;
    }

    public String getDist_stock_id() {
        return dist_stock_id;
    }

    public void setDist_stock_id(String dist_stock_id) {
        this.dist_stock_id = dist_stock_id;
    }

    public String getDist_id() {
        return dist_id;
    }

    public void setDist_id(String dist_id) {
        this.dist_id = dist_id;
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_qty() {
        return prod_qty;
    }

    public void setProd_qty(String prod_qty) {
        this.prod_qty = prod_qty;
    }

    public String getProd_update_qty() {
        return prod_update_qty;
    }

    public void setProd_update_qty(String prod_update_qty) {
        this.prod_update_qty = prod_update_qty;
    }

    public String getAvg_21days() {
        return avg_21days;
    }

    public void setAvg_21days(String avg_21days) {
        this.avg_21days = avg_21days;
    }

    public String getStock_date() {
        return stock_date;
    }

    public void setStock_date(String stock_date) {
        this.stock_date = stock_date;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }
}
