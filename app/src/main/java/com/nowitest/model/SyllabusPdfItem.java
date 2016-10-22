package com.nowitest.model;

/**
 * Created by mobintia-android-developer-1 on 14/1/16.
 */
public class SyllabusPdfItem
{
   String id, syllabus_id, file_name, path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSyllabus_id() {
        return syllabus_id;
    }

    public void setSyllabus_id(String syllabus_id) {
        this.syllabus_id = syllabus_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SyllabusPdfItem(String id, String syllabus_id, String file_name, String path) {
        this.id = id;
        this.syllabus_id = syllabus_id;
        this.file_name = file_name;
        this.path = path;
    }

    public SyllabusPdfItem() {
    }


}
