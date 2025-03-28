package com.janos.nagy.ezlearnapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janos.nagy.ezlearnapp.data.model.Lesson;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import java.util.List;

public class LessonViewModel extends AndroidViewModel {
    private StudyRepository repository;
    private String currentUserId;

    public LessonViewModel(@NonNull Application application) {
        super(application);
        repository = new StudyRepository(application);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            repository.syncLessonsFromFirestore(currentUserId);
        } else {

            currentUserId = null;
        }
    }

    public void deleteLesson(Lesson lesson) {
        repository.deleteLesson(lesson);
    }

    public LiveData<List<Lesson>> getLessons() {
        return repository.getLessons(currentUserId);
    }

    public void addLesson(String title, String filePath) {
        if (currentUserId != null) {
            repository.insertLesson(new Lesson(title, filePath, currentUserId));
        } else {
            Log.e("LessonViewModel", "Nincs bejelentkezett felhasználó, a lecke nem menthető.");
        }
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
}