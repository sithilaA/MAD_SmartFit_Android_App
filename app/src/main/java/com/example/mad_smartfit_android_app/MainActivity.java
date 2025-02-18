package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserType(currentUser.getUid());
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            // Redirect to Login Activity if no user is logged in
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void checkUserType(String userId) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();
                String userType = document.getString("userType");
                if (userType != null) {
                    switch (userType) {
                        case "Client": startActivity(new Intent(MainActivity.this, UserDashboardActivity.class));
                        finish();
                        break;
                        case "Trainer": startActivity(new Intent(MainActivity.this, TrainerDashboardActivity.class));
                        finish();
                        break;
                        case "Nutrition": startActivity(new Intent(MainActivity.this, NutritionistDashboardActivity.class));
                        finish();
                        break; default: Toast.makeText(MainActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                    }
                    finish(); // Close the current activity
                }
                else {
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }
            } else {
                Log.e(TAG, "Error getting document", task.getException());
                Toast.makeText(MainActivity.this, "Failed to retrieve user type", Toast.LENGTH_SHORT).show();
            }
        });
    }
}