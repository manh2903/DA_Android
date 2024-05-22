package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.Adapter.NotiAdapter;
import com.ndm.da_test.Entities.Data;

import com.ndm.da_test.Interface.IClickNotiListener;
import com.ndm.da_test.R;
import com.ndm.da_test.Utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rcv_noti;
    private ProgressDialog progressDialog;

    private NotiAdapter notiAdapter;

    private List<Data> noti_receiverList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        initUi();
        getListRealTimedb();
    }

    private void initUi() {

        progressDialog = new ProgressDialog(this);
        rcv_noti = findViewById(R.id.rcv_noti);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv_noti.setLayoutManager(linearLayoutManager);

        noti_receiverList = new ArrayList<>();

        notiAdapter = new NotiAdapter(noti_receiverList, new IClickNotiListener() {
            @Override
            public void onItemClick(Data notiReceiver) {

                progressDialog.show();
                onClickGoToDetail(notiReceiver);
            }
        });

        rcv_noti.setAdapter(notiAdapter);
    }

    private void getListRealTimedb() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("notifications_receiver").child(Utils.getUserId());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Notification", "onDataChange() called");
                noti_receiverList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("notificationTitle").getValue(String.class);
                    String body = dataSnapshot.child("notificationBody").getValue(String.class);
                    String time = dataSnapshot.child("timestamp").getValue(String.class);
                    String type = dataSnapshot.child("type").getValue(String.class);

//                    Double latitude = dataSnapshot.child("latitude").exists() ?
//                            dataSnapshot.child("latitude").getValue(Double.class) : null;
//
//                    Double longitude = dataSnapshot.child("longitude").exists() ?
//                            dataSnapshot.child("longitude").getValue(Double.class) : null;
//
//                    String source = dataSnapshot.child("source").exists() ?
//                            dataSnapshot.child("source").getValue(String.class) : null;


                    Double latitude = dataSnapshot.child("latitude").exists() ?
                            dataSnapshot.child("latitude").getValue(Double.class) : null;

                    Double longitude = dataSnapshot.child("longitude").exists() ?
                            dataSnapshot.child("longitude").getValue(Double.class) : null;

                    String source = dataSnapshot.child("source").exists() ?
                            dataSnapshot.child("source").getValue(String.class) : null;

                    String list_question = dataSnapshot.child("list_question").exists() ?
                            dataSnapshot.child("list_question").getValue(String.class) : null;

                    Data notiReceiver = new Data();


                    if (type.equals("type_incoming_call")) {
                        if (latitude != null && longitude != null) {
                            notiReceiver = new Data(type, title, body, latitude.doubleValue(), longitude.doubleValue(), time);
                        } else {
                            // Xử lý trường hợp latitude hoặc longitude bị null
                            // Ví dụ: sử dụng giá trị mặc định hoặc bỏ qua dữ liệu này
                            notiReceiver = new Data(type, title, body, 0.0, 0.0, time);
                        }
                    } else if (type.equals("question")) {
                        notiReceiver = new Data(type, title, body, time, list_question);
                    } else if (type.equals("noti")) {
                        notiReceiver = new Data(type, title, body, time, source);
                    }

                    noti_receiverList.add(notiReceiver);
                }
                Collections.sort(noti_receiverList, new Comparator<Data>() {
                    @Override
                    public int compare(Data d1, Data d2) {
                        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        try {
                            String time1 = d1.getTime();
                            String time2 = d2.getTime();

                            if (time1 != null && time2 != null) {
                                Date date1 = format.parse(time1);
                                Date date2 = format.parse(time2);
                                return date2.compareTo(date1); // Sắp xếp giảm dần theo thời gian
                            } else {
                                // Xử lý trường hợp time1 hoặc time2 bị null
                                // Ví dụ: giữ nguyên thứ tự hoặc áp dụng quy tắc sắp xếp khác
                                return 0;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

                notiAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("SkillFragment", "onCancelled() called");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Kết thúc Activity hiện tại
        } else {
            super.onBackPressed(); // Nếu có màn hình trước đó trong back stack, hoạt động như bình thường
        }
    }


    private void onClickGoToDetail(Data notiReceiver) {
        if (notiReceiver.getType().equals("type_incoming_call")) {
            Intent intent = new Intent(this, DetailNotiActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Noti Receiver", notiReceiver);
            intent.putExtras(bundle);
            startActivity(intent);

        } else if (notiReceiver.getType().equals("question")) {

            Intent intent = new Intent(this, QuizzActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Noti Receiver", notiReceiver);
            intent.putExtras(bundle);
            startActivity(intent);

        } else if (notiReceiver.getType().equals("noti")) {

            Intent intent = new Intent(this, DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Noti Receiver", notiReceiver);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}