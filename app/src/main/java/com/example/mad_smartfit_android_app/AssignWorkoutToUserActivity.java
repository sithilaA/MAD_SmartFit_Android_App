package com.example.mad_smartfit_android_app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignWorkoutToUserActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private Spinner mWorkoutSpinner, mUserSpinner;
    private List<String> userNameList, workoutNameList, userIdList, workoutIdList;
    private String selectedUserName = "", selectedWorkoutName = "", selectedUserID = "", selectedWorkoutID = "";
    private static final String PREFS_NAME = "reminder_prefs";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assign_workout_to_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mWorkoutSpinner = findViewById(R.id.workoutSpinner);
        mUserSpinner = findViewById(R.id.userSpinner);

        userNameList = new ArrayList<>();
        workoutNameList = new ArrayList<>();
        userIdList = new ArrayList<>();
        workoutIdList = new ArrayList<>();

        mUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUserID = userIdList.get(position);
                selectedUserName = userNameList.get(position);
                Log.d("AssignWorkout", "Selected User: " + selectedUserName + ", ID: " + selectedUserID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUserName = "";
                selectedUserID = "";
            }
        });

        mWorkoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWorkoutID = workoutIdList.get(position);
                selectedWorkoutName = workoutNameList.get(position);
                Log.d("AssignWorkout", "Selected Workout: " + selectedWorkoutName + ", ID: " + selectedWorkoutID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedWorkoutName = "";
                selectedWorkoutID = "";
            }
        });
        findViewById(R.id.workoutInputLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.userInputLayout).setVisibility(View.VISIBLE);

        fetchUserFromFirestore();
        fetchWorkoutFromFirestore();
    }

    private void fetchWorkoutFromFirestore() {
        Log.d("FetchWorkout", "Fetching Workout Start");
        fStore.collection("workouts") // Ensure the collection name is correct
                .whereEqualTo("trainerUserId", fAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        workoutNameList.clear();
                        workoutIdList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String workoutName = document.getString("workoutName");
                            String workoutID = document.getId(); // Correctly retrieve document ID

                            workoutNameList.add(workoutName);
                            workoutIdList.add(workoutID);
                            Log.d("FetchWorkout", "Workout: " + workoutName + ", ID: " + workoutID);
                        }

                        // Populate the Workout Spinner
                        ArrayAdapter<String> adapterWorkout = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workoutNameList);
                        adapterWorkout.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mWorkoutSpinner.setAdapter(adapterWorkout); // Correct spinner assignment
                    } else {
                        Toast.makeText(this, "Failed to fetch workouts.", Toast.LENGTH_SHORT).show();
                        Log.e("FetchWorkout", "Error fetching workouts: " + task.getException());
                    }
                });
    }

    private void fetchUserFromFirestore() {
        Log.d("FetchUser", "Fetching User Start");
        fStore.collection("appointments")
                .whereEqualTo("appointmentConductorUserID", fAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userNameList.clear();
                        userIdList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userName = document.getString("userName");
                            String userID = document.getString("userID");
                            Log.d("FetchUser", "User: " + userName + ", ID: " + userID);

                            userNameList.add(userName);
                            userIdList.add(userID);
                        }
                        // Populate the User Spinner
                        ArrayAdapter<String> adapterUser = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userNameList); // Use userNameList
                        adapterUser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mUserSpinner.setAdapter(adapterUser);
                    } else {
                        Toast.makeText(this, "Failed to fetch users.", Toast.LENGTH_SHORT).show();
                        Log.e("FetchUser", "Error fetching users: " + task.getException());
                    }
                });
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
        DocumentReference documentReference = fStore.collection("users-workout").document();
        Map<String, Object> userWorkout = new HashMap<>();
        userWorkout.put("workoutID", selectedWorkoutID);
        userWorkout.put("userID", selectedUserID);
        userWorkout.put("workoutName", selectedWorkoutName);

        if (selectedUserName.isEmpty()) {
            Toast.makeText(this, "Please select a user", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedWorkoutName.isEmpty()) {
            Toast.makeText(this, "Please select a workout", Toast.LENGTH_SHORT).show();
            return;
        }

        documentReference.set(userWorkout).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Save workout reminder details in SharedPreferences
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("workout_reminder_enabled", true);
                editor.putString("workout_reminder_details", selectedWorkoutName + " for " + selectedUserName);
                editor.apply();

                Log.d("AssignWorkout", "Assignment saved successfully");
                Toast.makeText(AssignWorkoutToUserActivity.this, "Workout assigned successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("AssignWorkout", "Error assigning workout: " + e.getMessage());
            Toast.makeText(AssignWorkoutToUserActivity.this, "Failed to assign workout.", Toast.LENGTH_SHORT).show();
        });
    }
}