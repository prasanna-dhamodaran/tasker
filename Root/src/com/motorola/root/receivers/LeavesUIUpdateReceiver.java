package com.motorola.root.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.motorola.root.activities.Leaves;
import com.motorola.root.activities.Tasks;

import java.util.ArrayList;

public class LeavesUIUpdateReceiver extends BroadcastReceiver
{
    Leaves leavesActivity;

    public void setLeavesActivity(Leaves leavesActivity) {
        this.leavesActivity = leavesActivity;
    }

    public void onReceive(Context context, Intent intent)
    {
        Log.i("LeavesUIUpdateReceiver", "Leaf: Tasks activity active. Updating tasksActivity UI...");
        leavesActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(leavesActivity, "Tasks status updated", Toast.LENGTH_SHORT).show();
                leavesActivity.loadAllLeaves();
            }
        });

        abortBroadcast();
    }
}
