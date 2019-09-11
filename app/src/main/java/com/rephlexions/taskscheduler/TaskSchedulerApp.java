package com.rephlexions.taskscheduler;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/*
Wrapper for our applications that starts before Main Activity and creates notification channels
 */
public class TaskSchedulerApp extends Application {
    public static final String CHANNEL_1_ID = "taskAlert";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Task Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Receive alerts for tasks");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
