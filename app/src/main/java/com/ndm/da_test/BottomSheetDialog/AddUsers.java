package com.ndm.da_test.BottomSheetDialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.Adapter.ReceiverAdapter;
import com.ndm.da_test.Adapter.RequestSendAdapter;
import com.ndm.da_test.DialogFragment.AddByEmailDialog;
import com.ndm.da_test.DialogFragment.AddByQrDialog;
import com.ndm.da_test.Entities.User;
import com.ndm.da_test.R;
import java.util.ArrayList;
import java.util.List;

public class AddUsers extends BottomSheetDialog {
    private LinearLayout layoutEmail, layoutQR;
    private RecyclerView rcv_request, rcv_receiver;
    private TextView txt_Count_Send, txt_Count_Receiver;

    private List<User> mListUser = new ArrayList<>();
    private List<User> mListUserRcv = new ArrayList<>();
    private RequestSendAdapter requestSendAdapter;
    private ReceiverAdapter receiverAdapter;
    private FragmentManager fragmentManager; // Biến FragmentManager

    public AddUsers(@NonNull Context context, FragmentManager fragmentManager) {
        super(context);
        this.fragmentManager = fragmentManager; // Khởi tạo fragmentManager
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottomsheet_add_users);

        getUserInfoFromRequest();
        getUserInfoFromReceiver();

        initUI();
        initListen();
    }

    private void initUI() {

        layoutQR = findViewById(R.id.layout_addbyQR);
        layoutEmail = findViewById(R.id.layout_addbyemail);
        txt_Count_Send = findViewById(R.id.txt_count_send);
        txt_Count_Receiver = findViewById(R.id.txt_count_receiver);

        rcv_request = findViewById(R.id.rcv_request);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcv_request.setLayoutManager(linearLayoutManager);
        requestSendAdapter = new RequestSendAdapter(mListUser);
        rcv_request.setAdapter(requestSendAdapter);

        rcv_receiver = findViewById(R.id.rcv_accept);
        LinearLayoutManager linearLayoutManagerReceiver = new LinearLayoutManager(getContext());
        rcv_receiver.setLayoutManager(linearLayoutManagerReceiver);
        receiverAdapter = new ReceiverAdapter(mListUserRcv);
        rcv_receiver.setAdapter(receiverAdapter);
    }

    private void initListen() {
        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một instance của MyDialogFragment và hiển thị nó bằng cách sử dụng FragmentManager đã được truyền vào
                AddByEmailDialog dialogFragment = new AddByEmailDialog();
                dialogFragment.show(fragmentManager, "MyDialogFragment");
            }
        });

        layoutQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddByQrDialog addByQrDialog = new AddByQrDialog();
                addByQrDialog.show(fragmentManager,"MyDialogFragment");
            }
        });
    }



    private void getUserInfoFromRequest() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserId = currentUser.getUid();
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("sendFriend").child(currentUserId);
        requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    String receiverId = requestSnapshot.getValue(String.class);
                    Log.d("receiverId", receiverId);
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(receiverId);
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Lấy thông tin của người nhận từ dataSnapshot
                                String FullName = dataSnapshot.child("fullName").getValue(String.class);
                                String Email = dataSnapshot.child("email").getValue(String.class);
                                String uri = dataSnapshot.child("avatarUrl").getValue(String.class);
                                Log.d("senderFullName", FullName + Email + uri);
                                User user = new User(receiverId, FullName, Email, uri);
                                mListUser.add(user);
                                txt_Count_Send.setText("Lời mời kết bạn mà bạn đã gửi (" + mListUser.size() + ")");
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Đã xảy ra lỗi khi truy vấn Realtime Database
                            Log.e("AddByEmailFragment", "Error getting user data", databaseError.toException());
                        }
                    });
                }
                requestSendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Đã xảy ra lỗi khi truy vấn Realtime Database
                Log.e("AddByEmailFragment", "Error getting requests data", databaseError.toException());
            }
        });
    }

    private void getUserInfoFromReceiver() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String currentUserId = currentUser.getUid();


        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("receiverFriend").child(currentUserId);
        requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    String sendID = requestSnapshot.getValue(String.class);
                    Log.d("receiverId", sendID);
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(sendID);
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Lấy thông tin của người gửi từ dataSnapshot
                                String senderFullName = dataSnapshot.child("fullName").getValue(String.class);
                                String senderEmail = dataSnapshot.child("email").getValue(String.class);
                                String uri = dataSnapshot.child("avatarUrl").getValue(String.class);
                                Log.d("senderFullName", senderFullName + senderEmail + uri);
                                User user = new User(sendID, senderFullName, senderEmail, uri);
                                mListUserRcv.add(user);
                                txt_Count_Receiver.setText("Yêu cầu kết bạn (" + mListUserRcv.size() + ")");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Đã xảy ra lỗi khi truy vấn Realtime Database
                            Log.e("AddByEmailFragment", "Error getting user data", databaseError.toException());
                        }
                    }
                    );
                }
                receiverAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Đã xảy ra lỗi khi truy vấn Realtime Database
                Log.e("AddByEmailFragment", "Error getting requests data", databaseError.toException());
            }
        });
    }

}


