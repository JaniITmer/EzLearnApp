package com.janos.nagy.ezlearnapp;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.app.Application;
import android.util.Log;


@RunWith(RobolectricTestRunner.class)
public class StudyViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private StudyRepository repository;

    private StudyViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(repository.getScore("testUserId")).thenReturn(mock(LiveData.class));
        viewModel = new StudyViewModel(RuntimeEnvironment.getApplication(), "testUserId", repository);
    }

    @Test
    public void testStopPomodoro_StopsTimerAndUpdatesScore() {
        viewModel.startPomodoro();
        StudySession session = viewModel.getCurrentSession().getValue();
        assertNotNull(session);
        assertTrue(viewModel.isPomodoroRunning().getValue());

        viewModel.stopPomodoro();

        assertFalse(viewModel.isPomodoroRunning().getValue());
        assertEquals(1500, viewModel.getRemainingTime().getValue().longValue());
        verify(repository, atLeastOnce()).updateScore(any(UserScore.class));
    }
}