package com.janos.nagy.ezlearnapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.janos.nagy.ezlearnapp.repository.StudyRepository;

public class LeaderboardViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public LeaderboardViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LeaderboardViewModel.class)) {
            return (T) new LeaderboardViewModel(new StudyRepository(context));
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}