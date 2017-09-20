package com.example.brydon.looperpedal;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String baseFilename = "loopRecord";

    Button recordButton = (Button) findViewById(R.id.recordButton);

    private int numLoops = 0;
    private boolean recordingFlag = false;

    private MediaRecorder spareRecorder = new MediaRecorder(); //this is done so that a new recorder doesn't have to be allocated when a user presses the button, loop recording is time critical
    private MediaRecorder currentRecorder = null;

    private MediaPlayer[] players = new MediaPlayer[3]; //these are global so that a handle can be kept on them.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        setUpRecorder(spareRecorder); //setup the initial spare recorder
    }


    private boolean setUpRecorder(MediaRecorder r){
        r.setAudioSource(MediaRecorder.AudioSource.MIC);
        r.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); //dont really care
        r.setOutputFile(getExternalCacheDir().getAbsolutePath() + "/" + baseFilename + (++numLoops));
        r.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //still dont care
        try{
            r.prepare();
        } catch(java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void playLatest(){ //plays 'loopRecord[numloops]'

    }

    public void restartAll(View view){

    }

    public void clearAll(View view){

    }

    public void clearLast(View view){

    }

    public void toggleRecord(View view){
        if (!recordingFlag){ //we're not already recording, start recording
            currentRecorder = spareRecorder; //use our stashed recorder so we don't waste time between button press and actual recording start
            currentRecorder.start();
            recordButton.setText("Recording");
            //now that we're recording, we can start setting up a new spare recorder in the background
            spareRecorder = new MediaRecorder();
            setUpRecorder(spareRecorder);
            recordingFlag = true;
        } else{ //we were recording, stop recording
            recordButton.setText("Record");
            currentRecorder.stop();
            currentRecorder.release(); //we're done with this one now, we don't need it anymore and we made a new one for next time. This is a spot where this recorder could be updated for a new looper but whatever.
            currentRecorder = null; //can't use it anymore it's been released
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
