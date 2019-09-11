package com.rephlexions.taskscheduler.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private CategoryDao categoryDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> allDueDates;
    private LiveData<List<Category>> allCategories;

    public TaskRepository(Application application) {
        //Since application is a subclass of context we can use it as a context to create the database instance
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        categoryDao = database.categoryDao();

        allTasks = taskDao.getAllTasks();
        allDueDates = taskDao.getAllDueDates();
        allCategories = categoryDao.getAllCategories();

    }

    public LiveData<List<Task>> getAllTasksByCategory(String category) {
        return taskDao.getAllTasksByCategory(category);
    }

    public LiveData<List<Task>> getAllTasksByPriority(String priority) {
        return taskDao.getAllTasksByPriority(priority);
    }

    public LiveData<List<Task>> getAllTasksByDateASC() {
        return taskDao.getAllTasksByDateASC();
    }

    public LiveData<List<Task>> getAllTasksByDateDESC() {
        return taskDao.getAllTasksByDateDESC();
    }

    public LiveData<List<Task>> getAllTasksByStatus(String status) {
        return taskDao.getAllTasksByStatus(status);
    }

    // The API that the repository exposes to the ViewModel
    public LiveData<List<Task>> getTaskID(String title) {
        return taskDao.getTaskID(title);
    }

    public void insert(Task task, InsertTaskAsyncTask.InsertResult insertResult) {
        new InsertTaskAsyncTask(taskDao, insertResult).execute(task);
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

    //Room doesnt allow database operations on the main thread (app crash). Use async tasks
    public static class InsertTaskAsyncTask extends AsyncTask<Task, Void, Long> {
        private final TaskDao taskDao;
        private final InsertResult insertResult;

        private InsertTaskAsyncTask(TaskDao taskDao, InsertResult insertResult) {
            this.taskDao = taskDao;
            this.insertResult = insertResult;
        }

        @Override
        protected Long doInBackground(Task... tasks) {
            return taskDao.insert(tasks[0]);
        }

        @Override
        protected void onPostExecute(Long taskId) {
            insertResult.onResult(taskId);
        }

        public interface InsertResult {
            void onResult(long result);
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
