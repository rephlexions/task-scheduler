package com.rephlexions.taskscheduler.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import static androidx.constraintlayout.widget.Constraints.TAG;

@Database(entities = {Task.class, Category.class},
        version = 1, exportSchema = false)

public abstract class TaskDatabase extends RoomDatabase {

    // Create a database instance that acts as a unique singleton throughout the app
    private static TaskDatabase instance;

    //Room generates the code for this method
    public abstract TaskDao taskDao();
    public abstract CategoryDao categoryDao();
    //public abstract TaskCategoryJoinDao taskCategoryJoinDao();

    // get Database instance. Synchronized (one thread at a time can access this method)
    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TaskDatabase.class,
                    "task_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    // Populate the database on Create
    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskDao taskDao;
        private CategoryDao categoryDao;
        //private TaskCategoryJoinDao taskCategoryJoinDao;

        private PopulateDbAsyncTask(TaskDatabase db) {
            taskDao = db.taskDao();
            categoryDao = db.categoryDao();
            //taskCategoryJoinDao = db.taskCategoryJoinDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            taskDao.insert(new Task("Buy some milk", "2 liters", "Low","pending",1568804400000L, "Home"));
            taskDao.insert(new Task("Plan your birthday party", "Shopping List", "High","pending", 1569661200000L, "Home"));
            categoryDao.insert(new Category("Home"));
            categoryDao.insert(new Category("Work"));
            return null;
        }
    }
}
