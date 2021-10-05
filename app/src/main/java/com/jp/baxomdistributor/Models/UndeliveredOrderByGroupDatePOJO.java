package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class UndeliveredOrderByGroupDatePOJO {

    String dates, total_cnt, total_amt, total_amt_del;
    ArrayList<UndeliveredOrderByDatePOJO> arrayList_Order_by_date;

    public UndeliveredOrderByGroupDatePOJO(String dates, String total_cnt, String total_amt, String total_amt_del, ArrayList<UndeliveredOrderByDatePOJO> arrayList_Order_by_date) {
        this.dates = dates;
        this.total_cnt = total_cnt;
        this.total_amt = total_amt;
        this.total_amt_del = total_amt_del;
        this.arrayList_Order_by_date = arrayList_Order_by_date;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
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

    public ArrayList<UndeliveredOrderByDatePOJO> getArrayList_Order_by_date() {
        return arrayList_Order_by_date;
    }

    public void setArrayList_Order_by_date(ArrayList<UndeliveredOrderByDatePOJO> arrayList_Order_by_date) {
        this.arrayList_Order_by_date = arrayList_Order_by_date;
    }
}
