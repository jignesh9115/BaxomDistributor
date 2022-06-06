package com.jp.baxomdistributor.ui.distributor_scheme_stock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DistributorSchemeStockViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DistributorSchemeStockViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}