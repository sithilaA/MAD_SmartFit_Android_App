package com.example.mad_smartfit_android_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WorkoutsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workouts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onHomeClick(View view) {
        startActivity(new Intent(getApplicationContext(),UserDashboardActivity.class));
        finish();
    }

    public void onWorkoutsClick(View view) {

    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        finish();
    }

    public void onWorkoutCardClick(View view) {
        @SuppressLint("ShowToast") Toast toast = Toast.makeText(this, "Workout clicked!", Toast.LENGTH_SHORT);
    }
}