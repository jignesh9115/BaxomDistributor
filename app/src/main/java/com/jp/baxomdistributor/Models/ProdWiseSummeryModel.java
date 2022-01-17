package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 13-01-2022
 */
public class ProdWiseSummeryModel {
    String prod_id, prod_name, prod_qty, pts_value, ptr_value, biz_value;

    public ProdWiseSummeryModel(String prod_id, String prod_name, String prod_qty, String pts_value, String ptr_value, String biz_value) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_qty = prod_qty;
        this.pts_value = pts_value;
        this.ptr_value = ptr_value;
        this.biz_value = biz_value;
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

    public String getPts_value() {
        return pts_value;
    }

    public void setPts_value(String pts_value) {
        this.pts_value = pts_value;
    }

    public String getPtr_value() {
        return ptr_value;
    }

    public void setPtr_value(String ptr_value) {
        this.ptr_value = ptr_value;
    }

    public String getBiz_value() {
        return biz_value;
    }

    public void setBiz_value(String biz_value) {
        this.biz_value = biz_value;
    }
}
