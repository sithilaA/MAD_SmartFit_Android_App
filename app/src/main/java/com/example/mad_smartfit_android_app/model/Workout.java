package com.example.mad_smartfit_android_app.model;

public class Workout {
    private String workoutName;
    private String imageUrl;
    private int caloriesBurned;
    private int duration;
    private String notes;
    private String videoLink;
    private String trainerUserId;

    // Default constructor required for calls to DataSnapshot.getValue(Workout.class)
    public Workout() {
    }

    public Workout(String workoutName, String imageUrl, int caloriesBurned, int duration, String notes, String videoLink, String trainerUserId) {
        this.workoutName = workoutName;
        this.imageUrl = imageUrl;
        this.caloriesBurned = caloriesBurned;
        this.duration = duration;
        this.notes = notes;
        this.videoLink = videoLink;
        this.trainerUserId = trainerUserId;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public int getDuration() {
        return duration;
    }

    public String getNotes() {
        return notes;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public String getTrainerUserId() {
        return trainerUserId;
    }
}