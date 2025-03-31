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

import com.google.firebase.auth.FirebaseAuth;
import com.janos.nagy.ezlearnapp.LessonAdapter;
import com.janos.nagy.ezlearnapp.LessonViewModel;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.data.model.Lesson;
import com.janos.nagy.ezlearnapp.repository.StudyRepository;

import java.io.File;

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

        adapter = new LessonAdapter(new LessonAdapter.OnLessonClickListener() {
            @Override
            public void onLessonClick(Lesson lesson) {
                openPdf(lesson.getFilePath());
            }

            @Override
            public void onLessonDelete(Lesson lesson) {
                showDeleteConfirmationDialog(lesson);
            }
        });
        recyclerView.setAdapter(adapter);


        StudyRepository repository = new StudyRepository(requireContext());
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        LessonViewModel.Factory factory = new LessonViewModel.Factory(
                requireActivity().getApplication(),
                repository,
                firebaseAuth
        );
        viewModel = new ViewModelProvider(this, factory).get(LessonViewModel.class);

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
                requireActivity().getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                Toast.makeText(getContext(), "A lecke neve nem lehet üres!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Mégse", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteConfirmationDialog(Lesson lesson) {
        new AlertDialog.Builder(getContext())
                .setTitle("Biztos, hogy törölni szeretnéd ezt a leckét?")
                .setPositiveButton("Igen", (dialog, which) -> {
                    viewModel.deleteLesson(lesson);
                    Toast.makeText(getContext(), "Lecke törölve!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Nem", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void openPdf(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(filePath), "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}