package com.jp.baxomdistributor.Models;

public class ViewLocationPOJO {

    String salesman_id, salesman_name, shop_id, shop_name, shop_image, last_order_rs, last_order_line, address, shop_no;
    Double latitude, longitude;

    public ViewLocationPOJO(String salesman_id, String salesman_name, String shop_id, String shop_name, String shop_image, String last_order_rs, String last_order_line, String address, String shop_no, Double latitude, Double longitude) {
        this.salesman_id = salesman_id;
        this.salesman_name = salesman_name;
        this.shop_id = shop_id;
        this.shop_name = shop_name;
        this.shop_image = shop_image;
        this.last_order_rs = last_order_rs;
        this.last_order_line = last_order_line;
        this.address = address;
        this.shop_no = shop_no;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getSalesman_id() {
        return salesman_id;
    }

    public void setSalesman_id(String salesman_id) {
        this.salesman_id = salesman_id;
    }

    public String getSalesman_name() {
        return salesman_name;
    }

    public void setSalesman_name(String salesman_name) {
        this.salesman_name = salesman_name;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_image() {
        return shop_image;
    }

    public void setShop_image(String shop_image) {
        this.shop_image = shop_image;
    }

    public String getLast_order_rs() {
        return last_order_rs;
    }

    public void setLast_order_rs(String last_order_rs) {
        this.last_order_rs = last_order_rs;
    }

    public String getLast_order_line() {
        return last_order_line;
    }

    public void setLast_order_line(String last_order_line) {
        this.last_order_line = last_order_line;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShop_no() {
        return shop_no;
    }

    public void setShop_no(String shop_no) {
        this.shop_no = shop_no;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
