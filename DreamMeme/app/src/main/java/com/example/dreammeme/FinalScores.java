package com.example.dreammeme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class FinalScores extends Voting {

    ListView mListView;
    ListView mListView2;

    int[] images = {R.drawable.firstplace,
            R.drawable.secondplace,
            R.drawable.thirdplace,
    };

    TreeMap<Long,String> players;
    ArrayList<Long> mscores;
    ArrayList<String> mnames;

    private DatabaseReference myRef;
    long count;
    private Button home;
    private Button playAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_scores);

        //call animate method from Base
        animate();

        mListView = findViewById(R.id.votingresults);
        mListView2 = findViewById(R.id.remainingresults);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("Room");
            player = extras.getString("Player");
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Rooms").child(roomName);

        players = new TreeMap<>(Collections.reverseOrder());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getKey();

                    if (!value.equals("Images")) {
                        Long score = (Long) dataSnapshot.child(value).child("score").getValue();
                        try {
                            players.put(score, value);
                        } catch (NullPointerException e) {  // This will happen if a player hasn't been voted for yet
                            players.put((long) 0, value);
                        }
                    }
                }
                CustomAdapter customAdapter = new CustomAdapter();
                mListView.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();

                count = dataSnapshot.getChildrenCount()-1;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //function to return home
        home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeScreen();
            }
        });

        playAgain = (Button) findViewById(R.id.playagain);
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRoomScreen();
            }
        });
    }

    //TODO reset everything in the group
    private void openRoomScreen() {
        Intent roomScreen = new Intent(this, WaitingRoom.class);
        roomScreen.putExtra("Room", roomName);
        roomScreen.putExtra("Player", player);
        myRef.child(player).child("score").setValue(0);
        startActivity(roomScreen);
    }

    class CustomAdapter2 extends BaseAdapter {

        @Override
        public int getCount() {
            return mscores.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = getLayoutInflater().inflate(R.layout.votingresultsbottom, null);
            TextView mTextView3 = view2.findViewById(R.id.remainingnames);
            TextView mTextView4 = view2.findViewById(R.id.remainingscores);

            mTextView3.setText(mnames.get(i));
            mTextView4.setText(mscores.get(i).toString());

            return view2;
        }
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.votingresults_layout, null);
            ImageView mImageView = view.findViewById(R.id.rank);
            TextView mTextView1 = view.findViewById(R.id.leaderboardnames);
            TextView mTextView2 = view.findViewById(R.id.leaderboardscores);

            ArrayList<Long> scores = new ArrayList<Long>(players.keySet());
            ArrayList<String> names = new ArrayList<String>(players.values());

            mscores = new ArrayList<>();
            mnames = new ArrayList<>();

            for (int j = 3; j < scores.size(); j++) {
                mscores.add(scores.get(j));
                mnames.add(names.get(j));

            }
            CustomAdapter2 customAdapter2 = new CustomAdapter2();
            mListView2.setAdapter(customAdapter2);
            customAdapter2.notifyDataSetChanged();
            notifyDataSetChanged();

            mImageView.setImageResource(images[i]);
            mTextView1.setText(names.get(i));
            mTextView2.setText(scores.get(i).toString());

            return view;
        }
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