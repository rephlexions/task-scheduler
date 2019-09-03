package com.rephlexions.taskscheduler.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);
/*
    @Query("SELECT * FROM CATEGORIES_TABLE ORDER BY ID ASC")
    LiveData<List<Category>> getAllCategories();
*/
    @Query("SELECT * FROM CATEGORIES_TABLE")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT id FROM categories_table WHERE categories_table.name = :catName")
    int getCategoryID( String catName);
}
