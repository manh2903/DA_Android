package com.ndm.da_test.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ndm.da_test.R;

public class Hello extends AppCompatActivity {

    private WebView webView;
    private static final String TAG = "HelloActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        webView = findViewById(R.id.webview);

        // Thiết lập cấu hình cho WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Bật JavaScript cho WebView
        webSettings.setDomStorageEnabled(true); // Bật bộ nhớ lưu trữ tạm thời cho WebView

        // Xử lý việc tải trang web trong WebView chính




        // Tải trang web từ URL cụ thể
        webView.loadUrl("http://192.168.1.190:8080/firebase/hello");

    }
}
