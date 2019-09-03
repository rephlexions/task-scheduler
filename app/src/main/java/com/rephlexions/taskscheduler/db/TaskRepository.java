package com.rephlexions.taskscheduler.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private CategoryDao categoryDao;
    private TaskCategoryJoinDao taskCategoryJoinDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> allDueDates;
    private LiveData<List<Category>> allCategories;

    public TaskRepository(Application application) {
        //Since application is a subclass of context we can use it as a context to create the database instance
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        categoryDao = database.categoryDao();
        taskCategoryJoinDao = database.taskCategoryJoinDao();

        allTasks = taskDao.getAllTasks();
        allDueDates = taskDao.getAllDueDates();
        allCategories = categoryDao.getAllCategories();
    }

    // The API that the repository exposes to the ViewModel
    public void insert(Task task) {
        new InsertTaskAsyncTask(taskDao).execute(task);
    }

    public void update(Task task) {
        new UpdateTaskAsyncTask(taskDao).execute(task);
    }

    public void delete(Task task) {
        new DeleteTaskAsyncTask(taskDao).execute(task);
    }

    public void deleteAllTasks() {
        new DeleteAllTasksAsyncTask(taskDao).execute();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getAllDueDates() {
        return allDueDates;
    }

    //Room doesnt allow database operations on the main thread (app crash). Use async tasks
    private static class InsertTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private InsertTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.insert(tasks[0]);
            return null;
        }
    }

    private static class UpdateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private UpdateTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.update(tasks[0]);
            return null;
        }
    }

    private static class DeleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private DeleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.delete(tasks[0]);
            return null;
        }
    }

    private static class DeleteAllTasksAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskDao taskDao;

        private DeleteAllTasksAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            taskDao.deleteAllTasks();
            return null;
        }
    }

    public void insertCategory(Category category) {
        new InsertCategoryAsyncTask(categoryDao).execute(category);
    }

    public void updateCategory(Category category) {
        new UpdateCategoryAsyncTask(categoryDao).execute(category);
    }

    public void deleteCategory(Category category) {
        new DeleteCategoryAsyncTask(categoryDao).execute(category);
    }

    private static class InsertCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private InsertCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.insert(categories[0]);
            return null;
        }
    }

    private static class UpdateCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private UpdateCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.update(categories[0]);
            return null;
        }
    }

    private static class DeleteCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private DeleteCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.delete(categories[0]);
            return null;
        }
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }


//    private static class GetCategoriesAsyncTask extends AsyncTask<Void, Void,  LiveData<Category> > {
//        private CategoryDao categoryDao;
//
//        private GetCategoriesAsyncTask(CategoryDao categoryDao) {
//            this.categoryDao = categoryDao;
//        }
//
//        @Override
//        protected LiveData<Category> doInBackground(Void... voids) {
//            return categoryDao.getAllCategories();
//        }
//    }

    //    public LiveData<List<Category>> getAllCategories() {
//        return allCategories;
//    }
//    private static class getCategoriesAsyncTask extends AsyncTask<Void, Void, Void> {
//        private CategoryDao categoryDao;
//
//        private getCategoriesAsyncTask(CategoryDao categoryDao) {
//            this.categoryDao = categoryDao;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            categoryDao.getAllCategoriesList();
//            return null;
//        }
//    }


}
