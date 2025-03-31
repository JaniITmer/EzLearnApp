package com.janos.nagy.ezlearnapp.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.janos.nagy.ezlearnapp.data.model.User;
import com.janos.nagy.ezlearnapp.database.AppDatabase;
import com.janos.nagy.ezlearnapp.database.UserDao;

public class UserRepository {
    private UserDao userDao;
    private FirebaseFirestore firestore;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<User> getUserById(String userId) {
        syncUserFromFirestore(userId);
        return userDao.getUserById(userId);
    }

    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insertUser(user);

            firestore.collection("users")
                    .document(user.getId())
                    .set(user)
                    .addOnSuccessListener(aVoid -> Log.d("UserRepository", "User saved to Firestore: " + user.getId()))
                    .addOnFailureListener(e -> Log.e("UserRepository", "Error saving user to Firestore", e));
        });
    }

    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updateUser(user);

            firestore.collection("users")
                    .document(user.getId())
                    .set(user)
                    .addOnSuccessListener(aVoid -> Log.d("UserRepository", "User updated in Firestore: " + user.getId()))
                    .addOnFailureListener(e -> Log.e("UserRepository", "Error updating user in Firestore", e));
        });
    }


    private void syncUserFromFirestore(String userId) {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            AppDatabase.databaseWriteExecutor.execute(() -> {
                                userDao.insertUser(user);
                                Log.d("UserRepository", "User synced from Firestore: " + userId);
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("UserRepository", "Error syncing user from Firestore", e));
    }
}