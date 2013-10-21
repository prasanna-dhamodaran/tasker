package com.motorola.root.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.motorola.root.R;

public class RootLogin extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_login_layout);

        String [] googleAccounts = getGoogleAccounts();
        final ListView googleAccountsListView = (ListView) findViewById(R.id.google_accounts_listview);
        googleAccountsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, googleAccounts));

        final RootLogin rootLogin = this;

        googleAccountsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String chosenGoogleAccountName = ((TextView) view).getText().toString();
                Intent intent = new Intent(rootLogin, Leaves.class);
                intent.putExtra("google.account", chosenGoogleAccountName);
                startActivity(intent);
                rootLogin.finish();
            }
        });
    }

    private String [] getGoogleAccounts()
    {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        String [] accountNames = new String[accounts.length];
        for(int i = 0; i < accountNames.length; ++i)
        {
            accountNames[i] = accounts[i].name;
        }
        return accountNames;
    }
}