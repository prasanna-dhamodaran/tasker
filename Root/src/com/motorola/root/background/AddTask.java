package com.motorola.root.background;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.motorola.root.providers.TasksProvider;

public class AddTask extends AsyncTask<Void, Void, Boolean>
{
    private Context context;
    private TextView statusDisplayTextView;
    private String leafId;
    private Button addTaskButton;
    private EditText taskDescEditText;
    private NumberPicker taskPointsNumberPicker;

    String taskDesc;
    Integer taskPoints;

    public AddTask(Context context, String leafId, TextView statusDisplayTextView,
                   Button addTaskButton, EditText taskDescEditText,
                   NumberPicker taskPointsNumberPicker)
    {
        this.context = context;
        this.statusDisplayTextView = statusDisplayTextView;
        this.leafId = leafId;
        this.addTaskButton = addTaskButton;
        this.taskDescEditText = taskDescEditText;
        this.taskPointsNumberPicker = taskPointsNumberPicker;
    }

    @Override
    protected void onPreExecute()
    {
        statusDisplayTextView.setVisibility(View.VISIBLE);
        addTaskButton.setEnabled(false);
        taskPoints = taskPointsNumberPicker.getValue();
        taskDesc = taskDescEditText.getText().toString();
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        Integer taskStatus = 0;

        // TODO: Write code to insert data into google app engine

        ContentValues values = new ContentValues();
        values.put(TasksProvider.COLUMN_CHILD_ID, leafId);
        values.put(TasksProvider.COLUMN_TASK_DESC, taskDesc);
        values.put(TasksProvider.COLUMN_TASK_STATUS, taskStatus);
        values.put(TasksProvider.COLUMN_TASK_POINTS, taskPoints);
        values.put(TasksProvider.COLUMN_TASK_ID, "");
        values.put(TasksProvider.COLUMN_TASK_CHANGED, 0);

        Uri uri = context.getContentResolver().insert(TasksProvider.CONTENT_URI, values);

        if(uri != null)
        {
            return true;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        statusDisplayTextView.setVisibility(View.INVISIBLE);
        addTaskButton.setEnabled(true);

        if(result == true)
        {
            taskDescEditText.setText("");
            taskPointsNumberPicker.setValue(1);
            Toast.makeText(context, "Task Added", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Task not added", Toast.LENGTH_SHORT).show();
        }
    }
}
