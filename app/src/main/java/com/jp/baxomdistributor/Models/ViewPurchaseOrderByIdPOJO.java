package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class ViewPurchaseOrderByIdPOJO {

    String shipping_code,name_hindi,order_amount,stock_distributor_name,contact_no,dist_town,order_no,order_date,invoice_type,tax_type,transporter_name,eway_bill;

    ArrayList<PackingProdlistPOJO> arrayList_packing_prod_list;
    ArrayList<VerificationProdlistPOJO> arrayList_verification_prod_list;

    public ViewPurchaseOrderByIdPOJO() {
    }

    public ViewPurchaseOrderByIdPOJO(String shipping_code, String name_hindi, String order_amount, String stock_distributor_name, String contact_no, String dist_town, String order_no, String order_date, String invoice_type, String tax_type, String transporter_name, String eway_bill, ArrayList<PackingProdlistPOJO> arrayList_packing_prod_list, ArrayList<VerificationProdlistPOJO> arrayList_verification_prod_list) {
        this.shipping_code = shipping_code;
        this.name_hindi = name_hindi;
        this.order_amount = order_amount;
        this.stock_distributor_name = stock_distributor_name;
        this.contact_no = contact_no;
        this.dist_town = dist_town;
        this.order_no = order_no;
        this.order_date = order_date;
        this.invoice_type = invoice_type;
        this.tax_type = tax_type;
        this.transporter_name = transporter_name;
        this.eway_bill = eway_bill;
        this.arrayList_packing_prod_list = arrayList_packing_prod_list;
        this.arrayList_verification_prod_list = arrayList_verification_prod_list;
    }

    public String getShipping_code() {
        return shipping_code;
    }

    public void setShipping_code(String shipping_code) {
        this.shipping_code = shipping_code;
    }

    public String getName_hindi() {
        return name_hindi;
    }

    public void setName_hindi(String name_hindi) {
        this.name_hindi = name_hindi;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public String getStock_distributor_name() {
        return stock_distributor_name;
    }

    public void setStock_distributor_name(String stock_distributor_name) {
        this.stock_distributor_name = stock_distributor_name;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getDist_town() {
        return dist_town;
    }

    public void setDist_town(String dist_town) {
        this.dist_town = dist_town;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getInvoice_type() {
        return invoice_type;
    }

    public void setInvoice_type(String invoice_type) {
        this.invoice_type = invoice_type;
    }

    public String getTax_type() {
        return tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }

    public String getTransporter_name() {
        return transporter_name;
    }

    public void setTransporter_name(String transporter_name) {
        this.transporter_name = transporter_name;
    }

    public String getEway_bill() {
        return eway_bill;
    }

    public void setEway_bill(String eway_bill) {
        this.eway_bill = eway_bill;
    }

    public ArrayList<PackingProdlistPOJO> getArrayList_packing_prod_list() {
        return arrayList_packing_prod_list;
    }

    public void setArrayList_packing_prod_list(ArrayList<PackingProdlistPOJO> arrayList_packing_prod_list) {
        this.arrayList_packing_prod_list = arrayList_packing_prod_list;
    }

    public ArrayList<VerificationProdlistPOJO> getArrayList_verification_prod_list() {
        return arrayList_verification_prod_list;
    }

    public void setArrayList_verification_prod_list(ArrayList<VerificationProdlistPOJO> arrayList_verification_prod_list) {
        this.arrayList_verification_prod_list = arrayList_verification_prod_list;
    }
}
