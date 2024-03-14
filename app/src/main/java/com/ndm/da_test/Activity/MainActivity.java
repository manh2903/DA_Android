package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ndm.da_test.R;
import com.ndm.da_test.ViewPager.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private BottomNavigationView mnavigationView;
    private NavigationView navigationView;
    private ViewPager mViewPager;
    private Toolbar toolbar;

    private ImageView imgAvatar;
    private TextView tvName,tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = findViewById(R.id.main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);


        setupViewPager();
        showUserInformation();

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

    private void initUI(){

        mnavigationView = findViewById(R.id.bottom_nav);
        mViewPager = findViewById(R.id.viewpager);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);

        imgAvatar = navigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_email);

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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_chinhsach) {
            Intent intent = new Intent(getApplicationContext(), Test1_Fragment.class);
            startActivity(intent);
        } else if (id == R.id.nav_thongtin) {
            Intent intent = new Intent(getApplicationContext(), Test2_Fragment.class);
            startActivity(intent);
        } else if(id == R.id.nav_logout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent (getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            finish();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer((GravityCompat.START));
        } else {
            super.onBackPressed();
        }
    }

    private void showUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null )
        {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        if(name == null)
        {
            tvName.setVisibility(View.GONE);
        }else
        {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(name);
        }
        tvEmail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.default_avatar).into(imgAvatar);
    }

}
