package com.rephlexions.taskscheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.rephlexions.taskscheduler.db.Category;
import com.rephlexions.taskscheduler.db.Task;
import com.rephlexions.taskscheduler.db.TaskRepository;
import com.rephlexions.taskscheduler.reminders.AlertReceiver;
import com.rephlexions.taskscheduler.utils.CategoryListAdapter;
import com.rephlexions.taskscheduler.utils.StatusListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private String date, time;
    private String year, month, day;
    private int hour, minute;
    private ArrayList<String> categoriesList = new ArrayList<>();
    private Menu menu;
    private ArrayList<String> pendingTasksList = new ArrayList<>();
    private ArrayList<String> completedTasksList = new ArrayList<>();
    private ArrayList<String> ongoingTasksList = new ArrayList<>();
    private int pendingTasks;
    private int completedTasks;
    private int ongoingTasks;

    BroadcastReceiver broadcastReceiverOngoing;
    BroadcastReceiver broadcastReceiverDelay;
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
                categoriesList.add("All tasks");
                for (int i = 0; i < categoryAdapter.getItemCount(); i++) {
                    categoriesList.add(String.valueOf(category.get(i).getName()));
                }
            }
        });

        registerReceiver(broadcastReceiverOngoing, new IntentFilter("ChangeTaskStatus"));
        registerReceiver(broadcastReceiverDelay, new IntentFilter("PostPoneTask"));
        broadcastReceiverOngoing = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("ChangeTaskStatus")) {
                    long id = intent.getLongExtra(EXTRA_ID, -1);
                    String title = intent.getStringExtra(EXTRA_TITLE);
                    String description = intent.getStringExtra(EXTRA_DESCRIPTION);
                    String priority = intent.getStringExtra(EXTRA_PRIORITY);
                    String status = intent.getStringExtra(EXTRA_STATUS);
                    long dateTimeLong = intent.getLongExtra(EXTRA_MILLI, 1);
                    String category = intent.getStringExtra(EXTRA_CATEGORY);
                    Task task = new Task(title, description, priority, status, dateTimeLong, category);
                    task.setId(id);
                    taskViewModel.update(task);
                }
            }
        };
        broadcastReceiverDelay = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("PostPoneTask")) {
                    long id = intent.getLongExtra(EXTRA_ID, -1);
                    String title = intent.getStringExtra(EXTRA_TITLE);
                    String description = intent.getStringExtra(EXTRA_DESCRIPTION);
                    String priority = intent.getStringExtra(EXTRA_PRIORITY);
                    String status = intent.getStringExtra(EXTRA_STATUS);
                    long dateTimeLong = intent.getLongExtra(EXTRA_MILLI, 1);
                    String category = intent.getStringExtra(EXTRA_CATEGORY);
                    Task task = new Task(title, description, priority, status, dateTimeLong, category);
                    task.setId(id);
                    taskViewModel.update(task);
                    startAlarm(id, title, description, priority, dateTimeLong, status, category);
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

        pendingTasks = countTasksByStatus("pending");
        completedTasks = countTasksByStatus("completed");
        ongoingTasks = countTasksByStatus("ongoing");
        // Create toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.unibo)
                .build();
        //Create Drawer Menu
        new DrawerBuilder().withActivity(this).build();
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("My Tasks");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Statistics");
        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intent = null;
                        switch ((int) drawerItem.getIdentifier()) {
                            case 2:
                                intent = new Intent(MainActivity.this, MPAndroidChartActivity.class);
                                intent.putExtra("PendingTasks", pendingTasks);
                                intent.putExtra("CompletedTasks", completedTasks);
                                intent.putExtra("OngoingTasks", ongoingTasks);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                })
                .build();
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("v1.0"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        registerReceiver(broadcastReceiverOngoing, new IntentFilter("ChangeTaskStatus"));
        broadcastReceiverOngoing = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("ChangeTaskStatus")) {
                    long id = intent.getLongExtra(EXTRA_ID, -1);
                    String title = intent.getStringExtra(EXTRA_TITLE);
                    String description = intent.getStringExtra(EXTRA_DESCRIPTION);
                    String priority = intent.getStringExtra(EXTRA_PRIORITY);
                    String status = intent.getStringExtra(EXTRA_STATUS);
                    long dateTimeLong = intent.getLongExtra(EXTRA_MILLI, 1);
                    String category = intent.getStringExtra(EXTRA_CATEGORY);
                    Task task = new Task(title, description, priority, status, dateTimeLong, category);
                    task.setId(id);
                    taskViewModel.update(task);
                }
            }
        };
        registerReceiver(broadcastReceiverDelay, new IntentFilter("PostPoneTask"));
        broadcastReceiverDelay = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("PostPoneTask")) {
                    long id = intent.getLongExtra(EXTRA_ID, -1);
                    String title = intent.getStringExtra(EXTRA_TITLE);
                    String description = intent.getStringExtra(EXTRA_DESCRIPTION);
                    String priority = intent.getStringExtra(EXTRA_PRIORITY);
                    String status = intent.getStringExtra(EXTRA_STATUS);
                    long dateTimeLong = intent.getLongExtra(EXTRA_MILLI, 0L);
                    String category = intent.getStringExtra(EXTRA_CATEGORY);
                    Task task = new Task(title, description, priority, status, dateTimeLong, category);
                    task.setId(id);
                    taskViewModel.update(task);
                    startAlarm(id, title, description, priority, dateTimeLong, status, category);
                }
            }
        };
        pendingTasks = countTasksByStatus("pending");
        completedTasks = countTasksByStatus("completed");
        ongoingTasks = countTasksByStatus("ongoing");

    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiverDelay = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("ChangeTaskStatus")) {
                    long id = intent.getLongExtra(EXTRA_ID, -1);
                    String title = intent.getStringExtra(EXTRA_TITLE);
                    String description = intent.getStringExtra(EXTRA_DESCRIPTION);
                    String priority = intent.getStringExtra(EXTRA_PRIORITY);
                    String status = intent.getStringExtra(EXTRA_STATUS);
                    long dateTimeLong = intent.getLongExtra(EXTRA_MILLI, 0L);
                    String category = intent.getStringExtra(EXTRA_CATEGORY);
                    Task task = new Task(title, description, priority, status, dateTimeLong, category);
                    task.setId(id);
                    taskViewModel.update(task);
                }
            }
        };
        registerReceiver(broadcastReceiverDelay, new IntentFilter("PostPoneTask"));
        broadcastReceiverDelay = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("PostPoneTask")) {
                    long id = intent.getLongExtra(EXTRA_ID, -1);
                    String title = intent.getStringExtra(EXTRA_TITLE);
                    String description = intent.getStringExtra(EXTRA_DESCRIPTION);
                    String priority = intent.getStringExtra(EXTRA_PRIORITY);
                    String status = intent.getStringExtra(EXTRA_STATUS);
                    long dateTimeLong = intent.getLongExtra(EXTRA_MILLI, 0L);
                    String category = intent.getStringExtra(EXTRA_CATEGORY);
                    Task task = new Task(title, description, priority, status, dateTimeLong, category);
                    task.setId(id);
                    taskViewModel.update(task);
                    startAlarm(id, title, description, priority, dateTimeLong, status, category);
                }
            }
        };

        pendingTasks = countTasksByStatus("pending");
        completedTasks = countTasksByStatus("completed");
        ongoingTasks = countTasksByStatus("ongoing");
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
                //categoriesList.add("All tasks");
                for (int i = 0; i < categoryAdapter.getItemCount(); i++) {
                    categoriesList.add(String.valueOf(category.get(i).getName()));
                }
            }
        });
        SubMenu categoryMenu = menu.findItem(R.id.filter_category).getSubMenu();
        categoryMenu.clear();
        categoryMenu.add(0, 0, Menu.NONE, "All tasks");
        for (int i = 0; i < categoriesList.size(); i++) {
            categoryMenu.add(0, i + 1, Menu.NONE, categoriesList.get(i));
        }

        pendingTasks = countTasksByStatus("pending");
        completedTasks = countTasksByStatus("completed");
        ongoingTasks = countTasksByStatus("ongoing");

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
            if (!((date.equals("No date")) && (time.equals("No time")))) {
                final long timeMillis = parseDate(date, time);
                //Create and insert task with a deadline into the database
                TaskRepository repository = new TaskRepository(getApplication());
                Task task = new Task(title, description, priority, status, timeMillis, category);
                repository.insert(task, new TaskRepository.InsertTaskAsyncTask.InsertResult() {
                    @Override
                    public void onResult(long result) {
                        newTaskID = result;
                        startAlarm(result, title, description, priority, timeMillis, status, category);
                    }
                });

                Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
            } else {
                //Create and insert a task without a deadline into the database
                TaskRepository repository = new TaskRepository(getApplication());
                Task task = new Task(title, description, priority, status, 0L, category);
                repository.insert(task, new TaskRepository.InsertTaskAsyncTask.InsertResult() {
                    @Override
                    public void onResult(long result) {
                    }
                });
                Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == EDIT_TASK_REQUEST && resultCode == RESULT_OK) {
            long id = data.getLongExtra(EXTRA_ID, -1);
            //Don't update if ID is not valid
            if (id == -1) {
                Toast.makeText(this, "Task can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            final String title = data.getStringExtra(EXTRA_TITLE);
            final String description = data.getStringExtra(EXTRA_DESCRIPTION);
            final String priority = data.getStringExtra(EXTRA_PRIORITY);
            final String status = data.getStringExtra(EXTRA_STATUS);
            final String category = data.getStringExtra(EXTRA_CATEGORY);

            date = data.getStringExtra(AddEditTaskActivity.EXTRA_DATE);
            time = data.getStringExtra(AddEditTaskActivity.EXTRA_TIME);

            if (!((date.equals("No date")) && (time.equals("No time")))) {
                final long timeMillis = parseDate(date, time);
                //Create and update task with a deadline into the database
                Task task = new Task(title, description, priority, status, timeMillis, category);
                task.setId(id);
                taskViewModel.update(task);
                startAlarm(id, title, description, priority, timeMillis, status, category);
                Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
            } else {
                //Create and update  a task without a deadline into the database
                Task task = new Task(title, description, priority, status, 0L, category);
                task.setId(id);
                taskViewModel.update(task);
                Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
            }
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
        categoryMenu.setGroupCheckable(0, true, true);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int n = categoriesList.size();
        Log.d(TAG, "onOptionsItemSelected: " + categoriesList);
        for (int i = 1; i < n; i++) {
            if (item.getItemId() == i) {
                item.setChecked(true);
                String s = categoriesList.get(i);
                taskViewModel.getAllTasksByCategory(s).observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        // Update RecyclerView
                        adapter.submitList(tasks);
                    }
                });
            }
            if (item.getItemId() == 0) {
                taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        // Update RecyclerView
                        adapter.submitList(tasks);
                    }
                });
            }
        }
        switch (item.getItemId()) {
            case R.id.filter_date_created:
                item.setChecked(true);
                taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        adapter.submitList(tasks);
                    }
                });
                return true;
            case R.id.filter_date_ascending:
                item.setChecked(true);
                taskViewModel.getAllTasksByDateASC().observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        adapter.submitList(tasks);
                    }
                });
                return true;
            case R.id.filter_date_descending:
                item.setChecked(true);
                taskViewModel.getAllTasksByDateDESC().observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        adapter.submitList(tasks);
                    }
                });
                return true;

            case R.id.delete_all_tasks:
                taskViewModel.deleteAllTasks();
                Toast.makeText(this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.filter_all_priority:
                item.setChecked(true);
                taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        // Update RecyclerView
                        adapter.submitList(tasks);
                    }
                });
                return true;
            case R.id.filter_none_priority:
                item.setChecked(true);
                taskViewModel.getAllTasksByPriority("None").observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        adapter.submitList(tasks);
                    }
                });
                return true;
            case R.id.filter_low_priority:
                item.setChecked(true);
                taskViewModel.getAllTasksByPriority("Low").observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        adapter.submitList(tasks);
                    }
                });
                return true;
            case R.id.filter_medium_priority:
                item.setChecked(true);
                taskViewModel.getAllTasksByPriority("Medium").observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        adapter.submitList(tasks);
                    }
                });
                return true;
            case R.id.filter_high_priority:
                item.setChecked(true);
                taskViewModel.getAllTasksByPriority("High").observe(this, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        adapter.submitList(tasks);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public long parseDate(String date, String time) {
        if (date == null && time == null) {
            return 0L;
        }
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

    public void startAlarm(long id, String title, String description, String priority, long timeMillis, String status, String category) {
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

    public int countTasksByStatus(String status) {
        final StatusListAdapter statusListAdapter = new StatusListAdapter();
        if (status.equals("pending")) {
            taskViewModel.getAllTasksByStatus("pending").observe(this, new Observer<List<Task>>() {
                @Override
                public void onChanged(@Nullable final List<Task> task) {
                    pendingTasksList.clear();
                    statusListAdapter.setTask(task);
                    for (int i = 0; i < statusListAdapter.getItemCount(); i++) {
                        pendingTasksList.add(task.get(i).getStatus());
                    }
                }
            });
            return pendingTasksList.size();
        } else if (status.equals("completed")) {
            taskViewModel.getAllTasksByStatus("completed").observe(this, new Observer<List<Task>>() {
                @Override
                public void onChanged(@Nullable final List<Task> task) {
                    completedTasksList.clear();
                    statusListAdapter.setTask(task);
                    for (int i = 0; i < statusListAdapter.getItemCount(); i++) {
                        completedTasksList.add(task.get(i).getStatus());
                    }
                }
            });
            return completedTasksList.size();
        } else {
            taskViewModel.getAllTasksByStatus("ongoing").observe(this, new Observer<List<Task>>() {
                @Override
                public void onChanged(@Nullable final List<Task> task) {
                    ongoingTasksList.clear();
                    statusListAdapter.setTask(task);
                    for (int i = 0; i < statusListAdapter.getItemCount(); i++) {
                        ongoingTasksList.add(task.get(i).getStatus());
                    }
                }
            });
            return ongoingTasksList.size();
        }
    }
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
