package com.jp.baxomdistributor.ui.undeliveredorders_3_1_0;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UndeliveredSalesOrders_3_1_0ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UndeliveredSalesOrders_3_1_0ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}