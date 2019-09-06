package com.rephlexions.taskscheduler;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rephlexions.taskscheduler.db.Category;
import com.rephlexions.taskscheduler.db.Task;
import com.rephlexions.taskscheduler.db.TaskRepository;

import java.util.List;

import static android.content.ContentValues.TAG;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Category>> allCategories;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
        allCategories = repository.getAllCategories();
    }

    LiveData<List<Task>> getAllTasksByCategory(String category) {
        return repository.getAllTasksByCategory(category);
    }

    LiveData<List<Task>> getAllTasksByPriority(String priority) {
        return repository.getAllTasksByPriority(priority);
    }

    public LiveData<List<Task>> getAllTasksByDateASC() {
        return repository.getAllTasksByDateASC();
    }

    public LiveData<List<Task>> getAllTasksByDateDESC() {
        return repository.getAllTasksByDateDESC();
    }

    public LiveData<List<Task>> getAllTasksByStatus(String status) {
        return repository.getAllTasksByStatus(status);
    }

    LiveData<List<Task>> getTaskID(String title) {
        return repository.getTaskID(title);
    }

    public void insert(Task task) {
        repository.insert(task, new TaskRepository.InsertTaskAsyncTask.InsertResult() {
            @Override
            public void onResult(long result) {
                Log.d(TAG, "onResult: " + result);
            }
        });
    }

    public void update(Task task) {
        repository.update(task);
    }

    public void delete(Task task) {
        repository.delete(task);
    }

    public void deleteAllTasks() {
        repository.deleteAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insertCategory(Category category) {
        repository.insertCategory(category);
    }

    public void updateCategory(Category category) {
        repository.updateCategory(category);
    }

    public void deleteCategory(Category category) {
        repository.deleteCategory(category);
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

}
