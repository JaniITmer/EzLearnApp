package com.janos.nagy.ezlearnapp;

import android.app.Application;
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
        // Lekérjük a jelenleg bejelentkezett felhasználó UID-jét
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        } else {
            // Ha nincs bejelentkezett felhasználó, kezeljük a helyzetet (pl. null érték, vagy default érték)
            currentUserId = null; // Vagy egy default érték, pl. "guest"
        }
    }
    public void deleteLesson(Lesson lesson) {
        repository.deleteLesson(lesson);  // Kérés a repository felé
    }
    public LiveData<List<Lesson>> getLessons() {
        // A Firebase UID-t használjuk a leckék lekérdezéséhez
        return repository.getLessons(currentUserId);
    }

    public void addLesson(String title, String filePath) {
        // A Firebase UID-t használjuk az új lecke mentéséhez
        if (currentUserId != null) {
            repository.insertLesson(new Lesson(title, filePath, currentUserId));
        } else {
            // Kezeljük a helyzetet, ha nincs bejelentkezett felhasználó
            // Pl. logolunk egy hibát, vagy nem mentjük el a leckét
            // Log.e("LessonViewModel", "Nincs bejelentkezett felhasználó, a lecke nem menthető.");
        }
    }
    public String getCurrentUserId() {
        return currentUserId;
    }
}