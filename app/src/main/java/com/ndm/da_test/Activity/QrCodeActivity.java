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
import android.graphics.Color;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ndm.da_test.DialogFragment.AddByQrDialog;
import com.ndm.da_test.R;

public class QrCodeActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100;
    private ImageView img_Qr;
    private Button btn_Clear, btn_Scan, btn_Save;
    private TextView tv_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        String UserID = getIntent().getStringExtra("qrCodeData");
      //  String fullname = getIntent().getStringExtra("fullname");
        Log.d("qrCodeData",UserID );


        initUI();

        Bitmap qrCodeBitmap = generateQRCode(UserID);
        if (qrCodeBitmap != null) {
            img_Qr.setImageBitmap(qrCodeBitmap);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(UserID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String fullname = dataSnapshot.child("fullName").getValue(String.class);
                    String[] parts = fullname.split(" ");
                    tv_name.setText(parts[parts.length - 1]);

                } else {
                    System.out.println("Không tìm thấy dữ liệu cho UserID: " + UserID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý nếu có lỗi xảy ra trong quá trình truy xuất cơ sở dữ liệu
                System.out.println("Lỗi khi truy xuất cơ sở dữ liệu: " + databaseError.getMessage());
            }
        });





        initListen();

    }

    private void initUI() {

        img_Qr = findViewById(R.id.img_qr);
        btn_Clear = findViewById(R.id.btn_clear);
        btn_Scan = findViewById(R.id.btn_scan);
        btn_Save = findViewById(R.id.btn_save);
        tv_name = findViewById(R.id.tv_name);


    }

    private void initListen() {
        btn_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QrCodeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    // Permission already granted, proceed with saving the image
                    saveQRCodeToGallery();
                }
            }
        });

        btn_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QrCodeActivity.this, ScanActivity.class);
                startActivityForResult(intent, 1); // Sử dụng startActivityForResult để nhận kết quả từ ScanActivity
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Lấy ID được gửi từ ScanActivity
                String id = data.getStringExtra("id");
                // Xử lý ID ở đây (ví dụ: hiển thị hoặc lưu trữ)
                Log.d("resultCode ID", id);
                if (id != null && !id.isEmpty()) {
                    // Hiển thị AddByQrDialog
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", id);
                    AddByQrDialog dialogFragment = new AddByQrDialog();
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "MyDialogFragment");
                }

            }
        }
    }

    private Bitmap generateQRCode(String data) {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 500, 500);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with saving the image
                saveQRCodeToGallery();
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(this, "Permission denied. Cannot save QR code to gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveQRCodeToGallery() {

        View layout = findViewById(R.id.layout_qr);
        layout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(layout.getDrawingCache());
        layout.setDrawingCacheEnabled(false);

        String imageName = "QR_Code_" + System.currentTimeMillis() + ".png";

        // Save the bitmap to the device's gallery
        String savedImagePath = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                imageName,
                "QR Code"
        );

        if (savedImagePath != null) {
            // Image saved successfully
            Log.d("QRCodeActivity", "QR Code saved to gallery: " + savedImagePath);
            // Show a success message to the user
            Toast.makeText(QrCodeActivity.this, "QR Code saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            // Failed to save image
            Log.d("QRCodeActivity", "Failed to save QR Code to gallery");
            // Show an error message to the user
            Toast.makeText(QrCodeActivity.this, "Failed to save QR Code to gallery", Toast.LENGTH_SHORT).show();
        }
    }



}