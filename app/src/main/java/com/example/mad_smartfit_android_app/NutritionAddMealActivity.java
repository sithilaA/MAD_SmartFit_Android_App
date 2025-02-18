package com.example.mad_smartfit_android_app;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mad_smartfit_android_app.utils.NetworkUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class NutritionAddMealActivity extends AppCompatActivity {
    TextInputEditText mMealName, mIngredients, mNotes;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutrition_add_meal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mMealName = findViewById(R.id.tietMealName);
        mIngredients = findViewById(R.id.tietIngredients);
        mNotes = findViewById(R.id.tietNotes);
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
    public void onSubmitClick(View view) {
        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(NutritionAddMealActivity.this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
            return;
        }

        String mealName = this.mMealName.getText().toString();
        String ingredients = this.mIngredients.getText().toString();
        String notes = this.mNotes.getText().toString();


        if (mealName.isEmpty()) {
            mMealName.setError("Meal Name is required.");
            Toast.makeText(NutritionAddMealActivity.this, "Meal Name is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (ingredients.isEmpty()) {
            mIngredients.setError("Ingredients is required.");
            Toast.makeText(NutritionAddMealActivity.this, "Ingredients is required.", Toast.LENGTH_LONG).show();
            return;
        }

        if (notes.isEmpty()) {
            mNotes.setError("Notes is required.");
            Toast.makeText(NutritionAddMealActivity.this, "Notes is required.", Toast.LENGTH_LONG).show();
            return;
        }

        // Run a Firestore transaction to increment the ID and create the workout document
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference mealRef = fStore.collection("meals").document(); // Auto-generate meal ID

        fStore.runTransaction(transaction -> {
            // Fetch document if it exists (optional, useful if updating)
            DocumentSnapshot snapshot = transaction.get(mealRef);

            // Prepare the meal data
            Map<String, Object> meal = new HashMap<>();
            meal.put("nutritionUserId", fAuth.getCurrentUser().getUid());
            meal.put("mealName", mealName);
            meal.put("ingredients", ingredients);
            meal.put("notes", notes);

            // Write data to Firestore
            transaction.set(mealRef, meal);
            return null; // Must return a value
        }).addOnSuccessListener(aVoid -> {
            // Success
            Toast.makeText(NutritionAddMealActivity.this, "Meal Plan added successfully!", Toast.LENGTH_SHORT).show();
            Log.d("Firestore", "Meal Plan added with auto-incremented ID");
            finish(); // Close activity after success
        }).addOnFailureListener(e -> {
            // Handle failure
            Toast.makeText(NutritionAddMealActivity.this, "Failed to add Meal Plan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Firestore", "Error adding Meal Plan", e);
        });

    }
}