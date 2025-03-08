package com.janos.nagy.ezlearnapp.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

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

public class StudyRepository {


    private StudySessionDao studySessionDao;
    private TaskDao taskDao;
    private UserScoreDao userScoreDao;
    private LessonDao lessonDao;

    public StudyRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        studySessionDao = db.studySessionDao();
        taskDao = db.taskDao();
        userScoreDao = db.userScoreDao();
        lessonDao = db.lessonDao();
    }

    public void insertSession(StudySession session) {
        AppDatabase.databaseWriteExecutor.execute(() -> studySessionDao.insertSession(session));
    }

    public void updateSession(StudySession session) {
        AppDatabase.databaseWriteExecutor.execute(() -> studySessionDao.insertSession(session));
    }

    public void insertTask(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.updateTask(task));
    }

    public LiveData<List<Task>> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public void updateScore(UserScore score) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserScore existing = userScoreDao.getScoreByUserId(score.getUserId());
            if (existing == null) {
                userScoreDao.insertScore(score);
            } else {
                userScoreDao.updateScore(score);
            }
        });
    }

    // Módosított metódus: LiveData-t ad vissza
    public LiveData<UserScore> getScore(String userId) {
        return userScoreDao.getScoreByUserIdLiveData(userId);
    }

    public void insertLesson(Lesson lesson) {
        AppDatabase.databaseWriteExecutor.execute(() -> lessonDao.insertLesson(lesson));
    }

    public LiveData<List<Lesson>> getLessons(String userId) {
        return lessonDao.getLessonsByUserId(userId);
    }
}