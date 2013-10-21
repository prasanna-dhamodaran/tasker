package com.motorola.leaf.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.motorola.leaf.R;
import com.motorola.leaf.activities.Tasks;

public class TasksUpdateNotificationReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        Log.i("Leaf", "TasksUpdateNotificationReceiver: Received broadcast to send notification");

        Intent tasksIntent = new Intent(context, Tasks.class);
        String leafId = intent.getStringExtra("leaf_id");
        tasksIntent.putExtra("leaf_id", leafId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tasksIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(context).
                setContentTitle("Tasks Updated").
                setContentText("Click here to view the updated tasks").
                setSmallIcon(R.drawable.ic_launcher).
                setContentIntent(pendingIntent).getNotification();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
