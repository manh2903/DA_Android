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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ndm.da_test.Activity.MainActivity;
import com.ndm.da_test.Entities.TokenLocation;
import com.ndm.da_test.R;

public class LocationTrackingService extends Service {

    private static final String TAG = "LocationTrackingService";
    private static final String CHANNEL_ID = "LocationTrackingService";
    private String token;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference databaseReference;

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

        Log.d(TAG, "onStartCommand: Service started");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("tokens");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    token = task.getResult();
                    Log.d(TAG, "Token: " + token);
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {

                        exception.printStackTrace();
                    }
                }
            }
        });

        // Bắt đầu cập nhật vị trí
        startLocationUpdates();
       // createNotificationChannel();
       // Notification notification = createNotification(0, 0); // Tham số 0, 0 chỉ là giá trị mẫu
       // startForeground(1, notification);
       // stopForeground(true);

        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "LocationTrackingService",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private Notification createNotification(double latitude, double longitude) {
        // Tạo intent để mở MainActivity khi người dùng nhấn vào notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Xây dựng nội dung cho notification
        String contentText = "Latitude: " + latitude + ", Longitude: " + longitude;

        // Xây dựng notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setContentTitle("Location Tracking Service")
                .setContentText(contentText)
                .setContentIntent(pendingIntent);

        return builder.build();
    }


    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // Cập nhật vị trí mỗi 30 giây
        locationRequest.setFastestInterval(1000);
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
                        Log.d(TAG, "New location received: " + location.getLatitude() + ", " + location.getLongitude());
                        updateLocationInDatabase(location.getLatitude(), location.getLongitude());
                        createNotification(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        };
    }

    private void updateLocationInDatabase(double latitude, double longitude) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            if (userId != null && !userId.isEmpty() && token != null && !token.isEmpty()) {
                Log.d(TAG, "Updating location for user: " + userId);
                TokenLocation tokenLocation = new TokenLocation(token, latitude, longitude);
                databaseReference.child(userId).child(token).setValue(tokenLocation)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Location updated successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update location", e));
            } else {
                Log.w(TAG, "User ID or token is null or empty");
            }
        } else {
            Log.w(TAG, "User is not signed in");
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}