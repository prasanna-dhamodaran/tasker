package com.motorola.root.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.motorola.root.adapters.LeavesListAdapter;
import com.motorola.root.background.AddLeaf;
import com.motorola.root.R;
import com.motorola.root.model.Leaf;
import com.motorola.root.providers.ChildrenProvider;
import com.motorola.root.receivers.LeavesUIUpdateReceiver;
import com.motorola.root.services.SyncService;

import java.util.ArrayList;

public class Leaves extends Activity
{
    private ArrayList<Leaf> leaves;
    private LeavesListAdapter adapter;
    private String rootId;
    private LeavesUIUpdateReceiver leavesUIUpdateReceiver;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_leaves_layout);
        final Context context = this;

        rootId = getSharedPreferences("login.data", Context.MODE_PRIVATE).getString("google.account", null);
        if(rootId == null || rootId.isEmpty())
        {
            rootId = getIntent().getStringExtra("google.account");
            if(rootId == null || rootId.isEmpty())
            {
                Intent loginIntent = new Intent(this, RootLogin.class);
                startActivity(loginIntent);
            }
            else
            {
                Intent authenticationIntent = new Intent(this, GoogleAppEngineAuthentication.class);
                authenticationIntent.putExtra("google.account", rootId);
                startActivity(authenticationIntent);
            }
            this.finish();
        }
        else
        {
            final TextView newLeafIdVerificationStatusTextView = (TextView) findViewById(R.id.root_leaves_new_leaf_id_verification_status_textview);
            newLeafIdVerificationStatusTextView.setVisibility(View.INVISIBLE);

            final EditText newLeafIdEditText = (EditText) findViewById(R.id.root_leaves_leaf_id_edittext);

            Button addLeadButton = (Button) findViewById(R.id.root_leaves_add_leaf_button);
            addLeadButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String newLeafId = newLeafIdEditText.getText().toString();
                    addLeaf(context, newLeafId, newLeafIdVerificationStatusTextView);
                    newLeafIdEditText.setText("");
                }
            });

            final Intent syncServiceIntent = new Intent(this, SyncService.class);
            syncServiceIntent.putExtra("root_id", rootId);
            startService(syncServiceIntent);

            final Leaves leaves = this;

            Button logoutButton = (Button) findViewById(R.id.root_logout_button);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.stopService(new Intent(context, SyncService.class));
                    SharedPreferences loginData = getSharedPreferences("login.data", Context.MODE_PRIVATE);
                    loginData.edit().putString("google.account", "").commit();
                    Intent intent = new Intent(leaves, RootLogin.class);
                    startActivity(intent);
                    leaves.finish();
                }
            });
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        loadAllLeaves();

        if(leavesUIUpdateReceiver == null)
        {
            leavesUIUpdateReceiver = new LeavesUIUpdateReceiver();
            leavesUIUpdateReceiver.setLeavesActivity(this);
            IntentFilter intentFilter = new IntentFilter("com.motorola.root.tasks.updated");
            intentFilter.setPriority(1);
            registerReceiver(leavesUIUpdateReceiver, intentFilter);
        }
    }

    public void loadAllLeaves()
    {
        leaves = getAllChildren();

        final Context context = this;
        final ListView leavesListView = (ListView) findViewById(R.id.root_leaves_list);
        adapter = new LeavesListAdapter(this, leaves);
        leavesListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        leavesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String leafId = ((TextView) view.findViewById(R.id.leaf_id_textview)).getText().toString();
                view.setBackgroundColor(Color.TRANSPARENT);

                String sel = ChildrenProvider.COLUMN_CHILD_ID + " = ?";
                String [] selArgs = {leafId};
                ContentValues values = new ContentValues();
                values.put(ChildrenProvider.COLUMN_CHILD_CHANGED, 0);
                long numRows = context.getContentResolver().update(ChildrenProvider.CONTENT_URI, values, sel, selArgs);

                Intent tasksIntent = new Intent(context, Tasks.class);
                tasksIntent.putExtra("leaf_id", leafId);
                context.startActivity(tasksIntent);
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if(leavesUIUpdateReceiver != null)
        {
            unregisterReceiver(leavesUIUpdateReceiver);
            leavesUIUpdateReceiver = null;
        }
    }

    private ArrayList<Leaf> getAllChildren()
    {
        ArrayList<Leaf> leaves = new ArrayList<Leaf>();

        String [] projection = {ChildrenProvider.COLUMN_CHILD_ID, ChildrenProvider.COLUMN_CHILD_CHANGED};
        String selection = ChildrenProvider.COLUMN_PARENT_ID + " = ?";
        String [] selectionArgs = {rootId};
        Cursor cursor = getContentResolver().query(ChildrenProvider.CONTENT_URI, projection, selection, selectionArgs, null);
        if(cursor.moveToFirst())
        {
            do
            {
                String leafId = cursor.getString(cursor.getColumnIndex(ChildrenProvider.COLUMN_CHILD_ID));
                Boolean isLeafChanged = (cursor.getInt(cursor.getColumnIndex(ChildrenProvider.COLUMN_CHILD_CHANGED)) != 0);
                leaves.add(new Leaf(leafId, isLeafChanged));
            }while(cursor.moveToNext());
        }

        return leaves;
    }

    private void addLeaf(Context context, String newLeafId, TextView statusDisplayTextView)
    {
        if(newLeafId == null || newLeafId.isEmpty() || !newLeafId.contains("@gmail.com"))
        {
            Toast.makeText(context, "Please enter a valid child id", Toast.LENGTH_SHORT).show();
            return;
        }

        AddLeaf addLeaf = new AddLeaf(context, rootId, newLeafId, statusDisplayTextView, leaves, adapter);
        addLeaf.execute();
    }
}