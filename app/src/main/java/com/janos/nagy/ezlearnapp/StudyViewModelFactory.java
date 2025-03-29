package com.janos.nagy.ezlearnapp;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.janos.nagy.ezlearnapp.repository.StudyRepository;

public class StudyViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String userId;
    private final StudyRepository repository;

    public StudyViewModelFactory(Application application, String userId) {
        this.application = application;
        this.userId = userId;
        this.repository = new StudyRepository(application); // StudyRepository inicializálása
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StudyViewModel.class)) {
            return (T) new StudyViewModel(application, userId, repository); // Három paraméter átadása
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}