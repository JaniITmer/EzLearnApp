package com.janos.nagy.ezlearnapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.janos.nagy.ezlearnapp.data.model.Lesson;

import java.util.List;

@Dao
public interface LessonDao {
    @Insert
    long insertLesson(Lesson lesson);

    @Query("SELECT * FROM lessons WHERE userId = :userId")
    LiveData<List<Lesson>> getLessonsByUserId(String userId);

    @Delete
    void deleteLesson(Lesson lesson);

    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    Lesson getLessonById(int lessonId);

    @Update
    void updateLesson(Lesson lesson);
}
