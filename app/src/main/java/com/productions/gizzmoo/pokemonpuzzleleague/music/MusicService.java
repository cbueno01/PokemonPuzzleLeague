package com.productions.gizzmoo.pokemonpuzzleleague.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public abstract class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    private final IBinder mBinder = new ServiceBinder(this);
    protected Context context;
    private int mediaPlayerIndex;
    private MediaPlayer mp[] = new MediaPlayer[3];

    @Override
    public void onCompletion(MediaPlayer curmp) {
        int mpPlaying;
        int mpNext;
        if(curmp == mp[0]) {
            mpPlaying = 1;
            mpNext = 2;
        } else if(curmp == mp[1]) {
            mpPlaying = 2;
            mpNext = 0;
        } else if(curmp == mp[2]) {
            mpPlaying = 0;
            mpNext = 1;
        } else {
            mpPlaying = 0;
            mpNext = 0;
        }

        mediaPlayerIndex = mpPlaying;
        try {
            if (mp[mpNext] != null) {
                mp[mpNext].release();
            }

            mp[mpNext] = MediaPlayer.create(context, getResource());
            mp[mpNext].setOnCompletionListener(this);
            mp[mpNext].setVolume(.7f, .7f);
            mp[mpPlaying].setNextMediaPlayer(mp[mpNext]);
            mp[mpPlaying].setVolume(.7f, .7f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mediaPlayerIndex = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void startMusic(int position) {
        // initialize and set listener to three mediaplayers
        for (int i = 0; i < mp.length; i++) {
            mp[i] = MediaPlayer.create(context, getResource());
            mp[i].setOnCompletionListener(this);
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
        for(MediaPlayer mediaPlayer : mp) {
            if (mediaPlayer != null) {
                if(mediaPlayer.isPlaying()) {
                    retValue = mediaPlayer.getCurrentPosition();
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            }
        }

        return retValue;
    }

    public abstract int getResource();
}
