package com.ndm.da_test.BottomSheetDialog;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ndm.da_test.DialogFragment.DeleteFrDialog;
import com.ndm.da_test.Entities.User;
import com.ndm.da_test.R;


public class DeleteFriend extends BottomSheetDialog {
    private User user;
    private Button btn_delete, btn_huy;

    private FragmentManager fragmentManager;

    public OnDeleteFriendSuccessListener onDeleteFriendSuccessListener;

    public interface OnDeleteFriendSuccessListener {
        void onDeleteFriendSuccess();
    }

    public void setOnDeleteFriendSuccessListener(OnDeleteFriendSuccessListener listener) {
        onDeleteFriendSuccessListener = listener;
    }

    public DeleteFriend(@NonNull Context context, User user, FragmentManager fragmentManager) {
        super(context);
        this.user = user;
        this.fragmentManager = fragmentManager;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottomsheet_delete_friend);
        initUI();
        initListen();
    }

    private void initUI() {
        btn_delete = findViewById(R.id.btn_delete);
        btn_huy = findViewById(R.id.btn_huy);
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
                showDeleteFrDialog(user);
                dismiss();
                if (onDeleteFriendSuccessListener != null) {
                    onDeleteFriendSuccessListener.onDeleteFriendSuccess();
                }
            }
        });
    }


    private void showDeleteFrDialog(User user) {
        DeleteFrDialog deleteFrDialog = new DeleteFrDialog(user); // Truyền đối tượng User vào constructor
        deleteFrDialog.show(fragmentManager , "hihi");
    }
}
