package com.ndm.da_test.Service;



import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.IBinder;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.ndm.da_test.Activity.MainActivity;
import com.ndm.da_test.R;

public class LocationTrackingService extends Service {

    private static final String CHANNEL_ID = "LocationTrackingService";
    private static final int NOTIFICATION_ID = 123;

    private String token;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("TOKEN")) {
            token = intent.getStringExtra("TOKEN");
            Log.d("FCMToken Tracking", "Token received in LocationTrackingService: " + token); // Log token to verify
        }

        // Tạo thông báo


        // Bắt đầu cập nhật vị trí
        startLocationUpdates();

        return START_STICKY;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(30000); // Cập nhật vị trí mỗi 30 giây
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    for (android.location.Location location : locationResult.getLocations()) {
                        saveLocationToDatabase(location.getLatitude(), location.getLongitude());
                      //  updateNotification(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        };
    }

    private void saveLocationToDatabase(double latitude, double longitude) {
        MainActivity.TokenManager tokenManager = new MainActivity.TokenManager();
        tokenManager.saveTokenAndLocation(token, latitude, longitude);
    }

    private void createNotification() {
        // Tạo kênh thông báo (chỉ cần thực hiện trên Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Location Tracking Service", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Foreground service for location tracking");
            channel.enableLights(false);
            channel.enableVibration(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo thông báo
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Tracking Service")
                .setContentText("Tracking your location...")
                .setSmallIcon(R.drawable.ic_location)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);

        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void updateNotification(double latitude , double longitude) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Tracking Service")
                .setContentText("Latitude: " + latitude + " Longitude:" + longitude)
                .setSmallIcon(R.drawable.ic_location)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}
