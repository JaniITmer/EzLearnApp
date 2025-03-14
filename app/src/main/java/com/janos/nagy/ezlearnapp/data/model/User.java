package com.janos.nagy.ezlearnapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private int score;

    public User(@NonNull String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}