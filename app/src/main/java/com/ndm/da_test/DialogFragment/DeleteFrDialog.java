package com.ndm.da_test.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.Entities.User;
import com.ndm.da_test.R;

public class DeleteFrDialog extends DialogFragment {
    private View view;
    private Button btn_delete, btn_huy;
    private TextView txt_delete;
    private User user;

    public DeleteFrDialog(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_delete_fr, container, false);
        initUI();
        initListen();
        return view;
    }

    private void initUI() {
        btn_delete = view.findViewById(R.id.btn_delete);
        btn_huy = view.findViewById(R.id.btn_huy);
        txt_delete = view.findViewById(R.id.txt_delete);
        String full_name = user.getFullName();
        String[] parts = full_name.split(" ");
        txt_delete.setText("Bạn có chắc chắn muốn xóa " + parts[parts.length - 1] + " khỏi danh sách bạn bè? Việc xóa một người bạn trong danh sách sẽ làm vô hiệu hóa quyền truy cập chia sẻ vị trí và cả hai bạn sẽ không thể nhìn thấy vị trí của nhau nữa.");

    }
    private void initListen() {
        btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriend();
                dismiss();
            }
        });
    }
    private void deleteFriend() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            DatabaseReference deleteFr1 = FirebaseDatabase.getInstance().getReference().child("friend").child(currentUserId);

            DatabaseReference deleteFr2 = FirebaseDatabase.getInstance().getReference().child("friend").child(user.getId());
            deleteFr1.orderByKey().equalTo(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        snapshot1.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Xóa bạn thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("DeleteFriend", "Error reading database", error.toException());
                }
            });

            deleteFr2.orderByKey().equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        snapshot1.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("DeleteFriend", "Error reading database", error.toException());
                }
            });
        }
    }
}
