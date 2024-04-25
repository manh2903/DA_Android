package com.ndm.da_test.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ndm.da_test.R;

public class ThongBao_Activity extends AppCompatActivity {

    private TextView fireLocation;

    private Button btn_xemvitri,btn_antoan;

    private String longitude,latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_bao);

        initUI();
        String notificationBody = getIntent().getStringExtra("notificationBody");
        longitude = getIntent().getStringExtra("longitude");
        latitude = getIntent().getStringExtra("latitude");


        fireLocation.setText(notificationBody);
        initListen();
        Log.d("ThongBao_Activity", longitude+ latitude);

    }


    private void initUI(){

        fireLocation = findViewById(R.id.fireLocation);
        fireLocation.setSingleLine(false); // Cho phép xuống dòng
        fireLocation.setMaxLines(2); // Số dòng tối đa
        btn_xemvitri = findViewById(R.id.btn_xemvitri);
        btn_antoan = findViewById(R.id.btn_antoan);

    }

    private void initListen(){

        btn_xemvitri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThongBao_Activity.this, MapActivity.class);
                intent.putExtra("longitude1", longitude);
                intent.putExtra("latitude1", latitude);
                startActivity(intent);
                finish(); // Kết thúc ThongBao_Activity
            }
        });

        btn_antoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}