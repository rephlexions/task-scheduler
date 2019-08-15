package com.rephlexions.taskscheduler.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskCategoryDao {
    //DAO defines our database operations. Dont provide the method body.
    // Just annotate and ROOM will generate the necessary code.

    @Insert
    void insert(TaskCategory taskCategory);

    @Update
    void update(TaskCategory taskCategory);

    @Delete
    void delete(TaskCategory taskCategory);

    /*
    //Define custom database operation
    @Query("DELETE FROM TASK_CATEGORIES_TABLE")
    void deleteAllTasks();

    @Query("SELECT * FROM TASK_TABLE ORDER BY ID ASC")  // ORDER BY priority DESC
    LiveData<List<Task>> getAllTasks();
    */
}