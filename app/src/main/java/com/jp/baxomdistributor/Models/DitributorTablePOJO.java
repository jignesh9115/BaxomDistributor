package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class DitributorTablePOJO {

    ArrayList<ViewSalesOrderByOrderIdPOJO> arrayList;

    public DitributorTablePOJO() {
    }

    public DitributorTablePOJO(ArrayList<ViewSalesOrderByOrderIdPOJO> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<ViewSalesOrderByOrderIdPOJO> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<ViewSalesOrderByOrderIdPOJO> arrayList) {
        this.arrayList = arrayList;
    }
}
