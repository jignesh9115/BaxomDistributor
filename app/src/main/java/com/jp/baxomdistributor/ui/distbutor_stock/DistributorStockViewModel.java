package com.jp.baxomdistributor.ui.distbutor_stock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DistributorStockViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DistributorStockViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}