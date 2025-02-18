package com.example.mad_smartfit_android_app.model;

public class WorkoutHistory {
    private String workoutId;
    private long duration;
    private String userId;
    private String dateWorkoutDone;

    public WorkoutHistory(String workoutId, long duration, String userId,String dateWorkoutDone) {
        this.workoutId = workoutId;
        this.duration = duration;
        this.userId = userId;
        this.dateWorkoutDone = dateWorkoutDone;

    }

    public String getWorkoutId() {
        return workoutId;
    }

    public long getDuration() {
        return duration;
    }
    public String getUserId() {
        return userId;
    }
    public String getDateWorkoutDone() {
        return dateWorkoutDone;
    }


}