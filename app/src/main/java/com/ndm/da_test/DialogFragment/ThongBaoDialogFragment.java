package com.ndm.da_test.DialogFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.ndm.da_test.R;

public class ThongBaoDialogFragment extends DialogFragment {
    private View view;

    private ImageView imgClear;

    private Button btn_call;

    private static final int CALL_PERMISSION_REQUEST_CODE = 100; // Khai báo mã yêu cầu quyền gọi điện thoại

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentDialog);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_thongbao, container, false);

        imgClear = view.findViewById(R.id.img_clear);
        btn_call = view.findViewById(R.id.btn_call);

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo intent cho cuộc gọi
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:123"));

                // Kiểm tra quyền gọi điện thoại trước khi thực hiện cuộc gọi
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Yêu cầu quyền gọi điện thoại nếu chưa được cấp
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
                    return;
                }

                // Thực hiện cuộc gọi
                startActivity(callIntent);
            }
        });

        return view;
    }

//    private void closeFragment() {
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.remove(ThongBaoFragment.this);
//        fragmentTransaction.commit();
//    }
}
