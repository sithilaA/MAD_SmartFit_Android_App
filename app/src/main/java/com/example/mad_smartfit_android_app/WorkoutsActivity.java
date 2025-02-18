package com.example.mad_smartfit_android_app;

import static com.example.mad_smartfit_android_app.R.layout.activity_workouts;

import static java.lang.Math.toIntExact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsActivity extends AppCompatActivity {

    private FirebaseAuth fAuth ;
    private FirebaseFirestore fStore ;
    private TextView workoutName , caloriesBurned , duration ;
    private Spinner workoutSpinner ;
    private ImageView workoutImg ;
    private List<String> workoutNameList , workoutIdList  ;
    private String selectedWorkoutName = "" ,  selectedWorkoutID = "", selectedWorkoutImg = "" , selectedWorkoutNote = "";
    private Long selectedCaloriesBurned , selectedDuration ;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(activity_workouts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        workoutName = findViewById(R.id.tvWorkoutName);
        caloriesBurned = findViewById(R.id.tvCaloriesBurned);
        duration = findViewById(R.id.tvDuration);
        workoutSpinner = findViewById(R.id.workoutSpinner);
        workoutImg = findViewById(R.id.imgWorkout);



        workoutNameList = new ArrayList<>();
        workoutIdList = new ArrayList<>();

        workoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWorkoutID = workoutIdList.get(position);
                selectedWorkoutName = workoutNameList.get(position);
                fetchWorkoutsFromFirestore();

                    workoutName.setText(selectedWorkoutName );
                    caloriesBurned.setText(String.valueOf(selectedCaloriesBurned)+" Kcal");
                    duration.setText(String.valueOf(selectedDuration)+" Minutes");
                    //Picasso.get().load(selectedWorkoutImg).into(workoutImg);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedWorkoutID = "";
                selectedWorkoutName = "";
            }
        });

        fetchUserWorkoutsFromFirestore();
    }
    private void fetchUserWorkoutsFromFirestore() {
        Log.d("fetchWorkouts", "Fetching Appointments Start ");
        fStore.collection("users-workout")
                .whereEqualTo("userID",fAuth.getCurrentUser().getUid() ) // Assuming trainers have a "role" field set to "trainer"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        workoutNameList.clear();
                        workoutIdList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String workoutName = document.getString("workoutName");
                            String workoutID = document.getString("workoutID");

                            Log.d("Workout Name",workoutName );
                            Log.d("Workout ID",workoutID );
                            workoutNameList.add(workoutName);
                            workoutIdList.add(workoutID);
                        }
                        // Populate the Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workoutNameList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        workoutSpinner.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to fetch trainers.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void fetchWorkoutsFromFirestore() {
        Log.d("fetchWorkouts", "Fetching Workouts Start");

        if (selectedWorkoutID == null || selectedWorkoutID.isEmpty()) {
            Log.e("fetchWorkouts", "Invalid Workout ID");
            Toast.makeText(getApplicationContext(), "Invalid Workout ID", Toast.LENGTH_SHORT).show();
            return;
        }

        fStore.collection("workouts")
                .whereEqualTo(FieldPath.documentId(), selectedWorkoutID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            selectedWorkoutName = document.getString("workoutName");
                            selectedDuration = document.getLong("duration");
                            selectedCaloriesBurned = document.getLong("caloriesBurned");
                            selectedWorkoutImg = document.getString("imageUrl");

                            Log.d("fetchWorkouts", "Workout Retrieved: " + selectedWorkoutName);
                            Log.d("fetchWorkouts", "Calories Burned: " + selectedCaloriesBurned);
                            Log.d("fetchWorkouts", "Duration: " + selectedDuration);
                            Log.d("fetchWorkouts", "Image URL: " + selectedWorkoutImg);
                        }
                    } else {
                        Log.e("fetchWorkouts", "No matching workout found");
                        runOnUiThread(() ->
                                Toast.makeText(getApplicationContext(), "Workout not found.", Toast.LENGTH_SHORT).show()
                        );
                    }
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
    public void onStartClick(View view) {
        Intent intent = new Intent(this, WorkoutSessionActivity.class);
        intent.putExtra("WORKOUT_ID", selectedWorkoutID);
        startActivity(intent);
    }
}