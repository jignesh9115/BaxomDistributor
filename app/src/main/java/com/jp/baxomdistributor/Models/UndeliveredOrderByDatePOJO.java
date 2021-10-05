package com.jp.baxomdistributor.Models;

public class UndeliveredOrderByDatePOJO {

    String entry_date, total_cnt, total_amt, salesman_id, salesman, total_amt_del;

    boolean isSelect;

    public UndeliveredOrderByDatePOJO(String entry_date, String total_cnt, String total_amt, String salesman_id, String salesman, String total_amt_del, boolean isSelect) {
        this.entry_date = entry_date;
        this.total_cnt = total_cnt;
        this.total_amt = total_amt;
        this.salesman_id = salesman_id;
        this.salesman = salesman;
        this.total_amt_del = total_amt_del;
        this.isSelect = isSelect;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
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

    public String getSalesman_id() {
        return salesman_id;
    }

    public void setSalesman_id(String salesman_id) {
        this.salesman_id = salesman_id;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getTotal_amt_del() {
        return total_amt_del;
    }

    public void setTotal_amt_del(String total_amt_del) {
        this.total_amt_del = total_amt_del;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
