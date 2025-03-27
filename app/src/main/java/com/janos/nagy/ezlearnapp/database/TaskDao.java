package com.janos.nagy.ezlearnapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.janos.nagy.ezlearnapp.data.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insertTask(Task task);

    @Update
    void updateTask(Task task);


    @Delete
    void deleteTask(Task task);
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    LiveData<List<Task>> getTasksByUserId(String userId);
}