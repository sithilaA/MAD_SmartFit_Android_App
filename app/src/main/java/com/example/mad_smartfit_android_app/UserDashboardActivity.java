package com.example.mad_smartfit_android_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mad_smartfit_android_app.model.NewsItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserDashboardActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TextView userGreetingText, tvFinishedWorkouts, tvTimeSpent;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LinearLayout newsContainer;
    String userName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Reference to TextView
        userGreetingText = findViewById(R.id.userGreetingText);
        tvFinishedWorkouts = findViewById(R.id.tvFinishedWorkouts);
        tvTimeSpent = findViewById(R.id.tvTimeSpent);

        // Fetch and display username
        getUserName();

        newsContainer = findViewById(R.id.news_container);

        fetchNewsData();
        getTodaysWorkoutHistoryCount(tvFinishedWorkouts);
        getTodaysWorkoutSummary(tvTimeSpent);
    }



    public void onSettingsClick(View view) {
        startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        finish();
    }
    public void onBookAppointmentClick(View view) {
        Intent intent = new Intent(UserDashboardActivity.this, AppointmentActivity.class);
        intent.putExtra("USER_NAME", userName);
        startActivity(intent);

    }
    public void onNutritionPlanClick(View view) {
        startActivity(new Intent(getApplicationContext(),NutritionPlanActivity.class));

    }
    public void onWorkoutClick(View view) {
        startActivity(new Intent(getApplicationContext(),WorkoutsActivity.class));
        finish();
    }
    //Set userName to TextView
    private void getUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("userName");
                                if (username == null) {
                                    username = "";
                                }
                                userGreetingText.setText("Welcome, " + username );
                                userName = username;
                            } else {
                                Toast.makeText(UserDashboardActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserDashboardActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
        }
    }
    private void fetchNewsData() {
        db.collection("news_feed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<NewsItem> newsItems = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            NewsItem newsItem = document.toObject(NewsItem.class);
                            newsItems.add(newsItem);
                        }
                        populateNews(newsItems);
                    } else {

                    }
                });
    }
    private void populateNews(List<NewsItem> newsItems) {
        for (NewsItem newsItem : newsItems) {
            View newsItemView = LayoutInflater.from(this).inflate(R.layout.news_item_layout, null);

            TextView titleTextView = newsItemView.findViewById(R.id.news_title);
            TextView descriptionTextView = newsItemView.findViewById(R.id.news_description);

            titleTextView.setText(newsItem.getTitle());
            descriptionTextView.setText(newsItem.getDescription());

            newsContainer.addView(newsItemView);
        }
    }

    public void onTextViewClick(View view) {
        Toast.makeText(this, "TextView clicked!", Toast.LENGTH_SHORT).show();
    }

    public void getTodaysWorkoutHistoryCount(TextView textView) {
        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get today's date in the format you store in Firestore (e.g., "yyyy-MM-dd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());

        // Reference to the Firestore collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query to filter records
        Query query = db.collection("user-workout-history")
                .whereEqualTo("userId", userId) // Match the current user's ID
                .whereEqualTo("dateWorkoutDone", todayDate); // Match today's date

        // Execute the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    int count = querySnapshot.size(); // Get the count of matching records
                    // Set the count to the TextView
                    textView.setText(String.valueOf(count));
                } else {
                    // No records found for today
                    textView.setText("00");
                }
            } else {
                // Handle errors
                textView.setText("Error fetching records");
                System.out.println("Error fetching records: " + task.getException().getMessage());
            }
        });
    }

    public void getTodaysWorkoutSummary(TextView textView) {
        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get today's date in the format you store in Firestore (e.g., "yyyy-MM-dd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());

        // Reference to the Firestore collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query to filter records
        Query query = db.collection("user-workout-history")
                .whereEqualTo("userId", userId) // Match the current user's ID
                .whereEqualTo("dateWorkoutDone", todayDate); // Match today's date

        // Execute the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    int totalWorkouts = querySnapshot.size(); // Total number of workouts
                    long totalDurationMillis = 0;

                    // Calculate total duration in milliseconds
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        long duration = document.getLong("duration");
                        totalDurationMillis += duration;
                    }

                    // Convert total duration to minutes
                    long totalDurationMinutes = totalDurationMillis / (1000 * 60);

                    // Update the TextView
                    String summary =  String.valueOf(totalDurationMinutes);
                    textView.setText(summary);
                } else {
                    // No records found for today
                    textView.setText("00");
                }
            } else {
                // Handle errors
                textView.setText("Error fetching records");
                System.out.println("Error fetching records: " + task.getException().getMessage());
            }
        });
    }

}