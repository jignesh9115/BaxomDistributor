package com.jp.baxomdistributor.Models;

/**
 * Created by Jignesh Chauhan on 12-10-2021
 */
public class SalesmanPDFGenerateModel {

    String salesman_id, salesman_name, pdf_date, bit_id;

    public SalesmanPDFGenerateModel(String salesman_id, String salesman_name, String pdf_date, String bit_id) {
        this.salesman_id = salesman_id;
        this.salesman_name = salesman_name;
        this.pdf_date = pdf_date;
        this.bit_id = bit_id;
    }

    public String getSalesman_id() {
        return salesman_id;
    }

    public void setSalesman_id(String salesman_id) {
        this.salesman_id = salesman_id;
    }

    public String getSalesman_name() {
        return salesman_name;
    }

    public void setSalesman_name(String salesman_name) {
        this.salesman_name = salesman_name;
    }

    public String getPdf_date() {
        return pdf_date;
    }

    public void setPdf_date(String pdf_date) {
        this.pdf_date = pdf_date;
    }

    public String getBit_id() {
        return bit_id;
    }

    public void setBit_id(String bit_id) {
        this.bit_id = bit_id;
    }
}
