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
    private String name;

    public UserScore() {

    }
    public UserScore(String userId,int score,String name){

        this.userId=userId;
        this.score=score;
        this.name = name;
    }

    @NonNull
    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}
    public int getScore(){return  score;}
    public void setScore(int score){this.score=score;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
