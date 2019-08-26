package com.rephlexions.taskscheduler.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskCategoryJoinDao {
    // DAO defines our database operations. Dont provide the method body.
    // Just annotate and ROOM will generate the necessary code.

    @Insert
    void insert(TaskCategoryJoin taskCategoryJoin);

    @Update
    void update(TaskCategoryJoin taskCategoryJoin);

    @Delete
    void delete(TaskCategoryJoin taskCategoryJoin);

    @Query("SELECT * FROM TASK_TABLE INNER JOIN TASK_CATEGORIES_JOIN_TABLE " +
            "ON TASK_TABLE.id=TASK_CATEGORIES_JOIN_TABLE.taskID WHERE categoryID=:categoryId")
    LiveData<List<Task>> getTasksForCategories(final int categoryId);
/*
    @Query("SELECT * FROM CATEGORIES_TABLE INNER JOIN TASK_CATEGORIES_JOIN_TABLE " +
            "ON CATEGORIES_TABLE.id=TASK_CATEGORIES_JOIN_TABLE.categoryID WHERE taskID=:taskID")
    LiveData<List<Task>> getCategoriesForTasks(final int taskID);

 */
}