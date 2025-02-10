package com.janos.nagy.ezlearnapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_sessions")
public class StudySession {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;
    public int duration;


    public StudySession(String date, int duration){
        this.date=date;
        this.duration=duration;
    }
}
