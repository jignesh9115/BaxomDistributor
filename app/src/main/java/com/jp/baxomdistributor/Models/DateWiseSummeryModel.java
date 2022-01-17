package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 13-01-2022
 */
public class DateWiseSummeryModel {
    String date, pts_value, ptr_value, biz_value;

    public DateWiseSummeryModel(String date, String pts_value, String ptr_value, String biz_value) {
        this.date = date;
        this.pts_value = pts_value;
        this.ptr_value = ptr_value;
        this.biz_value = biz_value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPts_value() {
        return pts_value;
    }

    public void setPts_value(String pts_value) {
        this.pts_value = pts_value;
    }

    public String getPtr_value() {
        return ptr_value;
    }

    public void setPtr_value(String ptr_value) {
        this.ptr_value = ptr_value;
    }

    public String getBiz_value() {
        return biz_value;
    }

    public void setBiz_value(String biz_value) {
        this.biz_value = biz_value;
    }
}
