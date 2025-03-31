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
    private long lastNameChangeTimestamp;

    public User(@NonNull String id, String name, long lastNameChangeTimestamp) {
        this.id = id;
        this.name = name;
        this.lastNameChangeTimestamp = lastNameChangeTimestamp;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    

    public long getLastNameChangeTimestamp() {
        return lastNameChangeTimestamp;
    }

    public void setLastNameChangeTimestamp(long lastNameChangeTimestamp) {
        this.lastNameChangeTimestamp = lastNameChangeTimestamp;
    }
}