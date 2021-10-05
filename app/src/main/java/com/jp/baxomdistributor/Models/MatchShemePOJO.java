package com.jp.baxomdistributor.Models;

public class MatchShemePOJO {

    String scheme_id,scheme_qty,scheme_price;

    public MatchShemePOJO(String scheme_id, String scheme_qty, String scheme_price) {
        this.scheme_id = scheme_id;
        this.scheme_qty = scheme_qty;
        this.scheme_price = scheme_price;
    }

    public String getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(String scheme_id) {
        this.scheme_id = scheme_id;
    }

    public String getScheme_qty() {
        return scheme_qty;
    }

    public void setScheme_qty(String scheme_qty) {
        this.scheme_qty = scheme_qty;
    }

    public String getScheme_price() {
        return scheme_price;
    }

    public void setScheme_price(String scheme_price) {
        this.scheme_price = scheme_price;
    }
}
