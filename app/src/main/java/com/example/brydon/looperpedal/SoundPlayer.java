package com.example.brydon.looperpedal;

import android.media.MediaPlayer;
import android.os.Environment;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by brydon on 20/09/17.
 */

public class SoundPlayer implements Runnable{

    public static final String baseFilename = Environment.getDataDirectory().getAbsolutePath() + "/loopRecord";

    private ArrayList<MediaPlayerWithStops> players;

    public SoundPlayer(){
        players = new ArrayList<>(1);

    }

    public void addPlayer(int startTime, int stopTime, String filename){
        MediaPlayerWithStops p = new MediaPlayerWithStops();
        p.setStartTime(startTime);
        p.setStopTime(stopTime);
        setUpPlayer(p, filename);
        p.start();
    }

    private boolean setUpPlayer(MediaPlayer p, String filename){
        try{
            p.setDataSource(filename);
            p.prepare();
        } catch (IOException e){
            e.printStackTrace(); //lol
            return false;
        }
        return true;
    }

    public void run(){
        /*this loop keeps track of all the players and loops them manually when they need to
        This is because the native looping has a delay and is clunky/unpredictable
         */
        for(int i = 0; i < players.size(); ++i){
            MediaPlayerWithStops p = players.get(i);
            if (p.getCurrentPosition() >= p.stopTime){
                p.seekTo(p.startTime);
            }
        }
    }

    public void restartAll(){
        for(int i = 0; i < players.size(); ++i){
            MediaPlayerWithStops p = players.get(i);
            p.seekTo(p.startTime);
        }
    }

    public void pauseAllButMostRecent(){
        for(int i = 0; i < players.size() - 1; ++i){
            MediaPlayerWithStops p = players.get(i);
            p.pause();
        }
    }

    public void deleteAll(){
        int i = 0;
        while (!players.isEmpty()){
            MediaPlayerWithStops p = players.get(i);
            p.stop();
            p.release();
            players.remove(i);
        }
    }

    public void deleteMostRecent(){
        MediaPlayerWithStops p = players.get(players.size() - 1);
        p.stop();
        p.release();
        players.remove(players.size() - 1);
    }

}
