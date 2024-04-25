package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.ndm.da_test.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout txtPassLayout, txtCfPassLayout;
    TextInputEditText txtEmail, txtUsername, txtPassEdt, txtCfPassEdt;
    private Button btnSignUp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initUi();
        initListener();
    }

    private void initUi() {

        txtEmail = findViewById(R.id.txt_emailedt);
        txtUsername = findViewById(R.id.txt_usernameedt);
        txtPassLayout = findViewById(R.id.txt_passlayout);
        txtPassEdt = findViewById(R.id.txt_passEdt);
        txtCfPassLayout = findViewById(R.id.txt_cfpasslayout);
        txtCfPassEdt = findViewById(R.id.txt_cfpassEdt);

        btnSignUp = findViewById(R.id.btn_sign_up);


        progressDialog = new ProgressDialog(this);
    }

    private void initListener() {
        txtPassEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String password = s.toString();
                if (isPasswordValid(password)) {
                    txtPassLayout.setHelperText("Your Password are Strong");
                    txtPassLayout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.teal_700)));
                    txtPassLayout.setError("");
                } else {
                    txtPassLayout.setHelperText("mix of letters(upper and lower case), number and symbols");
                    txtPassLayout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.orange3)));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtCfPassEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String password = s.toString();
                if (password.equals(txtPassEdt.getText().toString())) {
                    txtCfPassLayout.setHelperText("Mật khẩu hợp lệ");
                    txtCfPassLayout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.teal_700)));
                    txtCfPassLayout.setError("");
                } else {
                    txtCfPassLayout.setHelperText("Mật khẩu không khớp");
                    txtCfPassLayout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.orange3)));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp();
            }
        });
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false;
        }
        Pattern pattern = Pattern.compile("[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

    private void onClickSignUp() {

        String strEmail = txtEmail.getText().toString().trim();
        String strUsername = txtUsername.getText().toString().trim();
        String strPassword = txtPassEdt.getText().toString().trim();
        String strConfirmPassword = txtCfPassEdt.getText().toString().trim();

        if (strEmail.isEmpty() || strUsername.isEmpty() || strPassword.isEmpty() || strConfirmPassword.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem mật khẩu đã nhập và đáp ứng yêu cầu không
        if (!isPasswordValid(strPassword)) {
            txtPassLayout.setError("Mật khẩu không hợp lệ. Mật khẩu phải có ít nhất 8 ký tự và chứa ít nhất một chữ cái, một số và một ký tự đặc biệt.");
            return;
        }

        // Kiểm tra xem mật khẩu và mật khẩu xác nhận có khớp nhau không
        if (!strPassword.equals(strConfirmPassword)) {
            txtCfPassLayout.setError("Mật khẩu xác nhận không khớp.");
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        progressDialog.show();
        auth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                            // Kiểm tra xem bảng "users" đã tồn tại hay chưa
                            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        // Nếu bảng "users" chưa tồn tại, tạo mới bảng
                                        usersRef.setValue(true);
                                    }

                                    // Thêm người dùng vào bảng "users"
                                    usersRef.child(user.getUid()).child("email").setValue(strEmail);
                                    usersRef.child(user.getUid()).child("fullName").setValue(strUsername);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(SignUpActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}