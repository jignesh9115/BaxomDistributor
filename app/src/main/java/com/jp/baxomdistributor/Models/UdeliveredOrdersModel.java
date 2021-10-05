package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

/**
 * Created by Jignesh Chauhan on 28-09-2021
 */
public class UdeliveredOrdersModel {

    String bit_id, bit_name, tot_order, tot_amount;

    ArrayList<UndeliveredOrdersSalesmanModel> arrayList;

    public UdeliveredOrdersModel(String bit_id, String bit_name, String tot_order, String tot_amount, ArrayList<UndeliveredOrdersSalesmanModel> arrayList) {
        this.bit_id = bit_id;
        this.bit_name = bit_name;
        this.tot_order = tot_order;
        this.tot_amount = tot_amount;
        this.arrayList = arrayList;
    }

    public String getBit_id() {
        return bit_id;
    }

    public void setBit_id(String bit_id) {
        this.bit_id = bit_id;
    }

    public String getBit_name() {
        return bit_name;
    }

    public void setBit_name(String bit_name) {
        this.bit_name = bit_name;
    }

    public String getTot_order() {
        return tot_order;
    }

    public void setTot_order(String tot_order) {
        this.tot_order = tot_order;
    }

    public String getTot_amount() {
        return tot_amount;
    }

    public void setTot_amount(String tot_amount) {
        this.tot_amount = tot_amount;
    }

    public ArrayList<UndeliveredOrdersSalesmanModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<UndeliveredOrdersSalesmanModel> arrayList) {
        this.arrayList = arrayList;
    }
}
