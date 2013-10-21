package com.motorola.root.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.motorola.root.R;
import com.motorola.root.background.Authenticate;

public class GoogleAppEngineAuthentication extends Activity
{
    String rootId;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_authenticate_layout);
        rootId = getIntent().getStringExtra("google.account");
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(rootId == null || rootId.isEmpty())
        {
            startActivity(new Intent(this, Leaves.class));
            this.finish();
        }
        else
        {
            Authenticate authenticate = new Authenticate(this, rootId);
            authenticate.execute();
        }
    }
}