package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.ndm.da_test.R;

public class ForgotPassActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private EditText edt_email;
    private Button btn_huy_forgot, btn_ok_forgot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        initUI();
        initListen();
    }

    private void initUI()
    {
        edt_email = findViewById(R.id.edt_email);
        btn_huy_forgot = findViewById(R.id.btn_huy_forgot);
        btn_ok_forgot = findViewById(R.id.btn_ok_forgot);
        progressDialog = new ProgressDialog(this);
    }
    private void initListen()
    {
        btn_huy_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_ok_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emaill = String.valueOf(edt_email.getText());

                auth.sendPasswordResetEmail(emaill)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(ForgotPassActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}