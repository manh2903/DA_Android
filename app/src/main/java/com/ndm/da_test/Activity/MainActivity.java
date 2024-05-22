package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ndm.da_test.Entities.TokenLocation;
import com.ndm.da_test.R;
import com.ndm.da_test.Service.LocationTrackingService;
import com.ndm.da_test.ViewPager.ViewPagerAdapter;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public GoogleMap gMap;
    public Geocoder geocoder;
    public FusedLocationProviderClient fusedLocationClient;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FrameLayout notification;
    private DrawerLayout mDrawerLayout;
    private BottomNavigationView mnavigationView;
    private NavigationView navigationView;
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private TextView tvName, tvEmail, tv_location;
    private CircleImageView img_user;

    public String token;
    private ProgressDialog progressDialog;


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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        saveToken();


        // Kiểm tra quyền truy cập vị trí
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Nếu đã có quyền, lấy vị trí hiện tại
            getCurrentLocation(tv_location);
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Xác định thành phần ứng dụng đang chạy trong chế độ foreground
            String packageName = getApplicationContext().getPackageName();
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.getRunningAppProcesses().forEach(appProcess -> {
                if (appProcess.processName.equals(packageName)) {
                    appProcess.importance = ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                    appProcess.importanceReasonCode = ActivityManager.RunningAppProcessInfo.REASON_UNKNOWN;
                }
            });
        }

        initListener();
        showUserInformation();
        startLocationTrackingService();

    }

    private void startLocationTrackingService() {
        Intent intent = new Intent(this, LocationTrackingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           // startForegroundService(intent);
            startService(intent);
        } else {
            startService(intent);
        }

    }


    private void initUI() {
        mnavigationView = findViewById(R.id.bottom_nav);
        mViewPager = findViewById(R.id.viewpager);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        notification = toolbar.findViewById(R.id.notification);
        tv_location = toolbar.findViewById(R.id.tv_location);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        img_user = navigationView.getHeaderView(0).findViewById(R.id.img_user);
        progressDialog = new ProgressDialog(this);

    }

    private void initListener() {

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
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
            }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_chinhsach) {
            progressDialog.show();
            Intent intent = new Intent(getApplicationContext(), DieuKhoan_Activity.class);
            startActivity(intent);
        } else if (id == R.id.nav_thongtin) {
            Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_change_password) {
            Intent intent = new Intent(getApplicationContext(), ChangePWActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
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
        }
        else {
            super.onBackPressed();
        }


    }

    public void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String userId = user.getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String photoUrl = snapshot.child("avatarUrl").getValue(String.class);
                    if (fullName != null) {
                        tvName.setText(fullName);
                    }
                    if (email != null) {
                        tvEmail.setText(email);
                    }
                    if (photoUrl != null) {
                        Glide.with(MainActivity.this).load(photoUrl).error(R.drawable.ic_friend).into(img_user);
                    }
                } else {
                    Log.d("setUserInformation", "User data does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("setUserInformation", "Failed to read user data", error.toException());
            }
        });

    }

    public void getCurrentLocation(TextView tv_location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                LatLng currentLatLng = new LatLng(latitude, longitude);


                                TokenManager tokenManager = new TokenManager();
                                tokenManager.saveTokenAndLocation(token, latitude, longitude);


                                if (gMap != null) {
                                    gMap.addMarker(new MarkerOptions().position(currentLatLng).title("Your current location"));
                                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
                                }
                                // Sử dụng Geocoder để lấy địa chỉ từ tọa độ địa lý
                                geocoder = new Geocoder(MainActivity.this);
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        String locality = address.getSubLocality(); // Lấy tên phường xã
                                        if (locality != null && !locality.isEmpty()) {
                                            // Cập nhật TextView với địa chỉ phường xã
                                            String locationText = "Your current location: " + locality;
                                            tv_location.setText(locationText);
                                        } else {
                                            // Nếu không có thông tin về phường xã, sử dụng địa chỉ tổng quát
                                            String addressText = address.getAddressLine(0);
                                            tv_location.setText(addressText);
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Cannot get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Nếu quyền chưa được cấp, yêu cầu quyền
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }



    public static class TokenManager {
        private DatabaseReference databaseReference;

        public TokenManager() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("tokens");
        }

        public void saveTokenAndLocation(String deviceToken, double latitude, double longitude) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                TokenLocation tokenLocation = new TokenLocation(deviceToken, latitude, longitude);
                databaseReference.child(userId).child(deviceToken).setValue(tokenLocation);
            }
        }
    }

    public void saveToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    token = task.getResult();
                    Log.d("FCMToken", "Token: " + token);

                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            }
        });
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
