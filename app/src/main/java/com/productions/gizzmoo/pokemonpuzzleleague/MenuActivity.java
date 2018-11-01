package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private final String currentFrameKey = "CURRENT_FRAME_KEY";
    private final String isPanningRightKey = "IS_PANNING_RIGHT_KEY";

    private final int MAX_FPS = 30;
    private final int FRAME_PERIOD = 1000 / MAX_FPS;
    private final int NUM_OF_SECS_TO_PAN_VIEW = 10;

    private ImageViewPanning mPanningImage;
    private PanningLoop mPanningLoop;
    private int mCurrentFrame;
    private boolean mIsPanningRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button mStartButton = (Button) findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, TimeZoneActivity.class);
                startActivity(intent);
                }
        });

        mPanningImage = (ImageViewPanning) findViewById(R.id.backgroundPanning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            mCurrentFrame = savedInstanceState.getInt(currentFrameKey);
            mIsPanningRight = savedInstanceState.getBoolean(isPanningRightKey);
        } else {
            mCurrentFrame = NUM_OF_SECS_TO_PAN_VIEW * MAX_FPS / 2;
            mIsPanningRight = true;
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mPanningLoop = new PanningLoop();
        mPanningLoop.execute();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPanningLoop != null) {
            mPanningLoop.cancel(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(currentFrameKey, mPanningImage.getCurrentImageFrame());
        outState.putBoolean(isPanningRightKey, mPanningImage.isPanningRight());
    }

    private class PanningLoop extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while (!mPanningLoop.isCancelled()) {
                try {
                    Thread.sleep(FRAME_PERIOD);
                } catch (InterruptedException e) {}

                publishProgress();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mPanningImage.moveFrame();
            mPanningImage.invalidate();
        }

        @Override
        protected void onPreExecute() {
            mPanningImage.framesToLookAtView(NUM_OF_SECS_TO_PAN_VIEW * MAX_FPS);
            mPanningImage.setCurrentFrame(mCurrentFrame);
            mPanningImage.setDirectionRight(mIsPanningRight);
        }
    }
}
