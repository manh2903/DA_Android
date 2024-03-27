package com.ndm.da_test.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.ndm.da_test.Entities.Escape;
import com.ndm.da_test.Entities.Skill;
import com.ndm.da_test.R;

public class DetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initUI();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Escape escape = (Escape) bundle.getSerializable("object_escape");
            Skill skill = (Skill) bundle.getSerializable("Skill");

            if (escape != null) {
                tv_title.setText(escape.getName());
            }

            if (skill != null) {
                tv_title.setText(skill.getName());
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        tv_title = findViewById(R.id.title_detail);
        tv_title.setGravity(Gravity.CENTER); // Căn giữa
        tv_title.setSingleLine(false); // Cho phép xuống dòng
        tv_title.setMaxLines(2); // Số dòng tối đa
        tv_title.setEllipsize(TextUtils.TruncateAt.END);
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
