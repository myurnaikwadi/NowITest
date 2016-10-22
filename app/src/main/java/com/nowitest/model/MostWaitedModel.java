package com.nowitest.model;

/**
 * Created by mobintia on 29/7/16.
 */
public class MostWaitedModel {
    String id;
    String questionNo;
    String timeTaken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public MostWaitedModel(String id, String questionNo, String timeTaken) {
        this.id = id;
        this.questionNo = questionNo;
        this.timeTaken = timeTaken;
    }
}
