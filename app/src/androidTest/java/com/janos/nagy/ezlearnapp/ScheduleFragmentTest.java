package com.janos.nagy.ezlearnapp;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScheduleFragmentTest {
    @Rule
    public ActivityScenarioRule<HomeActivity> activityRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setUp() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("probafelhasznalo1@gmail.com", "probafelhasznalo1")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        throw new RuntimeException("Tesztfelhasználó bejelentkezése sikertelen!");
                    }
                });
    }

    @Test
    public void testStartScheduleAddButton() {

        Espresso.onView(withId(R.id.navigation_schedule)).perform(click());
        Espresso.onView(withId(R.id.addTaskButton)).perform(click());

        Espresso.onView(withId(R.id.taskTitleEditText)).perform(typeText("Teszt feladat"));


        Espresso.onView(withId(R.id.taskDeadlineEditText)).perform(typeText("2025-06-25"));
        Espresso.onView(withId(R.id.taskPomodoroCountEditText)).perform(typeText("3"));

        Espresso.onView(withText("Hozzáadás")).perform(click());


    }
}
