package com.rephlexions.taskscheduler.reminders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.rephlexions.taskscheduler.AddEditTaskActivity;
import com.rephlexions.taskscheduler.R;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context, "hey", "hey2", "alert");
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert) {
        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, new Intent(context, AddEditTaskActivity.class), 0);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_alarm_on_black)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);
        nb.setContentIntent(notificationIntent);
        nb.setDefaults(NotificationCompat.DEFAULT_SOUND);
        nb.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, nb.build());
    }
}

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        NotificationHelper notificationHelper = new NotificationHelper(context);
//        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
//        notificationHelper.getManager().notify(1, nb.build());
//    }
//}


