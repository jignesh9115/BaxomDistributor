package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 12-05-2022
 */
public class OrderAgainstSalesAutoOrderModel {

    String prod_id, prod_name;
    double curr_stock, aday_sale, pts, curr_stock_in_days, curr_stock_value_pts, aday_stock_value_pts,
            required_tot_stock_value, order_qty;

    public OrderAgainstSalesAutoOrderModel(String prod_id, String prod_name, double curr_stock, double aday_sale, double pts, double curr_stock_in_days, double curr_stock_value_pts, double aday_stock_value_pts, double required_tot_stock_value, double order_qty) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.curr_stock = curr_stock;
        this.aday_sale = aday_sale;
        this.pts = pts;
        this.curr_stock_in_days = curr_stock_in_days;
        this.curr_stock_value_pts = curr_stock_value_pts;
        this.aday_stock_value_pts = aday_stock_value_pts;
        this.required_tot_stock_value = required_tot_stock_value;
        this.order_qty = order_qty;
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

    public double getCurr_stock() {
        return curr_stock;
    }

    public void setCurr_stock(double curr_stock) {
        this.curr_stock = curr_stock;
    }

    public double getAday_sale() {
        return aday_sale;
    }

    public void setAday_sale(double aday_sale) {
        this.aday_sale = aday_sale;
    }

    public double getPts() {
        return pts;
    }

    public void setPts(double pts) {
        this.pts = pts;
    }

    public double getCurr_stock_in_days() {
        return curr_stock_in_days;
    }

    public void setCurr_stock_in_days(double curr_stock_in_days) {
        this.curr_stock_in_days = curr_stock_in_days;
    }

    public double getCurr_stock_value_pts() {
        return curr_stock_value_pts;
    }

    public void setCurr_stock_value_pts(double curr_stock_value_pts) {
        this.curr_stock_value_pts = curr_stock_value_pts;
    }

    public double getAday_stock_value_pts() {
        return aday_stock_value_pts;
    }

    public void setAday_stock_value_pts(double aday_stock_value_pts) {
        this.aday_stock_value_pts = aday_stock_value_pts;
    }

    public double getRequired_tot_stock_value() {
        return required_tot_stock_value;
    }

    public void setRequired_tot_stock_value(double required_tot_stock_value) {
        this.required_tot_stock_value = required_tot_stock_value;
    }

    public double getOrder_qty() {
        return order_qty;
    }

    public void setOrder_qty(double order_qty) {
        this.order_qty = order_qty;
    }
}
