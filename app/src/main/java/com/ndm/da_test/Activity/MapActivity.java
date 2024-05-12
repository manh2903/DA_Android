package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.DialogFragment.FireAlarmDialogFragment2;
import com.ndm.da_test.R;
import com.ndm.da_test.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FrameLayout notification;
    public GoogleMap gMap,gMap2,gMapMyLocation;
    private Geocoder geocoder;
    public FusedLocationProviderClient fusedLocationClient;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Toolbar toolbar;
    private TextView tv_location;
    private Button btn_myposition, btn_thongbao;

    private SearchView mapSearch;
    private ListView addressListView;
    private ArrayAdapter<String> addressAdapter;
    private List<Marker> markerList = new ArrayList<>();

    private FrameLayout fragmentContainer;

    private double selectedLongitude;
    private double selectedLatitude;
    private String selectedAddress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapSearch = findViewById(R.id.mapSearch);
        addressListView = findViewById(R.id.lv_location);
        mapSearch.setFocusable(false);
        addressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        addressListView.setAdapter(addressAdapter);
        addressListView.setVisibility(View.GONE); // Ẩn ListView đi ban đầu
        initUi();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        // Khởi tạo ListView và Adapter


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        initListener();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initUi() {
        toolbar = findViewById(R.id.toolbar);
        notification = toolbar.findViewById(R.id.notification);
        tv_location = toolbar.findViewById(R.id.tv_location);
        btn_myposition = findViewById(R.id.btn_mypositon);
        btn_thongbao = findViewById(R.id.btn_thongbao);
        fragmentContainer = findViewById(R.id.fragment_container);
    }

    private void initListener() {
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        btn_myposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                moveMapToSearchedLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi người dùng thay đổi văn bản tìm kiếm
                if (!newText.isEmpty()) {
                    addressListView.setVisibility(View.VISIBLE);
                    // Tìm kiếm vị trí dựa trên từ khóa và di chuyển bản đồ đến vị trí đó
                    moveMapToSearchedLocation(newText);
                } else {
                    addressListView.setVisibility(View.GONE); // Ẩn ListView nếu newText rỗng
                }
                return true;
            }
        });

        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedAddress = (String) parent.getItemAtPosition(position);
                moveMapToSearchedLocation(selectedAddress);
            }
        });

        btn_thongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo FragmentManager và FireAlarmFragment

                FireAlarmDialogFragment2 fireAlarmDialogFragment = new FireAlarmDialogFragment2();

                // Tạo Bundle để truyền dữ liệu
                Bundle bundle = new Bundle();
                // Thêm dữ liệu về longitude, latitude và địa chỉ vào Bundle
                bundle.putDouble("longitude", selectedLongitude);
                bundle.putDouble("latitude", selectedLatitude);
                bundle.putString("address", selectedAddress);

                // Truyền Bundle vào FireAlarmFragment
                fireAlarmDialogFragment.setArguments(bundle);

                // Mở FireAlarmFragment bằng FragmentTransaction
//                fragmentManager.beginTransaction()
//                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//                        .add(fragmentContainer.getId(), fireAlarmDialogFragment, "fireAlarmFragment")
//                        .addToBackStack(null)
//                        .commit();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fireAlarmDialogFragment.show(fragmentManager, "fireAlarmDialogFragment");

            }
        });
    }

    private void handleLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền truy cập vị trí nếu chưa được cấp
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Nếu đã có quyền, lấy vị trí hiện tại
            getCurrentLocation();
        }
    }

    private void check() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("longitude1") && intent.hasExtra("latitude1")) {
            // Lấy dữ liệu từ Intent
            String longitude = intent.getStringExtra("longitude1");
            String latitude = intent.getStringExtra("latitude1");

            // Kiểm tra longitude và latitude có giá trị không rỗng
            if (longitude != null && latitude != null) {
                // Chuyển đổi longitude và latitude thành kiểu dữ liệu double
                double parsedLongitude = Double.parseDouble(longitude);
                double parsedLatitude = Double.parseDouble(latitude);

                // Tạo LatLng từ longitude và latitude
                LatLng locationLatLng = new LatLng(parsedLatitude, parsedLongitude);

                // Tạo BitmapDescriptor từ ảnh drawable
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_fire1);

                // Thêm marker cho vị trí này trên bản đồ
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(locationLatLng)
                        .icon(icon)
                        .title("Địa chỉ có cháy");

                Marker marker = gMap.addMarker(markerOptions);

                markerList.add(marker);

                // Di chuyển bản đồ đến vị trí này
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 18));

                // Hiển thị địa chỉ tương ứng lên SearchView (nếu có)
                displayAddress(locationLatLng);

                // Log để kiểm tra
                Log.d("MapActivity", "Received longitude: " + longitude + ", Received latitude: " + latitude);
            }
        } else {
            handleLocationPermission();
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        gMap = googleMap;


        check();

        showFire();

        showFriendsOnMap();

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Xóa tất cả các marker trước đó

                gMap.clear();
                showFire();

                showFriendsOnMap();

                // Thêm marker mới tại vị trí được click
                addMarker(latLng);
                displayAddress(latLng);
                addressListView.setVisibility(View.GONE);
            }
        });
    }

    private void addMarker(LatLng latLng) {

        // Tạo marker mới
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Selected Location");
        // Thêm marker vào bản đồ
        gMap.addMarker(markerOptions);
        // Di chuyển camera đến vị trí được chọn
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));



    }

    private void displayAddress(LatLng latLng) {
        // Sử dụng Geocoder để lấy địa chỉ từ tọa độ
        geocoder = new Geocoder(MapActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Hiển thị địa chỉ trên SearchView
                mapSearch.setQuery(address.getAddressLine(0), false);
            } else {
                // Nếu không tìm thấy địa chỉ, hiển thị thông báo
                mapSearch.setQuery("", false);
                Toast.makeText(MapActivity.this, "No address found for this location", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                if (gMap != null) {
                                    gMap.addMarker(new MarkerOptions().position(currentLatLng).title("Your current location"));
                                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
                                }

                                // Sử dụng Geocoder để lấy địa chỉ từ tọa độ địa lý
                                geocoder = new Geocoder(MapActivity.this);
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
                                Toast.makeText(MapActivity.this, "Cannot get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Nếu quyền chưa được cấp, yêu cầu quyền
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void moveMapToSearchedLocation(String query) {
        // Sử dụng Geocoder để tìm kiếm vị trí dựa trên query
        geocoder = new Geocoder(MapActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(query, 3); // Số lượng địa chỉ tối đa
            if (!addresses.isEmpty()) {
                addressAdapter.clear();
                for (Address address : addresses) {
                    String addressString = address.getAddressLine(0); // Lấy địa chỉ đầu tiên
                    addressAdapter.add(addressString);
                }
                addressAdapter.notifyDataSetChanged();
                Address firstAddress = addresses.get(0);

                selectedAddress = firstAddress.getAddressLine(0);
                selectedLatitude = firstAddress.getLatitude();
                selectedLongitude = firstAddress.getLongitude();
                LatLng locationLatLng = new LatLng(selectedLatitude, selectedLongitude);
                // Di chuyển bản đồ đến vị trí được tìm kiếm
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 18));
            } else {
                // Không tìm thấy địa điểm phù hợp
                Toast.makeText(MapActivity.this, "No matching location found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFire() {

        Log.d("utils", Utils.getUserId());
        String UserID = Utils.getUserId();
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications_receiver").child(UserID);
        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Đọc dữ liệu từ mỗi thông báo
                    if (snapshot != null) {
                        String body = (String) snapshot.child("body").getValue();
                        double longitude = (double) snapshot.child("longitude").getValue();
                        double latitude = (double) snapshot.child("latitude").getValue();
                        // Tạo LatLng từ kinh độ và vĩ độ
                        LatLng locationLatLng = new LatLng(latitude, longitude);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_fire1);


                        // Thêm marker cho vị trí này trên bản đồ
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(locationLatLng)
                                .icon(icon)
                                .title(body);
                        gMap.addMarker(markerOptions);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }


    private void showFriendsOnMap() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String currentUserId = currentUser.getUid();

        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friend").child(currentUserId);
        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendId = friendSnapshot.getKey();
                    Log.d("friendId", friendId);

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(friendId);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {

                            if (userSnapshot.exists()) {
                                // Lấy thông tin avatar của bạn bè
                                String avatarUrl = userSnapshot.child("avatarUrl").getValue(String.class);
                                String fullname = userSnapshot.child("fullName").getValue(String.class);
                                String[] parts = fullname.split(" ");

                                // Lấy thông tin tọa độ từ bảng "token"
                                DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("tokens").child(friendId);
                                tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot tokenSnapshot) {
                                        for (DataSnapshot snapshot : tokenSnapshot.getChildren()) {
                                            if (snapshot.exists()) {
                                                // Lấy tọa độ của bạn bè

                                                double latitude = snapshot.child("latitude").getValue(Double.class);
                                                double longitude = snapshot.child("longitude").getValue(Double.class);


                                                LatLng friendLocation = new LatLng(latitude, longitude);

                                                Log.d("friendLocation", String.valueOf(friendLocation));


                                                // Tạo marker từ avatar của bạn bè
                                                BitmapDescriptor friendMarkerIcon = getMarkerIconFromUrl(avatarUrl);

                                                MarkerOptions markerOptions = new MarkerOptions()
                                                        .position(friendLocation)
                                                        .icon(friendMarkerIcon)
                                                        .title(parts[parts.length - 1]);


                                                // Thêm marker cho bạn bè trên bản đồ
                                                gMap.addMarker(markerOptions);
                                            }
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private BitmapDescriptor getMarkerIconFromUrl(String avatarUrl) {
        try {
            // Tạo View từ layout xml
            View markerView = getLayoutInflater().inflate(R.layout.img_friend_location, null);

            // Tìm CircleImageView trong markerView
            CircleImageView circleImageView = markerView.findViewById(R.id.avatarImageView);

            if (avatarUrl != null) {
                // Tải avatar từ URL sử dụng Glide và đặt vào CircleImageView
                Glide.with(this)
                        .load(avatarUrl)
                        .into(circleImageView);
            }

            // Chuyển đổi markerView thành Bitmap
            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromView(markerView));

            return markerIcon;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Trả về một BitmapDescriptor mặc định nếu có lỗi xảy ra
        return BitmapDescriptorFactory.defaultMarker();
    }

    private Bitmap getBitmapFromView(View view) {
        // Đo đạc kích thước của view
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        // Tạo bitmap với kích thước của view
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        // Tạo canvas để vẽ view lên bitmap
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);

        return bitmap;
    }


}





