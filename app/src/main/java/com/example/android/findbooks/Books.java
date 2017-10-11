package com.example.android.findbooks;

import android.graphics.Bitmap;

public class Books {

    private String mTitle;
    private String mAuthor;
    private String mUrl;
    private Bitmap mImage;

    public Books(String title, String author, String url, Bitmap image) {
        mTitle = title;
        mAuthor = author;
        mUrl = url;
        mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUri() {
        return mUrl;
    }

    public Bitmap getImage() {
        return mImage;
    }
}
