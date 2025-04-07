package com.janos.nagy.ezlearnapp;


import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;


public class StudyViewModel extends ViewModel {
    private final StudyRepository repository;
    private final MutableLiveData<Long> remainingTime = new MutableLiveData<>();
    private final MutableLiveData<StudySession> currentSession = new MutableLiveData<>();
    private final LiveData<UserScore> userScore;
    private CountDownTimer pomodoroTimer;
    private long pomodoroDuration = 25 * 60 * 1000;
    private final String userId;
    private final MutableLiveData<Boolean> isPomodoroRunning = new MutableLiveData<>(false);
    private final Application application;
    private final FirebaseFirestore firestore;

    public StudyViewModel(Application application, String userId, StudyRepository repository) {
        this.repository = repository;
        this.userId = userId;
        this.application = application;
        this.firestore = FirebaseFirestore.getInstance();
        remainingTime.setValue(pomodoroDuration / 1000);
        userScore = repository.getScore(userId);
        Log.d("StudyViewModel", "Konstruktor meghívva az user-hez " + userId);
    }

    StudyRepository getRepository() {
        return repository;
    }

    public LiveData<Long> getRemainingTime() {
        return remainingTime;
    }

    public LiveData<StudySession> getCurrentSession() {
        return currentSession;
    }

    public LiveData<UserScore> getUserScore() {
        return userScore;
    }

    public LiveData<Boolean> isPomodoroRunning() {
        return isPomodoroRunning;
    }

    public void setPomodoroDuration(int minutes) {
        pomodoroDuration = minutes * 60 * 1000;
        if (!isPomodoroRunning.getValue()) {
            remainingTime.setValue(pomodoroDuration / 1000);
        }
        Log.d("StudyViewModel", "Pomodoro ido beallitva: " + minutes + " perc");
    }

    public void startPomodoro() {
        if (pomodoroTimer != null) {
            pomodoroTimer.cancel();
        }

        StudySession session = new StudySession(System.currentTimeMillis(), "pomodoro");
        session.setUserId(userId);
        currentSession.setValue(session);
        repository.insertSession(session);
        isPomodoroRunning.setValue(true);
        Log.d("StudyViewModel", "startPomodoro meghivasa:" + userId);

        pomodoroTimer = new CountDownTimer(pomodoroDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime.setValue(millisUntilFinished / 1000);
                if (millisUntilFinished % (60 * 1000) == 0) {
                    updateScore(1);
                    Log.d("StudyViewModel", "1 pont hozzaadva");
                }
            }

            @Override
            public void onFinish() {
                remainingTime.setValue(0L);
                StudySession completedSession = currentSession.getValue();
                if (completedSession != null) {
                    completedSession.setEndTime(System.currentTimeMillis());
                    currentSession.setValue(completedSession);
                    repository.updateSession(completedSession);
                    int pointsToAdd = (int) (pomodoroDuration / (60 * 1000));
                    updateScore(pointsToAdd);
                    Log.d("StudyViewModel", "Pomodoro vege: " + pointsToAdd + " pont");
                }
                stopPomodoro(false);
                sendPomodoroFinishedNotification();
            }
        }.start();
    }

    public void stopPomodoro() {
        stopPomodoro(true);
    }

    protected void stopPomodoro(boolean addPoints) {
        if (pomodoroTimer != null) {
            pomodoroTimer.cancel();
            pomodoroTimer = null;
        }
        remainingTime.setValue(pomodoroDuration / 1000);
        StudySession session = currentSession.getValue();
        if (session != null) {
            session.setEndTime(System.currentTimeMillis());
            currentSession.setValue(session);
            repository.updateSession(session);
            if (addPoints) {
                long elapsedTime = session.getEndTime() - session.getStartTime();
                long durationInMinutes = elapsedTime / (60 * 1000);
                updateScore((int) durationInMinutes);
                Log.d("StudyViewModel", "Pomodoro leallitva: " + durationInMinutes + " pont.");
            }
        }
        isPomodoroRunning.setValue(false);
    }

    private void updateScore(int points) {
        UserScore currentScore = userScore.getValue();
        int newScore = (currentScore != null ? currentScore.getScore() : 0) + points;
        String name = (currentScore != null && currentScore.getName() != null) ? currentScore.getName() : null;

        if (name == null && userId != null) {

            firestore.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String fetchedName = documentSnapshot.exists() ? documentSnapshot.getString("name") : "Névtelen";
                        UserScore updatedScore = new UserScore(userId, newScore, fetchedName);
                        repository.updateScore(updatedScore);
                        Log.d("StudyViewModel", "Pontszam frissitve " + points + ", Uj pontszam " + newScore);
                    })
                    .addOnFailureListener(e -> {

                        UserScore updatedScore = new UserScore(userId, newScore, "Névtelen");
                        repository.updateScore(updatedScore);
                        Log.d("StudyViewModel", "Fontszam frissitve " + points + ", ujj pontszam osszesen " + newScore);
                    });
        } else {

            UserScore updatedScore = new UserScore(userId, newScore, name != null ? name : "Névtelen");
            repository.updateScore(updatedScore);
            Log.d("StudyViewModel", "Pontszam frissitve: " + points + ", Ujj pontszam osszesen: " + newScore);
        }
    }

    public void syncScoresFromFirestore() {
        repository.syncScoresFromFirestore(userId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (pomodoroTimer != null) {
            pomodoroTimer.cancel();
        }
    }

    private void sendPomodoroFinishedNotification() {
        NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(application, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(application, EzLearnApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Pomodoro véget ért")
                .setContentText("A tanulási időd letelt! Itt az ideje egy kis szünetnek.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
        Log.d("StudyViewModel", "Pomodor vege,  ertesites megjelenitve.");
    }
}