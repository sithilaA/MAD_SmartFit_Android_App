package com.example.mad_smartfit_android_app;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NutritionMealActivity extends AppCompatActivity {
    private RecyclerView mealRecyclerView;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_meal);

        // Initialize RecyclerView
        mealRecyclerView = findViewById(R.id.mealRecyclerView);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealAdapter = new MealAdapter(new ArrayList<>());
        mealRecyclerView.setAdapter(mealAdapter);

        // Fetch meal data from Firestore
        fetchMealData();
    }

    private void fetchMealData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("meals")
                .whereEqualTo("nutritionUserId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Meal> mealList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Meal meal = document.toObject(Meal.class);
                            mealList.add(meal);
                        }
                        mealAdapter.setMeals(mealList);
                    } else {
                        Log.d("NutritionMealActivity", "Error fetching meals: ", task.getException());
                    }
                });
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, NutritionSettingsActivity.class));
    }

    public void onHomeClick(View view) {
        startActivity(new Intent(this, NutritionistDashboardActivity.class));
    }

    public void onAddNewMealClick(View view) {
        startActivity(new Intent(this, NutritionAddMealActivity.class));
    }

    public void onAddNewMealToUserClick(View view) {
        startActivity(new Intent(this, AssignMealToUserActivity.class));
    }
}