package com.motorola.root.background;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.motorola.root.constants.Constants;
import com.motorola.root.model.Task;
import com.motorola.root.providers.ChildrenProvider;
import com.motorola.root.providers.TasksProvider;
import com.motorola.root.rest.RestFulGet;
import com.motorola.root.rest.RestFulPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SyncTasks implements Runnable
{
    private static ThreadPoolExecutor tasksStatusSyncThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    public static void startSyncTasksThread(Context context, String rootId)
    {
        if(tasksStatusSyncThreadPoolExecutor.getActiveCount() == 0)
        {
            tasksStatusSyncThreadPoolExecutor.execute(new SyncTasks(context, rootId));
        }
    }

    private final Context context;
    private final String rootId;

    private SyncTasks(Context context, String rootId)
    {
        this.context = context;
        this.rootId = rootId;
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                // Sync every x number of seconds
                // TODO: Add to constants
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                Log.i("SyncTasks", "Interrupted while waiting to sync tasks. Continuing...");
            }

            Log.i("SyncTasks", "SyncTasks running");
            if(SyncTasksFromToCloud())
            {
                Intent intent = new Intent("com.motorola.root.tasks.updated");
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
        String [] projection = {ChildrenProvider.COLUMN_CHILD_ID};
        String selection = ChildrenProvider.COLUMN_PARENT_ID + " = ?";
        String [] selectionArgs = {rootId};
        Cursor cursor = context.getContentResolver().query(ChildrenProvider.CONTENT_URI, projection, selection, selectionArgs, null);

        // Get all children for a given Parent
        if(cursor.moveToFirst())
        {
            do
            {
                String leafId = cursor.getString(cursor.getColumnIndex(ChildrenProvider.COLUMN_CHILD_ID));

                String [] tasksProjection = {TasksProvider.COLUMN_TASK_DESC, TasksProvider.COLUMN_TASK_POINTS, TasksProvider.COLUMN_TASK_STATUS, TasksProvider.COLUMN_TASK_ID, TasksProvider.COLUMN_ID};
                String tasksSelection = TasksProvider.COLUMN_CHILD_ID + " = ?";
                String [] tasksSelectionArgs = {leafId};
                Cursor tasksCursor = context.getContentResolver().query(TasksProvider.CONTENT_URI, tasksProjection, tasksSelection, tasksSelectionArgs, null);

                JSONObject jsonData = null;
                JSONArray tasks = null;

                try {
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
                        }

                        if(tasksCursor.moveToFirst())
                        {
                            do
                            {
                                String taskDesc = tasksCursor.getString(tasksCursor.getColumnIndex(TasksProvider.COLUMN_TASK_DESC));
                                boolean taskStatus = (tasksCursor.getInt(tasksCursor.getColumnIndex(TasksProvider.COLUMN_TASK_STATUS)) != 0);
                                Integer taskPoints = tasksCursor.getInt(tasksCursor.getColumnIndex(TasksProvider.COLUMN_TASK_POINTS));
                                String taskId = tasksCursor.getString(tasksCursor.getColumnIndex(TasksProvider.COLUMN_TASK_ID));
                                Integer id = tasksCursor.getInt(tasksCursor.getColumnIndex(TasksProvider.COLUMN_ID));

                                if(taskId.isEmpty())
                                {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("taskDescription", taskDesc);
                                    map.put("status", (taskStatus?"true":"false"));
                                    map.put("points", taskPoints.toString());
                                    String result = RestFulPost.post(Constants.postNewTask + leafId, map);
                                    String newTaskId = null;

                                    if(result != null)
                                    {
                                        newTaskId = new JSONObject(result).getString("taskId");
                                    }

                                    if(newTaskId != null && !newTaskId.isEmpty())
                                    {
                                        String sel = TasksProvider.COLUMN_ID + " = ?";
                                        String [] selArgs = {id.toString()};
                                        ContentValues values = new ContentValues();
                                        values.put(TasksProvider.COLUMN_TASK_ID, newTaskId);

                                        long numRows = context.getContentResolver().update(TasksProvider.CONTENT_URI, values, sel, selArgs);
                                    }
                                }
                                else
                                {
                                    if(tasks == null)
                                    {
                                        continue;
                                    }

                                    for(int i = 0; i < tasks.length(); ++i)
                                    {
                                        JSONObject task = (JSONObject) ((JSONObject) tasks.get(i)).get("taskId");
                                        if(taskId.equalsIgnoreCase(task.getString("id")))
                                        {
                                            Boolean newTaskStatus = ((JSONObject) tasks.get(i)).getBoolean("status");
                                            if(taskStatus != newTaskStatus)
                                            {
                                                String sel = TasksProvider.COLUMN_ID + " = ?";
                                                String [] selArgs = {id.toString()};
                                                ContentValues values = new ContentValues();
                                                values.put(TasksProvider.COLUMN_TASK_STATUS, newTaskStatus);
                                                long numRows = context.getContentResolver().update(TasksProvider.CONTENT_URI, values, sel, selArgs);

                                                statusChanged = true;
                                                setLeafChanged(leafId);
                                                setTaskChanged(id);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            while(tasksCursor.moveToNext());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }while(cursor.moveToNext());
        }

        return statusChanged;
    }

    private void setLeafChanged(String leafId)
    {
        String sel = ChildrenProvider.COLUMN_CHILD_ID + " = ?";
        String [] selArgs = {leafId};
        ContentValues values = new ContentValues();
        values.put(ChildrenProvider.COLUMN_CHILD_CHANGED, 1);
        long numRows = context.getContentResolver().update(ChildrenProvider.CONTENT_URI, values, sel, selArgs);
    }

    private void setTaskChanged(Integer id)
    {
        String sel = TasksProvider.COLUMN_ID + " = ?";
        String [] selArgs = {id.toString()};
        ContentValues values = new ContentValues();
        values.put(TasksProvider.COLUMN_TASK_CHANGED, 1);
        long numRows = context.getContentResolver().update(TasksProvider.CONTENT_URI, values, sel, selArgs);
    }
}
