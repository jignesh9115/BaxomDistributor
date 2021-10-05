package com.jp.baxomdistributor.Models;

public class GoodsSummeryDetailsPOJO {

    String prod_name,qty_out,amt_out,rate,qty_in,amt_in;

    public GoodsSummeryDetailsPOJO(String prod_name, String qty_out, String amt_out, String rate, String qty_in, String amt_in) {
        this.prod_name = prod_name;
        this.qty_out = qty_out;
        this.amt_out = amt_out;
        this.rate = rate;
        this.qty_in = qty_in;
        this.amt_in = amt_in;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getQty_out() {
        return qty_out;
    }

    public void setQty_out(String qty_out) {
        this.qty_out = qty_out;
    }

    public String getAmt_out() {
        return amt_out;
    }

    public void setAmt_out(String amt_out) {
        this.amt_out = amt_out;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getQty_in() {
        return qty_in;
    }

    public void setQty_in(String qty_in) {
        this.qty_in = qty_in;
    }

    public String getAmt_in() {
        return amt_in;
    }

    public void setAmt_in(String amt_in) {
        this.amt_in = amt_in;
    }
}
