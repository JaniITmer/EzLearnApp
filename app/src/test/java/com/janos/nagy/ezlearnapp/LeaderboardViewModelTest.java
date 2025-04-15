package com.janos.nagy.ezlearnapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.firestore.FirebaseFirestore;
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
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LeaderboardViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private StudyRepository repository;

    @Mock
    private FirebaseFirestore firestore;

    private LeaderboardViewModel viewModel;
    private MockedStatic<FirebaseFirestore> firestoreStaticMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        firestoreStaticMock = Mockito.mockStatic(FirebaseFirestore.class);
        firestoreStaticMock.when(FirebaseFirestore::getInstance).thenReturn(firestore);

        viewModel = new LeaderboardViewModel(repository);
    }

    @Test
    public void testSetUserId() {
        String newUserId = "testUserId";
        UserScore mockUserScore = new UserScore(newUserId, 100, newUserId);
        MutableLiveData<UserScore> mockLiveData = new MutableLiveData<>();
        mockLiveData.setValue(mockUserScore);

        Mockito.when(repository.getScore(anyString())).thenReturn(mockLiveData);

        viewModel.setUserId(newUserId);

        LiveData<UserScore> userScoreLiveData = viewModel.getUserScore();

        userScoreLiveData.observeForever(userScore -> {
            assertNotNull(userScore);
            assertEquals(newUserId, userScore.getUserId());
            assertEquals("Pontszám nem egyenlő a létrehozott értékkel",100, userScore.getScore());
        });
    }

    @After
    public void tearDown() {
        if (firestoreStaticMock != null) {
            firestoreStaticMock.close();
        }
    }
}