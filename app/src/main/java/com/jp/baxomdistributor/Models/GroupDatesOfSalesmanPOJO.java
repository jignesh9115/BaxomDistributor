package com.jp.baxomdistributor.Models;

public class GroupDatesOfSalesmanPOJO {

    String salesman_id, date;

    public GroupDatesOfSalesmanPOJO(String salesman_id, String date) {
        this.salesman_id = salesman_id;
        this.date = date;
    }

    public String getSalesman_id() {
        return salesman_id;
    }

    public void setSalesman_id(String salesman_id) {
        this.salesman_id = salesman_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
