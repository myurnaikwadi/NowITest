package com.nowitest.model;

/**
 * Created by mobintia on 29/7/16.
 */
public class NoticeCenterItem {
    String id;
    String description;
    String postDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public NoticeCenterItem(String id, String description, String postDate) {
        this.id = id;
        this.description = description;
        this.postDate = postDate;
    }
}
