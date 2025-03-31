package com.janos.nagy.ezlearnapp.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.janos.nagy.ezlearnapp.LoginActivity;
import com.janos.nagy.ezlearnapp.R;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private TextView userNameTextView, userEmailTextView;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        userNameTextView = view.findViewById(R.id.userNameTextView);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();


            firestore.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("name");
                            userNameTextView.setText(userName != null && !userName.isEmpty() ? userName : "Nincs név");
                        } else {
                            userNameTextView.setText("Nincs név");
                        }
                    })
                    .addOnFailureListener(e -> {
                        userNameTextView.setText("Hiba a név betöltésekor");
                    });


            String email = user.getEmail();
            userEmailTextView.setText(email != null && !email.isEmpty() ? email : "Nincs email");
        } else {

            userNameTextView.setText("Nincs bejelentkezett felhasználó");
            userEmailTextView.setText("");
        }


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