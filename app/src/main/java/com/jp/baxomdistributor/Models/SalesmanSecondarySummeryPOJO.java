package com.jp.baxomdistributor.Models;

public class SalesmanSecondarySummeryPOJO {

    String salesman_id, salesman, booking, delivery, delivery_fail;

    public SalesmanSecondarySummeryPOJO(String salesman_id, String salesman, String booking, String delivery, String delivery_fail) {
        this.salesman_id = salesman_id;
        this.salesman = salesman;
        this.booking = booking;
        this.delivery = delivery;
        this.delivery_fail = delivery_fail;
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

    public String getBooking() {
        return booking;
    }

    public void setBooking(String booking) {
        this.booking = booking;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getDelivery_fail() {
        return delivery_fail;
    }

    public void setDelivery_fail(String delivery_fail) {
        this.delivery_fail = delivery_fail;
    }
}
