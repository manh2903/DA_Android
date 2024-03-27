package com.ndm.da_test.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ndm.da_test.Activity.MainActivity;
import com.ndm.da_test.R;

import java.util.HashMap;

public class MyPageFragment extends Fragment {

    private View mview;
    private EditText edtFullName;
    private TextView edtEmail;
    private Button btnUpdateProfile;
    private  MainActivity mainActivity;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         mview = inflater.inflate(R.layout.fragment_mypage, container, false);
         initUi();
         mainActivity =(MainActivity) getActivity();
         setUserInformation();
         initListener();

        return mview;
    }

    private void initUi(){

        edtFullName = mview.findViewById(R.id.edt_full_name);
        edtEmail = mview.findViewById(R.id.edt_email);
        btnUpdateProfile = mview.findViewById(R.id.btn_update_profile);
        progressDialog = new ProgressDialog(getContext());
    }

    private void setUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
        {
            return;
        }
        edtFullName.setText(user.getDisplayName());
        edtEmail.setText(user.getEmail());
    }
    private void initListener(){
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateProfile();
            }
        });
    }

    private void onClickUpdateProfile(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String strFullName = edtFullName.getText().toString().trim();
        String userId = user.getUid();
        progressDialog.show();

// Tạo một tham chiếu đến node của người dùng trong Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

// Tạo một HashMap chứa dữ liệu cần cập nhật
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("fullName", strFullName);

// Thực hiện cập nhật thông tin người dùng trên Realtime Database
        usersRef.updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Nếu cập nhật trên Realtime Database thành công, tiến hành cập nhật trên Firebase Authentication
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(strFullName)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Update profile success", Toast.LENGTH_SHORT).show();
                                                mainActivity.showUserInformation();
                                            } else {
                                                Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
