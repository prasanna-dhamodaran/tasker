package com.motorola.root.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.motorola.root.R;
import com.motorola.root.model.Task;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskView = inflater.inflate(R.layout.tasks_list_item_layout, parent, false);
        TextView taskDescTextView = (TextView) taskView.findViewById(R.id.task_desc_textview);
        TextView taskPointsTextView = (TextView) taskView.findViewById(R.id.task_points_textview);

        taskDescTextView.setText(tasks.get(position).getTaskDesc());
        taskPointsTextView.setText(tasks.get(position).getTaskPoints().toString());
        if(tasks.get(position).getTaskStatus() == true)
        {
            taskDescTextView.setPaintFlags(taskDescTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if(tasks.get(position).isTaskChanged())
        {
            taskDescTextView.setBackgroundColor(Color.CYAN);
            taskPointsTextView.setBackgroundColor(Color.CYAN);
        }

        return taskView;
    }
}
