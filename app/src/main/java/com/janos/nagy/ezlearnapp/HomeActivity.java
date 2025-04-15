package com.janos.nagy.ezlearnapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {


    private static final String TAG = "HomeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "Layout loaded: " + R.layout.activity_home);


        View navHostView = findViewById(R.id.nav_host_fragment);
        if (navHostView == null) {
            Log.e(TAG, "FragmentContainerView nincs ilyen ID-vel: " + R.id.nav_host_fragment);
            return;
        } else {
            Log.d(TAG, "FragmentContainerView ilyen ID-vel: " + R.id.nav_host_fragment);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        if (navView == null) {
            Log.e(TAG, "BottomNavigationView nem található");
            return;
        } else {
            Log.d(TAG, "BottomNavigationView található ID-vel: " + R.id.nav_view);
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
        NavController navController;

        if (navHostFragment == null) {
            Log.d(TAG, "NavHostFragment nem talalhato, keszites manualisan..");
            try {
                navHostFragment = NavHostFragment.create(R.navigation.nav_graph);
                fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, navHostFragment)
                        .setPrimaryNavigationFragment(navHostFragment)
                        .commitNow();
                navController = navHostFragment.getNavController();
            } catch (Exception e) {
                Log.e(TAG, "Hiba navHostFragment letrehozasakor " + e.getMessage(), e);
                return;
            }
        } else {
            Log.d(TAG, "NavHostFragment megtalalhato ID: " + R.id.nav_host_fragment);
            navController = navHostFragment.getNavController();
        }

        NavigationUI.setupWithNavController(navView, navController);
        Log.d(TAG, "NavController sikeresen beallitva ID: " + R.id.nav_host_fragment);
    }
}
