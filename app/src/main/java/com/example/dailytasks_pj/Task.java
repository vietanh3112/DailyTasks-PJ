package com.example.dailytasks_pj;

public class Task {
    private int id;
    private String title;
    private String startTime;
    private String date;

    public Task(int id, String title, String startTime, String date) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDate() {
        return date;
    }
}
