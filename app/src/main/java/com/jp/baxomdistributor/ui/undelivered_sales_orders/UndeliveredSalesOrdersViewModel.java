package com.jp.baxomdistributor.ui.undelivered_sales_orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UndeliveredSalesOrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UndeliveredSalesOrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}