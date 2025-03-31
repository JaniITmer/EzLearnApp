package com.janos.nagy.ezlearnapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janos.nagy.ezlearnapp.data.model.Lesson;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import java.util.List;

public class LessonViewModel extends AndroidViewModel {
    private StudyRepository repository;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;


    public LessonViewModel(@NonNull Application application, StudyRepository repository, FirebaseAuth firebaseAuth) {
        super(application);
        this.repository = repository;
        this.firebaseAuth = firebaseAuth;

        FirebaseUser user = firebaseAuth.getCurrentUser();
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


    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;
        private final StudyRepository repository;
        private final FirebaseAuth firebaseAuth;

        public Factory(Application application, StudyRepository repository, FirebaseAuth firebaseAuth) {
            this.application = application;
            this.repository = repository;
            this.firebaseAuth = firebaseAuth;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LessonViewModel.class)) {
                return (T) new LessonViewModel(application, repository, firebaseAuth);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}