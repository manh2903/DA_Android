package com.ndm.da_test.Fragment;

import static com.ndm.da_test.Activity.MainActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.API.NotificationApi;
import com.ndm.da_test.API.RetrofitClient;
import com.ndm.da_test.Activity.MainActivity;
import com.ndm.da_test.Activity.MapActivity;
import com.ndm.da_test.Entities.Escape;
import com.ndm.da_test.Entities.Noti;
import com.ndm.da_test.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fire_alarm, container, false);
        initUi(view);
        getCurrentLocation();

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


    // Code để gửi thông báo
    private void sendNotification() {
        // Lấy địa chỉ hiện tại
        String location = tv_location.getText().toString();

        // Khởi tạo tham chiếu đến "tokens" trong cơ sở dữ liệu Firebase Realtime
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokensRef = database.getReference("tokens");
        List<String> tokens = new ArrayList<>();

        // Lấy danh sách token từ cơ sở dữ liệu
        tokensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String tokenKey = dataSnapshot.getKey();
                    tokens.add(tokenKey);
                }
                // Tạo đối tượng Noti với tiêu đề, nội dung và danh sách token
                Noti notice = new Noti();
                notice.setTitle("Thông báo báo cháy");
                notice.setBody("Địa chỉ: " + location);
                notice.setToken(tokens);

                Log.d("Noti","Noti : " + notice);

                // Gọi phương thức API để gửi thông báo
                Call<Noti> call = notificationApi.sendNotification(notice);
                call.enqueue(new Callback<Noti>() {
                    @Override
                    public void onResponse(Call<Noti> call, Response<Noti> response) {
                        if (response.isSuccessful()) {
                            displayNotification(notice.getTitle(),notice.getBody());
                            // Xử lý khi gửi thông báo thành công
                            Toast.makeText(requireContext(), "Thông báo đã được gửi", Toast.LENGTH_SHORT).show();
                        } else {
                            // Xử lý khi gặp lỗi trong quá trình gửi thông báo
                            Toast.makeText(requireContext(), "Lỗi khi gửi thông báo", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Noti> call, Throwable t) {
                        Log.d("hello",t.getMessage());
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
    }

    private void displayNotification(String title, String message) {
        // Tạo intent để mở MainActivity khi người dùng nhấn vào thông báo
        Intent intent = new Intent(requireContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Tạo builder thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "Your_Channel_ID")
                .setSmallIcon(R.drawable.ic_notification) // Icon nhỏ của thông báo
                .setContentTitle(title) // Tiêu đề của thông báo
                .setContentText(message) // Nội dung của thông báo
                .setContentIntent(pendingIntent) // Intent để mở khi nhấn vào thông báo
                .setAutoCancel(true) // Tự động đóng thông báo khi nhấn vào
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Đặt độ ưu tiên cao
                .setCategory(NotificationCompat.CATEGORY_CALL) // Đặt loại thông báo là cuộc gọi
                .setFullScreenIntent(pendingIntent, true); // Sử dụng intent để mở khi nhấn vào thông báo

        // Đặt âm thanh cho thông báo (có thể thay đổi theo nhu cầu của bạn)
        builder.setSound(android.provider.Settings.System.DEFAULT_RINGTONE_URI);

        // Nhận NotificationManager từ hệ thống
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Kiểm tra phiên bản Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Tạo kênh thông báo trên Android 8+
            NotificationChannel channel = new NotificationChannel("Your_Channel_ID", "Channel_Name", NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Hiển thị thông báo
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }



}