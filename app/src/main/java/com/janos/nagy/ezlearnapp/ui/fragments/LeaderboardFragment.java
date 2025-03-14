package com.janos.nagy.ezlearnapp.ui.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janos.nagy.ezlearnapp.LeaderboardViewModelFactory;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.LeaderboardViewModel;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

public class LeaderboardFragment extends Fragment {
    private LeaderboardViewModel viewModel;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        String userId = user.getUid();


        if (user == null) {
            Toast.makeText(getContext(), "Nincs bejelentkezett felhasználó!", Toast.LENGTH_SHORT).show();
            return view;
        }

        viewModel = new ViewModelProvider(this, new LeaderboardViewModelFactory(requireContext().getApplicationContext()))
                .get(LeaderboardViewModel.class);

        viewModel.setUserId(userId);




        TextView scoreTextView = view.findViewById(R.id.score_text_view);

        // Figyeld a LiveData változását
        viewModel.getUserScore().observe(getViewLifecycleOwner(), userScore -> {
            if (userScore != null) {
                scoreTextView.setText("Pontszám: " + userScore.getScore());
            } else {
                Toast.makeText(getContext(), "Nincs elérhető pontszám", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}