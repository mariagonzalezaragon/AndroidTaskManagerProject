package com.example.taskmanager_project;

public class Task {
    private String taskName;
    private String dueDate;
    private String status;

    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(Task.class)
    }

    public Task(String taskName, String dueDate, String status) {
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }
}
