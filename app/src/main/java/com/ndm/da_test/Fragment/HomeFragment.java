package com.ndm.da_test.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ndm.da_test.Activity.MapActivity;
import com.ndm.da_test.DialogFragment.DialogListener;
import com.ndm.da_test.DialogFragment.FireAlarmDialogFragment1;
import com.ndm.da_test.R;

public class HomeFragment extends Fragment {

    private Button btnMap,btnCall114;

    private LinearLayout layout;

    private View view;
    private FrameLayout fragmentContainer;

    private FragmentManager fragmentManager;

    private static final int CALL_PERMISSION_REQUEST_CODE = 100; // Khai báo mã yêu cầu quyền gọi điện thoại
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_home, container, false);

         initUi();
         initListen();
        return view;
    }

    private void initUi(){
        btnCall114 = view.findViewById(R.id.btn_call);
        btnMap = view.findViewById(R.id.btn_map);
        layout = view.findViewById(R.id.layout_fire_bell);
        fragmentContainer = getActivity().findViewById(R.id.fragment_container);// Khởi tạo fragment container
        fragmentManager = getChildFragmentManager();
    }

    private void initListen(){
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });
        btnCall114.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi hàm để thực hiện cuộc gọi tới số 114
                makePhoneCall("114");
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show overlay

                    FireAlarmDialogFragment1 fireAlarmDialogFragment1 = new FireAlarmDialogFragment1(() -> {
                    Log.e("Location", "Test");
                    Toast.makeText(requireContext(), "Thành công: ", Toast.LENGTH_SHORT).show();
                });

                fireAlarmDialogFragment1.show(fragmentManager, "HomeFragment");
            }
        });

    }

    private void makePhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:0375278021"));

        // Kiểm tra quyền gọi điện thoại trước khi thực hiện cuộc gọi
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền gọi điện thoại nếu chưa được cấp
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
            return;
        }

        // Thực hiện cuộc gọi
        startActivity(callIntent);
    }
}

