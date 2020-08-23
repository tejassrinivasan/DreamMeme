package com.example.dreammeme;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaitingInGame extends Game {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private boolean playersReady = true;
    private DatabaseReference readyPlayerOne;
    private ValueEventListener listener;
    protected int transition;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("WaitingScreen", "Waiting screen entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_in_game);

        animate();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("Room");
            player = extras.getString("Player");
            roundNumber = extras.getInt("Round");
            transition = extras.getInt("Transition");
            Log.d("trans-line36", transition+"");
        }

        listener = database.getReference().child("Rooms").child(roomName)
            .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playersReady = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot playerChild : snapshot.getChildren()) {
                        if (playerChild.getKey().equals("ready")) {
                            Long value = (Long) playerChild.getValue();
                            Log.d("playersReady", String.valueOf(value));
                            if (value == 0) {
                                playersReady = false;
                            }
                        }
                    }
                }

                if (playersReady) {
                    readyPlayerOne = database.getReference().child("Rooms").child(roomName).child(player+"/ready");
                    readyPlayerOne.setValue(0);
                    Log.d("trans-line57", transition+"");
                    database.getReference().child("Rooms").child(roomName).removeEventListener(listener);
                    switch (transition) {
                        case 0 :
                            Log.d("WaitingScreen", "Entering Game screen");
                            openGameScreen();
                            break;
                        case 1 :
                            Log.d("WaitingScreen", "Entering Voting screen");
                            openVotingScreen();
                            break;
                        case 2 :
                            Log.d("WaitingScreen", "Entering Voting Results screen");
                            openVotingResultsScreen();
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        readyPlayerOne = database.getReference().child("Rooms").child(roomName).child(player+"/ready");
        readyPlayerOne.setValue(1);
    }

    private void openGameScreen() {
        Intent gameScreen = new Intent(this, Game.class);
        gameScreen.putExtra("Room",roomName);
        gameScreen.putExtra("Player",player);
        gameScreen.putExtra("Round",1);

        startActivity(gameScreen);
    }

    private void openVotingScreen() {
        final Intent votingScreen = new Intent(this, Voting.class);

        votingScreen.putExtra("Player", player);
        votingScreen.putExtra("Room", roomName);
        votingScreen.putExtra("Round", roundNumber);

        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliseconds = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                startActivity(votingScreen);
            }
        }.start();
    }

    private void openVotingResultsScreen() {
        Intent votingResultsScreen = new Intent(this, VotingResults.class);
        votingResultsScreen.putExtra("Room", roomName);
        votingResultsScreen.putExtra("Player", player);
        votingResultsScreen.putExtra("Round",roundNumber);
        if (roundNumber < 10) {
            startActivity(votingResultsScreen);
        }
        else {
            openFinalScoresScreen();
        }
    }
    
    private void openFinalScoresScreen() {
        Intent finalScoresScreen = new Intent(this, FinalScores.class);
        finalScoresScreen.putExtra("Room",roomName);
        finalScoresScreen.putExtra("Player",player);
        startActivity(finalScoresScreen);
    }


}
