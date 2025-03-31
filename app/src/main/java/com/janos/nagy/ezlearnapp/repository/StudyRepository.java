package com.janos.nagy.ezlearnapp.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
            firestore = FirebaseFirestore.getInstance();
        }

        public void insertSession(StudySession session) {
            executorService.execute(() -> {
                long newId = studySessionDao.insertSession(session);
                session.setId((int) newId);
                firestore.collection("study_sessions")
                        .document(String.valueOf(newId))
                        .set(session)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "StudySession sikeresen mentve, ID: " + newId))
                        .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore mentésnél", e));
            });
        }

        public void insertTask(Task task) {
            executorService.execute(() -> {
                long newId = taskDao.insertTask(task);
                task.setId((int) newId);
                firestore.collection("tasks")
                        .document(String.valueOf(newId))
                        .set(task, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Task sikeresen mentve, ID: " + newId))
                        .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore mentésnél", e));
            });
        }

        public void updateTask(Task task) {
            executorService.execute(() -> {
                taskDao.updateTask(task);
                firestore.collection("tasks")
                        .document(String.valueOf(task.getId()))
                        .set(task, SetOptions.merge());
            });
        }

        public void deleteTask(Task task) {
            executorService.execute(() -> {
                taskDao.deleteTask(task);
                firestore.collection("tasks")
                        .document(String.valueOf(task.getId()))
                        .delete();
            });
        }

        public void updateScore(UserScore score) {
            executorService.execute(() -> {
                UserScore existing = userScoreDao.getScoreByUserId(score.getUserId());
                if (existing == null) {
                    userScoreDao.insertScore(score);
                } else {
                    userScoreDao.updateScore(score);
                }
                firestore.collection("user_scores")
                        .document(score.getUserId())
                        .set(score, SetOptions.merge());
            });
        }

        public LiveData<List<Task>> getTasksByUserId(String userId) {
            return taskDao.getTasksByUserId(userId);
        }

        public LiveData<UserScore> getScore(String userId) {
            return userScoreDao.getScoreByUserIdLiveData(userId);
        }

        public void insertLesson(Lesson lesson) {
            executorService.execute(() -> {
                long newId = lessonDao.insertLesson(lesson);
                lesson.setId((int) newId);
                firestore.collection("lessons")
                        .document(lesson.getLessonId())
                        .set(lesson)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Lecke sikeresen mentve, ID: " + lesson.getLessonId()))
                        .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore mentésnél", e));
            });
        }

        public void deleteLesson(Lesson lesson) {
            executorService.execute(() -> {
                lessonDao.deleteLesson(lesson);
                firestore.collection("lessons")
                        .document(lesson.getLessonId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Lecke sikeresen törölve!"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Hiba Firestore törlésnél", e));
            });
        }

        public LiveData<List<Lesson>> getLessons(String userId) {
            return lessonDao.getLessonsByUserId(userId);
        }

        public LiveData<List<UserScore>> getAllScoresOrdered() {
            return userScoreDao.getAllScoresOrdered();
        }

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

        public void syncAllScoresFromFirestore() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user_scores").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        UserScore userScore = document.toObject(UserScore.class);
                        if (userScore != null) {
                            updateScore(userScore);
                        }
                    }
                }
            });
        }

        public void syncLessonsFromFirestore(String userId) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("lessons")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Lesson lesson = document.toObject(Lesson.class);
                                if (lesson != null) {
                                    executorService.execute(() -> {
                                        Lesson existingLesson = lessonDao.getLessonById(lesson.getId());
                                        if (existingLesson == null) {
                                            lessonDao.insertLesson(lesson);
                                        } else {
                                            lessonDao.updateLesson(lesson);
                                        }
                                        Log.d("Firestore", "Lecke szinkronizálva: " + lesson.getTitle());
                                    });
                                }
                            }
                        } else {
                            Log.e("Firestore", "Hiba a leckék lekérdezésekor", task.getException());
                        }
                    });
        }

        public void syncScoresFromFirestore(String userId) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userScoreRef = db.collection("user_scores").document(userId);

            userScoreRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        UserScore userScore = task.getResult().toObject(UserScore.class);
                        if (userScore != null) {
                            updateScore(userScore);
                        }
                    } else {
                        db.collection("users").document(userId).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    String name = documentSnapshot.exists() ? documentSnapshot.getString("name") : "Névtelen";
                                    UserScore newUserScore = new UserScore(userId, 0, name);
                                    userScoreRef.set(newUserScore);
                                    updateScore(newUserScore);
                                })
                                .addOnFailureListener(e -> {
                                    UserScore newUserScore = new UserScore(userId, 0, "Névtelen");
                                    userScoreRef.set(newUserScore);
                                    updateScore(newUserScore);
                                });
                    }
                }
            });
        }
    }