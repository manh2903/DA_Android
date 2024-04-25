package com.ndm.da_test.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ndm.da_test.R;

public class DieuKhoan_Activity extends AppCompatActivity {


    private Toolbar toolbar;

    private ProgressDialog progressDialog;

    private TextView tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dieukhoan);

        progressDialog = new ProgressDialog(this);

        progressDialog.dismiss();


        toolbar = findViewById(R.id.toolbar);
        tv_text = findViewById(R.id.tv_text);
        tv_text.setGravity(Gravity.CENTER); // Căn giữa
        tv_text.setSingleLine(false); // Cho phép xuống dòng
        tv_text.setMaxLines(2);// Số dòng tối đa
        tv_text.setText("Chính sách bảo mật và điều khoản sử dụng ứng dụng");


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);


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
