package com.janos.nagy.ezlearnapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.janos.nagy.ezlearnapp.data.model.Lesson;

import java.util.List;

@Dao
public interface LessonDao {
    @Insert
    long insertLesson(Lesson lesson);

    @Query("SELECT * FROM lessons WHERE userId= :userId")
    LiveData<List<Lesson>> getLessonsByUserId(String userId);

}
