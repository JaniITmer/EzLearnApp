package com.janos.nagy.ezlearnapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janos.nagy.ezlearnapp.data.model.Lesson;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class LessonViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private StudyRepository repository;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private FirebaseUser firebaseUser;

    private LessonViewModel lessonViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("userId324");

        lessonViewModel = new LessonViewModel(RuntimeEnvironment.getApplication(), repository, firebaseAuth);
    }

    @Test
    public void testAddLesson_VerifiesTitle() {

        String expectedTitle = "teszt lecke";
        String expectedFilePath = "/disk/place";
        String expectedUserId = "userId324";
        doNothing().when(repository).insertLesson(any(Lesson.class));

        lessonViewModel.addLesson(expectedTitle, expectedFilePath);

        ArgumentCaptor<Lesson> lessonCaptor = ArgumentCaptor.forClass(Lesson.class);
        verify(repository, times(1)).insertLesson(lessonCaptor.capture());

        Lesson capturedLesson = lessonCaptor.getValue();
        assertEquals("A létrehozott lecke nem azonos nevű", expectedTitle, capturedLesson.getTitle());
        assertEquals("A fájl útvonala nem azonos", expectedFilePath, capturedLesson.getFilePath());
        assertEquals("Az userId nem egyezik meg", expectedUserId, capturedLesson.getUserId());
    }
}