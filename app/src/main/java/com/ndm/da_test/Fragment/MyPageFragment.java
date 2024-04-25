package com.ndm.da_test.Fragment;


import static android.app.Activity.RESULT_OK;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;



import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.ndm.da_test.Activity.MainActivity;
import com.ndm.da_test.Activity.QrCodeActivity;
import com.ndm.da_test.BottomSheetDialog.AddUsers;
import com.ndm.da_test.BottomSheetDialog.ListFriend;
import com.ndm.da_test.R;

import java.io.IOException;
import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;

public class MyPageFragment extends Fragment {
    public static final int MY_REQUEST_CODE = 0;
    private View mview;
    private EditText edtFullName;
    private TextView txtEmail;
    private Button btnUpdateProfile;
    private MainActivity mainActivity;
    private ProgressDialog progressDialog;
    private CircleImageView imgUser,imgListFriend, imgAddFriend;
    private StorageReference storageRef;

    private ImageView img_Qr;
    private Uri uri;

    private String userId ,fullName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_mypage, container, false);
        mainActivity = (MainActivity) getActivity();
        initUi();
        setUserInformation();
        initListener();
        return mview;
    }


    private void initUi() {

        edtFullName = mview.findViewById(R.id.edt_full_name);
        txtEmail = mview.findViewById(R.id.txt_email);
        img_Qr = mview.findViewById(R.id.img_qr);
        imgUser = mview.findViewById(R.id.img_user2);
        imgListFriend = mview.findViewById(R.id.img_list_friend);
        imgAddFriend = mview.findViewById(R.id.img_add_friend);
        btnUpdateProfile = mview.findViewById(R.id.btn_update_profile);
        storageRef = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(getContext());

    }

    private void initListener() {
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateProfile();
            }
        });

        imgListFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenListFriend();
            }
        });
        imgAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenAddUser();
            }
        });

        img_Qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), QrCodeActivity.class);
                // Đính kèm thông tin mã QR vào Intent
                intent.putExtra("qrCodeData", userId);
                intent.putExtra("fullname",fullName);
                // Chuyển sang QRCodeActivity
                startActivity(intent);
            }
        });

    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
         userId = user.getUid();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    fullName = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String photoUrl = snapshot.child("avatarUrl").getValue(String.class);
                    if (fullName != null) {
                        edtFullName.setText(fullName);
                    }
                    if (email != null) {
                        txtEmail.setText(email);
                    }
                    if (photoUrl != null) {
                        Glide.with(getActivity()).load(photoUrl).error(R.drawable.ic_friend).into(imgUser);
                    }


                } else {
                    Log.d("setUserInformation", "User data does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("setUserInformation", "Failed to read user data", error.toException());
            }
        });
    }




    private void openGallery() {
        // Kiểm tra xem phiên bản Android có hỗ trợ hành động Intent.ACTION_OPEN_DOCUMENT không
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, MY_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                imgUser.setImageBitmap(bitmap);
                uri = selectedImageUri;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void onClickUpdateProfile() {

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

        StorageReference avatarRef = storageRef.child("user_avatars/" + userId + ".jpg");

        if (uri != null) {
            avatarRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Lấy đường dẫn tới hình ảnh đã tải lên thành công
                    avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            // Lấy đường dẫn tải về và cập nhật vào Realtime Database
                            String avatarUrl = downloadUrl.toString();
                            updates.put("avatarUrl", avatarUrl);

                            // Thực hiện cập nhật thông tin người dùng trên Realtime Database
                            usersRef.updateChildren(updates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Nếu cập nhật trên Realtime Database thành công, tiến hành cập nhật trên Firebase Authentication
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(strFullName)
                                                        .setPhotoUri(uri)
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
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.d("AvatarUrl", e.getMessage());
                    Toast.makeText(getActivity(), "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nếu người dùng không chọn hình ảnh mới, tiếp tục thực hiện cập nhật thông tin người dùng
            // Thực hiện cập nhật thông tin người dùng trên Realtime Database
            usersRef.updateChildren(updates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Nếu cập nhật trên Realtime Database thành công, tiến hành cập nhật trên Firebase Authentication
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(strFullName)
                                        .setPhotoUri(uri)
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





    private void clickOpenAddUser() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        AddUsers bottomSheetDialog = new AddUsers(getContext(), fragmentManager);
        bottomSheetDialog.show();

        if (bottomSheetDialog.getWindow() != null) {
            bottomSheetDialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Chiều rộng là match_parent
                    ViewGroup.LayoutParams.WRAP_CONTENT // Chiều cao tự động
            );
        }
    }

    private void clickOpenListFriend() {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ListFriend bottomSheetDialog = new ListFriend(getContext(),fragmentManager);
        bottomSheetDialog.show();

        if (bottomSheetDialog.getWindow() != null) {
            bottomSheetDialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

}
