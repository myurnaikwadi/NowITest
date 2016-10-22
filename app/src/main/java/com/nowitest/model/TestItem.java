package com.nowitest.model;

/**
 * Created by mobintia on 29/7/16.
 */
public class TestItem {
    String id;
    String title;
    String totalQuestions;
    String duration;
    String marks;
    String course;
    String subject;
    String code;

    public TestItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TestItem(String id, String title, String totalQuestions, String duration, String marks, String course, String subject, String code) {
        this.id = id;
        this.title = title;
        this.totalQuestions = totalQuestions;
        this.duration = duration;
        this.marks = marks;
        this.course = course;
        this.subject = subject;
        this.code = code;
    }
}
