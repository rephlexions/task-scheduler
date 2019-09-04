package com.rephlexions.taskscheduler.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.ViewModelProviders;

import com.rephlexions.taskscheduler.MainActivity;
import com.rephlexions.taskscheduler.TaskViewModel;
import com.rephlexions.taskscheduler.db.Task;

import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_CATEGORY;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_DESCRIPTION;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_ID;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_MILLI;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_PRIORITY;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_STATUS;
import static com.rephlexions.taskscheduler.AddEditTaskActivity.EXTRA_TITLE;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra(EXTRA_ID, -1);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);
        String priority = intent.getStringExtra(EXTRA_PRIORITY);
        String status = intent.getStringExtra(EXTRA_STATUS);
        long dateTimeLong = intent.getLongExtra(EXTRA_MILLI,1);
        String category = intent.getStringExtra(EXTRA_CATEGORY);

        String action=intent.getStringExtra("action");
        if(action.equals("ongoing")){
            Intent intentStatus = new Intent("ChangeTaskStatus");
            intentStatus.putExtra(EXTRA_TITLE, title);
            intentStatus.putExtra(EXTRA_DESCRIPTION, description);
            intentStatus.putExtra(EXTRA_PRIORITY, priority);
            intentStatus.putExtra(EXTRA_MILLI, dateTimeLong);
            intentStatus.putExtra(EXTRA_STATUS, "ongoing");
            intentStatus.putExtra(EXTRA_CATEGORY, category);
            context.sendBroadcast(intentStatus);
        }
        else if(action.equals("action2")){
            performAction2();

        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void performAction1(){

    }

    public void performAction2(){

    }

}