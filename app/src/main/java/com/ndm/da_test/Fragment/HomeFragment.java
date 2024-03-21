package com.ndm.da_test.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ndm.da_test.Activity.MapActivity;
import com.ndm.da_test.R;

public class HomeFragment extends Fragment {

    private Button btnMap,btnCall114;

    private View view;
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
    }

    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}

