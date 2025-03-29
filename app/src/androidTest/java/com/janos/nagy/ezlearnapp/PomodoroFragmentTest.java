package com.janos.nagy.ezlearnapp;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PomodoroFragmentTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> activityRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void testStartPomodoroButton() {

        Espresso.onView(withId(R.id.navigation_pomodoro)).perform(click());
        Espresso.onView(withId(R.id.startButton)).perform(click());
        Espresso.onView(withId(R.id.startButton))
                .check(matches(withText("Tanulás befejezése")));
    }
}