package com.jp.baxomdistributor.ui.bussiness_summery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BussinessSummeryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BussinessSummeryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}