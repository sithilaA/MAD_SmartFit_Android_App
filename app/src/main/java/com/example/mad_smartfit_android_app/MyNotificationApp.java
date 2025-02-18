package com.example.mad_smartfit_android_app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyNotificationApp extends Application {

    // A public constant for channel ID
    public static final String CHANNEL_ID = "APPOINTMENT_CHANNEL";

    @Override
    public void onCreate() {
        super.onCreate();

        // Create the notification channel on app startup
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Notification channels are only available on Android O (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel name that will be visible to user in the system settings
            String channelName = "Reminders Channel";
            // Channel description for the system settings
            String channelDescription = "Delivers appointment and workout reminders";

            // The importance affects how "interruptive" the notifications are (sound, etc.)
            int importance = NotificationManager.IMPORTANCE_HIGH;

            // Create the channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            // Register the channel with the system
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
