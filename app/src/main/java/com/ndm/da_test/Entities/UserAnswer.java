package com.ndm.da_test.Entities;

import java.io.Serializable;

public class UserAnswer implements Serializable {
    private String questionId;
    private String selectedOption;

    public UserAnswer(String questionId, String selectedOption) {
        this.questionId = questionId;
        this.selectedOption = selectedOption;
    }

    public UserAnswer() {
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}