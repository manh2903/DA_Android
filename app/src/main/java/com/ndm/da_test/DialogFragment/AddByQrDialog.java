package com.ndm.da_test.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddByQrDialog extends DialogFragment {

    private View view;

    private Button btn_huy, btn_add;

    private CircleImageView img_avatar;

    private TextView tv_name;

    private String friendID;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.dialog_add_by_qr, container, false);
        initUI();

        Bundle bundle = getArguments();
        if (bundle != null) {
            friendID = bundle.getString("userId");
            if (friendID != null && !friendID.isEmpty()) {
                Log.d("UserID", "User ID: " + friendID);
                getInfo();
            } else {
                Log.d("UserID", "friendID is null or empty");
            }
        } else {
            Log.d("UserID", "Bundle is null");
        }
        initListen();
        return view;
    }
    private void initUI() {

        img_avatar = view.findViewById(R.id.img_avatar);
        btn_huy = view.findViewById(R.id.btn_huy_add1);
        btn_add = view.findViewById(R.id.btn_ok_add1);
        tv_name = view.findViewById(R.id.tv_name);

    }

    private void initListen() {
        btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acpAdd();
            }
        });
    }

    private void getInfo(){
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child("users").child(friendID);
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy thông tin người dùng từ snapshot
                    String userName = snapshot.child("fullName").getValue(String.class);
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                    // Hiển thị thông tin lên giao diện người dùng

                    Log.d("username",userName);
                    tv_name.setText("Bạn có muốn kết bạn với "+ userName +" không ?");
                    Glide.with(getActivity()).load(avatarUrl).error(R.drawable.ic_friend).into(img_avatar);
                    Log.d("GetInfo", "have user found with ID: " + friendID);
                } else {
                    // Không tìm thấy người dùng với friendID tương ứng
                    Log.d("GetInfo", "No user found with ID: " + friendID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi truy vấn bị hủy bỏ
                Log.e("GetInfo", "Database query cancelled.", error.toException());
            }
        });
    }

    private void acpAdd() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String currentUserId = currentUser.getUid();
        DatabaseReference senderFriendRef = FirebaseDatabase.getInstance().getReference().child("friend").child(currentUserId);
        senderFriendRef.child(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Người nhận đã là bạn bè của người gửi
                    Toast.makeText(getActivity(), "Người này đã là bạn bè của bạn!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else if (currentUserId.equals(friendID)) {
                    Toast.makeText(getActivity(), "Bạn không thể kết bạn với chính bạn!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    // Kiểm tra xem đã gửi lời mời kết bạn đến người dùng đó chưa
                    DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("sendFriend").child(currentUserId);
                    requestsRef.orderByValue().equalTo(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Người dùng đã nhận được lời mời kết bạn từ bạn trước đó
                                Toast.makeText(getActivity(), "Bạn đã gửi lời mời kết bạn đến người này!", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                // Người nhận chưa là bạn bè của người gửi và chưa nhận được lời mời kết bạn từ bạn
                                // Lưu ID của người nhận vào danh sách trong nút của người gửi
                                DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("sendFriend").child(currentUserId);
                                requestsRef.push().setValue(friendID);

                                DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("receiverFriend").child(friendID);
                                receiverRef.push().setValue(currentUserId);

                                // Hiển thị thông báo gửi lời mời thành công
                                Toast.makeText(getActivity(), "Gửi lời mời kết bạn thành công!", Toast.LENGTH_SHORT).show();

                                // Sau khi thực hiện xong, bạn có thể đóng fragment này và quay lại fragment trước đó
                                dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Đã xảy ra lỗi khi truy vấn Realtime Database
                            Toast.makeText(getActivity(), "Đã xảy ra lỗi khi kiểm tra bạn bè!", Toast.LENGTH_SHORT).show();
                            Log.e("AddByEmailFragment", "Error getting data", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Đã xảy ra lỗi khi truy vấn Realtime Database
                Toast.makeText(getActivity(), "Đã xảy ra lỗi khi kiểm tra bạn bè!", Toast.LENGTH_SHORT).show();
                Log.e("AddByEmailFragment", "Error getting data", databaseError.toException());
            }
        });
    }



}
