package com.jp.baxomdistributor.Models;

public class DeliveredOrderByDistIdPOJO {

    String total_cnt, total_amt,total_amt_del,total_amt_failed,pending_amount,entry_date,entry_date_pass,total_order,total_deliver_order;

    public DeliveredOrderByDistIdPOJO(String total_cnt, String total_amt, String total_amt_del, String total_amt_failed, String pending_amount, String entry_date, String entry_date_pass, String total_order, String total_deliver_order) {
        this.total_cnt = total_cnt;
        this.total_amt = total_amt;
        this.total_amt_del = total_amt_del;
        this.total_amt_failed = total_amt_failed;
        this.pending_amount = pending_amount;
        this.entry_date = entry_date;
        this.entry_date_pass = entry_date_pass;
        this.total_order = total_order;
        this.total_deliver_order = total_deliver_order;
    }

    public String getTotal_cnt() {
        return total_cnt;
    }

    public void setTotal_cnt(String total_cnt) {
        this.total_cnt = total_cnt;
    }

    public String getTotal_amt() {
        return total_amt;
    }

    public void setTotal_amt(String total_amt) {
        this.total_amt = total_amt;
    }

    public String getTotal_amt_del() {
        return total_amt_del;
    }

    public void setTotal_amt_del(String total_amt_del) {
        this.total_amt_del = total_amt_del;
    }

    public String getTotal_amt_failed() {
        return total_amt_failed;
    }

    public void setTotal_amt_failed(String total_amt_failed) {
        this.total_amt_failed = total_amt_failed;
    }

    public String getPending_amount() {
        return pending_amount;
    }

    public void setPending_amount(String pending_amount) {
        this.pending_amount = pending_amount;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getEntry_date_pass() {
        return entry_date_pass;
    }

    public void setEntry_date_pass(String entry_date_pass) {
        this.entry_date_pass = entry_date_pass;
    }

    public String getTotal_order() {
        return total_order;
    }

    public void setTotal_order(String total_order) {
        this.total_order = total_order;
    }

    public String getTotal_deliver_order() {
        return total_deliver_order;
    }

    public void setTotal_deliver_order(String total_deliver_order) {
        this.total_deliver_order = total_deliver_order;
    }
}
