package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mad_smartfit_android_app.model.Appointment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodayAppointmentActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Spinner appointmentSpinner;
    private List<Appointment> appointments;
    private ArrayAdapter<Appointment> adapter;
    private TextView tvAppointmentName, tvAppointmentDate, tvAppointmentTime, tvAppointmentStatus;
    private ImageButton btnZoom, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_today_appointment);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        appointmentSpinner = findViewById(R.id.appointmentSpinner);
        tvAppointmentName = findViewById(R.id.tvAppointmentName);
        tvAppointmentDate = findViewById(R.id.tvAppointmentDate);
        tvAppointmentTime = findViewById(R.id.tvAppointmentTime);
        tvAppointmentStatus = findViewById(R.id.tvAppointmentStatus);
        btnZoom = findViewById(R.id.btnZoom);
        btnDelete = findViewById(R.id.btnDelete);

        // Set up Spinner
        appointments = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, appointments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appointmentSpinner.setAdapter(adapter);

        // Fetch appointments
        fetchAppointments();

        // Handle Spinner selection
        appointmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Appointment selectedAppointment = appointments.get(position);
                updateAppointmentCard(selectedAppointment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Handle Zoom button click
        btnZoom.setOnClickListener(v -> {
            Appointment selectedAppointment = (Appointment) appointmentSpinner.getSelectedItem();
            if (selectedAppointment != null && selectedAppointment.getMeetingAccessLink() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedAppointment.getMeetingAccessLink()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No meeting link available", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Delete button click
        btnDelete.setOnClickListener(v -> {
            Appointment selectedAppointment = (Appointment) appointmentSpinner.getSelectedItem();
            if (selectedAppointment != null) {
                db.collection("appointments").document(selectedAppointment.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Appointment deleted", Toast.LENGTH_SHORT).show();
                            fetchAppointments(); // Refresh the list
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error deleting appointment", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void fetchAppointments() {
        String today = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        Log.d("TodayAppointmentActivity", "Fetching appointments for today: " + today);
        db.collection("appointments")
                .whereEqualTo("date", today)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointments.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Appointment appointment = document.toObject(Appointment.class);
                            appointment.setId(document.getId());
                            appointments.add(appointment);
                            Log.d("TodayAppointmentActivity", "Fetched appointment: " + appointment.getName());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("TodayAppointmentActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateAppointmentCard(Appointment appointment) {
        tvAppointmentName.setText(appointment.getName());
        tvAppointmentDate.setText(appointment.getDate());
        tvAppointmentTime.setText(appointment.getTime());
        tvAppointmentStatus.setText("Status: " + appointment.getStatus());
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
}