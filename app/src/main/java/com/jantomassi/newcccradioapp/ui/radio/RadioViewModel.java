package com.jantomassi.newcccradioapp.ui.radio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RadioViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RadioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Radio fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}