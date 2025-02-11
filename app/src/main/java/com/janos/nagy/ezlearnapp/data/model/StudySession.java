package com.janos.nagy.ezlearnapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_sessions")
public class StudySession {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private long startTime;
    private long endTime;
    private long duration;


    public StudySession(long startTime) {
        this.startTime = startTime;
        this.endTime = -1;
        this.duration = 0;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;

        setDuration(endTime - startTime);
    }

    public long getDuration() {
        return duration;
    }


    public void setDuration(long duration) {
        this.duration = duration / 60000;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}