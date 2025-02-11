package com.example.mad_smartfit_android_app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PendingAppointmentsActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private AutoCompleteTextView mStatus;
    private Spinner appontmentSpinner ;
    private TextView mAppoinmentName,mAppoinmentDate,mAppoinmentTime,mAppoinmentStatus;
    private TextInputEditText mZoomInput;
    private List<String> appontmentIdList,appontmentDateList,appontmentTimeList,appontmentReasonList ,appontmentStatusList ,appontmentZoomLinkList ;
    private String selectedAppontmentID = "",selectedAppontmentDate = "",selectedAppontmentTime = "",selectedAppontmentReason = "",selectedAppontmentStatus = "", selectedAppontmentZoomLink = "";
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending_appointments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mAppoinmentName = findViewById(R.id.tvAppointmentName);
        mAppoinmentDate = findViewById(R.id.tvAppointmentDate);
        mAppoinmentTime = findViewById(R.id.tvAppointmentTime);
        mAppoinmentStatus = findViewById(R.id.tvAppointmentStatus);
        mZoomInput = findViewById(R.id.tietZoom);


        appontmentSpinner = findViewById(R.id.appointmentSpinner);

        appontmentIdList = new ArrayList<>();
        appontmentDateList = new ArrayList<>();
        appontmentTimeList = new ArrayList<>();
        appontmentReasonList = new ArrayList<>();
        appontmentStatusList = new ArrayList<>();
        appontmentZoomLinkList = new ArrayList<>();

        appontmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAppontmentID = appontmentIdList.get(position);
                selectedAppontmentDate = appontmentDateList.get(position);
                selectedAppontmentTime = appontmentTimeList.get(position);
                selectedAppontmentReason = appontmentReasonList.get(position);
                selectedAppontmentStatus = appontmentStatusList.get(position);
                selectedAppontmentZoomLink = appontmentZoomLinkList.get(position);

                mAppoinmentName.setText( selectedAppontmentReason );
                mAppoinmentDate.setText("Date "+selectedAppontmentDate);
                mAppoinmentTime.setText("Time "+selectedAppontmentTime);
                mAppoinmentStatus.setText("Status "+selectedAppontmentStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAppontmentID ="";
                selectedAppontmentDate ="";
                selectedAppontmentTime ="";
                selectedAppontmentReason ="";
                selectedAppontmentStatus ="";
                selectedAppontmentZoomLink ="";
            }
        });
        findViewById(R.id.appointmentInputLayout).setVisibility(View.VISIBLE);
        fetchAppointmentsFromFirestore();

        mStatus = findViewById(R.id.acStatus);
        String[] status = {"Confirmed", "Cancelled","Completed"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, status);
        mStatus.setAdapter(adapter);


    }
    private void fetchAppointmentsFromFirestore() {
        Log.d("FetchAppointments", "Fetching Appointments Start ");
        fStore.collection("appointments")
                .whereEqualTo("appointmentConductorUserID",fAuth.getCurrentUser().getUid() ) // Assuming trainers have a "role" field set to "trainer"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appontmentIdList.clear();
                        appontmentDateList.clear();
                        appontmentTimeList.clear();
                        appontmentReasonList.clear();
                        appontmentStatusList.clear();
                        appontmentZoomLinkList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String date = document.getString("date");
                            String time = document.getString("time");
                            String reason = document.getString("reason");
                            String status = document.getString("status");
                            String zoomLink = document.getString("meeting_access_link");
                            String appoinmentId = document.getId();
                            Log.d("Appoiment Date",date );
                            Log.d("reason",reason );
                            appontmentIdList.add(appoinmentId);
                            appontmentDateList.add(date);
                            appontmentTimeList.add(time);
                            appontmentReasonList.add(reason);
                            appontmentStatusList.add(status);
                            appontmentZoomLinkList.add(zoomLink);

                        }
                        // Populate the Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, appontmentReasonList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        appontmentSpinner.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to fetch trainers.", Toast.LENGTH_SHORT).show();
                    }
                });
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

    public void onRefreshBtnClick(View view) {
        mAppoinmentName.setText( selectedAppontmentReason );
        mAppoinmentDate.setText("Date "+selectedAppontmentDate);
        mAppoinmentTime.setText("Time "+selectedAppontmentTime);
        mAppoinmentStatus.setText("Status "+selectedAppontmentStatus);
    }
    public void onSubmitClick(View view) {
        String zoomLink = mZoomInput.getText().toString();
        String status = mStatus.getText().toString();

        if (zoomLink.isEmpty()) {
            Toast.makeText(this, "Please enter Zoom link", Toast.LENGTH_SHORT).show();
            mZoomInput.setError("Please enter meeting link");
            return;
        }

        if (status.isEmpty()) {
            Toast.makeText(this, "Please enter status", Toast.LENGTH_SHORT).show();
            mStatus.setError("Please enter appointment status");
            return;
        }

        DocumentReference documentReference = fStore.collection("appointments").document(selectedAppontmentID);
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("meeting_access_link", zoomLink);
        appointment.put("status", status);  // Updating the status field

        documentReference.update(appointment)  // Using update() instead of set() to avoid overwriting existing fields
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "onSuccess: Appointment updated successfully for " + selectedAppontmentID);
                        Toast.makeText(PendingAppointmentsActivity.this, "Appointment updated successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "onFailure: Failed to update appointment", e);
                        Toast.makeText(PendingAppointmentsActivity.this, "Failed to update appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void onDeleteClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Appointment");
        builder.setMessage("Are you sure you want to delete this appointment?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAppointment();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog without deleting
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAppointment() {
        DocumentReference documentReference = fStore.collection("appointments").document(selectedAppontmentID);

        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "onSuccess: Appointment deleted for " + selectedAppontmentID);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "onFailure: Error deleting appointment", e);
                    }
                });
    }
    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "No app found to open the link", Toast.LENGTH_SHORT).show();
        }
    }

    public void onZoomClick(View view) {
        if (selectedAppontmentZoomLink.isEmpty() && selectedAppontmentZoomLink == "") {
            Toast.makeText(this, "No zoom link available", Toast.LENGTH_SHORT).show();

        }
        else {
            openLink(selectedAppontmentZoomLink);
        }
    }
}
