package com.jp.baxomdistributor.ui.mypurchaseorder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyPurchaseOrderViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyPurchaseOrderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}