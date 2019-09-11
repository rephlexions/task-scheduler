package com.rephlexions.taskscheduler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import com.rephlexions.taskscheduler.db.TaskDao;
import com.rephlexions.taskscheduler.db.TaskDatabase;
import com.rephlexions.taskscheduler.db.TaskRepository;
import com.rephlexions.taskscheduler.utils.CategoryListAdapter;
import com.rephlexions.taskscheduler.utils.StatusListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MPAndroidChartActivity extends AppCompatActivity {
    private static final String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpandroid_chart);
        Intent intent = getIntent();
        int pendingTasks = intent.getIntExtra("PendingTasks", 0);
        int completedTasks = intent.getIntExtra("CompletedTasks", 0);
        int ongoingTasks = intent.getIntExtra("OngoingTasks", 0);
        Log.d(TAG, "onCreate: " + pendingTasks + completedTasks + ongoingTasks);

        PieChart pieChart = findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(pendingTasks, "Pending"));
        entries.add(new PieEntry(completedTasks, "Completed"));
        entries.add(new PieEntry(ongoingTasks, "Ongoing"));
        PieDataSet set = new PieDataSet(entries, "Type of Task");
        set.setColors(ColorTemplate.PASTEL_COLORS);
        set.setValueTextSize(15f);
        set.setValueTextColor(Color.rgb(255,255,255));
        pieChart.setNoDataText("No data available right now");
        pieChart.setHoleRadius(50);
        Description description = pieChart.getDescription();
        description.setText("Number of tasks by type");
        PieData data = new PieData(set);
        pieChart.setData(data);

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
                            case 1:
                                intent = new Intent(MPAndroidChartActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                Log.d("Test", "Default");
                                break;
                        }
                        return true;
                    }
                })
                .build();
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("v1.0"));
    }
}