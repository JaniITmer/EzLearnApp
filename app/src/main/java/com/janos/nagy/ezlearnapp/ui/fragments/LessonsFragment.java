package com.janos.nagy.ezlearnapp.ui.fragments;

import android.app.Activity;  // Hozzáadott import
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.janos.nagy.ezlearnapp.LessonAdapter;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.LessonViewModel;  // Módosított import (viewmodel kisbetűs)

public class LessonsFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    private LessonViewModel viewModel;
    private RecyclerView recyclerView;
    private LessonAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lessons, container, false);

        recyclerView = view.findViewById(R.id.lessonRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LessonAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(LessonViewModel.class);
        viewModel.getLessons().observe(getViewLifecycleOwner(), lessons -> adapter.setLessons(lessons));

        view.findViewById(R.id.uploadButton).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, REQUEST_CODE);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {  // Most feloldódik
            String filePath = data.getData().toString();
            viewModel.addLesson("Új lecke", filePath);
        }
    }
}