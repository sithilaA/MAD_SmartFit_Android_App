package com.example.mad_smartfit_android_app.model;

public class Appointment {
    private String id;
    private String name;
    private String date;
    private String time;
    private String status;
    private String meetingAccessLink;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMeetingAccessLink() { return meetingAccessLink; }
    public void setMeetingAccessLink(String meetingAccessLink) { this.meetingAccessLink = meetingAccessLink; }
}
