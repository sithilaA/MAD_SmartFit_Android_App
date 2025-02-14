package com.example.mad_smartfit_android_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SosSettingActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;

    private TextInputEditText mEmergencyServicesNumber , mEmergencyContactNumber01 , mEmergencyContactNumber02 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sos_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mEmergencyServicesNumber = findViewById(R.id.tietESNumber);
        mEmergencyContactNumber01 = findViewById(R.id.tietECNumber01);
        mEmergencyContactNumber02 = findViewById(R.id.tietECNumber02);

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

    public void onSubmitClick(View view) {
        String EmergencyServicesNumber = mEmergencyServicesNumber.getText().toString().trim();
        String EmergencyContactNumber01 = mEmergencyContactNumber01.getText().toString().trim();
        String EmergencyContactNumber02 = mEmergencyContactNumber02.getText().toString().trim();

        // Validate inputs
        if (EmergencyServicesNumber.isEmpty()) {
            mEmergencyServicesNumber.setError("Emergency Services Number is required.");
            Toast.makeText(SosSettingActivity.this, "Emergency Services Number is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (EmergencyContactNumber01.isEmpty()) {
            mEmergencyContactNumber01.setError("Emergency Contact Number 01 is required.");
            Toast.makeText(SosSettingActivity.this, "Emergency Contact Number 01 is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (EmergencyContactNumber02.isEmpty()) {
            mEmergencyContactNumber02.setError("Emergency Contact Number 02 is required.");
            Toast.makeText(SosSettingActivity.this, "Emergency Contact Number 02 is required.", Toast.LENGTH_LONG).show();
            return;
        }
        String userID = fAuth.getCurrentUser().getUid();
        if (userID != null) {
            DocumentReference documentReference = fStore.collection("users").document(userID);

            Map<String, Object> updates = new HashMap<>();
            updates.put("EmergencyServicesNumber", EmergencyServicesNumber);
            updates.put("EmergencyContactNumber01", EmergencyContactNumber01);
            updates.put("EmergencyContactNumber02", EmergencyContactNumber02);

            // Use update() instead of set() to keep existing fields
            documentReference.update(updates)
                    .addOnSuccessListener(unused -> {
                        Log.d("TAG", "User SOS Setting Updated: " + userID);
                        Toast.makeText(SosSettingActivity.this, "User SOS Setting Updated", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TAG", "Error updating User SOS Setting: ", e);
                        Toast.makeText(SosSettingActivity.this, "Error updating settings", Toast.LENGTH_LONG).show();
                    });
        } else {
            Log.e("TAG", "Error: No authenticated user found.");
        }
    }

}