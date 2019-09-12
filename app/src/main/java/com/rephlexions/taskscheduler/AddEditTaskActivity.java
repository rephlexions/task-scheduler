package com.rephlexions.taskscheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.rephlexions.taskscheduler.db.Category;
import com.rephlexions.taskscheduler.fragments.AddCategoryDialog;
import com.rephlexions.taskscheduler.fragments.DatePickerFragment;
import com.rephlexions.taskscheduler.fragments.TimePickerFragment;
import com.rephlexions.taskscheduler.utils.CategoryListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, AddCategoryDialog.AddCategoryDialogListener {

    // Intent extra keys. Uses package name to keep them unique
    public static final String EXTRA_ID =
            "com.example.taskscheduler.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.taskscheduler.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.example.taskscheduler.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY =
            "com.example.taskscheduler.EXTRA_PRIORITY";
    public static final String EXTRA_STATUS =
            "com.example.taskscheduler.EXTRA_STATUS";
    public static final String EXTRA_DATE =
            "com.example.taskscheduler.EXTRA_DATE";
    public static final String EXTRA_TIME =
            "com.example.taskscheduler.EXTRA_TIME";
    public static final String EXTRA_MILLI =
            "com.example.taskscheduler.EXTRA_MILLI";
    public static final String EXTRA_CATEGORY =
            "com.example.taskscheduler.EXTRA_CATEGORY";
    public static final String EXTRA_CATEGORIESLIST =
            "com.example.taskscheduler.EXTRA_CATEGORIESLIST";
    public static final String ACTION_ONGOING =
            "com.example.taskscheduler.ACTION_ONGOING";


    public static final String EXTRA_ALERTTITLE = "com.example.taskscheduler.EXTRA_ALERTTITLE";
    public static final String EXTRA_ALERTDESCRIPTION = "com.example.taskscheduler.EXTRA_ALERTDESCRIPTION";
    public static final String EXTRA_ALERTID = "com.example.taskscheduler.EXTRA_ALERTID";
    public static final String EXTRA_ALERTPRIORITY = "com.example.taskscheduler.EXTRA_ALERTPRIORITY";
    public static final String EXTRA_ALERTMILLI = "com.example.taskscheduler.EXTRA_ALERTMILLI";
    public static final String EXTRA_ALERTSTATUS = "cm.example.taskscheduler.EXTRA_ALERTSTATUS";
    public static final String EXTRA_ALERTCATEGORY = "com.example.taskscheduler.EXTRA_ALERTCATEGORY";
    private static final String TAG = "";


    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewStatus;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String radioChoice;
    private CheckBox checkBox;
    private Switch switchButton;
    private ImageButton deleteButton;
    private TextView datetextView;
    private TextView timetextView;
    private Calendar cal = Calendar.getInstance();
    private String taskStatus = "pending";
    private Spinner categoriesSpinner;
    private TaskViewModel taskViewModel;
    private TextView categoryTextView;
    private RadioButton nonePriority;
    private RadioButton lowPriority;
    private RadioButton mediumPriority;
    private RadioButton highPriority;

    long dateTimeLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        textViewStatus = findViewById(R.id.text_view_status);
        deleteButton = (ImageButton) findViewById(R.id.delete_datetime_button);
        datetextView = (TextView) findViewById(R.id.text_view_date);
        timetextView = (TextView) findViewById(R.id.text_view_time);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        nonePriority = (RadioButton) findViewById(R.id.radio_priority_none);
        lowPriority = (RadioButton) findViewById(R.id.radio_priority_low);
        mediumPriority = (RadioButton) findViewById(R.id.radio_priority_medium);
        highPriority = (RadioButton) findViewById(R.id.radio_priority_high);
        switchButton = (Switch) findViewById(R.id.priority_switch);
        checkBox = (CheckBox) findViewById(R.id.task_checkBox);
        categoriesSpinner = findViewById(R.id.categories_spinner);

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        final CategoryListAdapter adapter = new CategoryListAdapter();
        taskViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> category) {
                // Update the cached copy of the words in the adapter.
                // Update scroll view here
                final ArrayList<String> categoryNames = new ArrayList<>();
                adapter.setCategory(category);
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    categoryNames.add(String.valueOf(category.get(i).getName()));
                }
                categoryNames.add("Add category");
                createSpinners(categoryNames);
            }
        });

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Add category")) {
                    openDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (nonePriority.isChecked()) {
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
        // Handle default priority radio button
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View defaultRadioButton = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(defaultRadioButton);
        if (idx == 1) {
            radioChoice = "Low";
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    taskStatus = "ongoing";
                    Toast.makeText(AddEditTaskActivity.this, "Task is ongoing", Toast.LENGTH_SHORT).show();
                } else {
                    taskStatus = "pending";
                    Toast.makeText(AddEditTaskActivity.this, "Task is pending", Toast.LENGTH_SHORT).show();

                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!editTextTitle.getText().toString().isEmpty()) {
                        taskStatus = "completed";
                        switchButton.setChecked(false);
                        switchButton.setClickable(false);
                        textViewStatus.setPaintFlags(editTextTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        editTextTitle.setPaintFlags(editTextTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        Toast.makeText(AddEditTaskActivity.this, "Task completed", Toast.LENGTH_SHORT).show();
                    } else {
                        checkBox.setChecked(false);
                        Toast.makeText(AddEditTaskActivity.this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    taskStatus = "pending";
                    switchButton.setClickable(true);
                    if (!editTextTitle.getText().toString().isEmpty()) {
                        editTextTitle.setPaintFlags(0);
                        textViewStatus.setPaintFlags(0);
                    }
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
            taskStatus = intent.getStringExtra(EXTRA_STATUS);
            categoriesSpinner.setSelection(intent.getIntExtra(EXTRA_CATEGORY, 1));
            dateTimeLong = intent.getLongExtra(EXTRA_MILLI, 0L);
            if (dateTimeLong == 0L) {
                datetextView.setText("No date");
                timetextView.setText("No time");
            } else {
                String[] dateTime = getDate(dateTimeLong, "MM/dd/yy HH:mm");
                datetextView.setText(dateTime[0]);
                timetextView.setText(dateTime[1]);
                deleteButton.setVisibility(View.VISIBLE);
            }
            if (taskStatus.equals("ongoing")) {
                switchButton.setChecked(true);
            }
            if (taskStatus.equals("completed")) {
                checkBox.setChecked(true);
                editTextTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                switchButton.setChecked(false);
            }

            if (intent.getStringExtra(EXTRA_PRIORITY).equals("None")) {
                nonePriority.setChecked(true);
            } else if (intent.getStringExtra(EXTRA_PRIORITY).equals("Low")) {
                lowPriority.setChecked(true);
            } else if (intent.getStringExtra(EXTRA_PRIORITY).equals("Medium")) {
                mediumPriority.setChecked(true);
            } else {
                highPriority.setChecked(true);
            }
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


    private void openDialog() {
        AddCategoryDialog addCategoryDialog = new AddCategoryDialog();
        addCategoryDialog.show(getSupportFragmentManager(), "category dialog");
    }

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String date = datetextView.getText().toString();
        String time = timetextView.getText().toString();
        String category = categoriesSpinner.getSelectedItem().toString();
        ArrayList<String> categoriesList = retrieveAllItems(categoriesSpinner);
        categoriesList.remove(categoriesList.size() - 1);

        if (title.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a Title", Toast.LENGTH_SHORT).show();
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
        data.putExtra(EXTRA_STATUS, taskStatus);
        data.putExtra(EXTRA_CATEGORY, category);
        data.putStringArrayListExtra(EXTRA_CATEGORIESLIST, categoriesList);

        long id = getIntent().getLongExtra(EXTRA_ID, -1);
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
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(cal.getTime());
        if (!currentDateString.isEmpty()) {
            datetextView.setText(currentDateString);
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        timetextView.setText(hourOfDay + ":" + minute);
        deleteButton.setVisibility(View.VISIBLE);
    }


    public static String[] getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String date = formatter.format(calendar.getTime());
        String[] split = date.split(" ");
        return split;
    }

    private void createSpinners(ArrayList<String> listToUse) {
        ArrayAdapter<String> adp1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listToUse);
        categoriesSpinner.setAdapter(adp1);
    }

    @Override
    public void applyName(String name) {
        Category category = new Category(name);
        taskViewModel.insertCategory(category);
    }

    public ArrayList<String> retrieveAllItems(Spinner theSpinner) {
        Adapter adapter = theSpinner.getAdapter();
        int n = adapter.getCount();
        final ArrayList<String> categoryNames = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String user = (String) adapter.getItem(i);
            categoryNames.add(user);
        }
        return categoryNames;
    }

/*
    private void startAlarm(@NonNull Calendar c) {
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));



        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

 */
}
