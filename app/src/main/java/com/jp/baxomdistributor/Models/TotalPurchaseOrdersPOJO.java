package com.jp.baxomdistributor.Models;

import java.util.ArrayList;

public class TotalPurchaseOrdersPOJO {

    ArrayList<ViewPurchaseOrderByIdPOJO> arrayList;

    public TotalPurchaseOrdersPOJO() {
    }

    public TotalPurchaseOrdersPOJO(ArrayList<ViewPurchaseOrderByIdPOJO> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<ViewPurchaseOrderByIdPOJO> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<ViewPurchaseOrderByIdPOJO> arrayList) {
        this.arrayList = arrayList;
    }
}
