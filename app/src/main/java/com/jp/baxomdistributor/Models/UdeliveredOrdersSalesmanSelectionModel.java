package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 09-10-2021
 */
public class UdeliveredOrdersSalesmanSelectionModel {

    String salesman_name, ticks;

    public UdeliveredOrdersSalesmanSelectionModel(String salesman_name, String ticks) {
        this.salesman_name = salesman_name;
        this.ticks = ticks;
    }

    public String getSalesman_name() {
        return salesman_name;
    }

    public void setSalesman_name(String salesman_name) {
        this.salesman_name = salesman_name;
    }

    public String getTicks() {
        return ticks;
    }

    public void setTicks(String ticks) {
        this.ticks = ticks;
    }
}
