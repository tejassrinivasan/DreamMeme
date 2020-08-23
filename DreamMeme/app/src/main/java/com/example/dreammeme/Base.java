package com.example.dreammeme;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Base extends AppCompatActivity {

    private static final String TAG = "Firebase Usage";   //tag used for debugging database reads

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //helper method to be called in other activities
        animate();

    }

    //rotates through the color palette of the background with fade features
    protected void animate() {
        ImageView background = findViewById(R.id.color_background);
        AnimationDrawable animationDrawable2 = (AnimationDrawable) background.getDrawable();
        animationDrawable2.start();
        animationDrawable2.setEnterFadeDuration(1500);
        animationDrawable2.setExitFadeDuration(1500);
    }

    //testing Firebase Database Reads/Writes
    public void basicReadWrite(){
        basicReadWrite("username");
    }

    public void basicReadWrite(String username) {
        // [START write_message]
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(username);

        myRef.setValue("Test2");
        // [END write_message]

        // [START read_message]
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        // [END read_message]
    }
}
