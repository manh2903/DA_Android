package com.ndm.da_test.Fragment;

import android.app.ProgressDialog;
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
import com.ndm.da_test.Adapter.SkillAdapter;
import com.ndm.da_test.Entities.Escape;
import com.ndm.da_test.Entities.Skill;
import com.ndm.da_test.Interface.IClickItemSkillListener;
import com.ndm.da_test.R;

import java.util.ArrayList;
import java.util.List;

public class SkillFragment extends Fragment   {
    private RecyclerView recyclerView;
    private View view;
    private SkillAdapter mSkillAdapter;
    private List<Skill> mSkillList;
    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_skill, container, false);

        initUi();
        getListRealTimedb();
        return view;
    }

    private void initUi(){

        progressDialog = new ProgressDialog(getContext());
        recyclerView = view.findViewById(R.id.rcv_skill);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mSkillList = new ArrayList<>();
        mSkillAdapter = new SkillAdapter(mSkillList, new IClickItemSkillListener() {
            @Override
            public void onItemClick(Skill skill) {
                progressDialog.show();
                onClickGoToDetail(skill);
            }
        });


        recyclerView.setAdapter(mSkillAdapter);


    }

    private void getListRealTimedb() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("skill");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("SkillFragment", "onDataChange() called");
                mSkillList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Lấy giá trị của tên và danh sách hình ảnh từ dataSnapshot
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String source = dataSnapshot.child("source").getValue(String.class);

                    // Tạo một đối tượng Escape với các giá trị đã lấy
                    Skill skill = new Skill(name, source);
                    mSkillList.add(skill);
                }
                mSkillAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("SkillFragment", "onCancelled() called");
            }
        });
    }

    private void onClickGoToDetail(Skill skill){
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Skill", skill);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}