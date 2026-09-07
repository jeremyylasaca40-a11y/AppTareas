package com.example.apptareas;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task {
    private int id;
    private String title;
    private String description;
    private String status;
    private String priority; // NUEVO
    private String dueDate;
    private String createdDate;
    private String assignedUser;

    public Task() {
        this.createdDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        this.status = "pendiente";
        this.priority = "media"; // Valor por defecto
    }

    public Task(String title, String description, String dueDate, String assignedUser, String priority) {
        this();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.assignedUser = assignedUser;
        this.priority = priority;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; } // NUEVO
    public void setPriority(String priority) { this.priority = priority; } // NUEVO

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getAssignedUser() { return assignedUser; }
    public void setAssignedUser(String assignedUser) { this.assignedUser = assignedUser; }
}