package com.janos.nagy.ezlearnapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.janos.nagy.ezlearnapp.data.model.UserScore;

import java.util.List;

@Dao
public interface UserScoreDao {
    @Insert
    void insertScore(UserScore score);

    @Update
    void updateScore(UserScore score);

    @Query("SELECT * FROM user_scores WHERE userId = :userId LIMIT 1")
    LiveData<UserScore> getScoreByUserIdLiveData(String userId);

    @Query("SELECT * FROM user_scores WHERE userId = :userId LIMIT 1")
    UserScore getScoreByUserId(String userId);
    @Query("SELECT * FROM user_scores ORDER BY score DESC")
    LiveData<List<UserScore>> getAllScoresOrdered();
}
