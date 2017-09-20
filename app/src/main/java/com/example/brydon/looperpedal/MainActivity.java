package com.example.brydon.looperpedal;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String baseFilename;

    private int numLoops = 0;

    Button recordButton;
    private boolean recordingFlag = false;

    private MediaRecorder spareRecorder = null; //this is done so that a new recorder doesn't have to be allocated when a user presses the button, loop recording is time critical
    private MediaRecorder currentRecorder = null;

    private ArrayList<MediaPlayer> players = new ArrayList<>(); //these are global so that a handle can be kept on them.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spareRecorder = new MediaRecorder();
        players.add(new MediaPlayer()); //always keep a spare of these as well.
        baseFilename = getFilesDir().getAbsolutePath() + "/loopRecord";
        System.out.println(baseFilename);
        recordButton = (Button) findViewById(R.id.recordButton);
        System.out.println("setup recorder returned" + setUpRecorder(spareRecorder)); //setup the initial spare recorder
    }


    private boolean setUpRecorder(MediaRecorder r){
        r.setAudioSource(MediaRecorder.AudioSource.MIC);
        r.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); //dont really care
        numLoops += 1;
        r.setOutputFile(baseFilename + (numLoops));
        r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //still dont care
        try{
            System.out.println("Next recording will be in file " + baseFilename + numLoops);
            r.prepare();
        } catch(java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void play(MediaPlayer p){ //preps and plays p
        try {
            p.prepare();
        } catch(java.io.IOException e){
            e.printStackTrace();
        }
        p.start();
    }

    public void restartAll(View view){
        for (int i = 0; i < players.size() - 1; ++i){
            MediaPlayer p = players.get(i);
            p.seekTo(0);
            
        }
    }

    public void clearAll(View view){
        int i = 0; //the first element
        while(!players.isEmpty()) {
            MediaPlayer p = players.get(i);
            p.stop();
            p.release();
            players.remove(i);
        }
    }

    public void clearLast(View view){
        MediaPlayer p = players.get(players.size() - 1);
        p.stop();
        p.release();
        players.remove(players.size()-1);
    }

    public void toggleRecord(View view){
        if (!recordingFlag){ //we're not already recording, start recording
            currentRecorder = spareRecorder; //use our stashed recorder so we don't waste time between button press and actual recording start
            currentRecorder.start();
            recordButton.setText("Recording");
            recordingFlag = true;
        } else{ //we were recording, stop recording
            currentRecorder.stop();
            currentRecorder.release(); //we're done with this one now, we don't need it anymore and we made a new one for next time. This is a spot where this recorder could be updated for a new looper but whatever.
            //first things first, get the track playing as soon as the button is pressed.
            MediaPlayer p = players.get(players.size()-1); //the last mediaPlayer in the list is free to use
            try{
                System.out.println("Trying to play from " + baseFilename+numLoops);
                p.setDataSource(baseFilename + numLoops);
            } catch (IOException e){
                e.printStackTrace();
            }
            p.setLooping(true); //make it loop
            play(p);
            players.add(new MediaPlayer());
            recordButton.setText("Record");
            currentRecorder = null; //can't use it anymore it's been released
            spareRecorder = new MediaRecorder(); //set up a spare recorder
            setUpRecorder(spareRecorder);
            recordingFlag = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
