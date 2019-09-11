package com.rephlexions.taskscheduler.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    //DAO defines our database operations. Dont provide the method body.
    // Just annotate and ROOM will generate the necessary code.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    //Define custom database operation
    @Query("DELETE FROM TASK_TABLE")
    void deleteAllTasks();

    @Query("SELECT * FROM TASK_TABLE ORDER BY ID ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT id, dueDate FROM TASK_TABLE ")
    LiveData<List<Task>> getAllDueDates();

    @Query("SELECT * FROM TASK_TABLE WHERE category = :category")
    LiveData<List<Task>> getAllTasksByCategory(String category);

    @Query("SELECT * FROM TASK_TABLE WHERE priority = :priority")
    LiveData<List<Task>> getAllTasksByPriority(String priority);

    @Query("SELECT * FROM TASK_TABLE ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasksByDateASC();

    @Query("SELECT * FROM TASK_TABLE ORDER BY dueDate DESC")
    LiveData<List<Task>> getAllTasksByDateDESC();

    @Query("SELECT * FROM TASK_TABLE WHERE title = :title")
    LiveData<List<Task>> getTaskID(String title);

    @Query("SELECT * FROM TASK_TABLE WHERE status = :status")
    LiveData<List<Task>> getAllTasksByStatus(String status);

}