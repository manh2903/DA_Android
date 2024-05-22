package com.ndm.da_test.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.Adapter.QuizPagerAdapter;
import com.ndm.da_test.Entities.Data;
import com.ndm.da_test.Entities.Question;
import com.ndm.da_test.Entities.UserAnswer;
import com.ndm.da_test.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuizzActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private QuizPagerAdapter adapter;

    private String list_question;
    private List<Question> questions;

    private Button btn_next, btn_previous;

    private TextView tv_count;

    private int currentPage = 0;

    private List<UserAnswer> userAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        userAnswers = new ArrayList<>();

        viewPager = findViewById(R.id.viewpager);
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        tv_count = findViewById(R.id.tv_count);

        questions = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        Data data = (Data) bundle.getSerializable("Noti Receiver");
        if (data != null) {
            list_question = data.getSource();
        }

        getListRealTimeDB();

        adapter = new QuizPagerAdapter(this, questions);
        viewPager.setAdapter(adapter);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // No-op
            }

            @Override
            public void onPageSelected(int position) {
                tv_count.setText(String.format("%d/%d", position + 1, questions.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // No-op
            }
        });


        btn_next.setOnClickListener(v -> {
            if (currentPage < adapter.getCount() - 1) {
                currentPage++;
                viewPager.setCurrentItem(currentPage);
            }
        });

        btn_previous.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                viewPager.setCurrentItem(currentPage);
            }
        });




    }

    private void getListRealTimeDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("questions").child(list_question);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    Question question = questionSnapshot.getValue(Question.class);
                    questions.add(question);
                }

                tv_count.setText(String.format("1/%d", questions.size()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("QuizzActivity", "Error getting data", databaseError.toException());
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}