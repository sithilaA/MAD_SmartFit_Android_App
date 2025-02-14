package com.example.mad_smartfit_android_app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class UserNotificationSettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "reminder_prefs";
    private static final String APPOINTMENT_ENABLED_KEY = "appointment_reminder_enabled";
    private static final String APPOINTMENT_DETAILS_KEY = "appointment_reminder_details";
    private static final String WORKOUT_ENABLED_KEY = "workout_reminder_enabled";
    private static final String WORKOUT_DETAILS_KEY = "workout_reminder_details";

    private static final int REQUEST_POST_NOTIFICATIONS = 123; // Arbitrary request code

    private Switch switchAppointmentReminder, switchWorkoutReminder;
    private TextView tvAppointmentReminderDetails, tvWorkoutReminderDetails;
    private Button btnSave;

    // Input fields for workout reminder date and time
    private TextInputEditText workoutDateInput, workoutTimeInput;

    private AlarmManager alarmManager;
    private PendingIntent appointmentPendingIntent;
    private PendingIntent workoutPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notification_settings);

        // 1) Request POST_NOTIFICATIONS permission on Android 13+
        requestPostNotificationsPermissionIfNeeded();

        // Initialize UI components
        switchAppointmentReminder = findViewById(R.id.switchAppointmentReminder);
        switchWorkoutReminder = findViewById(R.id.switchWorkoutReminder);
        tvAppointmentReminderDetails = findViewById(R.id.tvAppointmentReminderDetails);
        tvWorkoutReminderDetails = findViewById(R.id.tvWorkoutReminderDetails);
        btnSave = findViewById(R.id.btnSave);

        // New: Workout date/time input fields (make sure these IDs match your layout)
        workoutDateInput = findViewById(R.id.workoutDateInput);
        workoutTimeInput = findViewById(R.id.workoutTimeInput);

        // Load stored values from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean appointmentEnabled = prefs.getBoolean(APPOINTMENT_ENABLED_KEY, false);
        boolean workoutEnabled = prefs.getBoolean(WORKOUT_ENABLED_KEY, false);
        String appointmentDetails = prefs.getString(APPOINTMENT_DETAILS_KEY, "No appointment reminder set");
        String workoutDetails = prefs.getString(WORKOUT_DETAILS_KEY, "No workout reminder set");

        // Set UI states for appointment reminder
        switchAppointmentReminder.setChecked(appointmentEnabled);
        tvAppointmentReminderDetails.setText(appointmentDetails);

        // For workout, also update the input fields if details are already saved
        switchWorkoutReminder.setChecked(workoutEnabled);
        tvWorkoutReminderDetails.setText(workoutDetails);

        // Optionally, parse the stored workoutDetails to fill the date/time fields if it contains " at "
        String[] data = getIntent().getStringArrayExtra("some_key");
        if (data != null && data.length > 0) {
            String value = data[0];
        }


        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set listeners for the workout date and time inputs
        workoutDateInput.setOnClickListener(v -> showWorkoutDatePicker());
        workoutTimeInput.setOnClickListener(v -> showWorkoutTimePicker());

        btnSave.setOnClickListener(v -> {
            // Save toggle states to SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(APPOINTMENT_ENABLED_KEY, switchAppointmentReminder.isChecked());
            editor.putBoolean(WORKOUT_ENABLED_KEY, switchWorkoutReminder.isChecked());

            // For workout, combine the chosen date and time from the input fields:
            String newWorkoutDetails = workoutDateInput.getText().toString() + " at " + workoutTimeInput.getText().toString();
            editor.putString(WORKOUT_DETAILS_KEY, newWorkoutDetails);
            editor.apply();

            // Handle Appointment scheduling/canceling (unchanged)
            if (switchAppointmentReminder.isChecked()) {
                scheduleAppointmentAlarm(appointmentDetails);
            } else {
                cancelAppointmentAlarm();
            }

            // Handle Workout scheduling/canceling using details entered in this activity
            if (switchWorkoutReminder.isChecked()) {
                scheduleWorkoutAlarm(newWorkoutDetails);
            } else {
                cancelWorkoutAlarm();
            }

            Toast.makeText(UserNotificationSettingsActivity.this, "Notification settings saved", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * If we're running on Android 13+ (TIRAMISU), we must request POST_NOTIFICATIONS permission at runtime.
     */
    private void requestPostNotificationsPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_POST_NOTIFICATIONS);
            }
        }
    }

    /**
     * Callback for the result from requesting permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            // If the user denied, you can't legally post notifications on Android 13+ devices
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied. Reminders may not appear.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scheduleAppointmentAlarm(String details) {
        Calendar cal = parseDateTime(details);
        if (cal == null) {
            return;
        }
        // Subtract 30 minutes from appointment time
        long triggerTime = cal.getTimeInMillis() - (30 * 60 * 1000);
        if (triggerTime <= System.currentTimeMillis()) {
            return;
        }
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", "Appointment Reminder");
        intent.putExtra("message", "Your appointment is in 30 minutes!");
        appointmentPendingIntent = PendingIntent.getBroadcast(
                this,
                2001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, appointmentPendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, appointmentPendingIntent);
            }
        }
    }

    private void cancelAppointmentAlarm() {
        if (appointmentPendingIntent != null && alarmManager != null) {
            alarmManager.cancel(appointmentPendingIntent);
        }
    }

    /**
     * Schedules a workout reminder alarm using details provided in this activity.
     * Expects details in format "dd/MM/yyyy at HH:mm"
     */
    private void scheduleWorkoutAlarm(String details) {
        Calendar cal = parseDateTime(details);
        if (cal == null) {
            return;
        }
        long triggerTime = cal.getTimeInMillis();
        if (triggerTime <= System.currentTimeMillis()) {
            return;
        }
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", "Workout Reminder");
        intent.putExtra("message", "Get to your workout!");
        workoutPendingIntent = PendingIntent.getBroadcast(
                this,
                3001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, workoutPendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, workoutPendingIntent);
            }
        }
    }

    private void cancelWorkoutAlarm() {
        if (workoutPendingIntent != null && alarmManager != null) {
            alarmManager.cancel(workoutPendingIntent);
        }
    }

    private Calendar parseDateTime(String dateTime) {
        try {
            String[] parts = dateTime.split(" at ");
            if (parts.length != 2) return null;
            String datePart = parts[0].trim();
            String timePart = parts[1].trim();

            String[] dateSegments = datePart.split("/");
            int day = Integer.parseInt(dateSegments[0]);
            int month = Integer.parseInt(dateSegments[1]) - 1;
            int year = Integer.parseInt(dateSegments[2]);

            String[] timeSegments = timePart.split(":");
            int hour = Integer.parseInt(timeSegments[0]);
            int minute = Integer.parseInt(timeSegments[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Show a DatePicker for workout reminder date
    private void showWorkoutDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    workoutDateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Show a TimePicker for workout reminder time
    private void showWorkoutTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    workoutTimeInput.setText(selectedTime);
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    public void onHomeClick(View view) {
        super.onBackPressed();
        finish();
    }

    public void onSettingsClick(View view) {
        super.onBackPressed();
        finish();
    }

    public void onWorkoutClick(View view) {
        super.onBackPressed();
        finish();
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
