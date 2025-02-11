package com.example.mad_smartfit_android_app;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class SOSCallActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private ImageButton btnFindHospitals;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_soscall);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnFindHospitals = findViewById(R.id.btnFindHospitals);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnFindHospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocationAndShowHospitals();
            }
        });
    }
    private void getCurrentLocationAndShowHospitals() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Open Google Maps with a search query for nearby hospitals
                                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=hospitals");
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");

                                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                } else {
                                    Toast.makeText(SOSCallActivity.this, "Google Maps app is not installed.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SOSCallActivity.this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndShowHospitals();
            } else {
                Toast.makeText(SOSCallActivity.this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onSosNumber01Click(View view) {
        String phoneNumber = "1234567890";
        openDialer(phoneNumber);
    }
    private void openDialer(String phoneNumber) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        // Start the dialer activity
        startActivity(intent);
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

    public void onAmbulanceClick(View view) {
        String phoneNumber = "0000";
        openDialer(phoneNumber);
    }

    public void onSosNumber02Click(View view) {
        String phoneNumber = "1234567890";
        openDialer(phoneNumber);
    }
    
}