package com.ndm.da_test.Fragment;

import static com.ndm.da_test.Activity.MainActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ndm.da_test.API.NotificationApi;
import com.ndm.da_test.API.RetrofitClient;
import com.ndm.da_test.Activity.MapActivity;
import com.ndm.da_test.Entities.DistanceCalculator;
import com.ndm.da_test.Entities.Noti;
import com.ndm.da_test.Entities.TokenLocation;
import com.ndm.da_test.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FireAlarmFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private ImageView imgClear;

    private Button btnBaoChay, btnBaoChayMap;

    private TextView tv_location;

    private View view;

    private NotificationApi notificationApi;

    private String locality;

    private String addressText;

    private Address address;

    private String mytoken;

    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fire_alarm, container, false);
        initUi(view);
        getCurrentLocation();
        takeToken();
        notificationApi = RetrofitClient.getClient().create(NotificationApi.class);
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
        progressDialog = new ProgressDialog(getContext());
    }

    private void initListen() {

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
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

        btnBaoChay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                sendNotification();


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
                address = addresses.get(0);
                locality = address.getSubLocality(); // Lấy tên phường xã
                if (locality != null && !locality.isEmpty()) {
                    String locationText = "tại địa chỉ " + locality + " đúng không?";
                    tv_location.setText(locationText);
                } else {
                    // Nếu không có thông tin về phường xã, sử dụng địa chỉ tổng quát
                    addressText = "tại địa chỉ " + address.getAddressLine(0) + " đúng không?";
                    tv_location.setText(addressText);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String locationAddress;
                        if (locality == null) {
                            locationAddress = address.getAddressLine(0);
                        } else {
                            locationAddress = locality;
                        }

                        // Khởi tạo tham chiếu đến "tokens" trong cơ sở dữ liệu Firebase Realtime
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference tokensRef = database.getReference("tokens");
                        DatabaseReference notificationsRef = database.getReference("notifications");
                        List<String> tokens = new ArrayList<>();
                        List<TokenLocation> mTokenLocation = new ArrayList<>();

                        try {
                            // Lấy danh sách token từ cơ sở dữ liệu
                            tokensRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        for (DataSnapshot tokenLocationList : dataSnapshot.getChildren()) {
                                            TokenLocation tokenLocation = tokenLocationList.getValue(TokenLocation.class);
                                            mTokenLocation.add(tokenLocation);
                                        }
                                    }

                                    for (TokenLocation tokenLocation : mTokenLocation) {
                                        double tokenLatitude = tokenLocation.getLatitude();
                                        double tokenLongitude = tokenLocation.getLongitude();

                                        DistanceCalculator distanceCalculator = new DistanceCalculator();
                                        float distance = distanceCalculator.calculateDistance(location.getLatitude(), location.getLongitude(), tokenLatitude, tokenLongitude);

                                        if (distance <= 100) {
                                            // Nếu có, thêm TokenLocation vào danh sách
                                            tokens.add(tokenLocation.getDeviceToken());
                                        }
                                    }

                                    Set<String> uniqueTokens = new HashSet<>(tokens);


                                    uniqueTokens.remove(mytoken);
                                    // Tạo đối tượng Noti với tiêu đề, nội dung và danh sách token
                                    Noti notice = new Noti();
                                    notice.setTitle("Thông báo báo cháy");
                                    notice.setBody("Địa chỉ: " + locationAddress);
                                    notice.setToken(new ArrayList<>(uniqueTokens));
                                    notice.setLongitude(location.getLongitude());
                                    notice.setLatitude(location.getLatitude());
                                    Log.d("Noti", "token" + tokens);

                                    notificationsRef.push().setValue(notice)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Xử lý khi gặp lỗi trong quá trình gửi thông báo
                                                    Toast.makeText(requireContext(), "Lỗi khi gửi thông báo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    // Gọi phương thức API để gửi thông báo
                                    Call<Noti> call = notificationApi.sendNotification(notice);
                                    call.enqueue(new Callback<Noti>() {
                                        @Override
                                        public void onResponse(Call<Noti> call, Response<Noti> response) {
                                            progressDialog.dismiss(); // Tắt progressDialog sau khi nhận được phản hồi từ server
                                            if (response.isSuccessful()) {
                                                // Gửi thông báo đến Firebase Realtime Database
                                                // Đóng fragment hiện tại và mở fragment thông báo mới
                                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                                // Đóng fragment hiện tại
                                                fragmentTransaction.remove(FireAlarmFragment.this);

                                                // Mở fragment thông báo mới
                                                fragmentTransaction.replace(R.id.fragment_container2, new ThongBaoFragment());
                                                fragmentTransaction.commit();

                                                Toast.makeText(requireContext(), "Thông báo đã được gửi", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Xử lý khi gặp lỗi trong quá trình gửi thông báo
                                                Toast.makeText(requireContext(), "Lỗi khi gửi thông báo", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Noti> call, Throwable t) {
                                            Log.d("hello", t.getMessage());
                                            // Xử lý khi gặp lỗi trong quá trình gọi API
                                            Toast.makeText(requireContext(), "Lỗi khi gọi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Xử lý khi gặp lỗi trong quá trình đọc cơ sở dữ liệu
                                    Toast.makeText(requireContext(), "Lỗi khi đọc cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            // Xử lý ngoại lệ ở đây
                            Log.d("Exception", "Exception: " + e.getMessage());
                        }
                    } else {
                        // Xử lý khi không lấy được vị trí hiện tại
                        Toast.makeText(requireContext(), "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Yêu cầu quyền truy cập vị trí nếu chưa được cấp
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void takeToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    mytoken = task.getResult();
                    Log.d("mytoken", "Token: " + mytoken); // Imprime el token en el registro de logs
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                        Log.d("mytoken1", exception.getMessage());
                    }
                }
            }
        });
    }

}




