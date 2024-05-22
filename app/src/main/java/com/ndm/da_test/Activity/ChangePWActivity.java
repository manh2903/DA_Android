package com.ndm.da_test.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ndm.da_test.R;

public class ChangePWActivity extends AppCompatActivity {

    private EditText edt_current_password, edt_new_password, edt_confirm_new_password;
    private Button btn_change_password;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);

        mAuth = FirebaseAuth.getInstance();

        initUI();
        initListen();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        edt_current_password = findViewById(R.id.edt_current_password);
        edt_new_password = findViewById(R.id.edt_new_password);
        edt_confirm_new_password = findViewById(R.id.edt_confirm_new_password);
        btn_change_password = findViewById(R.id.btn_change_password);
    }

    private void initListen() {
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = edt_current_password.getText().toString().trim();
                String newPassword = edt_new_password.getText().toString().trim();
                String confirmPassword = edt_confirm_new_password.getText().toString().trim();

                if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(ChangePWActivity.this, "Vui lòng nhập đầy đủ !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ChangePWActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.getEmail() != null) {
                    mAuth.signInWithEmailAndPassword(user.getEmail(), currentPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(ChangePWActivity.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChangePWActivity.this, "Có lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(ChangePWActivity.this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}