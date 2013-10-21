package com.motorola.leaf.background;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.motorola.leaf.model.Task;
import com.motorola.leaf.providers.TasksProvider;

import java.util.ArrayList;

public class UpdateTaskStatus extends AsyncTask<Void, Void, Boolean>
{
    private Context context;
    private String taskId;
    private boolean taskStatus;

    public UpdateTaskStatus(Context context, String taskId, boolean taskStatus)
    {
        this.context = context;
        this.taskId = taskId;
        this.taskStatus = taskStatus;
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        ContentValues values = new ContentValues();
        values.put(TasksProvider.COLUMN_TASK_STATUS, (taskStatus ? 1 : 0));
        String selection = TasksProvider.COLUMN_TASK_ID + " = ?";
        String [] selectionArgs = {taskId};

        long numRows = context.getContentResolver().update(TasksProvider.CONTENT_URI, values, selection, selectionArgs);

        if(numRows == 0)
        {
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        Toast.makeText(context, "Tasks status " + (result ? "" : "not ") + "updated", Toast.LENGTH_SHORT).show();
    }
}
