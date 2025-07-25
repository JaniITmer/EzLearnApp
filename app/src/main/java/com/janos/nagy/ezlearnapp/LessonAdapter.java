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
        void onLessonDelete(Lesson lesson);
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
        Log.d(TAG, "onBindViewHolder: megívva, hely " + position);
        Lesson lesson = lessons.get(position);
        holder.textView.setText(lesson.getTitle());


        holder.deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: deleteButton kattintva  leckére:" + lesson.getTitle());
            if (listener != null) {
                listener.onLessonDelete(lesson);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: itemView kattintva leckére: " + lesson.getTitle());
            if (listener != null) {
                listener.onLessonClick(lesson);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: meghívva, size: " + lessons.size());
        return lessons.size();
    }

    public void setLessons(List<Lesson> lessons) {
        Log.d(TAG, "setLessons: meghívva, size: " + lessons.size());
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button deleteButton;

        LessonViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "LessonViewHolder: létrehozva");
            textView = itemView.findViewById(R.id.lessonTitleTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}