package com.example.taskmanager_project;

public class Task {
    private String id;
    private String taskName;
    private String dueDate;
    private String status;
    private String userId;
    private String userName;


    // Add this field to store the user's name

    // Default constructor
    public Task() {}

    // Parameterized constructor
    public Task(String taskName, String dueDate, String status, String userId, String userName) {
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.status = status;
        this.userId = userId;
        this.userName = userName;  // Set the user name
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    // Getters and setters
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
