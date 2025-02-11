package com.janos.nagy.ezlearnapp;

import android.os.Bundle;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.database.AppDatabase;
import com.janos.nagy.ezlearnapp.database.StudySessionDao;


public class HomeActivity extends AppCompatActivity {


    private AppDatabase db;
    private StudySessionDao studySessionDao;
    private int currentSessionId;
    private Button studyButton;
    private boolean isStudying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        db = AppDatabase.getInstance(this);
        studySessionDao = db.studySessionDao();
        new Thread(() -> {
            db.clearAllTables();
        }).start();
        studyButton = findViewById(R.id.studyButton);


        studyButton.setText("Start Study");


        Button profileButton=findViewById(R.id.profileButton);

        profileButton.setOnClickListener(v -> loadFragment(new ProfileFragment()));

        studyButton.setOnClickListener(v -> {
            if (isStudying) {

                endStudy();
                studyButton.setText("Start Study");
            } else {

                startStudy();
                studyButton.setText("End Study");
            }

            isStudying = !isStudying;  // Megváltoztatjuk a tanulás státuszát
        });


    }
    private void startStudy() {
        long startTime = System.currentTimeMillis();
        StudySession newSession = new StudySession(startTime);


        new Thread(() -> {

            currentSessionId = (int) studySessionDao.insertSession(newSession);
        }).start();
    }
    private void endStudy() {
        long endTime = System.currentTimeMillis();

        new Thread(() -> {
            StudySession session = studySessionDao.getSessionById(currentSessionId);
            session.setEndTime(endTime);  // Végső időpont beállítása
            studySessionDao.updateSession(session);  // Adatbázis frissítése
        }).start();
    }


    private void loadFragment(Fragment fragment){

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }
}