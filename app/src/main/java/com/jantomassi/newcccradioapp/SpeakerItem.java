package com.jantomassi.newcccradioapp;

public final class SpeakerItem {
    private String mTitle;
    private String mImageUrl;
    private String mDescription;

    public SpeakerItem(String title, String imageUrl, String description) {
        mTitle = title;
        mImageUrl = imageUrl;
        mDescription = description;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
}
