package com.example.mad_smartfit_android_app;

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

public class AssignMealToUserActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private Spinner mMealSpinner, mUserSpinner;
    private List<String> userNameList, mealNameList, mealIdList, userIdList;
    private String selectedUserName = "", selectedMealName = "", selectedUserID = "", selectedMealtID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assign_meal_to_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mMealSpinner = findViewById(R.id.mealSpinner);
        mUserSpinner = findViewById(R.id.userSpinner);

        userNameList = new ArrayList<>();
        mealNameList = new ArrayList<>();
        userIdList = new ArrayList<>();
        mealIdList = new ArrayList<>();

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
        mMealSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMealtID = mealIdList.get(position);
                selectedMealName = mealNameList.get(position);
                Log.d("AssignWorkout", "Selected Workout: " + selectedMealName + ", ID: " + selectedMealtID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMealName = "";
                selectedMealtID = "";
            }
        });
        findViewById(R.id.mealInputLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.userInputLayout).setVisibility(View.VISIBLE);

        fetchUserFromFirestore();
        fetchMealFromFirestore();

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
    private void fetchMealFromFirestore() {
        Log.d("FetchWorkout", "Fetching Workout Start");
        fStore.collection("meals") // Ensure the collection name is correct
                .whereEqualTo("nutritionUserId", fAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mealIdList.clear();
                        mealNameList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String mealName = document.getString("mealName");
                            String mealId = document.getId(); // Correctly retrieve document ID

                            mealNameList.add(mealName);
                            mealIdList.add(mealId);
                            Log.d("FetchMeal", "Meal: " + mealName + ", ID: " + mealId);
                        }

                        // Populate the Workout Spinner
                        ArrayAdapter<String> adapterWorkout = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mealNameList);
                        adapterWorkout.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mMealSpinner.setAdapter(adapterWorkout); // Correct spinner assignment
                    } else {
                        Toast.makeText(this, "Failed to fetch workouts.", Toast.LENGTH_SHORT).show();
                        Log.e("FetchWorkout", "Error fetching workouts: " + task.getException());
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
        DocumentReference documentReference = fStore.collection("users-meal").document();
        Map<String, Object> userWorkout = new HashMap<>();
        userWorkout.put("mealID", selectedMealtID);
        userWorkout.put("userID", selectedUserID);
        userWorkout.put("mealName", selectedMealName);

        if (selectedUserName.isEmpty()) {
            Toast.makeText(this, "Please select a user", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedMealName.isEmpty()) {
            Toast.makeText(this, "Please select a Meal", Toast.LENGTH_SHORT).show();
            return;
        }

        documentReference.set(userWorkout).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("AssignMeal", "Assignment saved successfully");
                Toast.makeText(AssignMealToUserActivity.this, "Meal assigned successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("AssignMeal", "Error assigning workout: " + e.getMessage());
            Toast.makeText(AssignMealToUserActivity.this, "Failed to assign Meal.", Toast.LENGTH_SHORT).show();
        });

    }
}