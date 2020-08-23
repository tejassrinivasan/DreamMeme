package com.example.dreammeme;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomList extends JoinGameName {

    //dynamic list of rooms open
    protected ListView roomsDisplay;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //player name which is saved in extras
    private String player;

    private String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        roomsDisplay = (ListView) findViewById(R.id.rooms);
        player = getIntent().getExtras().getString("Player");

        //change Action Bar title at top of screen
        getSupportActionBar().setTitle("Rooms");

        //call animate method from Base
        animate();

        //initialize vars
        final List<String> roomsList = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String androidID = android.provider.Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        DatabaseReference myRef = database.getReference("Rooms");

        //adapter is what gets updated every time a change is made to the database
        adapter=new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                roomsList);

        roomsDisplay.setAdapter(adapter);

        //when database changes, clear the rooms and reinitialize
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getKey();
                    adapter.add(value);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //when a room is clicked add player to that room: set key equal to the room name and value equal to the unique code
        roomsDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomName = roomsDisplay.getItemAtPosition(position).toString();
                DatabaseReference players = database.getReference().child("Rooms").child(roomName).child(player);
                players.child("ID").setValue(androidID);
                players.child("score").setValue(0);
                players.child("ready").setValue(0);
                addEventListener();
            }
        });
    }

    private void addEventListener() {
        Intent nameScreen = new Intent(this, WaitingRoom.class);
        nameScreen.putExtra("Room", roomName);
        nameScreen.putExtra("Player", player);
        startActivity(nameScreen);
    }

}
