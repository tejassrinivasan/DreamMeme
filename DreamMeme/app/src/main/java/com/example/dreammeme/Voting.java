package com.example.dreammeme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.GlideApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

//import com.bumptech.glide.module.GlideApp;

public class Voting extends Base{

    // TODO update image sizing
    // TODO restructure DB so that Images folder is not in same location as player folders
    private Context roomContext = this;
    protected String roomName, player;
    protected Integer roundNumber;

    //private int memeIndex;
    private ArrayList<ImageView> memeList;
    private ArrayList<String> memeNameList;

    private StorageReference roomStorageRef;
    private DatabaseReference roomRef;

    //private Button voteButton/*, leftButton, rightButton*/;

    protected ListView memesDisplay;

    // DEFINING A IMAGE VIEW ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<ImageView> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        Log.d("voting", "Voting screen opened");
        animate();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("Room");
            player = extras.getString("Player");
            roundNumber = extras.getInt("Round");
        }
        memeList = new ArrayList<>();
        memeNameList = new ArrayList<>();

        memesDisplay = findViewById(R.id.memeList);

        adapter = new ImageViewAdapter(getApplicationContext(),R.layout.image_list_item,
                memeList);
        memesDisplay.setAdapter(adapter);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        roomStorageRef = storage.getReference().child(roomName);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        roomRef = database.getReference().child("Rooms/"+roomName);

        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                memeNameList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    long round;
                    try {
                        round = (long) snapshot.child("round").getValue();
                    } catch (Exception e) {
                        round = (long) 1;
                    }
                    if (!name.equals(player) /* && round = current round number */) {
                        ImageView memeView = new ImageView(roomContext);
                        loadImageIntoView(name, memeView);
                        adapter.add(memeView);
                        memeNameList.add(name);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadImageIntoView(String imageName, ImageView view) {
        StorageReference imRef = roomStorageRef.child(imageName);

        GlideApp.with(this /* context */)
                .load(imRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(view);
    }

    private void vote(String voteRecipient) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference voteRef = database.getReference().child("Rooms").child(roomName).child(voteRecipient+"/score");

        voteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long currPts;
                try {
                    currPts = (long) dataSnapshot.getValue();
                } catch (NumberFormatException e) {
                    currPts = (long) 0;
                } catch (NullPointerException e) {
                    currPts = (long) 0;
                }
                Log.d("voting", "Score of player voted for: " + currPts);
                voteRef.setValue(currPts + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("voting", "Attempted read to database failed");
            }
        });
        openWaitingInGameScreen();
    }

    private void openWaitingInGameScreen() {
        Intent waitingScreen = new Intent(this, WaitingInGame.class);

        waitingScreen.putExtra("Player", player);
        waitingScreen.putExtra("Room", roomName);
        waitingScreen.putExtra("Round", roundNumber);
        waitingScreen.putExtra("Transition", 2);

        startActivity(waitingScreen);
    }

    private class ImageViewAdapter extends ArrayAdapter<ImageView> {
        public ImageViewAdapter(Context context, int textViewResourceId, ArrayList<ImageView> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(roomContext).inflate(R.layout.image_list_item, null);

            ImageView myImageView = convertView.findViewById(R.id.imageView);

            final String tempName = memeNameList.get(position);

            Log.d("voting", "Loading image at position: " + position);
            loadImageIntoView(tempName, myImageView);

            myImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vote(tempName);
                }
            });

            return convertView;
        }
    }
}
