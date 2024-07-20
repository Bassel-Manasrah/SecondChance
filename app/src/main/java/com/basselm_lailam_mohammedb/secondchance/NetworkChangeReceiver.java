package com.basselm_lailam_mohammedb.secondchance;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

       if(isConnected(context)) {
           Toast.makeText(context, "Your internet connection has been restored", Toast.LENGTH_SHORT).show();
       }
       else {
           Toast.makeText(context, "You are currently offline", Toast.LENGTH_SHORT).show();
       }

    }

    private boolean isConnected(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = cm.getActiveNetwork();

        if(activeNetwork == null)
            return false;



        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);

        if(caps == null)
            return false;

        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}