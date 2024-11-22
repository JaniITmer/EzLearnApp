package com.janos.nagy.ezlearnapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // Ellenőrizd, hogy van-e bejelentkezett felhasználó
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Ha be van jelentkezve a felhasználó, nyisd meg a HomeActivity-t
        if (currentUser != null) {
            // Ha be van jelentkezve, indítsd a HomeActivity-t
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();  // A MainActivity-t lezárjuk, hogy ne lehessen visszalépni
        } else {
            // Első indítás ellenőrzése
            boolean isFirstRun = getSharedPreferences("AppPrefs", MODE_PRIVATE).getBoolean("isFirstRun", true);
            if (isFirstRun) {
                // Onboarding képernyő megnyitása
                Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Normál indítás
                setContentView(R.layout.activity_main);

                // Hivatkozás a regisztráció gombra
                Button registerButton = findViewById(R.id.button);

                // Gomb eseménykezelője
                registerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Nyisd meg a RegisterActivity-t
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                });

                // Hivatkozás a login gombra
                Button loginButton = findViewById(R.id.button2);

                // Gomb eseménykezelője
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Nyisd meg a LoginActivity-t
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}