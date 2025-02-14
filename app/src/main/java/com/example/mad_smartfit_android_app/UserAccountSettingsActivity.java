package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserAccountSettingsActivity extends AppCompatActivity {

    private TextInputEditText mUserName, mFullName, mEmail, mMobileNumber, mPassword;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_account_settings);

        // Set window insets so that the content doesn't overlap system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views from the layout
        mUserName = findViewById(R.id.tietUserName);
        mFullName = findViewById(R.id.tietFullName);
        mEmail = findViewById(R.id.tietEmail);
        mMobileNumber = findViewById(R.id.tietMoblieNumber);
        mPassword = findViewById(R.id.tietPassword);

        // Initialize Firebase Auth and Firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserAccountSettingsActivity.this, LoginActivity.class));
            finish();
            return;
        }
        userID = currentUser.getUid();

        // Load the user's current profile data from Firestore
        fStore.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        mUserName.setText(documentSnapshot.getString("userName"));
                        mFullName.setText(documentSnapshot.getString("fullName"));
                        mEmail.setText(documentSnapshot.getString("email"));
                        mMobileNumber.setText(documentSnapshot.getString("mobileNumber"));
                        // For security, do not pre-fill the password field.
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(UserAccountSettingsActivity.this,
                                "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // Called when the "Save" button is clicked
    public void onSaveClick(View view) {
        // Retrieve input values
        String newUserName = mUserName.getText().toString().trim();
        String newFullName = mFullName.getText().toString().trim();
        String newEmail = mEmail.getText().toString().trim();
        String newMobileNumber = mMobileNumber.getText().toString().trim();
        String newPassword = mPassword.getText().toString().trim();

        // Validate required fields (note that user type is not updated)
        if (TextUtils.isEmpty(newUserName)) {
            mUserName.setError("User Name is required.");
            Toast.makeText(this, "User Name is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(newFullName)) {
            mFullName.setError("Full Name is required.");
            Toast.makeText(this, "Full Name is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(newEmail)) {
            mEmail.setError("Email is required.");
            Toast.makeText(this, "Email is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(newMobileNumber)) {
            mMobileNumber.setError("Phone Number is required.");
            Toast.makeText(this, "Phone Number is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!TextUtils.isEmpty(newPassword) && newPassword.length() < 6) {
            mPassword.setError("Password must be >= 6 characters");
            Toast.makeText(this, "Password must be >= 6 characters", Toast.LENGTH_LONG).show();
            return;
        }

        // Prepare updated data (excluding userType)
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("userName", newUserName);
        updatedData.put("fullName", newFullName);
        updatedData.put("email", newEmail);
        updatedData.put("mobileNumber", newMobileNumber);

        // Update Firestore user document
        fStore.collection("users").document(userID)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    FirebaseUser user = fAuth.getCurrentUser();
                    if (user != null) {
                        // If the email was changed, update it in Firebase Auth
                        if (!newEmail.equals(user.getEmail())) {
                            user.updateEmail(newEmail)
                                    .addOnSuccessListener(aVoid1 -> {
                                        // Email updated successfully
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(UserAccountSettingsActivity.this,
                                                    "Email update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                        // If a new password was provided, update it in Firebase Auth
                        if (!TextUtils.isEmpty(newPassword)) {
                            user.updatePassword(newPassword)
                                    .addOnSuccessListener(aVoid12 ->
                                            Toast.makeText(UserAccountSettingsActivity.this,
                                                    "Profile updated successfully", Toast.LENGTH_LONG).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(UserAccountSettingsActivity.this,
                                                    "Password update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(UserAccountSettingsActivity.this,
                                    "Profile updated successfully", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(UserAccountSettingsActivity.this,
                                "Profile update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
    public void onHomeClick(View view) {
        startActivity(new Intent(getApplicationContext(), UserDashboardActivity.class));
        finish();
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        finish();
    }

    public void onWorkoutsClick(View view) {
        startActivity(new Intent(getApplicationContext(), WorkoutsActivity.class));
        finish();
    }
}
