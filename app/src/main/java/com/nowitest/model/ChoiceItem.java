package com.nowitest.model;

/**
 * Created by mobintia-android-developer-1 on 14/1/16.
 */
public class ChoiceItem
{
    String questionID;
    String choiceId;
    String choice;
    String isRight;
    String userChoice;
    String is_file;

    public ChoiceItem() {
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(String choiceId) {
        this.choiceId = choiceId;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getUserChoice() {
        return userChoice;
    }

    public void setUserChoice(String userChoice) {
        this.userChoice = userChoice;
    }

    public String getIs_file() {
        return is_file;
    }

    public void setIs_file(String is_file) {
        this.is_file = is_file;
    }

    public ChoiceItem(String questionID, String choiceId, String choice, String isRight, String userChoice, String is_file) {
        this.questionID = questionID;
        this.choiceId = choiceId;
        this.choice = choice;
        this.isRight = isRight;
        this.userChoice = userChoice;
        this.is_file = is_file;
    }
}
