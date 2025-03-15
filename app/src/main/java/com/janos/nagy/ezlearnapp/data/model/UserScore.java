package com.janos.nagy.ezlearnapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
@Entity(tableName="user_scores")
public class UserScore {

    @PrimaryKey
    @NonNull
    private String userId;
    private int score;


    public UserScore() {

    }
    public UserScore(String userId,int score){

        this.userId=userId;
        this.score=score;
    }

    @NonNull
    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}
    public int getScore(){return  score;}
    public void setScore(int score){this.score=score;}



}
