package com.janos.nagy.ezlearnapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import java.util.List;

public class LeaderboardViewModel extends ViewModel {
    private final StudyRepository repository;
    private final MutableLiveData<String> userId = new MutableLiveData<>();

    public LeaderboardViewModel(StudyRepository repository) {
        this.repository = repository;
        repository.syncAllScoresFromFirestore();
    }

    public void setUserId(String newUserId) {
        if (!newUserId.equals(userId.getValue())) {
            userId.setValue(newUserId);
        }
    }

    public LiveData<UserScore> getUserScore() {
        return Transformations.switchMap(userId, repository::getScore);
    }
    public LiveData<List<UserScore>> getLeaderboard() {
        MutableLiveData<List<UserScore>> leaderboardData = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection("user_scores")
                .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserScore> scores = queryDocumentSnapshots.toObjects(UserScore.class);
                    leaderboardData.setValue(scores);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Hiba a ranglista lekérdezésénél", e));

        return leaderboardData;
    }
    public void syncScores(String userId) {
        repository.syncScoresFromFirestore(userId);
    }

}