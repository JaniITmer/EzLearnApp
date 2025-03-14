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
        Log.d("PomodoroFragment", "onCreateView called");
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

            // Timer szöveg frissítése valós időben
            viewModel.getRemainingTime().observe(getViewLifecycleOwner(), remainingTime -> {
                if (remainingTime != null) {
                    int minutes = (int) (remainingTime / 60);
                    int seconds = (int) (remainingTime % 60);
                    timerText.setText(String.format("%02d:%02d", minutes, seconds));
                    Log.d("PomodoroFragment", "Timer updated: " + remainingTime + " seconds");
                } else {
                    timerText.setText("25:00");
                    Log.d("PomodoroFragment", "Remaining time is null, resetting to 25:00");
                }
            });

            // Pontszám figyelése
            viewModel.getUserScore().observe(getViewLifecycleOwner(), userScore -> {
                if (userScore != null) {
                    scoreText.setText("Pontszám: " + userScore.getScore());
                    Log.d("PomodoroFragment", "Score updated: " + userScore.getScore());
                } else {
                    scoreText.setText("Pontszám: 0");
                    Log.d("PomodoroFragment", "Score is null, resetting to 0");
                }
            });

            // Pomodoro futásának figyelése
            viewModel.isPomodoroRunning().observe(getViewLifecycleOwner(), isRunning -> {
                if (isRunning) {
                    startButton.setText("Tanulás befejezése");
                    Log.d("PomodoroFragment", "Pomodoro is running, setting button text to 'Tanulás befejezése'");
                } else {
                    startButton.setText("Tanulás elkezdése");
                    Log.d("PomodoroFragment", "Pomodoro is not running, setting button text to 'Tanulás elkezdése'");
                }
            });

            // Start/Stop gomb eseménykezelő
            startButton.setOnClickListener(v -> {
                Log.d("PomodoroFragment", "Start button clicked");
                if (viewModel.isPomodoroRunning().getValue()) {
                    viewModel.stopPomodoro();
                    Log.d("PomodoroFragment", "Stopping Pomodoro session");
                } else {
                    viewModel.startPomodoro();
                    Log.d("PomodoroFragment", "Starting Pomodoro session");
                }
            });

        } else {
            Log.e("PomodoroFragment", "Nincs bejelentkezve felhasználó!");
            Toast.makeText(getContext(), "Nincs bejelentkezve felhasználó!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}