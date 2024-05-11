package com.os.timeguardian.ui.timeplan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimeplanViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TimeplanViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the timeplan fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}