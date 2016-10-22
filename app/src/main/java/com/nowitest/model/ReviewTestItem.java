package com.nowitest.model;

/**
 * Created by mobintia on 29/7/16.
 */
public class ReviewTestItem {
    String id;
    String title;

    public ReviewTestItem(String id, String title) {
        this.id = id;
        this.title = title;
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
}
