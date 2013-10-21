package com.motorola.leaf.background;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.motorola.leaf.adapters.TasksListAdapter;
import com.motorola.leaf.constants.Constants;
import com.motorola.leaf.providers.TasksProvider;
import com.motorola.leaf.rest.RestFulGet;
import com.motorola.leaf.rest.RestFulPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SyncTasks implements Runnable
{
    private static ThreadPoolExecutor tasksStatusSyncThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    public static void startSyncTasksThread(Context context, String leafId)
    {
        if(tasksStatusSyncThreadPoolExecutor.getActiveCount() == 0)
        {
            tasksStatusSyncThreadPoolExecutor.execute(new SyncTasks(context, leafId));
        }
    }

    private final Context context;
    private final String leafId;

    private SyncTasks(Context context, String leafId)
    {
        this.context = context;
        this.leafId = leafId;
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                Log.i("SyncTasks", "Interrupted while waiting to sync tasks. Conitnuing...");
            }

            Log.i("SyncTasks", "Leaf SyncTasks running");
            if(SyncTasksFromToCloud())
            {
                Intent intent = new Intent("com.motorola.leaf.tasks.updated");
                context.sendOrderedBroadcast(intent, null);
            }
        }
    }

    public static void stopSyncTasksThread()
    {
        tasksStatusSyncThreadPoolExecutor.shutdownNow();
    }

    public boolean SyncTasksFromToCloud()
    {
        // TODO: implement code to compare data from google app engine and local provider and sync both and notify

        boolean statusChanged = false;
        JSONObject jsonData = null;
        JSONArray tasks = null;
        try
        {
            String response = RestFulGet.get(Constants.getChildTasksUrl + leafId);
            if(response != null && !response.isEmpty())
            {
                jsonData = new JSONObject(response);
            }
            if(jsonData != null)
            {
                try
                {
                    tasks = jsonData.getJSONArray("tasks");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }

                if(tasks == null)
                {
                    return false;
                }

                for(int i = 0; i < tasks.length(); ++i)
                {
                    JSONObject task = (JSONObject) ((JSONObject) tasks.get(i)).get("taskId");
                    Boolean newTaskStatus = ((JSONObject) tasks.get(i)).getBoolean("status");
                    String newTaskDesc = ((JSONObject) tasks.get(i)).getString("taskDescription");
                    Integer newTaskPoints = ((JSONObject) tasks.get(i)).getInt("points");
                    String taskId = task.getString("id");

                    String sel = TasksProvider.COLUMN_TASK_ID + " = ?";
                    String [] selArgs = {taskId.toString()};
                    String [] tasksProjection = {TasksProvider.COLUMN_TASK_STATUS};

                    Cursor tasksCursor = context.getContentResolver().query(TasksProvider.CONTENT_URI, tasksProjection, sel, selArgs, null);
                    if(tasksCursor.getCount() == 0)
                    {
                        ContentValues values = new ContentValues();
                        values.put(TasksProvider.COLUMN_TASK_DESC, newTaskDesc);
                        values.put(TasksProvider.COLUMN_TASK_POINTS, newTaskPoints);
                        values.put(TasksProvider.COLUMN_TASK_ID, taskId);
                        values.put(TasksProvider.COLUMN_TASK_STATUS, (newTaskStatus? 1 : 0));
                        values.put(TasksProvider.COLUMN_CHILD_ID, leafId);
                        values.put(TasksProvider.COLUMN_TASK_CHANGED, 1);

                        Uri uri = context.getContentResolver().insert(TasksProvider.CONTENT_URI, values);

                        if(uri != null)
                        {
                            statusChanged = true;
                        }
                    }
                    else
                    {
                        if(tasksCursor.moveToFirst())
                        {
                            do
                            {
                                Integer status = tasksCursor.getInt(tasksCursor.getColumnIndex(TasksProvider.COLUMN_TASK_STATUS));
                                Integer serverTaskStatus = (newTaskStatus) ? 1 : 0;
                                if(status != serverTaskStatus)
                                {
                                    Map map = new HashMap<String, String>();
                                    map.put("status", (status == 1) ? true : false);
                                    String requestStatus =  RestFulPost.post(Constants.postUpdateTask + leafId + "/" + taskId, map);
                                }
                            }
                            while(tasksCursor.moveToNext());
                        }
                    }
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return statusChanged;
    }

    private void sendBroadCastToRefreshUI()
    {
        context.sendBroadcast(new Intent("com.motorola.leaf.refresh.tasks.ui"));
    }

    private void sendBoradcastToNotify()
    {
        context.sendBroadcast(new Intent("com.motorola.leaf.refresh.tasks.notification").putExtra("leaf_id", leafId));
    }
}