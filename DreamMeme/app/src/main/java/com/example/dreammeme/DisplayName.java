package com.example.dreammeme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DisplayName extends Base {

    protected Button home, nameButton;
    protected EditText username;
    protected String player;
    protected String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayname);

        //call animate method from Base
        animate();

        username = findViewById(R.id.enterUsername);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String androidID = android.provider.Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        final DatabaseReference myRef = database.getReference().child("Rooms");

        //sets function for home button
        home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeScreen();
            }
        });

        //switches to waiting room once name entered
        nameButton = (Button) findViewById(R.id.displayname);
        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the input is not null set player equal to the input (ADD AN ERROR FOR NULL)
                if (!username.getText().toString().matches("")) {
                    player = String.valueOf(username.getText());
                    roomName = player;
                    myRef.child(roomName).child(player).child("ID").setValue(androidID);
                    myRef.child(roomName).child(player).child("score").setValue(0);
                    myRef.child(roomName).child(player).child("ready").setValue(0);
                    openRoomScreen();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Player name cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    protected void openHomeScreen() {
        Intent homeScreen = new Intent(this, MainActivity.class);
        startActivity(homeScreen);
    }

    private void openRoomScreen() {
        Intent roomScreen = new Intent(this, WaitingRoom.class);
        roomScreen.putExtra("Room", roomName);
        roomScreen.putExtra("Player", player);
        startActivity(roomScreen);
    }
}
