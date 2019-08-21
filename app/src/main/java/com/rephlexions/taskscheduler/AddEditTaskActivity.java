package com.rephlexions.taskscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
            TimePickerDialog.OnTimeSetListener{

    // Intent extra keys. Uses package name to keep them unique
    public static final String EXTRA_ID =
            "com.example.taskscheduler.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.taskscheduler.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.example.taskscheduler.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY =
            "com.example.taskscheduler.EXTRA_PRIORITY";

    private EditText editTextTitle;
    private EditText editTextDescription;
    RadioGroup radioGroup;
    RadioButton radioButton;
    String radioChoice;
    CheckBox checkBox;
    ImageButton deleteButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        deleteButton = (ImageButton) findViewById(R.id.delete_datetime_button);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        final RadioButton nonePriority = (RadioButton) findViewById(R.id.radio_priority_none);
        final RadioButton lowPriority = (RadioButton) findViewById(R.id.radio_priority_low);
        final RadioButton mediumPriority = (RadioButton) findViewById(R.id.radio_priority_medium);
        RadioButton highPriority = (RadioButton) findViewById(R.id.radio_priority_high);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(nonePriority.isChecked()){
                    //Toast.makeText(AddEditTaskActivity.this, "None", Toast.LENGTH_SHORT).show();
                    radioChoice = "None";
                }
                else if (lowPriority.isChecked()){
                    radioChoice = "Low";
                }
                else if (mediumPriority.isChecked()){
                    radioChoice = "Medium";
                }
                else{
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
                dateView.setPaintFlags(dateView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                timeView.setPaintFlags(timeView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

            }
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Task");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            if(intent.getStringExtra(EXTRA_PRIORITY).equals("None")){
                nonePriority.setChecked(true);
            }
            else if(intent.getStringExtra(EXTRA_PRIORITY).equals("Low")){
                lowPriority.setChecked(true);
            }
            else if(intent.getStringExtra(EXTRA_PRIORITY).equals("Medium")){
                mediumPriority.setChecked(true);
            }
            else{
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        //TODO: save date string into db
        TextView textView = (TextView) findViewById(R.id.text_view_date);
        if (!currentDateString.isEmpty()){
            textView.setText(currentDateString);
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView textView = (TextView) findViewById(R.id.text_view_time);
        textView.setText(hourOfDay + ":" + minute);
        deleteButton.setVisibility(View.VISIBLE);
    }
}
