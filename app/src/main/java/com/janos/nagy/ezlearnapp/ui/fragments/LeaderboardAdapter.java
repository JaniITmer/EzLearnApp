package com.janos.nagy.ezlearnapp.ui.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.janos.nagy.ezlearnapp.R;
import com.janos.nagy.ezlearnapp.data.model.UserScore;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private final List<UserScore> userScores;
    private final String currentUserId;
    private final Context context;

    public LeaderboardAdapter(Context context, List<UserScore> userScores, String currentUserId) {
        this.context = context;
        this.userScores = userScores;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserScore userScore = userScores.get(position);
        holder.rankTextView.setText((position + 1) + ".");
        holder.nameTextView.setText(userScore.getName()); // Display the name
        holder.scoreTextView.setText("Pontszám: " + userScore.getScore());

        // Highlight the current user
        if (userScore.getUserId().equals(currentUserId)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return userScores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView, nameTextView, scoreTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rank_text_view);
            nameTextView = itemView.findViewById(R.id.name_text_view); // New TextView for name
            scoreTextView = itemView.findViewById(R.id.score_text_view);
        }
    }

    public void updateData(List<UserScore> newScores) {
        this.userScores.clear();
        this.userScores.addAll(newScores);
        notifyDataSetChanged();
    }
}