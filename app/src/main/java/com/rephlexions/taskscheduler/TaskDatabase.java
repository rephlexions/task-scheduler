package com.rephlexions.taskscheduler;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    // Create a database instance that acts as a unique singleton throughout the app
    private static TaskDatabase instance;

    //Room generates the code for this method
    public abstract TaskDao taskDao();

    // get Database instance. Synchronized (one thread at a time can access this method)
    public static synchronized TaskDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), TaskDatabase.class,
                    "task_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    // Populate the database on Create
    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private TaskDao taskDao;

        private PopulateDbAsyncTask (TaskDatabase db){
            taskDao = db.taskDao();
        }

        @Override
        protected Void doInBackground(Void...voids){
            taskDao.insert(new Task("Buy some milk", "2 liters", "Low"));
            taskDao.insert(new Task("Feed the cat", "also pet the cat", "Low"));
            return null;
        }
    }
}
