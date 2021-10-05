package com.jp.baxomdistributor.Models;

public class SalesmanSignAuthoritisPOJO {

    String parent_salesman_id, saleman, parent, previos_sign, verified_date, imei_no, ip_address;

    public SalesmanSignAuthoritisPOJO(String parent_salesman_id, String saleman, String parent, String previos_sign, String verified_date, String imei_no, String ip_address) {
        this.parent_salesman_id = parent_salesman_id;
        this.saleman = saleman;
        this.parent = parent;
        this.previos_sign = previos_sign;
        this.verified_date = verified_date;
        this.imei_no = imei_no;
        this.ip_address = ip_address;
    }

    public String getParent_salesman_id() {
        return parent_salesman_id;
    }

    public void setParent_salesman_id(String parent_salesman_id) {
        this.parent_salesman_id = parent_salesman_id;
    }

    public String getSaleman() {
        return saleman;
    }

    public void setSaleman(String saleman) {
        this.saleman = saleman;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getPrevios_sign() {
        return previos_sign;
    }

    public void setPrevios_sign(String previos_sign) {
        this.previos_sign = previos_sign;
    }

    public String getVerified_date() {
        return verified_date;
    }

    public void setVerified_date(String verified_date) {
        this.verified_date = verified_date;
    }

    public String getImei_no() {
        return imei_no;
    }

    public void setImei_no(String imei_no) {
        this.imei_no = imei_no;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }
}
