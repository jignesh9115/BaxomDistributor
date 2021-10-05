package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class LocationBysalesIdsPOJO {

    String  salesman,salesman_id;

    ArrayList<LocationDataPOJO> arrayList_location_list;

    public LocationBysalesIdsPOJO(String salesman, String salesman_id, ArrayList<LocationDataPOJO> arrayList_location_list) {
        this.salesman = salesman;
        this.salesman_id = salesman_id;
        this.arrayList_location_list = arrayList_location_list;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getSalesman_id() {
        return salesman_id;
    }

    public void setSalesman_id(String salesman_id) {
        this.salesman_id = salesman_id;
    }

    public ArrayList<LocationDataPOJO> getArrayList_location_list() {
        return arrayList_location_list;
    }

    public void setArrayList_location_list(ArrayList<LocationDataPOJO> arrayList_location_list) {
        this.arrayList_location_list = arrayList_location_list;
    }
}
