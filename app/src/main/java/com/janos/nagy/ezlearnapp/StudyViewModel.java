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
        private static final long POMODORO_DURATION = 25 * 60 * 1000; // 25 perc
        private final String userId;

        // Új állapotváltozó a Pomodoro munkamenet állapotának jelzésére
        private final MutableLiveData<Boolean> isPomodoroRunning = new MutableLiveData<>(false);

        public StudyViewModel(Application application, String userId) {
            this.repository = new StudyRepository(application);
            this.userId = userId;
            remainingTime.setValue(POMODORO_DURATION / 1000); // Kezdeti érték: 25 perc
            StudySession initialSession = new StudySession(System.currentTimeMillis(), "pomodoro");
            currentSession.setValue(initialSession);
            repository.insertSession(initialSession); // Kezdeti session mentése
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

        // Getter az isPomodoroRunning állapotváltozóhoz
        public LiveData<Boolean> isPomodoroRunning() {
            return isPomodoroRunning;
        }

        public void startPomodoro() {
            if (pomodoroTimer != null) {
                pomodoroTimer.cancel();
            }

            StudySession session = new StudySession(System.currentTimeMillis(), "pomodoro");
            currentSession.setValue(session);
            repository.insertSession(session); // Új session mentése

            // Frissítjük az állapotot, hogy a Pomodoro fut
            isPomodoroRunning.setValue(true);

            pomodoroTimer = new CountDownTimer(POMODORO_DURATION, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    remainingTime.setValue(millisUntilFinished / 1000); // Frissítjük az UI-t másodpercenként
                    if (millisUntilFinished % (60 * 1000) == 0) {
                        updateScore(1); // 1 pont minden percért
                    }
                }

                @Override
                public void onFinish() {
                    remainingTime.setValue(0L); // Miután vége, nullázzuk a számlálót
                    StudySession completedSession = currentSession.getValue();
                    if (completedSession != null) {
                        completedSession.setEndTime(System.currentTimeMillis());
                        currentSession.setValue(completedSession);
                        repository.updateSession(completedSession);
                    }
                    isPomodoroRunning.setValue(false); // A Pomodoro befejeződött
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
                repository.updateSession(session);
                long durationInMinutes = session.getDuration();
                updateScore((int) durationInMinutes);
            }
            // Frissítjük az állapotot, hogy a Pomodoro már nem fut
            isPomodoroRunning.setValue(false);
        }

        private void updateScore(int points) {
            UserScore currentScore = userScore.getValue();
            int newScore = (currentScore != null ? currentScore.getScore() : 0) + points;
            UserScore updatedScore = new UserScore(userId, newScore);
            repository.updateScore(updatedScore);
        }

        // Szinkronizálás Firestore-ból
        public void syncScoresFromFirestore() {
            repository.syncScoresFromFirestore(userId); // A repository-ban meghívva
        }

        @Override
        protected void onCleared() {
            super.onCleared();
            if (pomodoroTimer != null) {
                pomodoroTimer.cancel();
            }
        }
    }