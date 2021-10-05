package com.jp.baxomdistributor.Models;

public class LocationDataPOJO {

    String lat,lang,datetimeval;

    public LocationDataPOJO(String lat, String lang, String datetimeval) {
        this.lat = lat;
        this.lang = lang;
        this.datetimeval = datetimeval;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDatetimeval() {
        return datetimeval;
    }

    public void setDatetimeval(String datetimeval) {
        this.datetimeval = datetimeval;
    }
}
