package com.example.mad_smartfit_android_app.model;

public class Meal {
    private String mealName;
    private String ingredients;
    private String notes;
    private String imageUrl;

    // Default constructor required for Firestore
    public Meal() {}

    public Meal(String mealName, String ingredients, String notes, String imageUrl) {
        this.mealName = mealName;
        this.ingredients = ingredients;
        this.notes = notes;
        this.imageUrl = imageUrl;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}