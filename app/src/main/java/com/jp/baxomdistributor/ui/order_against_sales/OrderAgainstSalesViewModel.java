package com.jp.baxomdistributor.ui.order_against_sales;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderAgainstSalesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OrderAgainstSalesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}