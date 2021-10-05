package com.jp.baxomdistributor.Models;

public class DeliverySummeryDetailPOJO {

    String order_no,shop_name,salesman,order_amt,del_order_amt,coll_order_amt;

    public DeliverySummeryDetailPOJO(String order_no, String shop_name, String salesman, String order_amt, String del_order_amt, String coll_order_amt) {
        this.order_no = order_no;
        this.shop_name = shop_name;
        this.salesman = salesman;
        this.order_amt = order_amt;
        this.del_order_amt = del_order_amt;
        this.coll_order_amt = coll_order_amt;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getOrder_amt() {
        return order_amt;
    }

    public void setOrder_amt(String order_amt) {
        this.order_amt = order_amt;
    }

    public String getDel_order_amt() {
        return del_order_amt;
    }

    public void setDel_order_amt(String del_order_amt) {
        this.del_order_amt = del_order_amt;
    }

    public String getColl_order_amt() {
        return coll_order_amt;
    }

    public void setColl_order_amt(String coll_order_amt) {
        this.coll_order_amt = coll_order_amt;
    }
}
