package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.ndm.da_test.R;


import java.io.IOException;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 200;
    private static final int REQUEST_CODE_PICK_IMAGE = 300;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private CompoundBarcodeView barcodeView;
    private Button btn_clear, btn_Library , btn_Show_qr;

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
    }

    private void initListen()
    {
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_Library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Nếu quyền chưa được cấp, yêu cầu quyền
                    ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
                } else {
                    // Nếu quyền đã được cấp, mở Intent để chọn ảnh từ thư viện
                    openGallery();
                }
            }
        });
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // Lấy đường dẫn của ảnh từ Intent
            Uri selectedImageUri = data.getData();
            try {
                // Thực hiện quét mã QR từ ảnh đã chọn
                decodeQRCodeFromImage(selectedImageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to decode QR code from image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Trong phương thức decodeQRCodeFromImage() để quét mã QR từ ảnh
    private void decodeQRCodeFromImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            String decodedId = decodeQRCode(bitmap);
            if (decodedId != null) {
                // Tạo Intent để gửi ID về QrCodeActivity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("id", decodedId);
                setResult(RESULT_OK, returnIntent);

                Log.d("image_id",decodedId);


                finish(); // Kết thúc ScanActivity sau khi gửi ID
            } else {
                Toast.makeText(this, "Failed to decode QR code from image", Toast.LENGTH_SHORT).show();
                finish(); // Kết thúc ScanActivity nếu không quét được mã QR
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to decode QR code from image", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc ScanActivity nếu có lỗi xảy ra
        }
    }

    // Hàm tự định nghĩa để quét mã QR từ ảnh
    private String decodeQRCode(Bitmap bitmap) {
        try {
            int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            // Tạo một LuminanceSource từ bitmap
            LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            // Sử dụng MultiFormatReader để đọc mã QR
            MultiFormatReader reader = new MultiFormatReader();
            Result result = reader.decode(binaryBitmap);

            // Trả về nội dung của mã QR
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void initQRScanner() {
        // Start camera để quét mã QR
        barcodeView.resume();
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Xử lý kết quả quét ở đây
                if (result.getText() != null) {
                    String id = result.getText();
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                    // Kiểm tra ID trong Firebase
                    usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("id", id);
                                setResult(RESULT_OK, returnIntent);

                                // Trong phương thức barcodeResult
                                finish();
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
