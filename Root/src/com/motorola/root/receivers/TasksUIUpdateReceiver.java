package com.motorola.root.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.motorola.root.activities.Tasks;
import com.motorola.root.providers.ChildrenProvider;

import java.util.ArrayList;

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
        Log.i("LeavesUIUpdateReceiver", "Leaf: Tasks activity active. Updating tasksActivity UI...");

        tasksActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(tasksActivity, "Tasks status updated", Toast.LENGTH_SHORT).show();
                setCurrentLeafIdAsViewed(tasksActivity.getLeafId());
                tasksActivity.loadLeafTasks();
            }

            private void setCurrentLeafIdAsViewed(String currentLeafId)
            {
                String sel = ChildrenProvider.COLUMN_CHILD_ID + " = ?";
                String [] selArgs = {currentLeafId};
                ContentValues values = new ContentValues();
                values.put(ChildrenProvider.COLUMN_CHILD_CHANGED, 0);
                long numRows = tasksActivity.getContentResolver().update(ChildrenProvider.CONTENT_URI, values, sel, selArgs);
            }
        });

        abortBroadcast();
    }
}
