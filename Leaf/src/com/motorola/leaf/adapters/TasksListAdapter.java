package com.motorola.leaf.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.motorola.leaf.R;
import com.motorola.leaf.background.UpdateTaskStatus;
import com.motorola.leaf.model.Task;

import java.util.ArrayList;

public class TasksListAdapter extends ArrayAdapter<Task>
{
    private final Context context;
    private ArrayList<Task> tasks;

    public TasksListAdapter(Context context, ArrayList<Task> tasks)
    {
        super(context, R.layout.tasks_list_item_layout, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    public ArrayList<Task> getTasks()
    {
        return tasks;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskView = inflater.inflate(R.layout.tasks_list_item_layout, parent, false);
        final TextView taskDescTextView = (TextView) taskView.findViewById(R.id.task_desc_textview);
        CheckBox taskStatusCheckBox = (CheckBox) taskView.findViewById(R.id.task_status_checkbox);

        taskDescTextView.setText(tasks.get(position).getTaskDesc());
        taskStatusCheckBox.setChecked(tasks.get(position).getTaskStatus());

        setTaskDescriptionStrikeThrough(taskDescTextView, tasks.get(position).getTaskStatus());

        taskStatusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Task task = tasks.get(position);
                task.setTaskStatus(isChecked);
                tasks.set(position, task);
                setTaskDescriptionStrikeThrough(taskDescTextView, isChecked);

                UpdateTaskStatus updateTaskStatus = new UpdateTaskStatus(context, task.getTaskId(), task.getTaskStatus());
                updateTaskStatus.execute();
            }
        });

        if(tasks.get(position).isTaskChanged())
        {
            taskDescTextView.setBackgroundColor(Color.CYAN);
            taskStatusCheckBox.setBackgroundColor(Color.CYAN);
        }

        return taskView;
    }

    private void setTaskDescriptionStrikeThrough(TextView taskDescTextView, boolean status)
    {
        if(status)
        {
            taskDescTextView.setPaintFlags(taskDescTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
        {
            taskDescTextView.setPaintFlags(taskDescTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
