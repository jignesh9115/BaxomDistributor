package com.jp.baxomdistributor.Models;

public class DeliveredOrderbySalesmanPOJO {

    String total_cnt,total_amt,shop_title,ID,photo,latitude,longitude,total_amt_del,daily_status,shop_keeper_no;

    public DeliveredOrderbySalesmanPOJO(String total_cnt, String total_amt, String shop_title, String ID, String photo, String latitude, String longitude, String total_amt_del, String daily_status, String shop_keeper_no) {
        this.total_cnt = total_cnt;
        this.total_amt = total_amt;
        this.shop_title = shop_title;
        this.ID = ID;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.total_amt_del = total_amt_del;
        this.daily_status = daily_status;
        this.shop_keeper_no = shop_keeper_no;
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

    public String getShop_title() {
        return shop_title;
    }

    public void setShop_title(String shop_title) {
        this.shop_title = shop_title;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTotal_amt_del() {
        return total_amt_del;
    }

    public void setTotal_amt_del(String total_amt_del) {
        this.total_amt_del = total_amt_del;
    }

    public String getDaily_status() {
        return daily_status;
    }

    public void setDaily_status(String daily_status) {
        this.daily_status = daily_status;
    }

    public String getShop_keeper_no() {
        return shop_keeper_no;
    }

    public void setShop_keeper_no(String shop_keeper_no) {
        this.shop_keeper_no = shop_keeper_no;
    }
}
