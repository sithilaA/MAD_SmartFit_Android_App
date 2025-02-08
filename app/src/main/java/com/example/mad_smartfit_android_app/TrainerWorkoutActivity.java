package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TrainerWorkoutActivity extends AppCompatActivity {

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