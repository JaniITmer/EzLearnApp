package com.janos.nagy.ezlearnapp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janos.nagy.ezlearnapp.data.model.Task;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {
    private StudyRepository repository;
    private String userId;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        repository = new StudyRepository(application);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            userId = null;
        }
    }

    public LiveData<List<Task>> getTasks() {
        if (userId != null) {
            return repository.getTasksByUserId(userId);
        } else {
            return new MutableLiveData<>();
        }
    }

    public void addTask(String title, long deadline, int pomodoroCount, boolean completed) {
        if (userId != null) {
            Task task = new Task(title, deadline, pomodoroCount, completed, userId);
            repository.insertTask(task);
        }
    }

    public String getUserId() {
        return userId;
    }


}