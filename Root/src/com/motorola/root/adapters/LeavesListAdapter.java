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
import com.motorola.root.model.Leaf;
import com.motorola.root.model.Task;

import java.util.ArrayList;

public class LeavesListAdapter extends ArrayAdapter<Leaf>
{
    private final Context context;
    private ArrayList<Leaf> leaves;

    public LeavesListAdapter(Context context, ArrayList<Leaf> leaves)
    {
        super(context, R.layout.leaves_list_item_layout, leaves);
        this.context = context;
        this.leaves = leaves;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View leafIdView = inflater.inflate(R.layout.leaves_list_item_layout, parent, false);
        TextView leafIdTextView = (TextView) leafIdView.findViewById(R.id.leaf_id_textview);
        leafIdTextView.setText(leaves.get(position).getLeafId());

        if(leaves.get(position).isLeafIdChanged())
        {
            leafIdTextView.setBackgroundColor(Color.CYAN);
        }

        return leafIdView;
    }
}
