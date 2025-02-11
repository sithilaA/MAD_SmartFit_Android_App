package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_smartfit_android_app.adapter.WorkoutAdapter;
import com.example.mad_smartfit_android_app.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;

public class TrainerWorkoutActivity extends AppCompatActivity {

    private RecyclerView workoutRecyclerView;
    private WorkoutAdapter workoutAdapter;
    private List<Workout> workoutList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainer_workout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        workoutRecyclerView = findViewById(R.id.workoutRecyclerView);
        workoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>();
        workoutAdapter = new WorkoutAdapter(workoutList);
        workoutRecyclerView.setAdapter(workoutAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        fetchWorkouts();
    }

    private void fetchWorkouts() {
        String currentUserId = auth.getCurrentUser().getUid();
        db.collection("workouts")
                .whereEqualTo("trainerUserId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        workoutList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Workout workout = document.toObject(Workout.class);
                            workoutList.add(workout);
                        }
                        workoutAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void onHomeClick(View view) {
        startActivity(new Intent(getApplicationContext(), TrainerDashboardActivity.class));
        finish();
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(getApplicationContext(), TrainerSettingsActivity.class));
        finish();
    }

    public void onAddWorkoutClick(View view) {
        startActivity(new Intent(getApplicationContext(), TrainerAddWorkoutActivity.class));
    }

    public void onAddWorkoutToUserClick(View view) {
        startActivity(new Intent(getApplicationContext(), AssignWorkoutToUserActivity.class));
    }
}