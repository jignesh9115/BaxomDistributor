package com.jp.baxomdistributor.Models;

public class ProdWiseSummeryPOJO {

    String product_id, prod_name, prod_unit, opening, primary, secondary, secondary_pending, secondary_pen_del, closing, closing_pen_del;

    public ProdWiseSummeryPOJO(String product_id, String prod_name, String prod_unit, String opening, String primary, String secondary, String secondary_pending, String secondary_pen_del, String closing, String closing_pen_del) {
        this.product_id = product_id;
        this.prod_name = prod_name;
        this.prod_unit = prod_unit;
        this.opening = opening;
        this.primary = primary;
        this.secondary = secondary;
        this.secondary_pending = secondary_pending;
        this.secondary_pen_del = secondary_pen_del;
        this.closing = closing;
        this.closing_pen_del = closing_pen_del;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_unit() {
        return prod_unit;
    }

    public void setProd_unit(String prod_unit) {
        this.prod_unit = prod_unit;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getSecondary() {
        return secondary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public String getSecondary_pending() {
        return secondary_pending;
    }

    public void setSecondary_pending(String secondary_pending) {
        this.secondary_pending = secondary_pending;
    }

    public String getSecondary_pen_del() {
        return secondary_pen_del;
    }

    public void setSecondary_pen_del(String secondary_pen_del) {
        this.secondary_pen_del = secondary_pen_del;
    }

    public String getClosing() {
        return closing;
    }

    public void setClosing(String closing) {
        this.closing = closing;
    }

    public String getClosing_pen_del() {
        return closing_pen_del;
    }

    public void setClosing_pen_del(String closing_pen_del) {
        this.closing_pen_del = closing_pen_del;
    }
}
