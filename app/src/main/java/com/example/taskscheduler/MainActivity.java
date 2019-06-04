package com.example.taskscheduler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddTaskDialogFragment.TaskDialogListener {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<String> mNames = new ArrayList<>();
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started.");

        // Initiate RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(this, mNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });
    }

    public void showAddDialog() {
        DialogFragment dialog = new AddTaskDialogFragment();
        dialog.show(getSupportFragmentManager(), "task_dialog");
    }

    @Override
    public void applyTexts(String taskTitle, String taskDetail) {
        mNames.add(taskTitle);
        adapter.notifyDataSetChanged();
    }
}
