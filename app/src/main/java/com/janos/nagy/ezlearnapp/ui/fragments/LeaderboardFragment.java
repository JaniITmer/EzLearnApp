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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janos.nagy.ezlearnapp.LeaderboardViewModelFactory;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.LeaderboardViewModel;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

public class LeaderboardFragment extends Fragment {
    private LeaderboardViewModel viewModel;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "Nincs bejelentkezett felhasználó!", Toast.LENGTH_SHORT).show();
            return view;
        }

        String userId = user.getUid();
        viewModel = new ViewModelProvider(this, new LeaderboardViewModelFactory(requireContext().getApplicationContext()))
                .get(LeaderboardViewModel.class);

        recyclerView = view.findViewById(R.id.leaderboard_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getLeaderboard().observe(getViewLifecycleOwner(), userScores -> {
            adapter = new LeaderboardAdapter(getContext(), userScores, userId);
            recyclerView.setAdapter(adapter);
        });

        return view;
    }
}