package com.rephlexions.taskscheduler.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {
    //Class that represents one task object and the database table
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private long dueDate;
    private String category;

    public Task( String title, String description, String priority, String status, long dueDate, String category) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
        this.category = category;
    }
/*
    // Constructor for a task without a deadline
    public Task(String title, String description, String priority, String status) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
    }

 */

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    // counted in milliseconds
    public long getDueDate() {
        return dueDate;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }


}
