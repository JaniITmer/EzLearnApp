package com.janos.nagy.ezlearnapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lessons")
public class Lesson {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String filePath;
    private String userId;


    public Lesson(String title, String filePath, String userId){

        this.title=title;
        this.filePath=filePath;
        this.userId=userId;


    }
    public String getLessonId() {
        return String.valueOf(id);
    }

    public int getId() { return id;}
    public void setId(int id){this.id=id;}

    public String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}

    public String getFilePath(){return filePath;}
    public void setFilePath(String filePath){this.filePath=filePath;}

    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}




}
