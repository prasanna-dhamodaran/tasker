package com.motorola.root.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.motorola.root.R;
import com.motorola.root.activities.Leaves;

public class TasksUpdateNotificationReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        Log.i("Root", "TasksUpdateNotificationReceiver: Received broadcast to send notification");

        Intent leavesIntent = new Intent(context, Leaves.class);
        String rootId = intent.getStringExtra("root_id");
        leavesIntent.putExtra("root_id", rootId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, leavesIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(context).
                                        setContentTitle("Tasks Updated").
                                        setContentText("Click here to view the updated tasksActivity").
                                        setSmallIcon(R.drawable.icon).
                                        setContentIntent(pendingIntent).getNotification();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
