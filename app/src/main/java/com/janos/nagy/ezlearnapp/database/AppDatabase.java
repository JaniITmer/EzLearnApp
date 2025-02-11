package com.janos.nagy.ezlearnapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.janos.nagy.ezlearnapp.data.model.StudySession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {StudySession.class}, version =6)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;


    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract StudySessionDao studySessionDao();
    public static AppDatabase getInstance(Context context){

        if(INSTANCE ==null){
            synchronized (AppDatabase.class){
                if (INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"ezlearn_database").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
