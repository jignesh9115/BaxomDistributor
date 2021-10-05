package com.jp.baxomdistributor.Models;

public class SchemeWiseSummeryPOJO {

    String scheme_id, scheme_name, scheme_long_name, opening, primary, secondary, closing;

    public SchemeWiseSummeryPOJO(String scheme_id, String scheme_name, String scheme_long_name, String opening, String primary, String secondary, String closing) {
        this.scheme_id = scheme_id;
        this.scheme_name = scheme_name;
        this.scheme_long_name = scheme_long_name;
        this.opening = opening;
        this.primary = primary;
        this.secondary = secondary;
        this.closing = closing;
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

    public String getScheme_long_name() {
        return scheme_long_name;
    }

    public void setScheme_long_name(String scheme_long_name) {
        this.scheme_long_name = scheme_long_name;
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

    public String getClosing() {
        return closing;
    }

    public void setClosing(String closing) {
        this.closing = closing;
    }
}
