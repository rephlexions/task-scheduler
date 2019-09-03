package com.rephlexions.taskscheduler.db;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "task_categories_join_table",
        primaryKeys = {"taskID","categoryID"},
        foreignKeys = {
        @ForeignKey(entity = Task.class, parentColumns = "id", childColumns = "taskID"),
        @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryID")
})

public class TaskCategoryJoin {
    private int taskID;
    private int categoryID;

    public TaskCategoryJoin(int taskID, int categoryID) {
        this.taskID = taskID;
        this.categoryID = categoryID;
    }
    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getTaskID() {
        return taskID;
    }

    public int getCategoryID() {
        return categoryID;
    }

}
