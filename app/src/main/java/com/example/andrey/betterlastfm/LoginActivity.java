package com.example.andrey.betterlastfm;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andrey.betterlastfm.loaders.SessionKeyLoader;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Andrey on 09.04.2015.
 */
public class LoginActivity extends Activity implements LoaderManager.LoaderCallbacks<String>{
    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    private String username;
    private String password;
    private String apiKey;
    private String apiSignature;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("com.example.andrey.betterlastfm",MODE_PRIVATE);
        if (sharedPreferences.contains("username") && sharedPreferences.contains("session_key")){
            Log.d(LOG_TAG, "true!");
            startActivity(
                    new Intent(this, ProfileActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            );
        }

        Log.d(LOG_TAG,sharedPreferences.getString("username", "ERROR NO USERNAME"));
        Log.d(LOG_TAG,sharedPreferences.getString("session_key", "ERROR NO SESSION KEY"));

        final EditText editTextUsername = (EditText) findViewById(R.id.username_login_edit_text);
        final EditText editTextPassword = (EditText) findViewById(R.id.userpassword_login_edit_text);
        Button loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                apiKey = "f445e682840e750fc7c992898e868efb";
                String secret = "5b332291ad05138bd2e441a22262e5b2";
                String method = "auth.getMobileSession";
                String tmp = "api_key" + apiKey + "method" + method + "password" + password
                        + "username" + username + secret;

                apiSignature = Util.md5(tmp);

                sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("api_signature", apiSignature);
                editor.putString("username", username);
                editor.commit();

                Log.d(LOG_TAG, apiSignature);

                initLoader();

                getLoaderManager().getLoader(0).forceLoad();
            }
        });
    }

    private void initLoader(){
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new SessionKeyLoader(this, username, password, apiKey, apiSignature);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (sharedPreferences.contains("username") && sharedPreferences.contains("api_signature")){
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
