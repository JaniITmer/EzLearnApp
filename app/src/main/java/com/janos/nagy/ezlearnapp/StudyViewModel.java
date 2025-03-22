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
    private long pomodoroDuration = 25 * 60 * 1000;
    private final String userId;
    private final MutableLiveData<Boolean> isPomodoroRunning = new MutableLiveData<>(false);

    public StudyViewModel(Application application, String userId) {
        this.repository = new StudyRepository(application);
        this.userId = userId;
        remainingTime.setValue(pomodoroDuration / 1000);
        StudySession initialSession = new StudySession(System.currentTimeMillis(), "pomodoro");
        currentSession.setValue(initialSession);
        repository.insertSession(initialSession);
        userScore = repository.getScore(userId);
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
        pomodoroDuration = minutes * 60 * 1000; // milisec
        if (!isPomodoroRunning.getValue()) {                //szerkesztes nem futasidoben
            remainingTime.setValue(pomodoroDuration / 1000);
        }
        Log.d("StudyViewModel", "Pomodoro duration set to: " + minutes + " minutes");
    }

    public void startPomodoro() {
        if (pomodoroTimer != null) {
            pomodoroTimer.cancel();
        }

        StudySession session = new StudySession(System.currentTimeMillis(), "pomodoro");
        currentSession.setValue(session);
        repository.insertSession(session);
        isPomodoroRunning.setValue(true);

        pomodoroTimer = new CountDownTimer(pomodoroDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime.setValue(millisUntilFinished / 1000);
                if (millisUntilFinished % (60 * 1000) == 0) {
                    updateScore(1); // 1 point per minute
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
                    repository.updateSession(completedSession);
                    int pointsToAdd = (int) (pomodoroDuration / (60 * 1000)); // Convert duration to minutes
                    updateScore(pointsToAdd);
                    Log.d("StudyViewModel", "Pomodoro finished. Added " + pointsToAdd + " points. New score: " + (userScore.getValue() != null ? userScore.getValue().getScore() : 0));
                }
                stopPomodoro(false); // End the session
            }
        }.start();
    }

    public void stopPomodoro() {
        stopPomodoro(true);
    }

    private void stopPomodoro(boolean addPoints) {
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
                long durationInMinutes = session.getDuration();
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