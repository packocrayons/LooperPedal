package com.example.brydon.looperpedal;

import android.media.MediaPlayer;

/**
 * Created by brydon on 20/09/17.
 */

public class MediaPlayerWithStops extends MediaPlayer {
    public int startTime; //this is all this class does, it's so that the SoundPlayer thread can keep an eye on the tracks and loop them at their times.
    public int stopTime;

    public void setStartTime(int t){
        startTime = t;
    }

    public void setStopTime(int t){
        stopTime = t;
    }
}
