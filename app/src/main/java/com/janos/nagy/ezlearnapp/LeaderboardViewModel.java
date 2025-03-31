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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardViewModel extends ViewModel {
    private final StudyRepository repository;
    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final FirebaseFirestore firestore;

    public LeaderboardViewModel(StudyRepository repository) {
        this.repository = repository;
        this.firestore = FirebaseFirestore.getInstance();
        repository.syncAllScoresFromFirestore();
    }

    public void setUserId(String newUserId) {
        if (!newUserId.equals(userId.getValue())) {
            userId.setValue(newUserId);
        }
    }

    public LiveData<UserScore> getUserScore() {
        return repository.getScore(userId.getValue());
    }

    public LiveData<List<UserScore>> getLeaderboard() {
        MutableLiveData<List<UserScore>> leaderboardData = new MutableLiveData<>();
        firestore.collection("user_scores")
                .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserScore> scores = new ArrayList<>();
                    int totalUsers = queryDocumentSnapshots.size(); // Összes várt elem

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String userId = doc.getId();
                        int score = doc.getLong("score").intValue();

                        firestore.collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String name = userDoc.exists() ? userDoc.getString("name") : "Névtelen";
                                    scores.add(new UserScore(userId, score, name));

                                    // Csak akkor frissítsük a rangsort, ha minden adat megérkezett
                                    if (scores.size() == totalUsers) {
                                        scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                                        leaderboardData.setValue(new ArrayList<>(scores)); // Másolatot küldünk
                                        Log.d("Leaderboard", "Sorted scores: " + scores);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    scores.add(new UserScore(userId, score, "Hiba"));
                                    if (scores.size() == totalUsers) {
                                        scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                                        leaderboardData.setValue(new ArrayList<>(scores)); // Másolatot küldünk
                                        Log.d("Leaderboard", "Sorted scores with error: " + scores);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    leaderboardData.setValue(new ArrayList<>());
                });

        return leaderboardData;
    }

    public void syncScores(String userId) {
        repository.syncScoresFromFirestore(userId);
    }
}