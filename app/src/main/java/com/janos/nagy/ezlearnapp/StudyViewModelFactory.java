package com.janos.nagy.ezlearnapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.janos.nagy.ezlearnapp.repository.StudyRepository;

public class StudyViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String userId;
    private final StudyRepository repository;

    public StudyViewModelFactory(Application application, String userId, StudyRepository repository) {
        this.application = application;
        this.userId = userId;
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StudyViewModel.class)) {
            return (T) new StudyViewModel(application, userId, repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}