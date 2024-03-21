package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ndm.da_test.R;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private FrameLayout notification;
    public GoogleMap gMap;

    private Geocoder geocoder;
    public FusedLocationProviderClient fusedLocationClient;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Toolbar toolbar;

    private TextView tv_location;

    private Button btn_myposition;

    private SearchView mapSearch;
    private ListView addressListView;
    private ArrayAdapter<String> addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapSearch= findViewById(R.id.mapSearch);
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

        // Kiểm tra quyền truy cập vị trí
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Nếu đã có quyền, lấy vị trí hiện tại
            getCurrentLocation();
        }

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
                gMap.clear();
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

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Xóa tất cả các marker trước đó
                gMap.clear();
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
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
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
                                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
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
                double latitude = firstAddress.getLatitude();
                double longitude = firstAddress.getLongitude();
                LatLng locationLatLng = new LatLng(latitude, longitude);
                // Di chuyển bản đồ đến vị trí được tìm kiếm
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 17));
            } else {
                // Không tìm thấy địa điểm phù hợp
                Toast.makeText(MapActivity.this, "No matching location found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
