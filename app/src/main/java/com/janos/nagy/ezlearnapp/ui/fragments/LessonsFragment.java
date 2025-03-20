package com.janos.nagy.ezlearnapp.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.janos.nagy.ezlearnapp.LessonAdapter;
import com.janos.nagy.ezlearnapp.LessonViewModel;
import com.janos.nagy.ezlearnapp.R;

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                showLessonNameDialog(fileUri.toString());
            }
        }
    }

    private void showLessonNameDialog(String filePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add meg a leckének a nevét");

        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("Mentés", (dialog, which) -> {
            String lessonTitle = input.getText().toString().trim();
            if (!lessonTitle.isEmpty()) {
                viewModel.addLesson(lessonTitle, filePath);
                Toast.makeText(getContext(), "Lecke elmentve!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "A új leckéd neve nem lehet üres!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Mégse", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}