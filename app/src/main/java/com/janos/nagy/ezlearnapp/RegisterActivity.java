package com.janos.nagy.ezlearnapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.janos.nagy.ezlearnapp.data.model.User;
import com.janos.nagy.ezlearnapp.database.AppDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        database = AppDatabase.getInstance(this); // Adatbázis példány

        EditText emailField = findViewById(R.id.email_field);
        EditText passwordField = findViewById(R.id.password_field);
        Button registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Minden Mezőt tölts ki!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 8) {
                Toast.makeText(RegisterActivity.this, "A jelszónak legalább 8 karakter hosszúnak kell lennie!", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = auth.getCurrentUser().getUid();
                            String userName = email.split("@")[0]; // Egyszerű név generálás

                            // Felhasználó mentése a Room adatbázisba
                            User user = new User(userId, userName, 0); // Kezdetben 0 pont
                            new Thread(() -> database.userDao().insertUser(user)).start();

                            Toast.makeText(RegisterActivity.this, "Sikeres Regisztráció!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}