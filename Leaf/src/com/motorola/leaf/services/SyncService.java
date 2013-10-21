package com.motorola.leaf.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.motorola.leaf.background.SyncTasks;

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
        String leafId = intent.getStringExtra("leaf_id");
        SyncTasks.startSyncTasksThread(getApplicationContext(), leafId);
        return START_STICKY;
    }

    @Override
    public  void onDestroy()
    {
        SyncTasks.stopSyncTasksThread();
    }
}
