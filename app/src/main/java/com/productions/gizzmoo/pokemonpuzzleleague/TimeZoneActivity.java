package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

import static android.media.AudioManager.STREAM_MUSIC;
import static com.productions.gizzmoo.pokemonpuzzleleague.PuzzleBoardView.ANIMATION_MATCH_INVERT_FRAMES_NEEDED;
import static com.productions.gizzmoo.pokemonpuzzleleague.PuzzleBoardView.ANIMATION_MATCH_POP_FRAMES_NEEDED;

/**
 * Created by Chrystian on 1/23/2018.
 */

public class TimeZoneActivity extends FragmentActivity implements GameDialogFragment.OnGameEndingDialogFragmentReturnListener {

    private final static String matchAnimationCountKey = "MATCH_ANIMATION_COUNT_KEY";
    private final static String gridKey = "GRID_KEY";
    private final static String blockSwitcherKey = "BLOCK_SWITCHER_KEY";
    private final static String needRowUpdateKey = "NEED_ROW_UPDATE_KEY";
    private final static String newBlocksKey = "NEW_BLOCKS_KEY";
    private final static String elapsedTimeKey = "ELAPSED_TIME_KEY";
    private final static String startTimeKey = "START_TIME_KEY";


    private final static String currentFrameKey = "CURRENT_FRAME_KEY";
    private final static String statusKey = "STATUS_KEY";
    private final static String matchingBlocksKey = "MATCHING_BLOCKS_KEY";
    private final static String trackPositionKey = "TRACK_POSITION_KEY";

    private final static String gameSpeedLevelKey = "SPEED_LEVEL_KEY";

    private TextView mTimeView;
    private TextView mSpeedView;

    private int mBlockMatchAnimating;

    private boolean mNeedNewRowUpdate;
//    private int mCurrentFrameCount;
    private int mGameSpeedLevel;
    private int mGameSpeedFrames;

    private MusicService mMusicService;
    private boolean mIsMusicServiceBound;
    private int mTrackPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_zone_layout);
        mTimeView = findViewById(R.id.timerValue);
        mSpeedView = findViewById(R.id.speedValue);

        if (savedInstanceState != null) {
            mBlockMatchAnimating = savedInstanceState.getInt(matchAnimationCountKey);
//            mGrid = (Block[][]) savedInstanceState.getSerializable(gridKey);
//            mBlockSwitcher = (SwitchBlocks) savedInstanceState.getSerializable(blockSwitcherKey);
            mNeedNewRowUpdate = savedInstanceState.getBoolean(needRowUpdateKey);
//            mElapsedTime = savedInstanceState.getLong(elapsedTimeKey);
//            mStartTime = savedInstanceState.getLong(startTimeKey);

//            mCurrentFrameCount = savedInstanceState.getInt(currentFrameKey);
//            mStatus = (GameStatus) savedInstanceState.getSerializable(statusKey);
//            mNewRow = (Block[]) savedInstanceState.getSerializable(newBlocksKey);
//            mBlockMatch = (ArrayList<Block>) savedInstanceState.getSerializable(matchingBlocksKey);

            mGameSpeedLevel = savedInstanceState.getInt(gameSpeedLevelKey);

            mTrackPosition = savedInstanceState.getInt(trackPositionKey);

//            mBoardView.setNewRow(mNewRow);
//            mBoardView.setRisingAnimationCount(mCurrentFrameCount);
//            mBoardView.winLineAt(mNumOfLinesLeft);
        } else {
            mBlockMatchAnimating = 0;
//            mGrid = startNewGrid();
            mNeedNewRowUpdate = true;
//            mElapsedTime = 0;
//            mStartTime = System.nanoTime() / 1000000;
            mTrackPosition = 0;
//            mCurrentFrameCount = 0;
//            mStatus = GameStatus.Running;
//            mNewRow = new Block[NUM_OF_COLS];
//            mBlockMatch = new ArrayList<>();

//            mGameSpeedLevel = settings.getInt("pref_speed_key", 10);
        }

        mIsMusicServiceBound = false;
        mGameSpeedFrames = normalizeSpeedToFrames(mGameSpeedLevel);
//        mBoardView.setGameSpeed(mGameSpeedFrames);

//        mBoardView.setGrid(mGrid, mBlockSwitcher);
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
        outState.putInt(matchAnimationCountKey, mBlockMatchAnimating);
//        outState.putSerializable(gridKey, mGrid);
//        outState.putSerializable(blockSwitcherKey, mBlockSwitcher);
        outState.putBoolean(needRowUpdateKey, mNeedNewRowUpdate);
//        outState.putSerializable(newBlocksKey, mNewRow);
//        outState.putLong(elapsedTimeKey, mElapsedTime);
//        outState.putLong(startTimeKey, mStartTime);

//        outState.putInt(currentFrameKey, mCurrentFrameCount);
//        outState.putSerializable(statusKey, mStatus);
//        outState.putSerializable(matchingBlocksKey, mBlockMatch);
        outState.putInt(trackPositionKey, mTrackPosition);
        outState.putInt(gameSpeedLevelKey, mGameSpeedLevel);


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

    private int normalizeSpeedToFrames(int speed) {
        float maxSpeed = 50;
        float minSpeed = 1;

        if (speed > 50) {
            speed = 50;
        } else if (speed < 1) {
            speed = 1;
        }

        float normalizedSpeed = (speed - minSpeed) / (maxSpeed - minSpeed);
        float invertNormSpeed = 1 - normalizedSpeed;
        return (int)(((invertNormSpeed * 9) + 1) * 30);
    }

    private int getNumOfLinesForLevel() {
        return ((int)(mGameSpeedLevel * 1.25)) + 3;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mMusicService = ((MusicService.ServiceBinder) binder).getService();
//            boolean isPanicMode = false;
//            if (mGameLoop != null) {
//                isPanicMode = mGameLoop.getGameStatus() != GameStatus.Running;
//            }
            mMusicService.startMusic(mTrackPosition, false);
            mIsMusicServiceBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            mMusicService = null;
        }
    };

    private void changeSong(GameStatus songToChangeTo) {
        if (!mIsMusicServiceBound) {
            return;
        }

        boolean isPanic = songToChangeTo != GameStatus.Running;
        mMusicService.changeSong(isPanic);
    }

    @Override
    public void onGameEndingDialogResponse() {
        finish();
    }


}
