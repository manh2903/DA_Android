package com.ndm.da_test.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
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
        initListen();
        Bundle bundle = getArguments();
        if (bundle != null) {
            friendID = bundle.getString("userId");
            if (friendID != null) {
                // Sử dụng ID ở đây
                Log.d("UserID", "User ID: " + friendID);
            }
        }
        getInfo();
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
                dismiss();
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

}
