package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 28-09-2021
 */
public class UndeliveredOrdersSalesmanModel {

    String salesman_id, salesman, tot_order, tot_amount, entry_date, bit_id, is_pdf_generated;

    boolean checked = false;

    public UndeliveredOrdersSalesmanModel(String salesman_id, String salesman, String tot_order, String tot_amount, String entry_date, String bit_id, String is_pdf_generated, boolean checked) {
        this.salesman_id = salesman_id;
        this.salesman = salesman;
        this.tot_order = tot_order;
        this.tot_amount = tot_amount;
        this.entry_date = entry_date;
        this.bit_id = bit_id;
        this.is_pdf_generated = is_pdf_generated;
        this.checked = checked;
    }

    public String getSalesman_id() {
        return salesman_id;
    }

    public void setSalesman_id(String salesman_id) {
        this.salesman_id = salesman_id;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
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

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getBit_id() {
        return bit_id;
    }

    public void setBit_id(String bit_id) {
        this.bit_id = bit_id;
    }

    public String getIs_pdf_generated() {
        return is_pdf_generated;
    }

    public void setIs_pdf_generated(String is_pdf_generated) {
        this.is_pdf_generated = is_pdf_generated;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
