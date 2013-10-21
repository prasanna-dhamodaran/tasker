package com.motorola.root.background;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.motorola.root.model.Leaf;
import com.motorola.root.providers.ChildrenProvider;

import java.util.ArrayList;

public class AddLeaf extends AsyncTask<Void, Void, Boolean>
{
    private Context context;
    private String newLeafId;
    private TextView statusDisplayTextView;
    private ArrayList<Leaf> leafIds;
    private ArrayAdapter<Leaf> adapter;
    private String rootId;

    public AddLeaf(Context context, String rootId, String newLeafId, TextView statusDisplayTextView, ArrayList<Leaf> leafIds, ArrayAdapter<Leaf> adapter)
    {
        this.context = context;
        this.newLeafId = newLeafId;
        this.statusDisplayTextView = statusDisplayTextView;
        this.leafIds = leafIds;
        this.adapter = adapter;
        this.rootId = rootId;
    }

    @Override
    protected void onPreExecute()
    {
        statusDisplayTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        // Write code to add child to local content provider and google app engine here
        ContentValues values = new ContentValues();
        values.put(ChildrenProvider.COLUMN_PARENT_ID, rootId);
        values.put(ChildrenProvider.COLUMN_CHILD_ID, newLeafId);
        values.put(ChildrenProvider.COLUMN_CHILD_CHANGED, 0);
        Uri uri = context.getContentResolver().insert(ChildrenProvider.CONTENT_URI, values);

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

        if(result == true)
        {
            leafIds.add(new Leaf(newLeafId, false));
            adapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(context, "Could not add child", Toast.LENGTH_SHORT).show();
        }
    }
}
