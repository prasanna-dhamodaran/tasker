package com.motorola.root.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.motorola.root.R;
import com.motorola.root.background.AddTask;

public class ComposeTask extends Activity
{
    private String leafId;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_leaf_add_task_layout);

        Intent intent = getIntent();
        leafId = intent.getStringExtra("leaf_id");

        final TextView addTaskStatusTextView = (TextView) findViewById(R.id.add_task_status_textview);
        addTaskStatusTextView.setVisibility(View.INVISIBLE);

        final EditText taskDescEditText = (EditText) findViewById(R.id.task_description_edittext);
        taskDescEditText.setText("");

        final NumberPicker taskPointsNumberPicker = (NumberPicker) findViewById(R.id.task_points_numberpicker);
        taskPointsNumberPicker.setMaxValue(10);
        taskPointsNumberPicker.setMinValue(1);
        taskPointsNumberPicker.setValue(1);

        final Context context = this;

        final Button addTaskButton = (Button) findViewById(R.id.add_task_button);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskDesc = taskDescEditText.getText().toString();
                addTask(context, leafId, taskDesc, addTaskButton, addTaskStatusTextView, taskDescEditText, taskPointsNumberPicker);
            }
        });
    }

    private void addTask(Context context, String leafId, String taskDesc, Button addTaskButton,
                         TextView statusDisplayTextView, EditText taskDescEditText,
                         NumberPicker taskPointsNumberPicker)
    {
        if(taskDesc == null || taskDesc.isEmpty())
        {
            Toast.makeText(context, "Please provide a task description", Toast.LENGTH_SHORT).show();
            return;
        }

        AddTask addTask = new AddTask(context, leafId, statusDisplayTextView, addTaskButton, taskDescEditText, taskPointsNumberPicker);
        addTask.execute();
    }
}