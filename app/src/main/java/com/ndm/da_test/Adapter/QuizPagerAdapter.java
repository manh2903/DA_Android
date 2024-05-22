package com.ndm.da_test.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ndm.da_test.Entities.Question;
import com.ndm.da_test.Entities.UserAnswer;
import com.ndm.da_test.R;

import java.util.ArrayList;
import java.util.List;

public class QuizPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Question> mQuestions;
    private List<UserAnswer> userAnswers;


    public QuizPagerAdapter(Context context, List<Question> questions) {
        mContext = context;
        mQuestions = questions;
    }




    @Override
    public int getCount() {
        return mQuestions.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.quiz_item, container, false);

        TextView questionTextView = view.findViewById(R.id.question_text);
        TextView option1TextView = view.findViewById(R.id.option1_text);
        TextView option2TextView = view.findViewById(R.id.option2_text);
        TextView option3TextView = view.findViewById(R.id.option3_text);
        TextView option4TextView = view.findViewById(R.id.option4_text);

        Question currentQuestion = mQuestions.get(position);

        questionTextView.setText(currentQuestion.getQuestion());
        option1TextView.setText(currentQuestion.getOp1());
        option2TextView.setText(currentQuestion.getOp2());
        option3TextView.setText(currentQuestion.getOp3());
        option4TextView.setText(currentQuestion.getOp4());

        if (option4TextView.getText().toString().isEmpty())
        {
            option4TextView.setVisibility(View.GONE);
        }
        else
        {
            option4TextView.setVisibility(View.VISIBLE);
        }
        option1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedOption(option1TextView,position);
            }
        });

        option2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedOption(option2TextView,position);
            }
        });

        option3TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedOption(option3TextView,position);
            }
        });

        option4TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedOption(option4TextView,position);
            }
        });


        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void setSelectedOption(TextView selectedOptionTextView, int position) {
        selectedOptionTextView.setBackgroundResource(R.drawable.op_quizz_select);
        clearOtherOptionsBackground(selectedOptionTextView);

        // Get the current question
        Question currentQuestion = mQuestions.get(position);

        // Get the correct answer
        String correctAnswer = currentQuestion.getCorrect();

        // Check if the selected option is the correct answer
        if (selectedOptionTextView.getText().toString().equals(correctAnswer)) {
            // Correct answer
            selectedOptionTextView.setBackgroundResource(R.drawable.op_quizz_correct);
        } else {
            // Incorrect answer
            selectedOptionTextView.setBackgroundResource(R.drawable.op_quizz_wrong);

            // Find the correct option and highlight it in green
            ViewGroup parent = (ViewGroup) selectedOptionTextView.getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child instanceof TextView && ((TextView) child).getText().toString().equals(correctAnswer)) {
                    child.setBackgroundResource(R.drawable.op_quizz_correct);
                    break;
                }
            }
        }
    }



    private void clearOtherOptionsBackground(TextView selectedOptionTextView) {
        ViewGroup parent = (ViewGroup) selectedOptionTextView.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof TextView && child != selectedOptionTextView) {
                child.setBackgroundResource(R.drawable.op_quizz);
            }
        }
    }





}



