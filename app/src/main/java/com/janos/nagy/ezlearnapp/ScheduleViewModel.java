package com.janos.nagy.ezlearnapp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.janos.nagy.ezlearnapp.data.model.Task;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {
    private StudyRepository repository;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        repository = new StudyRepository(application);
    }

    public LiveData<List<Task>> getTasks() {
        return repository.getAllTasks();
    }

    public void addTask(String title, long startTime, int pomodoroCount) {
        repository.insertTask(new Task(title, startTime, pomodoroCount));
    }
}