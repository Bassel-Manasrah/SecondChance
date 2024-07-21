package com.basselm_lailam_mohammedb.secondchance.observers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.basselm_lailam_mohammedb.secondchance.broadcastReceivers.AlarmReceiver;

public class AppLifecycleObserver implements DefaultLifecycleObserver {

    // Context to be used for setting the alarm
    private Context context;

    // Constructor to initialize the context
    public AppLifecycleObserver(Context context) {
        this.context = context;
    }

    // Called when the LifecycleOwner's state changes to onStop
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        // Call the default implementation of onStop
        DefaultLifecycleObserver.super.onStop(owner);

        // Get the AlarmManager system service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create an intent to trigger the AlarmReceiver class
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Create a pending intent that wraps the intent to broadcast when the alarm is triggered
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Calculate the trigger time for the alarm (2 days from now)
        long triggerTime = System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000;

        // Set an exact alarm with the calculated trigger time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }
}

