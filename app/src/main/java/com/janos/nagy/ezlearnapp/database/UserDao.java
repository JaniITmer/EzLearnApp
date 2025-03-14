package com.janos.nagy.ezlearnapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.janos.nagy.ezlearnapp.data.model.User;


@Dao
public interface UserDao {
    @Insert
    void insertUser(User user); // Felhasználó hozzáadása

    @Update
    void updateUser(User user); // Felhasználó frissítése

    @Query("SELECT * FROM user_table WHERE id = :userId LIMIT 1")
    LiveData<User> getUserById(String userId);

    @Query("UPDATE user_table SET score = :score WHERE id = :userId")
    void updateScore(String userId, int score);
}