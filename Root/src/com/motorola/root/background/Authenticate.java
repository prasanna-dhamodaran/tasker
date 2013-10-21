package com.motorola.root.background;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.motorola.root.activities.GoogleAppEngineAuthentication;
import com.motorola.root.activities.Leaves;
import com.motorola.root.constants.Constants;
import com.motorola.root.rest.RestFulPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Authenticate extends AsyncTask<Void, Void, String>
{
    private Context context;
    private String chosenGoogleAccountName;
    private int numTimesAuthTokenFailure = 0;
    private volatile AccountManagerFuture authTokenResult = null;
    private String AUTH_TOKEN_NEEDS_USER_APPROVAL = "USER_APPROVAL_REQUIRED";
    private String AUTH_TOKEN_FAIL = "AUTH_TOKEN_FAILED";

    public Authenticate(Context context, String chosenGoogleAccountName)
    {
        this.context = context;
        this.chosenGoogleAccountName = chosenGoogleAccountName;
    }

    @Override
    protected String doInBackground(Void... voids)
    {
        // TODO: modify call with authentication parameters.
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account chosenAccount = null;
        for(int i = 0; i < accounts.length; ++i)
        {
            if(accounts[i].name.equalsIgnoreCase(chosenGoogleAccountName))
            {
                chosenAccount = accounts[i];
                break;
            }
        }

        if(chosenAccount == null)
        {
            return AUTH_TOKEN_FAIL;
        }

        accountManager.getAuthToken(chosenAccount, "ah", null, false, new GetAuthTokenCallback(), null);
        Integer retries = 0;
        while(authTokenResult == null)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            retries++;
            if(retries > Constants.authTokenRetries)
            {
                break;
            }
        }

        if(authTokenResult != null)
        {
            return processAuthTokenResult(authTokenResult);
        }
        else
        {
            return AUTH_TOKEN_FAIL;
        }
    }

    private String processAuthTokenResult(AccountManagerFuture result)
    {
        Bundle bundle;
        try
        {
            bundle = (Bundle) result.getResult();
            Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
            if(intent != null)
            {
                // User input required
                context.startActivity(intent);
                return AUTH_TOKEN_NEEDS_USER_APPROVAL;
            }
            else
            {
                String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                Map<String, String> map = new HashMap<String, String>();
                map.put("token", authToken);
                map.put("userId", chosenGoogleAccountName);
                map.put("userType", "parent");
                String res = RestFulPost.post(Constants.rootLogin, map);
                boolean requestStatus = false;

                if(res != null)
                {
                    requestStatus = new JSONObject(res).getBoolean("status");
                }

                if(requestStatus)
                {
                    return authToken;
                }
                else
                {
                    return AUTH_TOKEN_FAIL;
                }
            }
        }
        catch (OperationCanceledException e)
        {
            e.printStackTrace();
            return AUTH_TOKEN_FAIL;
        }
        catch (AuthenticatorException e)
        {
            e.printStackTrace();
            return AUTH_TOKEN_FAIL;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return AUTH_TOKEN_FAIL;
        } catch (JSONException e)
        {
            e.printStackTrace();
            return AUTH_TOKEN_FAIL;
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        if(result.equalsIgnoreCase(AUTH_TOKEN_FAIL))
        {
            Toast.makeText(context, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, Leaves.class);
            context.startActivity(intent);
            ((GoogleAppEngineAuthentication) context).finish();
        }
        else  if(result.equalsIgnoreCase(AUTH_TOKEN_NEEDS_USER_APPROVAL))
        {
            numTimesAuthTokenFailure++;
            if(numTimesAuthTokenFailure > 1)
            {
                Toast.makeText(context, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Leaves.class);
                context.startActivity(intent);
                ((GoogleAppEngineAuthentication) context).finish();
            }
        }
        else
        {
            // Start task to sync children
            SharedPreferences loginData = context.getSharedPreferences("login.data", Context.MODE_PRIVATE);
            loginData.edit().putString("google.account", chosenGoogleAccountName).commit();
            Intent nextIntent = new Intent(context, Leaves.class);
            context.startActivity(nextIntent);
            ((GoogleAppEngineAuthentication) context).finish();
        }
    }

    private class GetAuthTokenCallback implements AccountManagerCallback
    {
        public void run(AccountManagerFuture result)
        {
            authTokenResult = result;
        }
    };
}
