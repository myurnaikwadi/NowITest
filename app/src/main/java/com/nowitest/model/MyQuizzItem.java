package com.nowitest.model;

public class MyQuizzItem
{
    int id;
    String questionId;
    String question;
    String answer;
    String correctAnswer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public MyQuizzItem(int id, String questionId, String question, String answer, String correctAnswer) {
        this.id = id;
        this.questionId = questionId;
        this.question = question;
        this.answer = answer;
        this.correctAnswer = correctAnswer;
    }

    public MyQuizzItem() {
    }
}