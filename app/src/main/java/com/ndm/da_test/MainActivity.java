package com.ndm.da_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ndm.da_test.Fragment.LoginFragment;
import com.ndm.da_test.ViewPager.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mnavigationView;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mnavigationView = findViewById(R.id.bottom_nav);
        mViewPager = findViewById(R.id.viewpager);
        setupViewPager();
        mnavigationView.setOnItemSelectedListener(item -> {
            int i = item.getItemId();
            if (i == R.id.home) {
                mViewPager.setCurrentItem(0);
                Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
            }
            if (i == R.id.skill) {
                mViewPager.setCurrentItem(1);
                Toast.makeText(MainActivity.this, "Kỹ năng thoát hiểm", Toast.LENGTH_SHORT).show();
            }
            if (i == R.id.fire) {
                mViewPager.setCurrentItem(2);
                Toast.makeText(MainActivity.this, "Kỹ năng PCCC", Toast.LENGTH_SHORT).show();
            }
            if (i == R.id.my_page) {
                mViewPager.setCurrentItem(3);
                Toast.makeText(MainActivity.this, "Cá nhân", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }
    private void setupViewPager() {
        ViewPagerAdapter viewPagerAdapter;
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mnavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        mnavigationView.getMenu().findItem(R.id.skill).setChecked(true);
                        break;
                    case 2:
                        mnavigationView.getMenu().findItem(R.id.fire).setChecked(true);
                        break;
                    case 3:
                        mnavigationView.getMenu().findItem(R.id.my_page).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }


}
