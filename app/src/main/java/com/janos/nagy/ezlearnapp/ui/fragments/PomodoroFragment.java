package com.janos.nagy.ezlearnapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.StudyViewModel;
import com.janos.nagy.ezlearnapp.StudyViewModelFactory;
import com.janos.nagy.ezlearnapp.data.model.StudySession;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;
import com.janos.nagy.ezlearnapp.repository.UserRepository;


public class PomodoroFragment extends Fragment {
    private StudyViewModel viewModel;
    private TextView timerText;
    private TextView scoreText;
    private Button startButton;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) {
                Toast.makeText(getContext(), "Értesítések engedélyezése szükséges!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("PomodoroFragment", "onPause – App háttérbe került");

        if (viewModel != null && Boolean.TRUE.equals(viewModel.isPomodoroRunning().getValue())) {
            viewModel.stopPomodoro(false);
            Toast.makeText(getContext(), "Pomodoro megszakítva – háttérbe került a pomodoro menü", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PomodoroFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        timerText = view.findViewById(R.id.timerText);
        scoreText = view.findViewById(R.id.scoreText);
        startButton = view.findViewById(R.id.startButton);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        if (currentUser != null) {
            StudyRepository repository = new StudyRepository(requireContext());
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StudyViewModelFactory factory = new StudyViewModelFactory(requireActivity().getApplication(), userId, repository);
            viewModel = new ViewModelProvider(this, factory).get(StudyViewModel.class);

            viewModel.getRemainingTime().observe(getViewLifecycleOwner(), remainingTime -> {
                if (remainingTime != null) {
                    int minutes = (int) (remainingTime / 60);
                    int seconds = (int) (remainingTime % 60);
                    timerText.setText(String.format("%02d:%02d", minutes, seconds));
                    Log.d("PomodoroFragment", "Idozito frissitve: " + remainingTime + " masodperc");
                } else {
                    timerText.setText("25:00");
                    Log.d("PomodoroFragment", "Hatralevo ido Null");
                }
            });

            viewModel.getUserScore().observe(getViewLifecycleOwner(), userScore -> {
                if (userScore != null) {
                    scoreText.setText("Pontszám: " + userScore.getScore());
                    Log.d("PomodoroFragment", "Pontszám frissítve: " + userScore.getScore());
                } else {
                    scoreText.setText("Pontszám: 0");
                }
            });

            viewModel.isPomodoroRunning().observe(getViewLifecycleOwner(), isRunning -> {
                if (isRunning) {
                    startButton.setText("Tanulás befejezése");
                    timerText.setEnabled(false);
                } else {
                    startButton.setText("Tanulás elkezdése");
                    timerText.setEnabled(true);
                }
            });

            timerText.setOnClickListener(v -> {
                if (!viewModel.isPomodoroRunning().getValue()) {
                    showTimePickerDialog();
                }
            });

            startButton.setOnClickListener(v -> {
                if (viewModel.isPomodoroRunning().getValue()) {
                    viewModel.stopPomodoro();
                    Log.d("PomodoroFragment", "Pomodoro session leallitasa");
                } else {
                    viewModel.startPomodoro();
                    Log.d("PomodoroFragment", "Pomodoro session elkezdese");
                }
            });
        } else {
            Log.e("PomodoroFragment", "Nincs bejelentkezve felhasználó!");
            Toast.makeText(getContext(), "Nincs bejelentkezve felhasználó!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void showTimePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Pomodoro időtartam beállítása");

        final EditText input = new EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Perc (például: 25)");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String minutesStr = input.getText().toString();
            if (!minutesStr.isEmpty()) {
                try {
                    int minutes = Integer.parseInt(minutesStr);
                    if (minutes > 0) {
                        viewModel.setPomodoroDuration(minutes);
                        Toast.makeText(getContext(), "Időtartam beállítva: " + minutes + " perc", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Az időtartam pozitív szám kell legyen!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Érvénytelen számformátum!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Mégse", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}