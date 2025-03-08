package com.janos.nagy.ezlearnapp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.janos.nagy.ezlearnapp.data.model.Lesson;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import java.util.List;

public class LessonViewModel extends AndroidViewModel {
    private StudyRepository repository;

    public LessonViewModel(@NonNull Application application) {
        super(application);
        repository = new StudyRepository(application);
    }

    public LiveData<List<Lesson>> getLessons() {
        return repository.getLessons("user1"); // Firebase UID TODO
    }

    public void addLesson(String title, String filePath) {
        repository.insertLesson(new Lesson(title, filePath, "user1"));
    }
}