package com.janos.nagy.ezlearnapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.janos.nagy.ezlearnapp.data.model.User;
import com.janos.nagy.ezlearnapp.database.AppDatabase;
import com.janos.nagy.ezlearnapp.database.UserDao;

public class UserRepository {
    private UserDao userDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public LiveData<User> getUserById(String userId) {
        return userDao.getUserById(userId);
    }

    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insertUser(user));
    }

    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.updateUser(user));
    }
}