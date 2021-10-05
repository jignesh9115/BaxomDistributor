package com.jp.baxomdistributor.ui.my_stock_statement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyStockStatementViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyStockStatementViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}