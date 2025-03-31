package com.janos.nagy.ezlearnapp.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.janos.nagy.ezlearnapp.LoginActivity;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.data.model.User;
import com.janos.nagy.ezlearnapp.repository.UserRepository;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private EditText userNameEditText;
    private TextView userEmailTextView;
    private Button saveUserNameButton, logoutButton;
    private UserRepository userRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userRepository = new UserRepository(requireActivity().getApplication());

        userNameEditText = view.findViewById(R.id.userNameEditText);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        saveUserNameButton = view.findViewById(R.id.saveUserNameButton);
        logoutButton = view.findViewById(R.id.logoutButton);


        // Get the current user
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            // Load existing user data
            firestore.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("name");
                            Long lastChange = documentSnapshot.getLong("lastNameChangeTimestamp");
                            userNameEditText.setText(userName != null && !userName.isEmpty() ? userName : "Nincs név");
                        } else {
                            userNameEditText.setText("Nincs név");
                        }
                    })
                    .addOnFailureListener(e -> {
                        userNameEditText.setText("Hiba a név betöltésekor");
                    });

            String email = firebaseUser.getEmail();
            userEmailTextView.setText(email != null && !email.isEmpty() ? email : "Nincs email");

            // Save new username
            saveUserNameButton.setOnClickListener(v -> {
                String newName = userNameEditText.getText().toString().trim();
                if (!newName.isEmpty()) {
                    firestore.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Long lastChange = documentSnapshot.getLong("lastNameChangeTimestamp");
                                    long currentTime = System.currentTimeMillis();
                                    long oneMonthInMillis = 30L * 24 * 60 * 60 * 1000; // Approx 30 days

                                    if (lastChange == null || (currentTime - lastChange >= oneMonthInMillis)) {
                                        User updatedUser = new User(userId, newName,currentTime);
                                        userRepository.updateUser(updatedUser);
                                        Toast.makeText(getContext(), "Felhasználónév frissítve!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        long timeLeft = oneMonthInMillis - (currentTime - lastChange);
                                        long daysLeft = timeLeft / (24 * 60 * 60 * 1000);
                                        Toast.makeText(getContext(), "Még " + daysLeft + " nap, mire újra változtathatsz!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Hiba történt!", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getContext(), "A név nem lehet üres!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            userNameEditText.setText("Nincs bejelentkezett felhasználó");
            userEmailTextView.setText("");
            saveUserNameButton.setEnabled(false);
        }

        // Logout functionality
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}