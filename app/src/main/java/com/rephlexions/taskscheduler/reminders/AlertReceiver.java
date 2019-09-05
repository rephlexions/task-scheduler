package com.rephlexions.taskscheduler.reminders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.rephlexions.taskscheduler.AddEditTaskActivity;
import com.rephlexions.taskscheduler.R;
import com.rephlexions.taskscheduler.utils.ActionReceiver;

import static com.rephlexions.taskscheduler.AddEditTaskActivity.ACTION_ONGOING;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ALERTCATEGORY;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ALERTDESCRIPTION;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ALERTID;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ALERTMILLI;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ALERTPRIORITY;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ALERTSTATUS;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ALERTTITLE;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_CATEGORY;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_DESCRIPTION;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ID;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_MILLI;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_PRIORITY;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_STATUS;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_TITLE;

public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "TaskScheduler";

    @Override
    public void onReceive(Context context, Intent intent) {

        long id = intent.getLongExtra(EXTRA_ALERTID, -1);
        String title = intent.getStringExtra(EXTRA_ALERTTITLE);
        String description = intent.getStringExtra(EXTRA_ALERTDESCRIPTION);
        String radioChoice = intent.getStringExtra(EXTRA_ALERTPRIORITY);
        long dateTimeLong = intent.getLongExtra(EXTRA_ALERTMILLI, 1L);
        String taskStatus = intent.getStringExtra(EXTRA_ALERTSTATUS);
        String category = intent.getStringExtra(EXTRA_ALERTCATEGORY);

        createNotification(context, title, description, "alert", id, radioChoice, dateTimeLong, taskStatus, category);
    }

    public void createNotification(Context context, String title, String contentText,
                                   String msgAlert, long id, String radioChoice, long dateTimeLong,
                                   String taskStatus, String category) {

        Intent intent = new Intent(context, AddEditTaskActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, contentText);
        intent.putExtra(EXTRA_PRIORITY, radioChoice);
        intent.putExtra(EXTRA_MILLI, dateTimeLong);
        intent.putExtra(EXTRA_STATUS, taskStatus);
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent actionIntent = new Intent(context, ActionReceiver.class);
        actionIntent.putExtra(EXTRA_ID, id);
        actionIntent.putExtra(EXTRA_TITLE, title);
        actionIntent.putExtra(EXTRA_DESCRIPTION, contentText);
        actionIntent.putExtra(EXTRA_PRIORITY, radioChoice);
        actionIntent.putExtra(EXTRA_MILLI, dateTimeLong);
        actionIntent.putExtra(EXTRA_STATUS, taskStatus);
        actionIntent.putExtra(EXTRA_CATEGORY, category);
        actionIntent.putExtra("action","ongoing");

        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context,1,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_alarm_on_black)
                .setContentTitle(title)
                .setTicker(msgAlert)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_close, "ongoing",actionPendingIntent);
        nb.setContentIntent(notificationIntent);
        nb.setDefaults(NotificationCompat.DEFAULT_SOUND);
        nb.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TAG, 1, nb.build());
    }
}

