package com.ndm.da_test.Activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.ndm.da_test.Entities.Data;
import com.ndm.da_test.R;
import com.ndm.da_test.Receiver.HangUpBroadcast;
import com.ndm.da_test.Service.CallingService;


public class IncomingCallActivity extends AppCompatActivity {
    private Data data;
    private String title, body;
    private TextView tvTitle;
    private TextView tvBody;
    private Button btnDecline, btnAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_incoming_call);

        data = (Data) getIntent().getSerializableExtra("data");

        tvTitle = findViewById(R.id.tvTitle);
        tvBody = findViewById(R.id.tvBody);
        btnDecline = findViewById(R.id.decline);
        btnAnswer = findViewById(R.id.answer);
        tvTitle.setText(data.getTitle());
        tvBody.setText(data.getBody());

        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ThongBao_Activity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent serviceIntent = new Intent(getApplicationContext(), CallingService.class);
                stopService(serviceIntent);


                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(getApplicationContext(), CallingService.class);
        stopService(serviceIntent);
    }


}
