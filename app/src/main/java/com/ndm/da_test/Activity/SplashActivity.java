package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ndm.da_test.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String token = task.getResult();
                            Log.d("Token", "Token FCM: " + token); // Log token FCM

                            // Thực hiện kiểm tra và thêm token vào bảng "tokens"
                            DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("tokens");
                            tokensRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        // Nếu bảng "tokens" chưa tồn tại, tạo mới nó và thêm token
                                        tokensRef.setValue(true);
                                        tokensRef.child(token).setValue(true);
                                        Log.d("Token", "Added token to database: " + token);
                                    } else {
                                        // Nếu bảng "tokens" đã tồn tại, kiểm tra xem token đã tồn tại hay chưa
                                        if (!dataSnapshot.hasChild(token)) {
                                            // Nếu token chưa tồn tại, thêm token vào bảng "tokens"
                                            tokensRef.child(token).setValue(true);
                                            Log.d("Token", "Added token to database: " + token);
                                        } else {
                                            // Nếu token đã tồn tại, không cần thêm gì cả
                                            Log.d("Token", "Token already exists in database: " + token);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("Token", "Database error: " + databaseError.getMessage());
                                }
                            });
                        } else {
                            // Handle token retrieval error
                            Toast.makeText(SplashActivity.this, "Failed to get token.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);


    }

    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null )
        {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}