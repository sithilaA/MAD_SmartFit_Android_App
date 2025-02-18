package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class NutritionSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutrition_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onSignOutClick(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void onHomeClick(View view) {
        startActivity(new Intent(getApplicationContext(), NutritionistDashboardActivity.class));
        finish();
    }
    public void onNutritionClick(View view) {
        startActivity(new Intent(getApplicationContext(), NutritionMealActivity.class));
        finish();
    }

    public void onSettingsClick(View view){
        startActivity(new Intent(getApplicationContext(),UserAccountSettingsActivity.class));
        finish();
    }

    public void onAboutClick(View view) {
        startActivity(new Intent(getApplicationContext(),AboutUS.class));

    }
}