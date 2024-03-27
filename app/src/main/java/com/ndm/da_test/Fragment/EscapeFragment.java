package com.ndm.da_test.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.Activity.DetailActivity;
import com.ndm.da_test.Adapter.EscapeAdapter;
import com.ndm.da_test.Entities.Escape;
import com.ndm.da_test.Interface.IClickItemEscapeListener;
import com.ndm.da_test.R;

import java.util.ArrayList;
import java.util.List;

public class EscapeFragment extends Fragment  {
    private RecyclerView recyclerView;
    private View view;
    private EscapeAdapter mEscapeAdapter;
    private List<Escape> mEscapeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_escape, container, false);

        initUi();
        getListRealTimedb();
        return view;
    }

    private void initUi(){
        recyclerView = view.findViewById(R.id.rcv_escape);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mEscapeList = new ArrayList<>();
        mEscapeAdapter = new EscapeAdapter(mEscapeList, new IClickItemEscapeListener() {
            @Override
            public void onItemClick(Escape escape) {
              onClickGoToDetail(escape);
            }
        });


        recyclerView.setAdapter(mEscapeAdapter);
    }

    private void getListRealTimedb() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("action_ascape");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("EscapeFragment", "onDataChange() called");
                mEscapeList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Escape escape = dataSnapshot.getValue(Escape.class);
                    mEscapeList.add(escape);
                }
                mEscapeAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("EscapeFragment", "onCancelled() called");
            }
        });
    }

    private void onClickGoToDetail(Escape escape){
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_escape", escape);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}

