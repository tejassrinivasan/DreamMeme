package com.example.dreammeme;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;

public class MainActivity extends Base {

    private Button createButton, joinButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //call animate method from Base
        animate();

        //calls DisplayName class when create button pressed (first person in room)
        createButton = (Button) findViewById(R.id.creategame);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateNameScreen();
            }
        });

        //calls JoinGameName class when join button pressed (takes to a list of open rooms)
        joinButton = (Button) findViewById(R.id.joingame);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJoinNameScreen();
            }
        });

        //performs animation of meme pictures at home screen
        ImageView imageView = findViewById(R.id.start_background);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
        animationDrawable.setEnterFadeDuration(1000);

        //store value of "notifications" preference in a SharedPreferences object
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref.getBoolean
                (AppSettings.KEY_PREF_NOTIF, true);
    }

    //Creates app bar menu containing "settings"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    private void openCreateNameScreen() {
        Intent nameScreen = new Intent(this, DisplayName.class);
        startActivity(nameScreen);
    }

    private void openJoinNameScreen() {
        Intent nameScreen = new Intent(this, JoinGameName.class);
        startActivity(nameScreen);
    }

    //Handles selection of items in menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings_page:
                Intent appSettings = new Intent(this, AppSettings.class);
                startActivity(appSettings);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
