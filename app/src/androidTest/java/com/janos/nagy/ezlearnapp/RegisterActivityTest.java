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
public class RegisterActivityTest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> activityRule = new ActivityScenarioRule<>(RegisterActivity.class);

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
    public void testRegistrationWrongEmail() {

        String invalidEmail = "rosszemail";
        String password = "jelszo123123";

        onView(withId(R.id.email_field)).perform(typeText(invalidEmail));
        onView(withId(R.id.password_field)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_button)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.register_button)).check(matches(isDisplayed()));
    }
}