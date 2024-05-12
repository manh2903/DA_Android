package com.ndm.da_test.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ndm.da_test.Service.LocationTrackingService;

public class LocationTrackingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null) {
            Intent serviceIntent = new Intent(context, LocationTrackingService.class);
            context.startService(serviceIntent);
        }
    }
}
