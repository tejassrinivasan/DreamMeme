package com.example.dreammeme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class WaitingRoom extends Base {

    private Button home;
    private Button game;
    private String TAG = "Test";
    private String roomName;

    protected ListView playersDisplay;
    private DatabaseReference myRef;
    private String player;

    long count;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        //call animate method from Base
        animate();

        final List<String> playerList = new ArrayList<>();

        //function to return home
        home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeScreen();
            }
        });

        game = (Button) findViewById(R.id.startgame);
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWaitingInGameScreen();
            }
        });

        playersDisplay = findViewById(R.id.playerList);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            roomName = extras.getString("Room");
            player = extras.getString("Player");
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Rooms").child(roomName);

        adapter=new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                playerList);

        playersDisplay.setAdapter(adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getKey();
                    adapter.add(value);
                    adapter.notifyDataSetChanged();
                }
                count = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void openWaitingInGameScreen() {
        Intent waitingScreen = new Intent(this, WaitingInGame.class);

        waitingScreen.putExtra("Player", player);
        waitingScreen.putExtra("Room", roomName);
        waitingScreen.putExtra("Transition", 0);
        waitingScreen.putExtra("Round", 1);
        
        if(count < 3) {
            Toast errorToast = Toast.makeText(WaitingRoom.this, "Not enough players! Must have 3 to begin", Toast.LENGTH_SHORT);
            errorToast.show();
        }
        else{
            startActivity(waitingScreen);
            Log.d("success", "success");
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("hasBackPressed",true);
        if(count == 1) {
            myRef.removeValue();
        }
        myRef.child(player).removeValue();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void openHomeScreen() {
        Intent homeScreen = new Intent(this, MainActivity.class);
        if(count == 1) {
            myRef.removeValue();
        }
        myRef.child(player).removeValue();
        startActivity(homeScreen);
    }
}

