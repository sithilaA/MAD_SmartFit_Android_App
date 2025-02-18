package com.example.mad_smartfit_android_app.model;
public class NewsItem {
    private String title;
    private String description;

    public NewsItem() {
        // Default constructor required for Firestore
    }

    public NewsItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
