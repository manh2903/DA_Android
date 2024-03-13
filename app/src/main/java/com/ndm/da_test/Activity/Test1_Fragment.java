package com.ndm.da_test.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.widget.Toolbar;

import com.ndm.da_test.R;

public class Test1_Fragment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        Toolbar toolbar = findViewById(R.id.toolbar);
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
