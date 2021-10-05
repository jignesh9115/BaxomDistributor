package com.jp.baxomdistributor.Models;

public class UndeliveredOrderByDistIdPOJO {

    String total_cnt,total_amt,entry_date,entry_date_pass,total_amt_del;

    public UndeliveredOrderByDistIdPOJO(String total_cnt, String total_amt, String entry_date, String entry_date_pass, String total_amt_del) {
        this.total_cnt = total_cnt;
        this.total_amt = total_amt;
        this.entry_date = entry_date;
        this.entry_date_pass = entry_date_pass;
        this.total_amt_del = total_amt_del;
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

    public String getTotal_amt_del() {
        return total_amt_del;
    }

    public void setTotal_amt_del(String total_amt_del) {
        this.total_amt_del = total_amt_del;
    }
}
