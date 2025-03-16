package com.janos.nagy.ezlearnapp.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.janos.nagy.ezlearnapp.data.model.Lesson;
import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.data.model.Task;
import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.database.AppDatabase;
import com.janos.nagy.ezlearnapp.database.LessonDao;
import com.janos.nagy.ezlearnapp.database.StudySessionDao;
import com.janos.nagy.ezlearnapp.database.TaskDao;
import com.janos.nagy.ezlearnapp.database.UserScoreDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudyRepository {

    private StudySessionDao studySessionDao;
    private TaskDao taskDao;
    private UserScoreDao userScoreDao;
    private LessonDao lessonDao;
    private FirebaseFirestore firestore;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public StudyRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        studySessionDao = db.studySessionDao();
        taskDao = db.taskDao();
        userScoreDao = db.userScoreDao();
        lessonDao = db.lessonDao();
        firestore = FirebaseFirestore.getInstance();  // Firestore inicializálása
    }

    // Session hozzáadása - aszinkron művelet
    public void insertSession(StudySession session) {
        executorService.execute(() -> {
            // Room adatbázisba mentés
            studySessionDao.insertSession(session);

            // Firestore-ba mentés
            firestore.collection("study_sessions")
                    .document(String.valueOf(session.getId()))
                    .set(session)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "StudySession sikeresen mentve!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore mentésnél", e));
        });
    }

    public void insertTask(Task task) {
        // Először mentjük el a feladatot a Room adatbázisba
        executorService.execute(() -> {
            long taskId = taskDao.insertTask(task);

            // Most küldjük el a feladatot a Firestore-ba
            firestore.collection("tasks")
                    .add(task)  // Firestore automatikusan generál egy ID-t
                    .addOnSuccessListener(documentReference -> {
                        String firestoreId = documentReference.getId();  // Lekérjük a Firestore ID-t

                        // Frissítjük a task objektumot a Firestore ID-val
                        task.setFirestoreId(firestoreId);

                        // Most frissítjük a Room adatbázist a Firestore ID-val
                        executorService.execute(() -> {
                            taskDao.updateTask(task);  // Ezt most háttérszálon végezzük el
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Hiba a Firestore-ba mentés közben", e);
                    });
        });
    }

    // Task frissítése - aszinkron művelet
    public void updateTask(Task task) {
        executorService.execute(() -> {
            // Room adatbázis frissítése
            taskDao.updateTask(task);

            // Firestore frissítése
            firestore.collection("tasks")
                    .document(task.getFirestoreId())
                    .set(task, SetOptions.merge()) // Módosítjuk a meglévő dokumentumot
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Task sikeresen frissítve!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore frissítésnél", e));
        });
    }

    // Task törlése - aszinkron művelet
    public void deleteTask(int taskId) {
        executorService.execute(() -> {
            // Room adatbázis törlése
            taskDao.deleteTask(taskId);

            // Firestore törlése
            firestore.collection("tasks")
                    .document(String.valueOf(taskId))
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Task sikeresen törölve!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore törlésnél", e));
        });
    }

    // Pontszám frissítése - mind Room, mind Firestore
    public void updateScore(UserScore score) {
        executorService.execute(() -> {
            // Helyi adatbázis frissítése
            UserScore existing = userScoreDao.getScoreByUserId(score.getUserId());
            if (existing == null) {
                userScoreDao.insertScore(score);
            } else {
                userScoreDao.updateScore(score);
            }

            // Firestore frissítése
            firestore.collection("user_scores")
                    .document(score.getUserId())
                    .set(score, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Pontszám sikeresen frissítve!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore frissítésnél", e));
        });
    }

    // Firestore-ból adat szinkronizálása
    public void syncScoresFromFirestore(String userId) {
        firestore.collection("user_scores")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            UserScore userScore = task.getResult().toObject(UserScore.class);
                            if (userScore != null) {
                                updateScore(userScore);
                            }
                        } else {
                            Log.d("Firestore", "Felhasználó nem található Firestore-ban, inicializálás...");
                            UserScore newUserScore = new UserScore(userId, 0);
                            firestore.collection("user_scores").document(userId).set(newUserScore)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Pontszám inicializálva Firestore-ban."))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Hiba történt az inicializálás során.", e));
                            updateScore(newUserScore);  // Helyileg is frissítjük
                        }
                    } else {
                        Log.e("Firestore", "Hiba Firestore lekérdezésnél", task.getException());
                    }
                });
    }

    // Az összes Task lekérése - LiveData
    public LiveData<List<Task>> getAllTasks() {
        return taskDao.getAllTasks();
    }

    // Pontszám lekérése
    public LiveData<UserScore> getScore(String userId) {
        return userScoreDao.getScoreByUserIdLiveData(userId);
    }

    // Lesson hozzáadása - aszinkron művelet
    public void insertLesson(Lesson lesson) {
        executorService.execute(() -> {
            lessonDao.insertLesson(lesson);
            firestore.collection("lessons")
                    .document(lesson.getLessonId())
                    .set(lesson)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Lecke sikeresen mentve!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore mentésnél", e));
        });
    }

    // Leckék lekérése
    public LiveData<List<Lesson>> getLessons(String userId) {
        return lessonDao.getLessonsByUserId(userId);
    }

    // Az összes pontszám lekérése rendezett listában
    public LiveData<List<UserScore>> getAllScoresOrdered() {
        return userScoreDao.getAllScoresOrdered();
    }

    // Session frissítése
    public void updateSession(StudySession session) {
        executorService.execute(() -> {
            studySessionDao.updateSession(session);
            firestore.collection("study_sessions")
                    .document(String.valueOf(session.getId()))
                    .set(session, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "StudySession sikeresen frissítve!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore frissítésnél", e));
        });
    }
}