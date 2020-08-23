package com.example.dreammeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinGameName extends DisplayName {

    //player input
    String player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game_name);

        //call animate method from Base
        animate();

        //user input box
        final EditText editText = findViewById(R.id.enterUsername);

        //sets function for home button
        home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeScreen();
            }
        });

        //takes to a list of rooms to join once button pushed
        nameButton = (Button) findViewById(R.id.displayname);
        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the input is not null set player equal to the input (ADD AN ERROR FOR NULL)
                if (!editText.getText().toString().matches("")) {
                    player = String.valueOf(editText.getText());
                    openRoomListScreen();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Player name cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //call room list screen and STORE VALUE OF PLAYER IN EXTRAS (IMPORTANT)
    private void openRoomListScreen() {
        Intent roomListScreen = new Intent(this, RoomList.class);
        roomListScreen.putExtra("Player", player);
        startActivity(roomListScreen);
    }

}
