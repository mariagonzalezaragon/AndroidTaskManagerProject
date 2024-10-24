package com.example.taskmanager_project;

public class Task {
    private String id;
    private String taskName;
    private String dueDate;
    private String status;
    private String userId;
    private String userName;



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

    // Getters and setters
    public String getTaskName() {
        return taskName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
