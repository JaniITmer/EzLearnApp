package com.janos.nagy.ezlearnapp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import java.util.List;

public class LeaderboardViewModel extends ViewModel {
    private final StudyRepository repository;
    private final MutableLiveData<String> userId = new MutableLiveData<>();

    public LeaderboardViewModel(StudyRepository repository) {
        this.repository = repository;
    }

    public void setUserId(String newUserId) {
        if (!newUserId.equals(userId.getValue())) {
            userId.setValue(newUserId); // Csak akkor állítjuk be, ha változott
        }
    }

    public LiveData<UserScore> getUserScore() {
        return Transformations.switchMap(userId, repository::getScore);
    }
    public LiveData<List<UserScore>> getLeaderboard() {
        return repository.getAllScoresOrdered();
    }

}