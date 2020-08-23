package com.example.dreammeme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class Game extends Base {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference readyPlayerOne;
    boolean playersReady = true;

    private static final int MY_PERMISSION_REQUEST = 1;

    final Random rnd = new Random();

    protected String roomName;
    protected String player;

    TextView textView1, textView2, textView3;
    EditText editText1, editText2;
    Button generate, submit, save;

    protected int roundNumber;

    protected static final String TAG = "GameScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // call animate method from Base
        animate();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("Room");
            roundNumber = extras.getInt("Round");
            player = extras.getString("Player");
        }

        Log.d("Msg", ""+roundNumber);
      
        // sets the random meme to be edited
        final ImageView img = (ImageView) findViewById(R.id.meme);
        // I have 3 images named img_0 to img_2, so...
        final String str = "meme" + rnd.nextInt(5);
        img.setImageDrawable
                (
                        getResources().getDrawable(getResourceID(str, "drawable",
                                getApplicationContext()))
                );

        textView1 = (TextView) findViewById(R.id.toptext_output);
        textView2 = (TextView) findViewById(R.id.bottomtext_output);

        textView3 = (TextView) findViewById(R.id.roundNumber);
        textView3.setText("Round " + roundNumber + "/10");

        editText1 = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);

        generate = (Button) findViewById(R.id.button3);
        submit = (Button) findViewById(R.id.button);
        save = (Button) findViewById(R.id.button2);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView1.setText(editText1.getText().toString());
                textView2.setText(editText2.getText().toString());
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openWaitingInGameScreen();
                }
            }
        );

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checks to see if permission is given to save memes
                if (ContextCompat.checkSelfPermission(Game.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(Game.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(Game.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
                    }
                    else {
                        ActivityCompat.requestPermissions(Game.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
                    }
                }
                else {
                    //do nothing
                }
                View content = findViewById(R.id.lay);
                Bitmap bitmap = getScreenShot(content);
                String currentImage = "meme" + System.currentTimeMillis() + ".png";
                save(bitmap, currentImage);
            }
        });
    }

    //request permission for saving pictures
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(Game.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //do nothing
                    }
                }
                else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    public static Bitmap getScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
    
    private void openWaitingInGameScreen() {
        Log.d("GameScreen", "Opening Waiting Screen");
        Intent waitingScreen = new Intent(this, WaitingInGame.class);
        View content = findViewById(R.id.lay);
        Bitmap bitmap = getScreenShot(content);

        // Notify Database that image has been uploaded
        // notifyDatabaseOfUpload();

        // Upload bitmap to storage as image file
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String filepath = roomName + "/" + player;
        StorageReference memeRef = storageRef.child(filepath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = memeRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Upload failure: " + exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload success");
            }
        });

        waitingScreen.putExtra("Player", player);
        waitingScreen.putExtra("Room", roomName);
        waitingScreen.putExtra("Round", roundNumber);
        waitingScreen.putExtra("Transition", 1);

        startActivity(waitingScreen);
    }

    //function for calling a random meme
    protected final static int getResourceID
            (final String resName, final String resType, final Context ctx)
    {
        final int ResourceID =
                ctx.getResources().getIdentifier(resName, resType,
                        ctx.getApplicationInfo().packageName);
        if (ResourceID == 0)
        {
            throw new IllegalArgumentException
                    (
                            "No resource string found with name " + resName
                    );
        }
        else
        {
            return ResourceID;
        }
    }


    //save edited meme
    public void save(Bitmap bm, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MEME";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dirPath, fileName);
        try{
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void notifyDatabaseOfUpload() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference playerImageRef = database.getReference().child("Rooms/"+roomName+"/Images/"+player);

        playerImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long roundNum;
                try {
                    roundNum = (long) dataSnapshot.getValue() + 1;
                } catch (NumberFormatException e) {
                    roundNum = (long) 1;
                } catch (NullPointerException e) {
                    roundNum = (long) 1;
                }
                Log.d("game", "Image for round " + roundNum + " uploaded by player " + player);
                playerImageRef.setValue(roundNum);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("voting", "Attempted read to database failed");
            }
        });
    }
    
}

