package com.example.mad_smartfit_android_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mad_smartfit_android_app.R;
import com.example.mad_smartfit_android_app.model.Workout;


import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;

    public WorkoutAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_card_layout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.txtWorkoutName.setText(workout.getWorkoutName());
        holder.txtCaloriesBurned.setText(String.valueOf(workout.getCaloriesBurned()+" KCal"));
        holder.txtDuration.setText(String.valueOf(workout.getDuration()+" Minits"));
        Glide.with(holder.itemView.getContext()).load(workout.getImageUrl()).into(holder.imgWorkout);
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView txtWorkoutName , txtCaloriesBurned, txtDuration;
        ImageView imgWorkout;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWorkoutName = itemView.findViewById(R.id.txtWorkoutName);
            imgWorkout = itemView.findViewById(R.id.imgWorkout);
            txtCaloriesBurned = itemView.findViewById(R.id.txtCaloriesBurned);
            txtDuration = itemView.findViewById(R.id.txtDuration);
        }
    }
}