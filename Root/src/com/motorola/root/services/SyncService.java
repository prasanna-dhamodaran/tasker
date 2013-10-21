package com.motorola.root.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.motorola.root.background.SyncTasks;

public class SyncService extends Service
{
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String rootId = intent.getStringExtra("root_id");
        SyncTasks.startSyncTasksThread(getApplicationContext(), rootId);
        return START_STICKY;
    }

    @Override
    public  void onDestroy()
    {
        SyncTasks.stopSyncTasksThread();
    }
}
