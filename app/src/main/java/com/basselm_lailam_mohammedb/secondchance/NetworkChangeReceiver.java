package com.basselm_lailam_mohammedb.secondchance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("mylog", "onReceive: ");
        Toast.makeText(context, "hey", Toast.LENGTH_LONG).show();
    }
}