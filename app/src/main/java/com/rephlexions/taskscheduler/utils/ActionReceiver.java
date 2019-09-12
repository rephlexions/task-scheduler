package com.rephlexions.taskscheduler.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_CATEGORY;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_DESCRIPTION;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ID;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_MILLI;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_PRIORITY;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_STATUS;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_TITLE;

public class ActionReceiver extends BroadcastReceiver {
    private static final String TAG = "CreateNotification";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        long id = intent.getLongExtra(EXTRA_ID, -1);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);
        String priority = intent.getStringExtra(EXTRA_PRIORITY);
        String status = intent.getStringExtra(EXTRA_STATUS);
        long dateTimeLong = intent.getLongExtra(EXTRA_MILLI, 1);
        String category = intent.getStringExtra(EXTRA_CATEGORY);

        String action = intent.getStringExtra("action");
        if (action.equals("ongoing")) {
            Intent intentStatus = new Intent("ChangeTaskStatus");
            intentStatus.putExtra(EXTRA_ID, id);
            intentStatus.putExtra(EXTRA_TITLE, title);
            intentStatus.putExtra(EXTRA_DESCRIPTION, description);
            intentStatus.putExtra(EXTRA_PRIORITY, priority);
            intentStatus.putExtra(EXTRA_MILLI, dateTimeLong);
            intentStatus.putExtra(EXTRA_STATUS, "ongoing");
            intentStatus.putExtra(EXTRA_CATEGORY, category);
            context.sendBroadcast(intentStatus);
            notificationManager.cancel(TAG, (int) id);
        } else if (action.equals("postpone")) {
            Intent intentStatus = new Intent("PostPoneTask");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 10);
            long newMillis = cal.getTimeInMillis();
            intentStatus.putExtra(EXTRA_ID, id);
            intentStatus.putExtra(EXTRA_TITLE, title);
            intentStatus.putExtra(EXTRA_DESCRIPTION, description);
            intentStatus.putExtra(EXTRA_PRIORITY, priority);
            intentStatus.putExtra(EXTRA_MILLI, newMillis);
            intentStatus.putExtra(EXTRA_STATUS, status);
            intentStatus.putExtra(EXTRA_CATEGORY, category);
            context.sendBroadcast(intentStatus);
            notificationManager.cancel(TAG, (int) id);
        }
    }
}