package com.janos.nagy.ezlearnapp.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.ScheduleViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScheduleFragment extends Fragment {
    private ScheduleViewModel viewModel;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Nincs bejelentkezett felhasználó!", Toast.LENGTH_SHORT).show();
            return view;
        }

        recyclerView = view.findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        viewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> adapter.setTasks(tasks));

        view.findViewById(R.id.addTaskButton).setOnClickListener(v -> showAddTaskDialog());

        return view;
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Új feladat hozzáadása");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.taskTitleEditText);
        EditText deadlineEditText = dialogView.findViewById(R.id.taskDeadlineEditText);
        EditText pomodoroCountEditText = dialogView.findViewById(R.id.taskPomodoroCountEditText);
        CheckBox completedCheckBox = dialogView.findViewById(R.id.taskCompletedCheckBox);

        builder.setPositiveButton("Hozzáadás", (dialog, which) -> {
            String title = titleEditText.getText().toString().trim();
            String deadlineStr = deadlineEditText.getText().toString().trim();
            String pomodoroCountStr = pomodoroCountEditText.getText().toString().trim();
            boolean isCompleted = completedCheckBox.isChecked();

            if (title.isEmpty() || deadlineStr.isEmpty()) {
                Toast.makeText(getContext(), "A név és a határidő kötelező!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date deadlineDate = sdf.parse(deadlineStr);
                long deadline = deadlineDate.getTime();

                int pomodoroCount = 1; // Alapértelmezett érték
                if (!pomodoroCountStr.isEmpty()) {
                    pomodoroCount = Integer.parseInt(pomodoroCountStr);
                    if (pomodoroCount <= 0) {
                        Toast.makeText(getContext(), "A Pomodoro szám pozitív kell legyen!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                viewModel.addTask(title, deadline, pomodoroCount, isCompleted);
                Toast.makeText(getContext(), "Feladat hozzáadva!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Hibás dátumformátum! (yyyy-MM-dd)", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Mégse", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}