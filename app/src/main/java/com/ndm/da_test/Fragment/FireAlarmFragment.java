package com.ndm.da_test.Fragment;

import static com.ndm.da_test.Activity.MainActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ndm.da_test.Activity.MapActivity;
import com.ndm.da_test.R;

import java.io.IOException;
import java.util.List;

public class FireAlarmFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;

    private ImageView imgClear;

    private Button btnBaoChay, btnBaoChayMap;

    private TextView tv_location;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fire_alarm, container, false);
        initUi(view);
        getCurrentLocation();
        initListen();
        return view;
    }

    private void initUi(View view) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        geocoder = new Geocoder(requireContext());
        imgClear = view.findViewById(R.id.img_clear);
        tv_location = view.findViewById(R.id.text_location);
        btnBaoChay = view.findViewById(R.id.btn_baochay);
        btnBaoChayMap = view.findViewById(R.id.btn_baochay_map);
    }

    private void initListen(){

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null) {
                    getActivity().onBackPressed(); // Đóng Fragment và quay lại Fragment trước đó
                }
            }
        });

        btnBaoChayMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.detach(FireAlarmFragment.this);
                fragmentTransaction.commit();

                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        updateLocationText(location);
                    } else {
                        Toast.makeText(requireContext(), "Cannot get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Yêu cầu quyền truy cập vị trí
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void updateLocationText(Location location) {
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String locality = address.getSubLocality(); // Lấy tên phường xã
                if (locality != null && !locality.isEmpty()) {
                    String locationText = "tại địa chỉ " + locality + " đúng không?";
                    tv_location.setText(locationText);
                } else {
                    // Nếu không có thông tin về phường xã, sử dụng địa chỉ tổng quát
                    String addressText = "tại địa chỉ " + address.getAddressLine(0) + " đúng không?";
                    tv_location.setText(addressText);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
