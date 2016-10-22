package com.nowitest.model;

/**
 * Created by mobintia-android-developer-1 on 14/1/16.
 */
public class SyllabusItem
{
   String syllabus_id, title;

    public String getSyllabus_id() {
        return syllabus_id;
    }

    public void setSyllabus_id(String syllabus_id) {
        this.syllabus_id = syllabus_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SyllabusItem(String syllabus_id, String title) {
        this.syllabus_id = syllabus_id;
        this.title = title;
    }

    public SyllabusItem() {
    }


}
