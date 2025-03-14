package com.janos.nagy.ezlearnapp.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.StudyViewModel;
import com.janos.nagy.ezlearnapp.StudyViewModelFactory;
import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.repository.UserRepository;


public class PomodoroFragment extends Fragment {
    private StudyViewModel viewModel;
    private TextView timerText;
    private TextView scoreText;
    private Button startButton;
    private UserRepository userRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        timerText = view.findViewById(R.id.timerText);
        scoreText = view.findViewById(R.id.scoreText);
        startButton = view.findViewById(R.id.startButton);

        userRepository = new UserRepository(requireActivity().getApplication());

        // Lekérjük a bejelentkezett felhasználó ID-ját
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid(); // Az aktuális felhasználó UID-ja

            StudyViewModelFactory factory = new StudyViewModelFactory(requireActivity().getApplication(), userId);
            viewModel = new ViewModelProvider(PomodoroFragment.this, factory).get(StudyViewModel.class);

            // Az aktuális felhasználó pontszámának megjelenítése
            viewModel.getUserScore().observe(getViewLifecycleOwner(), userScore -> {
                if (userScore != null) {
                    scoreText.setText("Pontszám: " + userScore.getScore());
                } else {
                    scoreText.setText("Pontszám: 0");
                }
            });

            startButton.setOnClickListener(v -> {
                if (viewModel.isPomodoroRunning().getValue()) {
                    viewModel.stopPomodoro();
                } else {
                    viewModel.startPomodoro();
                }
            });

        } else {
            Log.e("PomodoroFragment", "Nincs bejelentkezve felhasználó!");
            Toast.makeText(getContext(), "Nincs bejelentkezve felhasználó!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}