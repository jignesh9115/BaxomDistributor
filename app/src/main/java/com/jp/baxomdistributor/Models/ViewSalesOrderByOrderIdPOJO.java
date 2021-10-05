package com.jp.baxomdistributor.Models;


import java.util.ArrayList;

public class ViewSalesOrderByOrderIdPOJO {

    String saleman_name, dist_name, dist_number, order_id, order_date, title, shop_keeper_name, latitude, longitude, shop_image,
            last_order_rs, last_visit_ave, last_10visit_ave, contact_no, shop_keeper_no, tin_no, address, total_discount, shop_no,
            order_time;

    ArrayList<SalesProdDetailPOJO> arrayList_sales_prod_detail;
    ArrayList<SchemeListPDFPOJO> arrayList_scheme_name;

    public ViewSalesOrderByOrderIdPOJO() {
    }

    public ViewSalesOrderByOrderIdPOJO(String saleman_name, String dist_name, String dist_number, String order_id, String order_date, String title, String shop_keeper_name, String latitude, String longitude, String shop_image, String last_order_rs, String last_visit_ave, String last_10visit_ave, String contact_no, String shop_keeper_no, String tin_no, String address, String total_discount, String shop_no, String order_time, ArrayList<SalesProdDetailPOJO> arrayList_sales_prod_detail, ArrayList<SchemeListPDFPOJO> arrayList_scheme_name) {
        this.saleman_name = saleman_name;
        this.dist_name = dist_name;
        this.dist_number = dist_number;
        this.order_id = order_id;
        this.order_date = order_date;
        this.title = title;
        this.shop_keeper_name = shop_keeper_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.shop_image = shop_image;
        this.last_order_rs = last_order_rs;
        this.last_visit_ave = last_visit_ave;
        this.last_10visit_ave = last_10visit_ave;
        this.contact_no = contact_no;
        this.shop_keeper_no = shop_keeper_no;
        this.tin_no = tin_no;
        this.address = address;
        this.total_discount = total_discount;
        this.shop_no = shop_no;
        this.order_time = order_time;
        this.arrayList_sales_prod_detail = arrayList_sales_prod_detail;
        this.arrayList_scheme_name = arrayList_scheme_name;
    }

    public String getSaleman_name() {
        return saleman_name;
    }

    public void setSaleman_name(String saleman_name) {
        this.saleman_name = saleman_name;
    }

    public String getDist_name() {
        return dist_name;
    }

    public void setDist_name(String dist_name) {
        this.dist_name = dist_name;
    }

    public String getDist_number() {
        return dist_number;
    }

    public void setDist_number(String dist_number) {
        this.dist_number = dist_number;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShop_keeper_name() {
        return shop_keeper_name;
    }

    public void setShop_keeper_name(String shop_keeper_name) {
        this.shop_keeper_name = shop_keeper_name;
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

    public String getLast_visit_ave() {
        return last_visit_ave;
    }

    public void setLast_visit_ave(String last_visit_ave) {
        this.last_visit_ave = last_visit_ave;
    }

    public String getLast_10visit_ave() {
        return last_10visit_ave;
    }

    public void setLast_10visit_ave(String last_10visit_ave) {
        this.last_10visit_ave = last_10visit_ave;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getShop_keeper_no() {
        return shop_keeper_no;
    }

    public void setShop_keeper_no(String shop_keeper_no) {
        this.shop_keeper_no = shop_keeper_no;
    }

    public String getTin_no() {
        return tin_no;
    }

    public void setTin_no(String tin_no) {
        this.tin_no = tin_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<SalesProdDetailPOJO> getArrayList_sales_prod_detail() {
        return arrayList_sales_prod_detail;
    }

    public void setArrayList_sales_prod_detail(ArrayList<SalesProdDetailPOJO> arrayList_sales_prod_detail) {
        this.arrayList_sales_prod_detail = arrayList_sales_prod_detail;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(String total_discount) {
        this.total_discount = total_discount;
    }

    public String getShop_no() {
        return shop_no;
    }

    public void setShop_no(String shop_no) {
        this.shop_no = shop_no;
    }

    public ArrayList<SchemeListPDFPOJO> getArrayList_scheme_name() {
        return arrayList_scheme_name;
    }

    public void setArrayList_scheme_name(ArrayList<SchemeListPDFPOJO> arrayList_scheme_name) {
        this.arrayList_scheme_name = arrayList_scheme_name;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }
}
