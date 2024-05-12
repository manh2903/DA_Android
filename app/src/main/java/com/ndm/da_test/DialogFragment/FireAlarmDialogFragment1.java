package com.ndm.da_test.DialogFragment;

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
import androidx.fragment.app.DialogFragment;
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
import com.ndm.da_test.Entities.Data;
import com.ndm.da_test.Entities.DistanceCalculator;
import com.ndm.da_test.Entities.Noti_v2;
import com.ndm.da_test.Entities.TokenLocation;
import com.ndm.da_test.R;
import com.ndm.da_test.Utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FireAlarmDialogFragment1 extends DialogFragment {

    private DialogListener listener;
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private ImageView imgClear;

    private Button btnBaoChay, btnBaoChayMap;

    public FireAlarmDialogFragment1(DialogListener listener) {
        this.listener = listener;
    }

    private TextView tv_location;

    private View view;

    private NotificationApi notificationApi;

    private String locality;

    private String addressText;

    private Address address;

    private String mytoken;

    private ProgressDialog progressDialog;

    private Location currentLocation;
    private List<String> tokens = new ArrayList<>();
    private List<TokenLocation> mTokenLocation = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentDialog);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fire_alarm, container, false);
        initUi(view);
        getCurrentLocation();
        takeToken();
        getToken();
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
                dismiss();
            }
        });

        btnBaoChayMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng FireAlarmFragment1 trước khi mở MapActivity
                dismiss();

                // Mở MapActivity
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
                        currentLocation = location;
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
                    addressText = "tại địa chỉ " + address.getAddressLine(0) + " đúng không?";
                    tv_location.setText(addressText);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getToken() {

        DatabaseReference tokensRef = database.getReference("tokens");
        tokensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot tokenLocationList : dataSnapshot.getChildren()) {
                        TokenLocation tokenLocation = tokenLocationList.getValue(TokenLocation.class);
                        mTokenLocation.add(tokenLocation);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void sendNotification() {

        if (tv_location.getText() != "") {
            String locationAddress;
            if (locality == null) {
                locationAddress = address.getAddressLine(0);
            } else {
                locationAddress = locality;
            }

            // Khởi tạo tham chiếu đến "tokens" trong cơ sở dữ liệu Firebase Realtime
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference tokensRef = database.getReference("tokens");
//
//            List<String> tokens = new ArrayList<>();
//            List<TokenLocation> mTokenLocation = new ArrayList<>();
//
//            try {
//                // Lấy danh sách token từ cơ sở dữ liệu
//                tokensRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            for (DataSnapshot tokenLocationList : dataSnapshot.getChildren()) {
//                                TokenLocation tokenLocation = tokenLocationList.getValue(TokenLocation.class);
//                                mTokenLocation.add(tokenLocation);
//                            }
//                        }

            for (TokenLocation tokenLocation : mTokenLocation) {
                double tokenLatitude = tokenLocation.getLatitude();
                double tokenLongitude = tokenLocation.getLongitude();

                DistanceCalculator distanceCalculator = new DistanceCalculator();
                float distance = distanceCalculator.calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), tokenLatitude, tokenLongitude);
                Log.d("distance : " , String.valueOf(distance));

                if (distance <= 100) {
                    // Nếu có, thêm TokenLocation vào danh sách
                    tokens.add(tokenLocation.getDeviceToken());
                }
            }

            Set<String> uniqueTokens = new HashSet<>(tokens);

            uniqueTokens.remove(mytoken);
            // Tạo đối tượng Noti với tiêu đề, nội dung và danh sách token
//                                    Noti_v1 notice = new Noti_v1();
//                                    notice.setTitle("Thông báo báo cháy");
//                                    notice.setBody("Địa chỉ: " + locationAddress);
//                                    notice.setToken(new ArrayList<>(uniqueTokens));
//                                    notice.setLongitude(location.getLongitude());
//                                    notice.setLatitude(location.getLatitude());
//                                    Log.d("Noti", "token" + tokens);

            Data data = new Data();
            data.setType("type_incoming_call");
            data.setTitle("Thông báo báo cháy");
            data.setBody("Địa chỉ: " + locationAddress);
            data.setLatitude(currentLocation.getLatitude());
            data.setLongitude(currentLocation.getLongitude());





            Noti_v2 noti_v2 = new Noti_v2();
            noti_v2.setData(data);
            noti_v2.setTime(getFormattedTimestamp());
            noti_v2.setTokens(new ArrayList<>(uniqueTokens));

            Log.d("Data", "Type: " + data.getType());
            Log.d("Data", "Title: " + data.getTitle());
            Log.d("Data", "Body: " + data.getBody());
            Log.d("Data", "Latitude: " + data.getLatitude());
            Log.d("Data", "Longitude: " + data.getLongitude());

            Log.d("Noti_v2", "Tokens: " + noti_v2.getTokens());


            DatabaseReference userNotiRef = database.getReference("notifications_send").child(Utils.getUserId()).push();
            userNotiRef.setValue(noti_v2)
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

            Call<Noti_v2> call = notificationApi.sendNotification_v2(noti_v2);
            call.enqueue(new Callback<Noti_v2>() {
                @Override
                public void onResponse(Call<Noti_v2> call, Response<Noti_v2> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        dismiss();

                        ThongBaoDialogFragment thongBaoDialogFragment = new ThongBaoDialogFragment();
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.add(thongBaoDialogFragment,"thông báo" );
//                    transaction.commit();

                        thongBaoDialogFragment.show(fragmentManager, "thông báo");

                    } else {
                        Toast.makeText(requireContext(), "Lỗi khi gửi thông báo", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Noti_v2> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("Notification API", "Failed to send notifications: " + t.getMessage(), t);
                    Toast.makeText(requireContext(), "Lỗi khi gọi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        // Xử lý khi gặp lỗi trong quá trình đọc cơ sở dữ liệu
//                        Toast.makeText(requireContext(), "Lỗi khi đọc cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (Exception e) {
//                // Xử lý ngoại lệ ở đây
//                Log.d("Exception", "Exception: " + e.getMessage());
//            }
//        }
        else {
            // Xử lý khi không lấy được vị trí hiện tại
            Toast.makeText(requireContext(), "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
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

    private String getFormattedTimestamp() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

}

