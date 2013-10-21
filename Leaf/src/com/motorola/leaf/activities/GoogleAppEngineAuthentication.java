package com.motorola.leaf.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.motorola.leaf.R;
import com.motorola.leaf.background.Authenticate;

public class GoogleAppEngineAuthentication extends Activity
{
    String leafId;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaf_authenticate_layout);
        leafId = getIntent().getStringExtra("google.account");
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(leafId == null || leafId.isEmpty())
        {
            startActivity(new Intent(this, Tasks.class));
            this.finish();
        }
        else
        {
            Authenticate authenticate = new Authenticate(this, leafId);
            authenticate.execute();
        }
    }
}