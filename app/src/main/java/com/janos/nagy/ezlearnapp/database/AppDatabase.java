package com.janos.nagy.ezlearnapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.janos.nagy.ezlearnapp.data.model.Lesson;
import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.data.model.Task;
import com.janos.nagy.ezlearnapp.data.model.User;
import com.janos.nagy.ezlearnapp.data.model.UserScore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {StudySession.class, Task.class, UserScore.class, Lesson.class, User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract StudySessionDao studySessionDao();
    public abstract TaskDao taskDao();
    public abstract UserScoreDao userScoreDao();
    public abstract LessonDao lessonDao();
    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "ezlearn_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}