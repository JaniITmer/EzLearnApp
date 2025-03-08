package com.janos.nagy.ezlearnapp.ui.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.LeaderboardViewModel;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

public class LeaderboardFragment extends Fragment {
    private LeaderboardViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        // Inicializáld a ViewModel-t
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new LeaderboardViewModel(new StudyRepository(requireContext().getApplicationContext()));
            }
        }).get(LeaderboardViewModel.class);

        // Figyeld meg a LiveData-t
        viewModel.getUserScore().observe(getViewLifecycleOwner(), userScore -> {
            if (userScore != null) {
                // Frissítsd a UI-t az adatokkal
                TextView scoreTextView = view.findViewById(R.id.score_text_view);
                scoreTextView.setText("Score: " + userScore.getScore());
            } else {
                // Kezeld az esetet, ha nincs adat
                Toast.makeText(getContext(), "No score found", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}