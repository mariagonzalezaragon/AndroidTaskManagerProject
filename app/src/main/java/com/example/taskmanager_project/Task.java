package com.example.taskmanager_project;

public class Task {
    private String id;
    private String taskName;
    private String dueDate;
    private String status;

    // Default constructor required for Firebase
    public Task() {
    }

    // Constructor
    public Task(String taskName, String dueDate, String status) {
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.status = status;
    }

    // Getter and setter for taskName
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    // Getter and setter for dueDate
    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    // Getter and setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
