package com.janos.nagy.ezlearnapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.data.model.Lesson;

import java.util.ArrayList;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    private static final String TAG = "LessonAdapter";
    private List<Lesson> lessons = new ArrayList<>();
    private OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson);
        void onLessonDelete(Lesson lesson); // Törlés esemény kezelése
    }

    public LessonAdapter(OnLessonClickListener listener) {
        Log.d(TAG, "LessonAdapter: created");
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called, position: " + position);
        Lesson lesson = lessons.get(position);
        holder.textView.setText(lesson.getTitle());

        // Törlés gomb hozzáadása
        holder.deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: deleteButton clicked for lesson: " + lesson.getTitle());
            if (listener != null) {
                listener.onLessonDelete(lesson);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: itemView clicked for lesson: " + lesson.getTitle());
            if (listener != null) {
                listener.onLessonClick(lesson);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called, size: " + lessons.size());
        return lessons.size();
    }

    public void setLessons(List<Lesson> lessons) {
        Log.d(TAG, "setLessons: called, size: " + lessons.size());
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button deleteButton; // Törlés gomb

        LessonViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "LessonViewHolder: created");
            textView = itemView.findViewById(R.id.lessonTitleTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton); // Gomb referencia
        }
    }
}