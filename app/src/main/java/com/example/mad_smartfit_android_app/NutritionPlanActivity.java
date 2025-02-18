package com.example.mad_smartfit_android_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_smartfit_android_app.adapter.MealAdapter;
import com.example.mad_smartfit_android_app.model.Meal;
import com.example.mad_smartfit_android_app.model.UserMeal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NutritionPlanActivity extends AppCompatActivity {
    private RecyclerView mealRecyclerView;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_plan);

        // Initialize RecyclerView
        mealRecyclerView = findViewById(R.id.mealRecyclerView);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealAdapter = new MealAdapter(new ArrayList<>());
        mealRecyclerView.setAdapter(mealAdapter);

        // Fetch meal data from Firestore
        fetchUserMeals();
    }

    private void fetchUserMeals() {
        Log.d("NutritionPlanActivity", "Fetching user meals...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("NutritionPlanActivity", "Current User ID: " + currentUserId);
        // Step 1: Fetch meal IDs from users-meal collection
        db.collection("users-meal")
                .whereEqualTo("userID", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> mealIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserMeal userMeal = document.toObject(UserMeal.class);
                            mealIds.add(userMeal.getMealID());
                            Log.d("NutritionPlanActivity", "Meal ID: " + userMeal.getMealID());
                        }
                        // Step 2: Fetch meal details from meals collection
                        fetchMealDetails(mealIds);
                    } else {
                        Log.d("NutritionPlanActivity", "Error fetching user meals: ", task.getException());
                    }
                });
    }

    private void fetchMealDetails(List<String> mealIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Meal> mealList = new ArrayList<>();

        for (String mealId : mealIds) {
            db.collection("meals")
                    .document(mealId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Meal meal = documentSnapshot.toObject(Meal.class);
                        if (meal != null) {
                            mealList.add(meal);
                            mealAdapter.setMeals(mealList);
                        }
                    })
                    .addOnFailureListener(e -> Log.d("NutritionPlanActivity", "Error fetching meal details: ", e));
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
}