package com.janos.nagy.ezlearnapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nullable;


public class ProfileFragment extends Fragment {


    private FirebaseAuth mAuth;
    private TextView userNameTextView, userEmailTextView;
    private Button logoutbutton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        mAuth = FirebaseAuth.getInstance();


        userNameTextView = view.findViewById(R.id.userNameTextView);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        logoutbutton = view.findViewById(R.id.logoutButton);


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userNameTextView.setText(user.getDisplayName() != null ? user.getDisplayName() : "Nincs név beállítva");
            userEmailTextView.setText(user.getEmail());
        }


        logoutbutton.setOnClickListener(v -> {
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