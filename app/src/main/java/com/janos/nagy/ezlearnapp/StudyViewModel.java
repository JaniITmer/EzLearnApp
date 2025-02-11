package com.janos.nagy.ezlearnapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.database.AppDatabase;
import com.janos.nagy.ezlearnapp.database.StudySessionDao;

public class StudyViewModel extends AndroidViewModel {
    private StudySessionDao studySessionDao;
    private int currentSessionId = -1;

    public StudyViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        studySessionDao = db.studySessionDao();
    }

    // A tanulás elindítása
    public void startStudy() {
        long startTime = System.currentTimeMillis(); // Az aktuális idő kezdete
        StudySession session = new StudySession(startTime);

        // Az adatbázisba történő írás háttérszálon
        AppDatabase.databaseWriteExecutor.execute(() -> {
            currentSessionId = (int) studySessionDao.insertSession(session);
        });
    }

    // A tanulás befejezésének kezelése
    public void endStudy(int sessionId) {
        long endTime = System.currentTimeMillis(); // Az aktuális idő vége

        // Frissítjük az endTime-ot és kiszámítjuk a duration-t
        AppDatabase.databaseWriteExecutor.execute(() -> {
            StudySession session = studySessionDao.getSessionById(sessionId);
            session.setEndTime(endTime); // Az endTime frissítése
            studySessionDao.updateSession(session); // Az adatbázis frissítése
        });
    }
}
