package com.ndm.da_test.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.ndm.da_test.Entities.Data;
import com.ndm.da_test.R;

public class DetailNotiActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private TextView tv_time, tv_address;

    private String address, time;

    private Double longitude , latitude ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_noti);

        Bundle bundle = getIntent().getExtras();
        Data data = (Data) bundle.getSerializable("Noti Receiver");
        if (data != null) {
             address = data.getBody();
             time = data.getTime();
        }


        toolbar = findViewById(R.id.toolbar);
        tv_time = findViewById(R.id.tv_time);
        tv_address = findViewById(R.id.tv_address);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        tv_time.setText(time);
        tv_address.setText("Đang có cháy tại " +address);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}