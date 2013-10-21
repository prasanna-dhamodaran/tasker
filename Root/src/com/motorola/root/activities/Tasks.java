package com.motorola.root.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.motorola.root.R;
import com.motorola.root.adapters.TasksListAdapter;
import com.motorola.root.model.Task;
import com.motorola.root.providers.TasksProvider;
import com.motorola.root.receivers.LeavesUIUpdateReceiver;
import com.motorola.root.receivers.TasksUIUpdateReceiver;

import java.util.ArrayList;

public class Tasks extends Activity
{
    private String leafId;
    private TasksUIUpdateReceiver tasksUIUpdateReceiver = null;
    private TasksListAdapter tasksListAdapter;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_leaf_tasks_layout);
        Intent intent = getIntent();
        leafId = intent.getStringExtra("leaf_id");

        final Context context = this;

        ListView tasksListView = (ListView) findViewById(R.id.tasks_list_view);
        tasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                TextView taskDescTextView = (TextView) view.findViewById(R.id.task_desc_textview);
                TextView taskPointsTextView = (TextView) view.findViewById(R.id.task_points_textview);

                taskDescTextView.setBackgroundColor(Color.TRANSPARENT);
                taskPointsTextView.setBackgroundColor(Color.TRANSPARENT);

                setTaskAsViewed(tasksListAdapter.getItem(i).getTaskId());
            }

            private void setTaskAsViewed(String taskId)
            {
                String sel = TasksProvider.COLUMN_TASK_ID + " = ?";
                String [] selArgs = {taskId};
                ContentValues values = new ContentValues();
                values.put(TasksProvider.COLUMN_TASK_CHANGED, 0);
                long numRows = context.getContentResolver().update(TasksProvider.CONTENT_URI, values, sel, selArgs);
            }
        });
    }

    public String getLeafId()
    {
        return this.leafId;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final Context context = this;

        TextView leafIdTextView = (TextView) findViewById(R.id.leaf_id_textview);
        leafIdTextView.setText(leafId);

        // TODO: Write code here to get the tasks for this leaf from TasksProvider
        loadLeafTasks();

        Button addTaskButton = (Button) findViewById(R.id.leaf_add_task_button);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ComposeTask.class);
                intent.putExtra("leaf_id", leafId);
                startActivity(intent);
            }
        });

        if(tasksUIUpdateReceiver == null)
        {
            tasksUIUpdateReceiver = new TasksUIUpdateReceiver();
            tasksUIUpdateReceiver.setTasksActivity(this);
            IntentFilter intentFilter = new IntentFilter("com.motorola.root.tasks.updated");
            intentFilter.setPriority(2);
            registerReceiver(tasksUIUpdateReceiver, intentFilter);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if(tasksUIUpdateReceiver != null)
        {
            unregisterReceiver(tasksUIUpdateReceiver);
            tasksUIUpdateReceiver = null;
        }
    }

    public void loadLeafTasks()
    {
        String [] projection = {TasksProvider.COLUMN_TASK_DESC, TasksProvider.COLUMN_TASK_POINTS, TasksProvider.COLUMN_TASK_STATUS, TasksProvider.COLUMN_TASK_ID, TasksProvider.COLUMN_TASK_CHANGED};
        String selection = TasksProvider.COLUMN_CHILD_ID + " = ?";
        String [] selectionArgs = {leafId};

        Cursor cursor = getContentResolver().query(TasksProvider.CONTENT_URI, projection, selection, selectionArgs, null);
        Integer totalPoints = 0;
        ArrayList<Task> tasks = new ArrayList<Task>();

        if(cursor.moveToFirst())
        {
            do
            {
                String taskDesc = cursor.getString(cursor.getColumnIndex(TasksProvider.COLUMN_TASK_DESC));
                boolean taskStatus = (cursor.getInt(cursor.getColumnIndex(TasksProvider.COLUMN_TASK_STATUS)) != 0);
                Integer taskPoints = cursor.getInt(cursor.getColumnIndex(TasksProvider.COLUMN_TASK_POINTS));
                String taskId = cursor.getString(cursor.getColumnIndex(TasksProvider.COLUMN_TASK_ID));
                boolean taskChanged = (cursor.getInt(cursor.getColumnIndex(TasksProvider.COLUMN_TASK_CHANGED)) != 0);

                if(taskStatus == true)
                {
                    totalPoints += taskPoints;
                }

                tasks.add(new Task(taskDesc, taskPoints, taskStatus, taskId, taskChanged));
            }while(cursor.moveToNext());
        }

        tasksListAdapter = new TasksListAdapter(this, tasks);
        ListView tasksListView = (ListView) findViewById(R.id.tasks_list_view);
        tasksListView.setAdapter(tasksListAdapter);
        tasksListAdapter.notifyDataSetChanged();

        TextView leafPointsTextView = (TextView) findViewById(R.id.leaf_points_textview);
        leafPointsTextView.setText(totalPoints.toString());
    }
}