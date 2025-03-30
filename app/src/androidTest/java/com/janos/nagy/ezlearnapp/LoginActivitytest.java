package com.janos.nagy.ezlearnapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivitytest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    private FirebaseAuth mAuth;

    @Before
    public void setUp() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    @After
    public void tearDown() {
        mAuth.signOut();
    }

    @Test
    public void testSuccessfulLogin() {
        String testEmail = "probafelhasznalo1@gmail.com";
        String testPassword = "probafelhasznalo1";

        onView(withId(R.id.emailEditText)).perform(typeText(testEmail));
        onView(withId(R.id.passwordEditText)).perform(typeText(testPassword));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.startButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testFailedLogin() {
        String wrongEmail = "rosszemail@gmail.com";
        String wrongPassword = "hibasjelszo01";

        onView(withId(R.id.emailEditText)).perform(typeText(wrongEmail));
        onView(withId(R.id.passwordEditText)).perform(typeText(wrongPassword));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
    }
}