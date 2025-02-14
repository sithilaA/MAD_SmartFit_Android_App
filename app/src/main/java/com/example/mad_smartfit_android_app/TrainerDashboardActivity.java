package com.example.mad_smartfit_android_app;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrainerDashboardActivity extends AppCompatActivity {
    private TextView userGreetingText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LinearLayout newsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainer_dashboard);
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

        // Fetch and display username
        getUserName();

        newsContainer = findViewById(R.id.news_container);

        fetchNewsData();

    }

    public void onWorkoutClick(View view) {
        startActivity(new Intent(getApplicationContext(),TrainerWorkoutActivity.class));

    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(getApplicationContext(),TrainerSettingsActivity.class));
        finish();
    }

    public void onPendingAppointmentClick(View view) {
        startActivity(new Intent(getApplicationContext(), PendingAppointmentsActivity.class));

    }
    public void onTodayAppointmentClick(View view) {
        startActivity(new Intent(getApplicationContext(), TodayAppointmentActivity.class));

    }
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

                            } else {
                                Toast.makeText(TrainerDashboardActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(TrainerDashboardActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
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
}