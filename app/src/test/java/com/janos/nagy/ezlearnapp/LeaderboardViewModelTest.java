package com.janos.nagy.ezlearnapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.janos.nagy.ezlearnapp.data.model.UserScore;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class LeaderboardViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private StudyRepository repository;

    private LeaderboardViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new LeaderboardViewModel(repository);
    }

    @Test
    public void testSetUserId() {
        String newUserId = "testUserId";
        UserScore mockUserScore = new UserScore(newUserId, 100);
        MutableLiveData<UserScore> mockLiveData = new MutableLiveData<>();
        mockLiveData.setValue(mockUserScore);

        Mockito.when(repository.getScore(anyString())).thenReturn(mockLiveData);

        viewModel.setUserId(newUserId);

        LiveData<UserScore> userScoreLiveData = viewModel.getUserScore();

        userScoreLiveData.observeForever(new Observer<UserScore>() {
            @Override
            public void onChanged(UserScore userScore) {
                assertNotNull(userScore);
                assertEquals(newUserId, userScore.getUserId());
                assertEquals(100,userScore.getScore());
            }
        });
    }
}