package com.motorola.leaf.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.motorola.leaf.activities.Tasks;

public class TasksUIUpdateReceiver extends BroadcastReceiver
{
    Tasks tasksActivity;

    public void setTasksActivity(Tasks tasksActivity)
    {
        this.tasksActivity = tasksActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("TasksUIUpdateReceiver", "Leaf: Tasks activity active. Updating tasks UI...");
        tasksActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tasksActivity.loadLeafTasks();
                Toast.makeText(tasksActivity, "Tasks synced", Toast.LENGTH_SHORT).show();
            }
        });

        abortBroadcast();
    }
}
