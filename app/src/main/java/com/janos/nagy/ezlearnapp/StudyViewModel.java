package com.janos.nagy.ezlearnapp;


import android.app.Application;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;


public class StudyViewModel extends ViewModel {
    private final StudyRepository repository;
    private final MutableLiveData<Long> remainingTime = new MutableLiveData<>();
    private final MutableLiveData<StudySession> currentSession = new MutableLiveData<>();
    private final LiveData<UserScore> userScore;
    private CountDownTimer pomodoroTimer;
    private long pomodoroDuration = 25 * 60 * 1000; // Alapértelmezett 25 perc milliszekundumban
    private final String userId;
    private final MutableLiveData<Boolean> isPomodoroRunning = new MutableLiveData<>(false);

    public StudyViewModel(Application application, String userId) {
        this.repository = new StudyRepository(application);
        this.userId = userId;
        remainingTime.setValue(pomodoroDuration / 1000); // Alapértelmezett idő másodpercben
        // Nem hozunk létre és nem mentünk StudySession-t itt
        userScore = repository.getScore(userId);
        Log.d("StudyViewModel", "Constructor called for userId: " + userId);
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
        pomodoroDuration = minutes * 60 * 1000; // Milliszekundumra konvertálás
        if (!isPomodoroRunning.getValue()) { // Csak akkor frissítjük, ha nem fut
            remainingTime.setValue(pomodoroDuration / 1000);
        }
        Log.d("StudyViewModel", "Pomodoro duration set to: " + minutes + " minutes");
    }

    public void startPomodoro() {
        if (pomodoroTimer != null) {
            pomodoroTimer.cancel();
        }

        // Új StudySession létrehozása csak itt, amikor a felhasználó elindítja
        StudySession session = new StudySession(System.currentTimeMillis(), "pomodoro");
        session.setUserId(userId); // userId beállítása
        currentSession.setValue(session);
        repository.insertSession(session); // Mentés Room-ba és Firestore-ba
        isPomodoroRunning.setValue(true);
        Log.d("StudyViewModel", "startPomodoro called, new session created for userId: " + userId);

        pomodoroTimer = new CountDownTimer(pomodoroDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime.setValue(millisUntilFinished / 1000);
                if (millisUntilFinished % (60 * 1000) == 0) {
                    updateScore(1); // 1 pont percenként
                    Log.d("StudyViewModel", "Added 1 point during tick. Current score: " + (userScore.getValue() != null ? userScore.getValue().getScore() : 0));
                }
            }

            @Override
            public void onFinish() {
                remainingTime.setValue(0L);
                StudySession completedSession = currentSession.getValue();
                if (completedSession != null) {
                    completedSession.setEndTime(System.currentTimeMillis());
                    currentSession.setValue(completedSession);
                    repository.updateSession(completedSession); // Frissítés a befejezéskor
                    int pointsToAdd = (int) (pomodoroDuration / (60 * 1000)); // Pontok a teljes időtartam alapján
                    updateScore(pointsToAdd);
                    Log.d("StudyViewModel", "Pomodoro finished. Added " + pointsToAdd + " points. New score: " + (userScore.getValue() != null ? userScore.getValue().getScore() : 0));
                }
                stopPomodoro(false); // Befejezés
            }
        }.start();
    }

    public void stopPomodoro() {
        stopPomodoro(true); // Pontok hozzáadásával állítjuk le
    }

    private void stopPomodoro(boolean addPoints) {
        if (pomodoroTimer != null) {
            pomodoroTimer.cancel();
            pomodoroTimer = null;
        }
        remainingTime.setValue(pomodoroDuration / 1000); // Visszaállítás az alapértelmezett időre
        StudySession session = currentSession.getValue();
        if (session != null) {
            session.setEndTime(System.currentTimeMillis());
            currentSession.setValue(session);
            repository.updateSession(session); // Frissítés Room-ban és Firestore-ban
            if (addPoints) {
                long elapsedTime = session.getEndTime() - session.getStartTime();
                long durationInMinutes = elapsedTime / (60 * 1000); // Eltelt idő percekben
                updateScore((int) durationInMinutes);
                Log.d("StudyViewModel", "Pomodoro stopped. Added " + durationInMinutes + " points. New score: " + (userScore.getValue() != null ? userScore.getValue().getScore() : 0));
            }
        }
        isPomodoroRunning.setValue(false);
    }

    private void updateScore(int points) {
        UserScore currentScore = userScore.getValue();
        int newScore = (currentScore != null ? currentScore.getScore() : 0) + points;
        UserScore updatedScore = new UserScore(userId, newScore);
        repository.updateScore(updatedScore);
        Log.d("StudyViewModel", "Score updated. Points added: " + points + ", New total: " + newScore);
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
}