package com.rephlexions.taskscheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rephlexions.taskscheduler.fragments.DatePickerFragment;
import com.rephlexions.taskscheduler.fragments.TimePickerFragment;
import com.rephlexions.taskscheduler.reminders.AlertReceiver;

import java.text.DateFormat;
import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    // Intent extra keys. Uses package name to keep them unique
    public static final String EXTRA_ID =
            "com.example.taskscheduler.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.taskscheduler.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.example.taskscheduler.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY =
            "com.example.taskscheduler.EXTRA_PRIORITY";
    public static final String EXTRA_DATE =
            "com.example.taskscheduler.EXTRA_DATE";
    public static final String EXTRA_TIME =
            "com.example.taskscheduler.EXTRA_TIME";
    private static final String TAG = "";


    private EditText editTextTitle;
    private EditText editTextDescription;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String radioChoice;
    private CheckBox checkBox;
    private ImageButton deleteButton;
    private TextView datetextView;
    private TextView timetextView;
    private Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        deleteButton = (ImageButton) findViewById(R.id.delete_datetime_button);
        datetextView = (TextView) findViewById(R.id.text_view_date);
        timetextView = (TextView) findViewById(R.id.text_view_time);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        final RadioButton nonePriority = (RadioButton) findViewById(R.id.radio_priority_none);
        final RadioButton lowPriority = (RadioButton) findViewById(R.id.radio_priority_low);
        final RadioButton mediumPriority = (RadioButton) findViewById(R.id.radio_priority_medium);
        final RadioButton highPriority = (RadioButton) findViewById(R.id.radio_priority_high);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (nonePriority.isChecked()) {
                    //Toast.makeText(AddEditTaskActivity.this, "None", Toast.LENGTH_SHORT).show();
                    radioChoice = "None";
                } else if (lowPriority.isChecked()) {
                    radioChoice = "Low";
                } else if (mediumPriority.isChecked()) {
                    radioChoice = "Medium";
                } else {
                    radioChoice = "High";
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dateView = (TextView) findViewById(R.id.text_view_date);
                TextView timeView = (TextView) findViewById(R.id.text_view_time);
                dateView.setText("No date");
                timeView.setText("No time");
                dateView.setPaintFlags(dateView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                timeView.setPaintFlags(timeView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            }
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Task");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            if (intent.getStringExtra(EXTRA_PRIORITY).equals("None")) {
                nonePriority.setChecked(true);
            } else if (intent.getStringExtra(EXTRA_PRIORITY).equals("Low")) {
                lowPriority.setChecked(true);
            } else if (intent.getStringExtra(EXTRA_PRIORITY).equals("Medium")) {
                mediumPriority.setChecked(true);
            } else {
                highPriority.setChecked(true);
            }
            //numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        } else {
            setTitle("Add Task");
        }

        TextView dateTextView = (TextView) findViewById(R.id.text_view_date);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        TextView timeTextView = (TextView) findViewById(R.id.text_view_time);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String date = datetextView.getText().toString();
        String time = timetextView.getText().toString();

        if (title.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a Title and a Description", Toast.LENGTH_SHORT).show();
            return;
        }

        /* Accept input and save task. Return data to the onActivityResult method in MainActivity
        Insert data in database and close activity */
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, radioChoice);
        data.putExtra(EXTRA_DATE, date);
        data.putExtra(EXTRA_TIME, time);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        // Close activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_task:
                saveTask();
                // startAlarm(c);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());
        if (!currentDateString.isEmpty()) {
            datetextView.setText(currentDateString);
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        timetextView.setText(hourOfDay + ":" + minute);
        deleteButton.setVisibility(View.VISIBLE);
/*
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
*/

    }

    private void startAlarm(@NonNull Calendar c) {
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        /*

Intent alertIntent = new Intent(this, AlertReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                PendingIntent.getBroadcast(this,1,alertIntent,PendingIntent.FLAG_UPDATE_CURRENT));
         */

    }
}
