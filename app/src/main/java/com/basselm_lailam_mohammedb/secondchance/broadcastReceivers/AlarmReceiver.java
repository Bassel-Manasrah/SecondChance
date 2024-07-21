package com.basselm_lailam_mohammedb.secondchance.broadcastReceivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.basselm_lailam_mohammedb.secondchance.activities.MainActivity;
import com.basselm_lailam_mohammedb.secondchance.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Show a notification when the alarm is received
        showNotification(context, "We miss you", "Check the amazing deals we have");
    }

    // Method to show a notification
    private void showNotification(Context context, String title, String text) {

        // Define the notification channel ID and name
        String channelId = "second_chance_channel";
        String channelName = "Second Chance Notifications";

        // Create a notification builder with the specified channel ID
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.baseline_notifications_24) // Set the small icon for the notification
                .setContentTitle(title) // Set the notification title
                .setContentText(text) // Set the notification text
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Set the notification priority

        // Create an intent to open MainActivity when the notification is clicked
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Create a pending intent for the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent); // Set the pending intent to the notification

        // Get the notification manager system service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Get or create the notification channel
        NotificationChannel notificationChannel = getNotificationChannel(context, channelId, channelName);

        // Show the notification
        notificationManager.notify(0, builder.build());
    }

    // Method to get or create a notification channel
    private NotificationChannel getNotificationChannel(Context context, String channelId, String channelName) {

        // Get the notification manager system service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Get the existing notification channel or create a new one if it doesn't exist
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
        if (notificationChannel == null) {
            // Create a new notification channel with the specified ID and name, and set the importance level
            notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel); // Create the notification channel
        }

        return notificationChannel; // Return the notification channel
    }
}
