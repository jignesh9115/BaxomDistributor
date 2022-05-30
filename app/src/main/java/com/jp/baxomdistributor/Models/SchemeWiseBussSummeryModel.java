package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 26-01-2022
 */
public class SchemeWiseBussSummeryModel {

    String scheme_id, scheme_name, result_prod_name, result_prod_qty, scheme_biz, disc_pts, disc_biz;

    public SchemeWiseBussSummeryModel(String scheme_id, String scheme_name, String result_prod_name, String result_prod_qty, String scheme_biz, String disc_pts, String disc_biz) {
        this.scheme_id = scheme_id;
        this.scheme_name = scheme_name;
        this.result_prod_name = result_prod_name;
        this.result_prod_qty = result_prod_qty;
        this.scheme_biz = scheme_biz;
        this.disc_pts = disc_pts;
        this.disc_biz = disc_biz;
    }

    public String getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(String scheme_id) {
        this.scheme_id = scheme_id;
    }

    public String getScheme_name() {
        return scheme_name;
    }

    public void setScheme_name(String scheme_name) {
        this.scheme_name = scheme_name;
    }

    public String getResult_prod_name() {
        return result_prod_name;
    }

    public void setResult_prod_name(String result_prod_name) {
        this.result_prod_name = result_prod_name;
    }

    public String getResult_prod_qty() {
        return result_prod_qty;
    }

    public void setResult_prod_qty(String result_prod_qty) {
        this.result_prod_qty = result_prod_qty;
    }

    public String getScheme_biz() {
        return scheme_biz;
    }

    public void setScheme_biz(String scheme_biz) {
        this.scheme_biz = scheme_biz;
    }

    public String getDisc_pts() {
        return disc_pts;
    }

    public void setDisc_pts(String disc_pts) {
        this.disc_pts = disc_pts;
    }

    public String getDisc_biz() {
        return disc_biz;
    }

    public void setDisc_biz(String disc_biz) {
        this.disc_biz = disc_biz;
    }
}
