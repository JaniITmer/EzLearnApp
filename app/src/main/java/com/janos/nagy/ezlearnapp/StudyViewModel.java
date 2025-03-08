package com.janos.nagy.ezlearnapp;


import android.app.Application;
import android.os.CountDownTimer;
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
    private static final long POMODORO_DURATION = 25 * 60 * 1000; // 25 perc
    private final String userId; // Felhasználó azonosítója

    public StudyViewModel(Application application, String userId) {
        this.repository = new StudyRepository(application);
        this.userId = userId; // Példa: "default_user", ezt valahonnan meg kell kapni
        remainingTime.setValue(POMODORO_DURATION / 1000); // Kezdeti érték: 25 perc
        currentSession.setValue(new StudySession(System.currentTimeMillis(), "pomodoro"));
        userScore = repository.getScore(userId); // LiveData a pontszámhoz
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

    public void startPomodoro() {
        if (pomodoroTimer != null) {
            pomodoroTimer.cancel();
        }

        StudySession session = new StudySession(System.currentTimeMillis(), "pomodoro");
        currentSession.setValue(session);
        repository.insertSession(session); // Session mentése az adatbázisba

        pomodoroTimer = new CountDownTimer(POMODORO_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime.setValue(millisUntilFinished / 1000);
                // Pontszám növelése minden perc után
                if (millisUntilFinished % (60 * 1000) == 0) {
                    updateScore(1); // 1 pont minden percért
                }
            }

            @Override
            public void onFinish() {
                remainingTime.setValue(null);
                StudySession completedSession = currentSession.getValue();
                if (completedSession != null) {
                    completedSession.setEndTime(System.currentTimeMillis());
                    currentSession.setValue(completedSession);
                    repository.updateSession(completedSession); // Frissített session mentése
                }
            }
        }.start();
    }

    public void stopPomodoro() {
        if (pomodoroTimer != null) {
            pomodoroTimer.cancel();
            pomodoroTimer = null;
        }
        remainingTime.setValue(POMODORO_DURATION / 1000);
        StudySession session = currentSession.getValue();
        if (session != null) {
            session.setEndTime(System.currentTimeMillis());
            currentSession.setValue(session);
            repository.updateSession(session); // Frissített session mentése
            long durationInMinutes = session.getDuration();
            updateScore((int) durationInMinutes); // Pontszám növelése a session időtartama alapján
        }
    }

    private void updateScore(int points) {
        UserScore currentScore = userScore.getValue();
        int newScore = (currentScore != null ? currentScore.getScore() : 0) + points;
        UserScore updatedScore = new UserScore(userId, newScore);
        repository.updateScore(updatedScore);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (pomodoroTimer != null) {
            pomodoroTimer.cancel();
        }
    }

}