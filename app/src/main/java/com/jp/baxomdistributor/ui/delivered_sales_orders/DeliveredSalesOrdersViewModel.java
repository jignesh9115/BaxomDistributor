package com.jp.baxomdistributor.ui.delivered_sales_orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeliveredSalesOrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DeliveredSalesOrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}