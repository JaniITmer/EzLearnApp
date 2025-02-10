package com.janos.nagy.ezlearnapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.janos.nagy.ezlearnapp.data.model.StudySession;

import java.util.List;

@Dao
public interface StudySessionDao {


    @Insert
    void insertSession(StudySession session);

    @Query("SELECT * FROM study_sessions ORDER BY date DESC")
    List<StudySession> getAllSessions();


    @Query("SELECT SUM(duration) FROM study_sessions")
    int getTotalStudyTime();


    @Delete
    void deleteSession(StudySession session);
}
