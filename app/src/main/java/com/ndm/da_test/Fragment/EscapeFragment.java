package com.ndm.da_test.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ndm.da_test.Activity.MainActivity;
import com.ndm.da_test.Adapter.EscapeAdapter;
import com.ndm.da_test.Entities.Escape;
import com.ndm.da_test.R;

import java.util.ArrayList;
import java.util.List;

public class EscapeFragment extends Fragment {
    private RecyclerView recyclerView;
    private EscapeAdapter mEscapeAdapter;
    private List<Escape> mEscapeList;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_action_escape, container, false);

        recyclerView = view.findViewById(R.id.rcv_escape);
        mEscapeAdapter = new EscapeAdapter(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //mEscapeAdapter.setData(get());
        recyclerView.setAdapter(mEscapeAdapter);
        return view;
    }

    private List<Escape> get() {

        mEscapeList = new ArrayList<>();
        mEscapeList.add(new Escape(1, "hello"));
        mEscapeList.add(new Escape(2, "world"));
        return mEscapeList;

    }
}


