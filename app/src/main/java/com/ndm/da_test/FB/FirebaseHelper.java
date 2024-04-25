package com.ndm.da_test.FB;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static final String USER_NOTI_NODE = "usernoti";

    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveNotificationData(String userId, String notificationTitle, String notificationBody, String longitude, String latitude, String timestamp) {
        DatabaseReference userNotiRef = databaseReference.child(USER_NOTI_NODE).child(userId).push();

        userNotiRef.child("notificationTitle").setValue(notificationTitle);
        userNotiRef.child("notificationBody").setValue(notificationBody);
        userNotiRef.child("longitude").setValue(longitude);
        userNotiRef.child("latitude").setValue(latitude);
        userNotiRef.child("timestamp").setValue(timestamp);
    }
}
