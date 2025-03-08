package com.janos.nagy.ezlearnapp;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class StudyViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String userId;

    public StudyViewModelFactory(Application application, String userId) {
        this.application = application;
        this.userId = userId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StudyViewModel.class)) {
            return (T) new StudyViewModel(application, userId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}