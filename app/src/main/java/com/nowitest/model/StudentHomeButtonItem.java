package com.nowitest.model;

import android.graphics.drawable.Drawable;

/**
 * Created by android on 3/6/16.
 */
public class StudentHomeButtonItem
{
    String id;
    String name;
    Drawable image;

    public StudentHomeButtonItem(String id, String name, Drawable image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
