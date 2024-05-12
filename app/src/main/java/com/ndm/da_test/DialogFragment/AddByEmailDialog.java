package com.ndm.da_test.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.R;

public class AddByEmailDialog extends DialogFragment {

    private View view;

    private Button btn_huy, btn_add;

    private EditText edt_email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_add_by_email, container, false);
        initUI();
        initListen();
        return view;
    }

    private void initUI() {
        btn_huy = view.findViewById(R.id.btn_huy_add);
        btn_add = view.findViewById(R.id.btn_ok_add);
        edt_email = view.findViewById(R.id.edt_email);
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
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    return;
                }

                // Lấy email từ EditText
                final String email = edt_email.getText().toString();

                //id người gửi
                String currentUserId = currentUser.getUid();

                // Kiểm tra xem email có hợp lệ không (ở đây bạn có thể thêm logic kiểm tra hợp lệ của email)

                // Thực hiện gửi lời mời kết bạn và lưu vào Realtime Database
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                final String receiverId = userSnapshot.getKey();

                                // Kiểm tra xem người gửi và người nhận đã là bạn bè chưa
                                DatabaseReference senderFriendRef = FirebaseDatabase.getInstance().getReference().child("friend").child(currentUserId);
                                senderFriendRef.child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // Người nhận đã là bạn bè của người gửi
                                            Toast.makeText(getActivity(), "Người này đã là bạn bè của bạn!", Toast.LENGTH_SHORT).show();
                                            dismiss();
                                        } else if (currentUserId.equals(receiverId)) {
                                            Toast.makeText(getActivity(), "Bạn không thể kết bạn với chính bạn!", Toast.LENGTH_SHORT).show();
                                            dismiss();
                                        } else {
                                            // Người nhận chưa là bạn bè của người gửi
                                            // Lưu ID của người nhận vào danh sách trong nút của người gửi
                                            DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("sendFriend").child(currentUserId);
                                            requestsRef.push().setValue(receiverId);

                                            DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("receiverFriend").child(receiverId);
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
                        } else {
                            // Email không tồn tại trong bảng users
                            Toast.makeText(getActivity(), "Email không tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Đã xảy ra lỗi khi truy vấn Realtime Database
                        Toast.makeText(getActivity(), "Đã xảy ra lỗi khi kiểm tra email!", Toast.LENGTH_SHORT).show();
                        Log.e("AddByEmailFragment", "Error getting data", databaseError.toException());
                    }
                });
            }
        });
    }
}
