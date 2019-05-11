package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import java.util.Locale;

/**
 * Created by Chrystian on 1/23/2018.
 */

public class TimeZoneActivity extends FragmentActivity implements GameDialogFragment.OnGameEndingDialogFragmentReturnListener, TimeZoneGameFragment.TimeZoneFragmentInterface {

    private final static String trackPositionKey = "TRACK_POSITION_KEY";

    private TextView mTimeView;
    private TextView mSpeedView;
    private TimeZoneGameFragment gameFragment;

    private MusicService mMusicService;
    private boolean mIsMusicServiceBound;
    private int mTrackPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_zone_layout);
        mTimeView = findViewById(R.id.timerValue);
        mSpeedView = findViewById(R.id.speedValue);
        gameFragment = (TimeZoneGameFragment) (getFragmentManager().findFragmentById(R.id.puzzleBoard));
        gameFragment.setFragmentListener(this);

        if (savedInstanceState != null) {
            mTrackPosition = savedInstanceState.getInt(trackPositionKey);
        } else {
            mTrackPosition = 0;
        }

        mIsMusicServiceBound = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(trackPositionKey, mTrackPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindService(new Intent(this, MusicService.class), mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onStop() {
        super.onStop();

        if (mIsMusicServiceBound) {
            mTrackPosition = mMusicService.stopMusic();
            unbindService(mConnection);
            mIsMusicServiceBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mMusicService = ((MusicService.ServiceBinder) binder).getService();
            boolean isPanicMode = false;
            if (gameFragment.getGameLoop() != null) {
                isPanicMode = gameFragment.getGameLoop().getGameStatus() != GameStatus.Running;
            }
            mMusicService.startMusic(mTrackPosition, isPanicMode);
            mIsMusicServiceBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            mMusicService = null;
        }
    };

    @Override
    public void changeSong(boolean isPanic) {
        if (!mIsMusicServiceBound) {
            return;
        }

        mMusicService.changeSong(isPanic);
    }

    @Override
    public void updateGameTimeAndSpeed(long timeInMilli, int gameSpeed) {
        mTimeView.setText(String.format(Locale.US, "%04d", (int)(timeInMilli / 1000)));
        mSpeedView.setText(String.format(Locale.US, "%02d", gameSpeed));
    }

    @Override
    public void onGameEndingDialogResponse() {
        finish();
    }
}
