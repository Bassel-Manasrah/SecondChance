package com.basselm_lailam_mohammedb.secondchance;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import es.dmoral.toasty.Toasty;

public class NetworkChangeReceiver extends BroadcastReceiver {

    // This method is called when the network connectivity changes
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isConnected(context)) {
            // Display a success toast if the device is connected to the internet
            Toasty.success(context, "You are currently online", Toast.LENGTH_SHORT, true).show();
        } else {
            // Display an error toast if the device is not connected to the internet
            Toasty.error(context, "You are currently offline", Toast.LENGTH_SHORT, true).show();
        }
    }

    // Helper method to check if the device is connected to the internet
    private boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get the active network
        Network activeNetwork = cm.getActiveNetwork();

        // If there is no active network, return false
        if (activeNetwork == null)
            return false;

        // Get the network capabilities of the active network
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);

        // If the capabilities are null, return false
        if (caps == null)
            return false;

        // Return true if the network has internet capability, otherwise false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
