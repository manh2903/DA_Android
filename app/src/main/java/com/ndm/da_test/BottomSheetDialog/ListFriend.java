package com.ndm.da_test.BottomSheetDialog;


import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
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

import com.ndm.da_test.Adapter.FriendAdapter;
import com.ndm.da_test.Entities.User;
import com.ndm.da_test.Interface.IClickItemFriendListener;
import com.ndm.da_test.R;

import java.util.ArrayList;
import java.util.List;

public class ListFriend extends BottomSheetDialog {
    private TextView txtCountFr;
    private RecyclerView rcv_friend;
    private FriendAdapter friendAdapter;
    private List<User> listFriend;
    private FragmentManager fragmentManager;
    public ListFriend(@NonNull Context context, FragmentManager FragmentManager) {

        super(context);
        fragmentManager = FragmentManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListFriend();
        setContentView(R.layout.bottomsheet_listfriend);
        initUi();

    }

    private void initUi() {
        txtCountFr = findViewById(R.id.txt_countfr);
        rcv_friend = findViewById(R.id.rcv_friend);
        listFriend = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcv_friend.setLayoutManager(linearLayoutManager);
        friendAdapter = new FriendAdapter(listFriend, new IClickItemFriendListener() {
            @Override
            public void onItemClick(User user) {
//              onCLickGoToDelete(user);
                onDeleteFriendClicked(user);
            }
        });
        rcv_friend.setAdapter(friendAdapter);
    }
    private void getListFriend() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserId = currentUser.getUid();

        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("friend").child(currentUserId);
        requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    String friendID = requestSnapshot.getKey();
                    Log.d("receiverId", friendID);
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(friendID);
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Lấy thông tin của người gửi từ dataSnapshot
                                String senderFullName = dataSnapshot.child("fullName").getValue(String.class);
                                String senderEmail = dataSnapshot.child("email").getValue(String.class);
                                String uri = dataSnapshot.child("avatarUrl").getValue(String.class);
                                Log.d("senderFullName", senderFullName + senderEmail + uri);
                                User user = new User(friendID, senderFullName, senderEmail, uri);
                                listFriend.add(user);
                                txtCountFr.setText("Bạn bè ("+listFriend.size()+")");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Đã xảy ra lỗi khi truy vấn Realtime Database
                            Log.e("AddByEmailFragment", "Error getting user data", databaseError.toException());
                        }
                    });
                }
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Đã xảy ra lỗi khi truy vấn Realtime Database
                Log.e("AddByEmailFragment", "Error getting requests data", databaseError.toException());
            }
        });
    }

    private void onCLickGoToDelete(User user)
    {
        DeleteFriend delete = new DeleteFriend(getContext(),user ,fragmentManager);
        delete.show();
        if (delete.getWindow() != null) {
            delete.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void onDeleteFriendClicked(User user) {
        DeleteFriend delete = new DeleteFriend(getContext(), user, fragmentManager);
        delete.show();
        if (delete.getWindow() != null) {
            delete.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        delete.setOnDeleteFriendSuccessListener(new DeleteFriend.OnDeleteFriendSuccessListener() {
            @Override
            public void onDeleteFriendSuccess() {
                onFriendDeleted();
            }
        });
    }

    private void onFriendDeleted() {
        // Update the UI or perform any necessary actions when a friend is deleted
    }


}