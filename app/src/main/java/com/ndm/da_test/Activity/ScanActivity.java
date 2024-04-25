package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.ndm.da_test.DialogFragment.AddByEmailDialog;
import com.ndm.da_test.DialogFragment.AddByQrDialog;
import com.ndm.da_test.Entities.User;
import com.ndm.da_test.R;

import java.io.Serializable;
import java.util.List;

public class ScanActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private CompoundBarcodeView barcodeView;
    private Button btn_clear, btn_Library , btn_Show_qr;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Khởi tạo giao diện người dùng
        initUI();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Nếu quyền chưa được cấp, yêu cầu quyền
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Nếu quyền đã được cấp, khởi tạo quét mã QR
            initQRScanner();
        }

        // Khởi tạo quét mã QR
        initQRScanner();
        initListen();
    }

    private void initUI() {
        btn_Library = findViewById(R.id.btn_library);
        btn_Show_qr = findViewById(R.id.btn_qr);
        btn_clear = findViewById(R.id.btn_clear);
        barcodeView = findViewById(R.id.camera_preview);
        fragmentManager = getSupportFragmentManager();
    }

    private void initListen()
    {
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initQRScanner() {
        // Start camera để quét mã QR
        barcodeView.resume();
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Xử lý kết quả quét ở đây
                if (result.getText() != null ) {
                    String id = result.getText();
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                    // Kiểm tra ID trong Firebase
                    usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Nếu ID tồn tại trong bảng người dùng, mở AddByQrDialog


                                Bundle bundle = new Bundle();
                                bundle.putString("userId", id);

                                AddByQrDialog dialogFragment = new AddByQrDialog();
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getSupportFragmentManager(), "MyDialogFragment");

                            } else {

                                // Nếu ID không tồn tại trong bảng người dùng, có thể hiển thị thông báo hoặc xử lý khác
                                Toast.makeText(ScanActivity.this, "ID không tồn tại", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý khi truy vấn bị hủy bỏ
                            Toast.makeText(ScanActivity.this, "Lỗi khi kiểm tra ID", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Xử lý các điểm kết quả có thể
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barcodeView.pause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu quyền được cấp, khởi tạo quét mã QR
                initQRScanner();
            } else {
                // Nếu quyền không được cấp, thông báo cho người dùng và kết thúc activity
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
