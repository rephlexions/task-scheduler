package com.rephlexions.taskscheduler.reminders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.rephlexions.taskscheduler.AddEditTaskActivity;
import com.rephlexions.taskscheduler.R;
import com.rephlexions.taskscheduler.TaskSchedulerApp;
import com.rephlexions.taskscheduler.utils.ActionReceiver;

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
    private static final String TAG = "CreateNotification";

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

        Intent firstAction = new Intent(context, ActionReceiver.class);
        firstAction.putExtra(EXTRA_ID, id);
        firstAction.putExtra(EXTRA_TITLE, title);
        firstAction.putExtra(EXTRA_DESCRIPTION, contentText);
        firstAction.putExtra(EXTRA_PRIORITY, radioChoice);
        firstAction.putExtra(EXTRA_MILLI, dateTimeLong);
        firstAction.putExtra(EXTRA_STATUS, taskStatus);
        firstAction.putExtra(EXTRA_CATEGORY, category);
        firstAction.putExtra("action", "ongoing");

        Intent secondAction = new Intent(context, ActionReceiver.class);
        secondAction.putExtra(EXTRA_ID, id);
        secondAction.putExtra(EXTRA_TITLE, title);
        secondAction.putExtra(EXTRA_DESCRIPTION, contentText);
        secondAction.putExtra(EXTRA_PRIORITY, radioChoice);
        secondAction.putExtra(EXTRA_MILLI, dateTimeLong);
        secondAction.putExtra(EXTRA_STATUS, taskStatus);
        secondAction.putExtra(EXTRA_CATEGORY, category);
        secondAction.putExtra("action", "postpone");


        PendingIntent firstActionPendingIntent = PendingIntent.getBroadcast(context, 1,
                firstAction, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent secondActionPendingIntent = PendingIntent.getBroadcast(context, 2,
                secondAction, PendingIntent.FLAG_UPDATE_CURRENT);

//        Intent dialog = new Intent(context, MainActivity.class);
//        dialog.putExtra("fromnotification", true);
//
//        PendingIntent postPoneIntent = PendingIntent.getActivity(context, 0,
//                dialog, 0);

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, TaskSchedulerApp.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_alarm_on_black)
                .setContentTitle(title)
                .setTicker(msgAlert)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_play_arrow_black_, "ongoing", firstActionPendingIntent)
                .addAction(R.drawable.ic_add_alarm_black, "postpone by 10 minutes", secondActionPendingIntent);
                //
        nb.setContentIntent(notificationIntent);
        nb.setDefaults(NotificationCompat.DEFAULT_SOUND);
        nb.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TAG, (int) id, nb.build());
    }
}
