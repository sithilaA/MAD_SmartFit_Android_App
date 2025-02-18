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

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
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
        startActivity(new Intent(getApplicationContext(),UserDashboardActivity.class));
        finish();
    }

    public void onWorkoutClick(View view) {
        startActivity(new Intent(getApplicationContext(),WorkoutsActivity.class));
        finish();
    }
    public void onSettingsClick(View view){
        startActivity(new Intent(getApplicationContext(),UserAccountSettingsActivity.class));
    }
    public void onNotificationClick(View view){
        startActivity(new Intent(getApplicationContext(),UserNotificationSettingsActivity.class));

    }
    public void onSosClick(View view){
        startActivity(new Intent(getApplicationContext(),SosSettingActivity.class));
    }

    public void onAboutClick(View view) {
        startActivity(new Intent(getApplicationContext(),AboutUS.class));

    }
}