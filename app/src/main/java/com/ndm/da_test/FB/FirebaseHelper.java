package com.ndm.da_test.FB;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static final String USER_NOTI_NODE = "notifications_receiver";
    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveNotificationData(String userId,String type, String notificationTitle, String notificationBody, Double longitude, Double latitude, String timestamp) {
        DatabaseReference userNotiRef = databaseReference.child(USER_NOTI_NODE).child(userId).push();

        userNotiRef.child("type").setValue(type);
        userNotiRef.child("notificationTitle").setValue(notificationTitle);
        userNotiRef.child("notificationBody").setValue(notificationBody);
        userNotiRef.child("latitude").setValue(latitude);
        userNotiRef.child("longitude").setValue(longitude);
        userNotiRef.child("timestamp").setValue(timestamp);

    }
    public void saveNotificationData1(String userId,String type, String notificationTitle, String notificationBody, String source, String timestamp) {
        DatabaseReference userNotiRef = databaseReference.child(USER_NOTI_NODE).child(userId).push();

        userNotiRef.child("type").setValue(type);
        userNotiRef.child("notificationTitle").setValue(notificationTitle);
        userNotiRef.child("notificationBody").setValue(notificationBody);
        userNotiRef.child("source").setValue(source);
        userNotiRef.child("timestamp").setValue(timestamp);

    }
}
