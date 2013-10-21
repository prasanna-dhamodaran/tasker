package com.motorola.leaf.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.motorola.leaf.R;
import com.motorola.leaf.adapters.TasksListAdapter;
import com.motorola.leaf.background.UpdateTaskStatus;
import com.motorola.leaf.model.Task;
import com.motorola.leaf.providers.TasksProvider;
import com.motorola.leaf.receivers.TasksUIUpdateReceiver;
import com.motorola.leaf.services.SyncService;

import java.util.ArrayList;

public class Tasks extends Activity
{
    private String leafId;
    private TasksListAdapter tasksListAdapter;
    private TasksUIUpdateReceiver tasksUIUpdateReceiver;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaf_tasks_layout);

        leafId = getSharedPreferences("login.data", Context.MODE_PRIVATE).getString("google.account", null);
        if(leafId == null || leafId.isEmpty())
        {
            leafId = getIntent().getStringExtra("google.account");
            if(leafId == null || leafId.isEmpty())
            {
                Intent loginIntent = new Intent(this, LeafLogin.class);
                startActivity(loginIntent);
            }
            else
            {
                Intent authenticationIntent = new Intent(this, GoogleAppEngineAuthentication.class);
                authenticationIntent.putExtra("google.account", leafId);
                startActivity(authenticationIntent);
            }
            this.finish();
        }
        else
        {
            final Context context = this;

            final Intent syncServiceIntent = new Intent(this, SyncService.class);
            syncServiceIntent.putExtra("leaf_id", leafId);
            startService(syncServiceIntent);

            final Tasks tasks = this;

            Button logoutButton = (Button) findViewById(R.id.leaf_logout_button);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.stopService(new Intent(context, SyncService.class));
                    SharedPreferences loginData = getSharedPreferences("login.data", Context.MODE_PRIVATE);
                    loginData.edit().putString("google.account", "").commit();
                    Intent intent = new Intent(context, LeafLogin.class);
                    startActivity(intent);
                    tasks.finish();
                }
            });

            TextView leafIdTextView = (TextView) findViewById(R.id.leaf_id_textview);
            leafIdTextView.setText(leafId);

            ListView tasksListView = (ListView) findViewById(R.id.tasks_list_view);
            tasksListView.setItemsCanFocus(true);
            tasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView textView = (TextView) view.findViewById(R.id.task_desc_textview);
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.task_status_checkbox);
                    textView.setBackgroundColor(Color.TRANSPARENT);
                    checkBox.setBackgroundColor(Color.TRANSPARENT);

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
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final Context context = this;

        loadLeafTasks();

        if(tasksUIUpdateReceiver == null)
        {
            tasksUIUpdateReceiver = new TasksUIUpdateReceiver();
            tasksUIUpdateReceiver.setTasksActivity(this);
            IntentFilter intentFilter = new IntentFilter("com.motorola.leaf.tasks.updated");
            intentFilter.setPriority(1);
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
                boolean isTaskChanged = (cursor.getInt(cursor.getColumnIndex(TasksProvider.COLUMN_TASK_CHANGED)) != 0);

                if(taskStatus == true)
                {
                    totalPoints += taskPoints;
                }

                tasks.add(new Task(taskDesc, taskPoints, taskStatus, taskId, isTaskChanged));
            }while(cursor.moveToNext());
        }

        tasksListAdapter = new TasksListAdapter(this, tasks);
        ListView tasksListView = (ListView) findViewById(R.id.tasks_list_view);
        tasksListView.setAdapter(tasksListAdapter);
        tasksListAdapter.notifyDataSetChanged();
    }
}