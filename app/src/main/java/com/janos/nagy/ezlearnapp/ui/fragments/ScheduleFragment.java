package com.janos.nagy.ezlearnapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.ScheduleViewModel;

public class ScheduleFragment extends Fragment {
    private ScheduleViewModel viewModel;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        recyclerView = view.findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        viewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> adapter.setTasks(tasks));

        view.findViewById(R.id.addTaskButton).setOnClickListener(v -> viewModel.addTask("Új feladat", System.currentTimeMillis(), 2));

        return view;
    }
}