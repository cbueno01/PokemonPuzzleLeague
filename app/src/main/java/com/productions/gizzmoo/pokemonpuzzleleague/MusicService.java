package com.productions.gizzmoo.pokemonpuzzleleague;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

public class MusicService extends Service {

    private final IBinder mBinder = new ServiceBinder();
    private Context _context;

    private int resourceIndex;
    private int mediaPlayerIndex;
    private int[] resourceId;
    private MediaPlayer mp[] = new MediaPlayer[3];



    public MusicService() {}

    public class ServiceBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _context = this;
        resourceIndex = 0;
        mediaPlayerIndex = 0;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context.getApplicationContext());
        Trainer currentTrainer = Trainer.Companion.getTypeByID(settings.getInt("pref_trainer_key", 0));

        resourceId = TrainerResources.Companion.getTrainerSong(currentTrainer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void startMusic(int position, boolean isPanic) {
        resourceIndex = (isPanic) ? 1 : 0;

        // initialize and set listener to three mediaplayers
        for (int i = 0; i < mp.length; i++) {
            mp[i] = MediaPlayer.create(_context, resourceId[resourceIndex]);
            mp[i].setOnCompletionListener(completionListener);
        }

        // set nextMediaPlayers
        mp[0].setNextMediaPlayer(mp[1]);
        mp[1].setNextMediaPlayer(mp[2]);

        mp[mediaPlayerIndex].seekTo(position);
        mp[mediaPlayerIndex].setVolume(.7f, .7f);
        mp[mediaPlayerIndex].start();
    }

    public int stopMusic() {
        int retValue = 0;
        for(int i = 0 ; i < mp.length ; i++) {
            if (mp[i] != null) {
                if(mp[i].isPlaying()) {
                    retValue = mp[i].getCurrentPosition();
                    mp[i].stop();
                }
                mp[i].release();
            }
        }

        return retValue;
    }

    public void changeSong(boolean isPanic) {
        stopMusic();
        startMusic(0, isPanic);
    }

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer curmp) {
            //int mpEnds = 0;
            int mpPlaying = 0;
            int mpNext = 0;
            if(curmp == mp[0]) {
            //    mpEnds = 0;
                mpPlaying = 1;
                mpNext = 2;
            }
            else if(curmp == mp[1]) {
            //    mpEnds = 1;
                mpPlaying = 2;
                mpNext = 0;  // corrected, else index out of range
            }
            else if(curmp == mp[2]) {
             //   mpEnds = 2;
                mpPlaying = 0; // corrected, else index out of range
                mpNext = 1; // corrected, else index out of range
            }

            // as we have set mp2 mp1's next, so index will be 1
            mediaPlayerIndex = mpPlaying;
            try {
                // mp3 is already playing release it
                if (mp[mpNext] != null) {
                    mp[mpNext].release();
                }

                mp[mpNext] = MediaPlayer.create(_context, resourceId[resourceIndex]);
                // at listener to mp3
                mp[mpNext].setOnCompletionListener(this);
                // set vol
                mp[mpNext].setVolume(.7f, .7f);
                // set nextMediaPlayer
                mp[mpPlaying].setNextMediaPlayer(mp[mpNext]);
                // set nextMediaPlayer vol
                mp[mpPlaying].setVolume(.7f, .7f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
