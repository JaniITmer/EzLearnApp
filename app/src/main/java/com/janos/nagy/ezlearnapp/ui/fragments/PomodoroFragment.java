package com.janos.nagy.ezlearnapp.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.StudyViewModel;
import com.janos.nagy.ezlearnapp.StudyViewModelFactory;


public class PomodoroFragment extends Fragment {
    private StudyViewModel viewModel;
    private TextView timerText;
    private TextView scoreText;
    private Button startButton;
    private boolean isStudying = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PomodoroFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        timerText = view.findViewById(R.id.timerText);
        scoreText = view.findViewById(R.id.scoreText);
        startButton = view.findViewById(R.id.startButton);

        // StudyViewModel inicializálása a Factory használatával
        String userId = "default_user"; // Ezt valahonnan meg kell kapni, pl. bejelentkezéskor
        StudyViewModelFactory factory = new StudyViewModelFactory(requireActivity().getApplication(), userId);
        viewModel = new ViewModelProvider(this, factory).get(StudyViewModel.class);

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

        // Tanulási állapot figyelése
        viewModel.getCurrentSession().observe(getViewLifecycleOwner(), session -> {
            if (session != null && session.getEndTime() == -1) {
                // Session aktív
                startButton.setText("Tanulás befejezése");
                isStudying = true;
                Log.d("PomodoroFragment", "Session active, setting UI to studying");
            } else {
                // Session befejeződött
                startButton.setText("Tanulás elkezdése");
                isStudying = false;
                Log.d("PomodoroFragment", "Session ended, resetting UI");
            }
        });

        startButton.setOnClickListener(v -> {
            Log.d("PomodoroFragment", "Start button clicked, isStudying: " + isStudying);
            if (isStudying) {
                viewModel.stopPomodoro();
                Log.d("PomodoroFragment", "Stopping Pomodoro session");
            } else {
                viewModel.startPomodoro();
                Log.d("PomodoroFragment", "Starting Pomodoro session");
            }
            isStudying = !isStudying;
        });

        return view;
    }
}