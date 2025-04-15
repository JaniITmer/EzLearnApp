package com.janos.nagy.ezlearnapp;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;



@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StudyViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private StudyRepository repository;

    @Mock
    private FirebaseFirestore firestore;

    private StudyViewModel viewModel;
    private MockedStatic<FirebaseFirestore> firestoreStaticMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        firestoreStaticMock = Mockito.mockStatic(FirebaseFirestore.class);
        firestoreStaticMock.when(FirebaseFirestore::getInstance).thenReturn(firestore);

        MutableLiveData<UserScore> userScoreLiveData = new MutableLiveData<>();
        userScoreLiveData.setValue(new UserScore("testUserId", 0, "TestUser"));
        when(repository.getScore("testUserId")).thenReturn(userScoreLiveData);


        viewModel = new StudyViewModel(RuntimeEnvironment.getApplication(), "testUserId", repository);

        doNothing().when(repository).insertSession(any(StudySession.class));
        doNothing().when(repository).updateSession(any(StudySession.class));
        doNothing().when(repository).updateScore(any(UserScore.class));
    }

    @Test
    public void testStopPomodoro_StopsTimerAndUpdatesScore() throws InterruptedException {

        viewModel.startPomodoro();

        StudySession session = viewModel.getCurrentSession().getValue();
        assertNotNull("A session nem lehet null", session);
        assertTrue("Pomodoronak futnia kell", viewModel.isPomodoroRunning().getValue());


        Thread.sleep(1000);


        viewModel.stopPomodoro();


        assertFalse("A pomodoronak meg kell állnia", viewModel.isPomodoroRunning().getValue());
        assertEquals("Hátralévő időnek 25 percnek kell lennie(1500 másodperc)",
                1500L, viewModel.getRemainingTime().getValue().longValue());


        verify(repository, atLeastOnce()).updateScore(any(UserScore.class));
    }

    @After
    public void tearDown() {
        if (firestoreStaticMock != null) {
            firestoreStaticMock.close();
        }
    }
}