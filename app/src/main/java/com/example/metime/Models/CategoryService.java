package com.example.metime.Models;

import android.graphics.drawable.Drawable;

public class CategoryService {
    private String name;
    private int imageDrawableId;

    public CategoryService(String name, int imageDrawableId) {
        this.name = name;
        this.imageDrawableId = imageDrawableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageDrawableId() {
        return imageDrawableId;
    }

    public void setImageDrawableId(int imageDrawableId) {
        this.imageDrawableId = imageDrawableId;
    }
}
