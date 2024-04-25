package com.ndm.da_test.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.ndm.da_test.R;

public class InfoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView img;
    private ProgressDialog progressDialog;
    private Button btn_share, btn_danhgia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initUI();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        initListener();

    }

    private void initUI(){
        toolbar = findViewById(R.id.toolbar);
        img = findViewById(R.id.img_dieukhoan);
        btn_share = findViewById(R.id.share_button);
        btn_danhgia = findViewById(R.id.danhgia_button);
        progressDialog = new ProgressDialog(this);

    }

    private void initListener(){

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Intent intent = new Intent(getApplicationContext(), DieuKhoan_Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra nếu progressDialog đang hiển thị, ẩn nó đi.
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
