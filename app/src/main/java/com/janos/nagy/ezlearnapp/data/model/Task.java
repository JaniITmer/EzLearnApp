package com.janos.nagy.ezlearnapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private long startTime; // Határidőként használjuk
    private int pomodoroCount;
    private boolean completed;
    private String userId; // Új mező a felhasználó azonosításához


    public Task(String title, long startTime, int pomodoroCount, boolean completed, String userId) {
        this.title = title;
        this.startTime = startTime;
        this.pomodoroCount = pomodoroCount;
        this.completed = completed;
        this.userId = userId;
    }


    public Task() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTaskId() { return String.valueOf(id); }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public int getPomodoroCount() { return pomodoroCount; }
    public void setPomodoroCount(int pomodoroCount) { this.pomodoroCount = pomodoroCount; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}