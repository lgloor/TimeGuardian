package com.os.timeguardian.ui.openings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OpeningsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public OpeningsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the openings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}