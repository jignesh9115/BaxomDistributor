package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 18-07-2022
 */
public class DelTotAmtDateWiseModel {

    String date, tot_amount;

    public DelTotAmtDateWiseModel(String date, String tot_amount) {
        this.date = date;
        this.tot_amount = tot_amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTot_amount() {
        return tot_amount;
    }

    public void setTot_amount(String tot_amount) {
        this.tot_amount = tot_amount;
    }
}
