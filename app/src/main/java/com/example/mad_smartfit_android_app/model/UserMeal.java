package com.example.mad_smartfit_android_app.model;

public class UserMeal {
    private String userID;
    private String mealID;

    // Default constructor required for Firestore
    public UserMeal() {}

    public UserMeal(String userID, String mealID) {
        this.userID = userID;
        this.mealID = mealID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userId) {
        this.userID = userId;
    }

    public String getMealID() {
        return mealID;
    }

    public void setMealID(String mealId) {
        this.mealID = mealId;
    }
}