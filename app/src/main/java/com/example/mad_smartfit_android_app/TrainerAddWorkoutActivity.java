package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.mad_smartfit_android_app.utils.NetworkUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrainerAddWorkoutActivity extends AppCompatActivity {
    TextInputEditText mWorkoutName, mDuration, mCaloriesBurned, mVideoLink, mNotes;
    ImageView ivWorkoutImage;
    Button btnUploadImage;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainer_add_workout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize Cloudinary
        initCloudinary();

        // Initialize views
        mWorkoutName = findViewById(R.id.tietWorkoutName);
        mDuration = findViewById(R.id.tietDuration);
        mCaloriesBurned = findViewById(R.id.tietCaloriesBurned);
        mVideoLink = findViewById(R.id.tietVideoLink);
        mNotes = findViewById(R.id.tietNotes);
        ivWorkoutImage = findViewById(R.id.ivWorkoutImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        // Set click listener for the upload image button
        btnUploadImage.setOnClickListener(v -> openImageChooser());

    }

    private void initCloudinary() {
        // Initialize Cloudinary with your cloud name, API key, and API secret
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "danvawddf");
        config.put("api_key", "363937876417461");
        config.put("api_secret", "Rwt0SN4j4CS72cUHcBflLPAixqY");
        MediaManager.init(this, config);
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivWorkoutImage.setImageURI(imageUri);
        }
    }

    public void onBackClick(View view) {
        super.onBackPressed();
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Close the current activity

    }

    public void onSubmitClick(View view) {
        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(TrainerAddWorkoutActivity.this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
            return;
        }

        String workoutName = this.mWorkoutName.getText().toString();
        int duration = Integer.parseInt(this.mDuration.getText().toString());
        int caloriesBurned = Integer.parseInt(this.mCaloriesBurned.getText().toString());
        String videoLink = this.mVideoLink.getText().toString();
        String notes = this.mNotes.getText().toString();

        if (workoutName.isEmpty()) {
            mWorkoutName.setError("Workout Name is required.");
            Toast.makeText(TrainerAddWorkoutActivity.this, "Workout Name is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (duration == 0) {
            mDuration.setError("Duration is required.");
            Toast.makeText(TrainerAddWorkoutActivity.this, "Duration is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (caloriesBurned == 0) {
            mCaloriesBurned.setError("Calories Burned is required.");
            Toast.makeText(TrainerAddWorkoutActivity.this, "Calories Burned is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (videoLink.isEmpty()) {
            mVideoLink.setError("Video Link is required.");
            Toast.makeText(TrainerAddWorkoutActivity.this, "Video Link is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (notes.isEmpty()) {
            mNotes.setError("Notes is required.");
            Toast.makeText(TrainerAddWorkoutActivity.this, "Notes is required.", Toast.LENGTH_LONG).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(TrainerAddWorkoutActivity.this, "Please upload an image.", Toast.LENGTH_LONG).show();
            return;
        }

        // Run a Firestore transaction to increment the ID and create the workout document
        fStore.runTransaction((Transaction.Function<Void>) transaction -> {


            // Upload the image to Cloudinary
            String requestId = MediaManager.get().upload(imageUri)
                    .option("public_id", "workout_" + UUID.randomUUID().toString())
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d("Cloudinary", "Upload started");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            Log.d("Cloudinary", "Upload in progress: " + bytes + "/" + totalBytes);
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            Log.d("Firestore", "Upload start: ");

                            // Get the secure URL of the uploaded image
                            String imageUrl = (String) resultData.get("secure_url");

                            // Prepare the workout data
                            Map<String, Object> workout = new HashMap<>();
                            workout.put("trainerUserId", fAuth.getCurrentUser().getUid());
                            workout.put("workoutName", workoutName);
                            workout.put("duration", duration);
                            workout.put("caloriesBurned", caloriesBurned);
                            workout.put("videoLink", videoLink);
                            workout.put("notes", notes);
                            workout.put("imageUrl", imageUrl);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference workoutRef = db.collection("workouts").document(); // Auto-generate ID

                            // Run Firestore transaction
                            db.runTransaction(transaction -> {
                                transaction.set(workoutRef, workout);
                                return null; // Transaction must return a value
                            }).addOnSuccessListener(aVoid -> {
                                Toast.makeText(TrainerAddWorkoutActivity.this, "Workout added successfully!", Toast.LENGTH_SHORT).show();
                                Log.d("Firestore", "Workout added with auto-incremented ID");
                                finish(); // Close the activity after successful submission
                            }).addOnFailureListener(e -> {
                                Log.e("Firestore", "Error adding workout", e);
                                Toast.makeText(TrainerAddWorkoutActivity.this, "Failed to add workout!", Toast.LENGTH_SHORT).show();
                            });
                        }


                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            // Handle failure
                            Toast.makeText(TrainerAddWorkoutActivity.this, "Failed to upload image: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                            Log.e("Cloudinary", "Error uploading image");
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            // Handle reschedule
                            Log.d("Cloudinary", "Upload rescheduled");
                        }
                    })
                    .dispatch();

            return null;
        }).addOnFailureListener(e -> {
            // Handle failure
            Toast.makeText(TrainerAddWorkoutActivity.this, "Failed to add workout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Firestore", "Error adding workout", e);
        });
    }
}