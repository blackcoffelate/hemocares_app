package com.example.hemocares.utils;

public class OnBoardItem {

    String Title, Description;
    int OnBoardImage;

    public OnBoardItem(String title, String description, int onBoardImage) {
        Title = title;
        Description = description;
        OnBoardImage = onBoardImage;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getOnBoardImage() {
        return OnBoardImage;
    }

    public void setOnBoardImage(int onBoardImage) {
        OnBoardImage = onBoardImage;
    }
}
