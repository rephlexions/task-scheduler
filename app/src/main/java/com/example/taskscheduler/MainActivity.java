package com.example.taskscheduler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";

    private ArrayList<String> mNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started.");

        // Initiate RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final EditText edit = (EditText) findViewById(R.id.editText);
        final Button button = (Button) findViewById(R.id.btnAddItem);
        // Register the onClick listener with the implementation above
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick: I've been clicked." + " " +edit.getText().toString());
                mNames.add(edit.getText().toString());
                adapter.notifyDataSetChanged();
                // Code here executes on main thread after user presses button
            }
        });

    }


    /*
    private void initListItems(){
        Log.d(TAG, "initListItems: preparing bitmaps.");
        mNames.add("Havasu Falls");
        mNames.add("Trondheim");
        mNames.add("Portugal");
        mNames.add("Rocky Mountain National Park");
        mNames.add("Mahahual");
        mNames.add("Frozen Lake");
        initRecyclerView();
    }
    */

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}
