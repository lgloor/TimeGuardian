package com.os.timeguardian.ui.time;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TimeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Du Hurensohn");
    }

    public LiveData<String> getText() {
        return mText;
    }
}