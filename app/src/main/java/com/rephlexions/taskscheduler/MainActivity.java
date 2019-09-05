package com.rephlexions.taskscheduler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.rephlexions.taskscheduler.db.Category;
import com.rephlexions.taskscheduler.db.Task;
import com.rephlexions.taskscheduler.db.TaskDao;
import com.rephlexions.taskscheduler.db.TaskDatabase;
import com.rephlexions.taskscheduler.db.TaskRepository;
import com.rephlexions.taskscheduler.reminders.AlertReceiver;
import com.rephlexions.taskscheduler.utils.CategoryListAdapter;
import com.rephlexions.taskscheduler.utils.MyIntentService;
import com.rephlexions.taskscheduler.utils.TaskListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlarmManager;
import android.app.Application;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;
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

public class MainActivity extends AppCompatActivity {
    // Constants to distinguish between different requests
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    private static final String TAG = "parseDate";

    public static final String EXTRA_ALERTTITLE = "com.example.taskscheduler.EXTRA_ALERTTITLE";
    public static final String EXTRA_ALERTDESCRIPTION = "com.example.taskscheduler.EXTRA_ALERTDESCRIPTION";
    public static final String EXTRA_ALERTID = "com.example.taskscheduler.EXTRA_ALERTID";
    public static final String EXTRA_ALERTPRIORITY = "com.example.taskscheduler.EXTRA_ALERTPRIORITY";
    public static final String EXTRA_ALERTMILLI = "com.example.taskscheduler.EXTRA_ALERTMILLI";
    public static final String EXTRA_ALERTSTATUS = "cm.example.taskscheduler.EXTRA_ALERTSTATUS";
    public static final String EXTRA_ALERTCATEGORY = "com.example.taskscheduler.EXTRA_ALERTCATEGORY";


    private TaskViewModel taskViewModel;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private FloatingActionButton buttonAddTask;
    private DrawerLayout drawer;
    private Button navButton;

    String date, time;
    String year, month, day;
    int hour, minute;
    ArrayList<String> categoriesList = new ArrayList<>();
    ArrayList<Integer> idList = new ArrayList<>();
    private Menu menu;
    final TaskListAdapter taskAdapter = new TaskListAdapter();
    BroadcastReceiver broadcastReceiver;
    public long newTaskID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiate RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        // Get ViewModel instance inside the activity
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        //Observe the live data and get changes in the ViewModel
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                // Update RecyclerView
                adapter.submitList(tasks);
            }
        });
        final CategoryListAdapter categoryAdapter = new CategoryListAdapter();
        taskViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> category) {
                // Update the cached copy of the words in the adapter.
                // Update scroll view here
                categoryAdapter.setCategory(category);
                for (int i = 0; i < categoryAdapter.getItemCount(); i++) {
                    categoriesList.add(String.valueOf(category.get(i).getName()));
                }
            }
        });
//
//        final TaskListAdapter taskAdapter = new TaskListAdapter();
//        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
//            @Override
//            public void onChanged(@Nullable final List<Task> task) {
//                taskAdapter.setTask(task);
//                idList.clear();
//                for (int i = 0; i < taskAdapter.getItemCount(); i++) {
//                    idList.add(task.get(i).getId());
//                }
////                Log.d(TAG, "onActivityResult: " + idList);
////                Log.d(TAG, "onActivityResult: " + idList.get(idList.size() - 1));
//                newTaskID = idList.get(idList.size() - 1);
//            }
//        });

        Intent intent = new Intent(this, MyIntentService.class);
        startService(intent);

        registerReceiver(broadcastReceiver, new IntentFilter("ChangeTaskStatus"));
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("ChangeTaskStatus")){
                    long id = intent.getLongExtra(EXTRA_ID,-1);
                    String title = intent.getStringExtra(EXTRA_TITLE);
                    String description = intent.getStringExtra(EXTRA_DESCRIPTION);
                    String priority = intent.getStringExtra(EXTRA_PRIORITY);
                    String status = intent.getStringExtra(EXTRA_STATUS);
                    long dateTimeLong = intent.getLongExtra(EXTRA_MILLI,1);
                    String category = intent.getStringExtra(EXTRA_CATEGORY);
                    Task task = new Task(title,description,priority,status,dateTimeLong,category);
                    task.setId(id);
                    taskViewModel.update(task);
                }
            }
        };

        // Delete on swipe
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // TODO: apply different actions depending on the direction
                // on swipe get task position and delete
                taskViewModel.delete(adapter.getTaskAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        // Implements onItemClickListener interface. Get task details and startActivityForResult
        adapter.setOnItemClickListener(new TaskAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                intent.putExtra(EXTRA_ID, task.getId());
                intent.putExtra(EXTRA_TITLE, task.getTitle());
                intent.putExtra(EXTRA_DESCRIPTION, task.getDescription());
                intent.putExtra(EXTRA_PRIORITY, task.getPriority());
                intent.putExtra(EXTRA_STATUS, task.getStatus());
                //TODO: putExtra task category
                intent.putExtra(EXTRA_MILLI, task.getDueDate());
                startActivityForResult(intent, EDIT_TASK_REQUEST);
            }
        });

        buttonAddTask = findViewById(R.id.button_add_task);
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                // Get our input back from AddEditTaskActivity
                startActivityForResult(intent, ADD_TASK_REQUEST);
            }
        });

        // Create drawer menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navButton = (Button) findViewById(R.id.nav_add_category);

    }

    @Override
    protected void onPause() {
        super.onPause();
        registerReceiver(broadcastReceiver, new IntentFilter("ChangeTaskStatus"));
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("ChangeTaskStatus")){
                    long id = intent.getLongExtra(EXTRA_ID,-1);
                    String title = intent.getStringExtra(EXTRA_TITLE);
                    String description = intent.getStringExtra(EXTRA_DESCRIPTION);
                    String priority = intent.getStringExtra(EXTRA_PRIORITY);
                    String status = intent.getStringExtra(EXTRA_STATUS);
                    long dateTimeLong = intent.getLongExtra(EXTRA_MILLI,1);
                    String category = intent.getStringExtra(EXTRA_CATEGORY);
                    Task task = new Task(title,description,priority,status,dateTimeLong,category);
                    task.setId(id);
                    taskViewModel.update(task);
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(broadcastReceiver, new IntentFilter("ChangeTaskStatus"));
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("ChangeTaskStatus")){
                    long id = intent.getLongExtra(EXTRA_ID,-1);
                    String title = intent.getStringExtra(EXTRA_TITLE);
                    String description = intent.getStringExtra(EXTRA_DESCRIPTION);
                    String priority = intent.getStringExtra(EXTRA_PRIORITY);
                    String status = intent.getStringExtra(EXTRA_STATUS);
                    long dateTimeLong = intent.getLongExtra(EXTRA_MILLI,1);
                    String category = intent.getStringExtra(EXTRA_CATEGORY);
                    Task task = new Task(title,description,priority,status,dateTimeLong,category);
                    task.setId(id);
                    taskViewModel.update(task);
                }
            }
        };
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final CategoryListAdapter categoryAdapter = new CategoryListAdapter();
        taskViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> category) {
                // Update the cached copy of the words in the adapter.
                // Update scroll view here
                categoryAdapter.setCategory(category);
                categoriesList.clear();
                for (int i = 0; i < categoryAdapter.getItemCount(); i++) {
                    categoriesList.add(String.valueOf(category.get(i).getName()));
                }
            }
        });
        SubMenu categoryMenu = menu.findItem(R.id.filter_category).getSubMenu();
        categoryMenu.clear();
        for (int i = 0; i < categoriesList.size(); i++) {
            categoryMenu.add(0, i, Menu.NONE, categoriesList.get(i));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            final String title = data.getStringExtra(EXTRA_TITLE);
            final String description = data.getStringExtra(EXTRA_DESCRIPTION);
            final String priority = data.getStringExtra(EXTRA_PRIORITY);
            final String status = data.getStringExtra(EXTRA_STATUS);
            final String category = data.getStringExtra(EXTRA_CATEGORY);
            categoriesList = data.getStringArrayListExtra(AddEditTaskActivity.EXTRA_CATEGORIESLIST);

            date = data.getStringExtra(AddEditTaskActivity.EXTRA_DATE);
            time = data.getStringExtra(AddEditTaskActivity.EXTRA_TIME);
            final long timeMillis = parseDate(date, time);

            //Create and insert task into the database
            TaskRepository repository = new TaskRepository(getApplication());
            Task task = new Task(title, description, priority, status, timeMillis, category);
            repository.insert(task, new TaskRepository.InsertTaskAsyncTask.InsertResult() {
                @Override
                public void onResult(long result) {
                    Log.d(TAG, "onResultMain: " + result);
                    newTaskID= result;
                    startAlarm(result, title, description,priority,timeMillis,status,category);
                }
            });

//            Intent alertIntent = new Intent(this, AlertReceiver.class);
//            alertIntent.putExtra(EXTRA_ID, newTaskID);
//            alertIntent.putExtra(EXTRA_TITLE, title);
//            alertIntent.putExtra(EXTRA_DESCRIPTION, description);
//            alertIntent.putExtra(EXTRA_PRIORITY, priority);
//            alertIntent.putExtra(EXTRA_MILLI, timeMillis);
//            alertIntent.putExtra(EXTRA_STATUS, status);
//            alertIntent.putExtra(EXTRA_CATEGORY, category);
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1,
//                    alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);

            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();



            //TODO: check IF duedate is empty -> create Task without a due date.


//            Intent alertIntent = new Intent(this, AlertReceiver.class);
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 1,
//                    alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMilis, pendingIntent);
//
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(this, AlertReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMilis, pendingIntent);

        } else if (requestCode == EDIT_TASK_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(EXTRA_ID, -1);

            //Don't update id ID is not valid
            if (id == -1) {
                Toast.makeText(this, "Task can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(EXTRA_TITLE);
            String description = data.getStringExtra(EXTRA_DESCRIPTION);
            String priority = data.getStringExtra(EXTRA_PRIORITY);
            String status = data.getStringExtra(EXTRA_STATUS);
            String category = data.getStringExtra(EXTRA_CATEGORY);
            ArrayList<String> categoriesList = data.getStringArrayListExtra(AddEditTaskActivity.EXTRA_CATEGORIESLIST);

            date = data.getStringExtra(AddEditTaskActivity.EXTRA_DATE);
            time = data.getStringExtra(AddEditTaskActivity.EXTRA_TIME);
            long timeMillis = parseDate(date, time);

            Task task = new Task(title, description, priority, status, timeMillis, category);
            task.setId(id);
            taskViewModel.update(task);

            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Task not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        SubMenu categoryMenu = menu.findItem(R.id.filter_category).getSubMenu();
        categoryMenu.clear();
        for (int i = 0; i < categoriesList.size(); i++) {
            categoryMenu.add(0, i, Menu.NONE, categoriesList.get(i));
        }
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int n = categoriesList.size();
        //item.getSubMenu().size()
        for(int i = 0; i < n; i++){
            if(item.getItemId() == i){
                String s = categoriesList.get(i);
                taskViewModel.getAllTasksByCategory(s).observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        // Update RecyclerView
                        adapter.submitList(tasks);
                    }
                });
            }
        }
        switch (item.getItemId()) {
            case R.id.delete_all_tasks:
                taskViewModel.deleteAllTasks();
                Toast.makeText(this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public long parseDate(String date, String time) {
        SimpleDateFormat sdfYear = new SimpleDateFormat("yy");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
        String[] split = time.split(":");

        year = sdfYear.format(Date.parse(date));
        month = sdfMonth.format(Date.parse(date));
        day = sdfDay.format(Date.parse(date));
        hour = Integer.valueOf(split[0]);
        minute = Integer.valueOf(split[1]);

        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(System.currentTimeMillis());
//        cal.clear();
        cal.set(Calendar.YEAR, 2000 + Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        cal.set(Calendar.DATE, Integer.parseInt(day));
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    public void startAlarm(long id, String title, String description, String priority, long timeMillis,
                           String status, String category){
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        alertIntent.putExtra(EXTRA_ALERTID, id);
        alertIntent.putExtra(EXTRA_ALERTTITLE, title);
        alertIntent.putExtra(EXTRA_ALERTDESCRIPTION, description);
        alertIntent.putExtra(EXTRA_ALERTPRIORITY, priority);
        alertIntent.putExtra(EXTRA_ALERTMILLI, timeMillis);
        alertIntent.putExtra(EXTRA_ALERTSTATUS, status);
        alertIntent.putExtra(EXTRA_ALERTCATEGORY, category);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1,
                alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
    }

    /*
    @Override
    public void onTimeSet(TimePicker view, int hour, int minute){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month));
        cal.set(Calendar.DATE, Integer.parseInt(day));
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
    */
}
