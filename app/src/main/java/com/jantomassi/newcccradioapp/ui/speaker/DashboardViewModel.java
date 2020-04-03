package com.jantomassi.newcccradioapp.ui.speaker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}