package com.janos.nagy.ezlearnapp.ui.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.data.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String deadlineStr = sdf.format(new Date(task.getStartTime()));
        String status = task.isCompleted() ? "Teljesítve" : "Nincs teljesítve";
        holder.textView.setText(task.getTitle() + " (Határidő: " + deadlineStr + ", " + task.getPomodoroCount() + " Pomodoro, " + status + ")");
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        TaskViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.taskTextView);
        }
    }
}