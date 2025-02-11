package com.janos.nagy.ezlearnapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.janos.nagy.ezlearnapp.data.model.StudySession;

import java.util.List;

@Dao
public interface StudySessionDao {


    @Insert
    long insertSession(StudySession session);

    @Query("SELECT * FROM study_sessions WHERE id = :sessionId")
    StudySession getSessionById(int sessionId);

    @Transaction
    @Update
    void updateSession(StudySession session);
}
