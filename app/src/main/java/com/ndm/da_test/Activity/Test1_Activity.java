package com.ndm.da_test.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.ndm.da_test.Notification.NotificationHelper;
import com.ndm.da_test.R;

public class Test1_Activity extends AppCompatActivity {

    private Button btn;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_1);
        btn = findViewById(R.id.post);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationHelper.showCallNotification(Test1_Activity.this);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
