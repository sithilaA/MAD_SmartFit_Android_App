package com.example.mad_smartfit_android_app;

import static com.google.firebase.auth.AuthKt.getAuth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mad_smartfit_android_app.model.WorkoutHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;

public class WorkoutSessionActivity extends AppCompatActivity {

    private TextView workoutTitle, workoutDescription, countdownTimer;
    private ImageView workoutImage;
    private Button startButton, endButton;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private String workoutId;
    private CountDownTimer timer;
    private long startTime;
    String videoLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);

        // Initialize views
        workoutTitle = findViewById(R.id.workoutTitle);
        workoutDescription = findViewById(R.id.workoutDescription);
        workoutImage = findViewById(R.id.workoutImage);
        startButton = findViewById(R.id.startButton);
        endButton = findViewById(R.id.endButton);
        countdownTimer = findViewById(R.id.countdownTimer);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        // Get workout ID from intent
        workoutId = getIntent().getStringExtra("WORKOUT_ID");
        if (workoutId != null) {
            fetchWorkoutData(workoutId);
        }

        // Set click listeners
        startButton.setOnClickListener(v -> startWorkout());
        endButton.setOnClickListener(v -> endWorkout());
    }

    private void fetchWorkoutData(String workoutId) {
        db.collection("workouts").document(workoutId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Update UI with workout data
                    workoutTitle.setText(document.getString("workoutName"));
                    workoutDescription.setText(document.getString("notes"));
                    videoLink = document.getString("videoLink");
                    Picasso.get().load(document.getString("imageUrl")).into(workoutImage);
                }
            }
        });
    }

    private void startWorkout() {
        // Hide Start button and show End button and countdown timer
        startButton.setVisibility(View.GONE);
        endButton.setVisibility(View.VISIBLE);
        countdownTimer.setVisibility(View.VISIBLE);

        // Start countdown timer
        startTime = System.currentTimeMillis();
        timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                countdownTimer.setText(formatTime(elapsedTime));
            }

            @Override
            public void onFinish() {
                // Not used
            }
        }.start();
    }

    @SuppressLint("NewApi")
    private void endWorkout() {
        // Stop the timer
        if (timer != null) {
            timer.cancel();
        }

        // Calculate workout duration
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        LocalDate currentDate = LocalDate.now();
        Log.d("Current Date", currentDate.toString());
        String currentDateString = currentDate.toString();
        final String userId = fAuth.getCurrentUser().getUid();

        // Send workout completion data to Firebase
        db.collection("user-workout-history").add(new WorkoutHistory(workoutId, duration,userId ,currentDateString))
                .addOnSuccessListener(documentReference -> finish())
                .addOnFailureListener(e -> finish());
    }

    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Invalid Zoom link", Toast.LENGTH_SHORT).show();
            return;
        }
        // Verify there is an app that can handle this intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "No app found to open the link", Toast.LENGTH_SHORT).show();
        }
    }

    public void onYoutubeClick(View view) {
        if (videoLink.isEmpty()) {
            Toast.makeText(this, "No zoom link available", Toast.LENGTH_SHORT).show();
        }
        else {
            openLink(videoLink);
        }
    }
    public void onBackClick(View view) {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onSosClick(View view) {
        startActivity(new Intent(getApplicationContext(), SOSCallActivity.class));
        finish();
    }
}