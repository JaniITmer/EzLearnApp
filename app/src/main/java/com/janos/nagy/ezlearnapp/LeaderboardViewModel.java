package com.janos.nagy.ezlearnapp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

public class LeaderboardViewModel extends ViewModel {
    private final StudyRepository repository;
    private final LiveData<UserScore> userScore;

    public LeaderboardViewModel(StudyRepository repository) {
        this.repository = repository;
        // Tegyük fel, hogy van egy userId, amit valahonnan megkapunk
        String userId = "example_user_id"; // Ezt dinamikusan kell meghatározni
        this.userScore = repository.getScore(userId);
    }

    public LiveData<UserScore> getUserScore() {
        return userScore;
    }
}