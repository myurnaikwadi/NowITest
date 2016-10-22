package com.nowitest.model;

/**
 * Created by mobintia-android-developer-1 on 14/1/16.
 */
public class QuestionItem
{
    String questionId;
    String question;
    String questionFile;
    Boolean aBoolean;
    Boolean ansGiven;
    String testId;
    String quesIsFile;
    String marks;
    String timeTaken;
    String questionNo;
    String randomNo;

    public String getRandomNo() {
        return randomNo;
    }

    public void setRandomNo(String randomNo) {
        this.randomNo = randomNo;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public String getQuestionFile() {
        return questionFile;
    }

    public void setQuestionFile(String questionFile) {
        this.questionFile = questionFile;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public QuestionItem() {
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public Boolean getAnsGiven() {
        return ansGiven;
    }

    public void setAnsGiven(Boolean ansGiven) {
        this.ansGiven = ansGiven;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getQuesIsFile() {
        return quesIsFile;
    }

    public void setQuesIsFile(String quesIsFile) {
        this.quesIsFile = quesIsFile;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public QuestionItem(String questionId, String question, String questionFile, Boolean aBoolean, Boolean ansGiven, String testId, String quesIsFile, String marks, String timeTaken, String questionNo,
                        String randomNo) {
        this.questionId = questionId;
        this.question = question;
        this.questionFile = questionFile;
        this.aBoolean = aBoolean;
        this.ansGiven = ansGiven;
        this.testId = testId;
        this.quesIsFile = quesIsFile;
        this.marks = marks;
        this.timeTaken = timeTaken;
        this.questionNo = questionNo;
        this.randomNo = randomNo;
    }
}
