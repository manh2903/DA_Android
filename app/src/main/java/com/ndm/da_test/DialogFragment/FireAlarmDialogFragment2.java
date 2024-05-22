package com.ndm.da_test.DialogFragment;

import android.app.ProgressDialog;
import android.location.Geocoder;
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
import com.ndm.da_test.Entities.Data;
import com.ndm.da_test.Entities.DistanceCalculator;
import com.ndm.da_test.Entities.Noti_v2;
import com.ndm.da_test.Entities.TokenLocation;

import com.ndm.da_test.R;
import com.ndm.da_test.Utils.Utils;

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

public class FireAlarmDialogFragment2 extends DialogFragment {
    private ImageView imgClear;
    private Button btnBaoChay;
    private TextView tv_location;
    private View view;

    private NotificationApi notificationApi;
    private String mytoken;
    private double longitude, latitude;
    private String address;
    private ProgressDialog progressDialog;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference tokensRef = database.getReference("tokens");
    List<String> tokens = new ArrayList<>();
    List<TokenLocation> mTokenLocation = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentDialog);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fire_alarm2, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            longitude = bundle.getDouble("longitude", 0.0);
            latitude = bundle.getDouble("latitude", 0.0);
            address = bundle.getString("address", "");

            Log.d("FireAlarmDialogFragment2", longitude + latitude + address);

        }

        takeToken();
        initUi();
        getToken();
        notificationApi = RetrofitClient.getClient().create(NotificationApi.class);
        initListen();
        tv_location.setText(address);

        return view;
    }

    private void initUi() {

        imgClear = view.findViewById(R.id.img_clear);
        tv_location = view.findViewById(R.id.text_location);
        btnBaoChay = view.findViewById(R.id.btn_baochay);
        progressDialog = new ProgressDialog(getContext());
    }

    private void initListen() {

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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

    private void getToken() {

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

        DatabaseReference tokenFr = database.getReference("friend").child(Utils.getUserId());
        tokenFr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String IDFriend = dataSnapshot.getKey();
                    DatabaseReference tokensFR = database.getReference("tokens").child(IDFriend);
                    tokensFR.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                String tokenLocationFR = dataSnapshot1.getKey();
                                Log.d("tokenLocationFR", tokenLocationFR);
                                tokens.add(tokenLocationFR);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendNotification() {


        // Khởi tạo tham chiếu đến "tokens" trong cơ sở dữ liệu Firebase Realtime

        for (TokenLocation tokenLocation : mTokenLocation) {
            double tokenLatitude = tokenLocation.getLatitude();
            double tokenLongitude = tokenLocation.getLongitude();

            DistanceCalculator distanceCalculator = new DistanceCalculator();
            float distance = distanceCalculator.calculateDistance(latitude, longitude, tokenLatitude, tokenLongitude);

            if (distance <= 100) {
                // Nếu có, thêm TokenLocation vào danh sách
                tokens.add(tokenLocation.getDeviceToken());
            }
        }

        Set<String> uniqueTokens = new HashSet<>(tokens);


//                    uniqueTokens.remove(mytoken);
//                    // Tạo đối tượng Noti với tiêu đề, nội dung và danh sách token
//                    Noti_v1 notice = new Noti_v1();
//                    notice.setTitle("Thông báo báo cháy");
//                    notice.setBody("Địa chỉ: " + address);
//                    notice.setToken(new ArrayList<>(uniqueTokens));
//                    notice.setLongitude(longitude);
//                    notice.setLatitude(latitude);
//                    Log.d("Noti", "token" + tokens);
//
//                    notificationsRef.push().setValue(notice)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    // Xử lý khi gặp lỗi trong quá trình gửi thông báo
//                                    Toast.makeText(requireContext(), "Lỗi khi gửi thông báo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
        Data data = new Data();
        data.setType("type_incoming_call");
        data.setTitle("Thông báo báo cháy");
        data.setBody("Địa chỉ: " + address);
        data.setLatitude(latitude);
        data.setLongitude(longitude);


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

        // Gọi phương thức API để gửi thông báo
        Call<Noti_v2> call = notificationApi.sendNotification_v2(noti_v2);
        call.enqueue(new Callback<Noti_v2>() {
            @Override
            public void onResponse(Call<Noti_v2> call, Response<Noti_v2> response) {
                progressDialog.dismiss(); // Tắt progressDialog sau khi nhận được phản hồi từ server
                if (response.isSuccessful()) {

                    dismiss();

                    ThongBaoDialogFragment thongBaoDialogFragment = new ThongBaoDialogFragment();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();


                    thongBaoDialogFragment.show(fragmentManager, "thông báo");


                    Toast.makeText(requireContext(), "Thông báo đã được gửi", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý khi gặp lỗi trong quá trình gửi thông báo
                    Toast.makeText(requireContext(), "Lỗi khi gửi thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Noti_v2> call, Throwable t) {
                Log.d("hello", t.getMessage());
                // Xử lý khi gặp lỗi trong quá trình gọi API
                Toast.makeText(requireContext(), "Lỗi khi gọi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Xử lý khi gặp lỗi trong quá trình đọc cơ sở dữ liệu
//                    Toast.makeText(requireContext(), "Lỗi khi đọc cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (Exception e) {
//            // Xử lý ngoại lệ ở đây
//            Log.d("Exception", "Exception: " + e.getMessage());
//        }
//      }

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
