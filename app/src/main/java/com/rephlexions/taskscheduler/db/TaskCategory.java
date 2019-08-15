package com.rephlexions.taskscheduler.db;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_categories_table", foreignKeys = {
        @ForeignKey(entity = Task.class, parentColumns = "id", childColumns = "taskID"),
        @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryID")
})

public class TaskCategory {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int taskID;
    private int categoryID;

    public TaskCategory(int taskID, int categoryID) {
        this.taskID = taskID;
        this.categoryID = categoryID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getTaskID() {
        return taskID;
    }

    public int getCategoryID() {
        return categoryID;
    }

}
