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
    private final static String didWinKey = "DID_WIN_KEY";
    private final static String linesToWinKey = "LINES_TO_WIN_KEY";
    private final static String currentFrameKey = "CURRENT_FRAME_KEY";
    private final static String statusKey = "STATUS_KEY";
    private final static String matchingBlocksKey = "MATCHING_BLOCKS_KEY";
    private final static String trackPositionKey = "TRACK_POSITION_KEY";
    private final static String gameSpeedLevelKey = "SPEED_LEVEL_KEY";
    private final static String linesToSpeedIncreaseKey = "LINES_FOR_SPEED_INCREASE_KEY";

    private int POKEMON_SOUND_PRIORITY = 4;
    private int TRAINER_SOUND_PRIORITY = 3;
    private int POP_SOUND_PRIORITY = 2;
    private int SWITCH_SOUND_PRIORITY = 1;
    private int MOVE_SOUND_PRIORITY = 0;

    private final int MAX_FPS = 30;
    private final int FRAME_PERIOD = 1000 / MAX_FPS;
    private final int NUM_OF_COLS = 6;
    private final int NUM_OF_ROWS = 12;

    private final int[] popSoundResources = {R.raw.pop_sound_1, R.raw.pop_sound_2, R.raw.pop_sound_3, R.raw.pop_sound_4};
    private final int[] pokemonSoundResources = {R.raw.pikachu_sound_1, R.raw.pikachu_sound_2, R.raw.pikachu_sound_3, R.raw.pikachu_sound_4};

    private PuzzleBoardView mBoardView;
    private TextView mTimeView;
    private TextView mSpeedView;

    private SwitchBlocks mBlockSwitcher;
    private Block[][] mGrid;
    private GameLoop mGameLoop;
    private ArrayList<Block> mBlockMatch;
    private int mBlockMatchAnimating;
    private GameStatus mStatus;

    private Block[] mNewRow;

    private boolean mNeedNewRowUpdate;
    private long mStartTime;

    private long mElapsedTime;
    private int mCurrentFrameCount;
    private boolean mDidWin;
    private int mGameSpeedLevel;
    private int mGameSpeedFrames;

    int mNumOfLinesLeft;
    int mLinesToNewLevel;

    private MusicService mMusicService;
    private boolean mIsMusicServiceBound;
    private int mTrackPosition;

    private SoundPool mSoundPool;
    private boolean mLoadedSoundPool;
    private int mTrainerSoundID;
    private int mMoveSoundID;
    private int mSwitchSoundID;
    private int[] mPopSoundIDs = new int[4];
    private int[] mPokemonSoundIDs = new int[4];

    private int comboCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_zone_layout);
        mBoardView = findViewById(R.id.puzzleBoard);
        mTimeView = findViewById(R.id.timerValue);
        mSpeedView = findViewById(R.id.speedValue);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (savedInstanceState != null) {
            mBlockMatchAnimating = savedInstanceState.getInt(matchAnimationCountKey);
            mGrid = (Block[][]) savedInstanceState.getSerializable(gridKey);
            mBlockSwitcher = (SwitchBlocks) savedInstanceState.getSerializable(blockSwitcherKey);
            mNeedNewRowUpdate = savedInstanceState.getBoolean(needRowUpdateKey);
            mElapsedTime = savedInstanceState.getLong(elapsedTimeKey);
            mStartTime = savedInstanceState.getLong(startTimeKey);
            mDidWin = savedInstanceState.getBoolean(didWinKey);
            mNumOfLinesLeft = savedInstanceState.getInt(linesToWinKey);
            mCurrentFrameCount = savedInstanceState.getInt(currentFrameKey);
            mStatus = (GameStatus) savedInstanceState.getSerializable(statusKey);
            mNewRow = (Block[]) savedInstanceState.getSerializable(newBlocksKey);
            mBlockMatch = (ArrayList<Block>) savedInstanceState.getSerializable(matchingBlocksKey);
            mTrackPosition = savedInstanceState.getInt(trackPositionKey);
            mGameSpeedLevel = savedInstanceState.getInt(gameSpeedLevelKey);
            mLinesToNewLevel = savedInstanceState.getInt(linesToSpeedIncreaseKey);

            mBoardView.setNewRow(mNewRow);
            mBoardView.setRisingAnimationCount(mCurrentFrameCount);
            mBoardView.winLineAt(mNumOfLinesLeft);
        } else {
            mBlockMatchAnimating = 0;
            mGrid = startNewGrid();
            mBlockSwitcher = new SwitchBlocks(2, 9, 3, 9);
            mNeedNewRowUpdate = true;
            mElapsedTime = 0;
            mStartTime = System.nanoTime() / 1000000;
            mDidWin = false;
            mNumOfLinesLeft = settings.getInt("pref_lines_key", 15) + NUM_OF_ROWS;
            mCurrentFrameCount = 0;
            mStatus = GameStatus.Running;
            mNewRow = new Block[NUM_OF_COLS];
            mBlockMatch = new ArrayList<>();
            mTrackPosition = 0;
            mGameSpeedLevel = settings.getInt("pref_speed_key", 10);
            mLinesToNewLevel =  getNumOfLinesForLevel();
        }

        mGameSpeedFrames = normalizeSpeedToFrames(mGameSpeedLevel);
        mBoardView.setGameSpeed(mGameSpeedFrames);
        mIsMusicServiceBound = false;
        mLoadedSoundPool = false;
        mBoardView.setGrid(mGrid, mBlockSwitcher);
        mSoundPool = new SoundPool(2, STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                mLoadedSoundPool = true;
            }
        });


        int currentTrainer = settings.getInt("pref_trainer_key", 0);
        mTrainerSoundID = mSoundPool.load(this, TrainerResources.getTrainerComboSound(currentTrainer), 1);
        mSwitchSoundID = mSoundPool.load(this, R.raw.switch_sound, 1);
        mMoveSoundID = mSoundPool.load(this, R.raw.move_sound, 1);

        for (int i = 0; i < pokemonSoundResources.length; i++) {
            mPokemonSoundIDs[i] = mSoundPool.load(this, pokemonSoundResources[i], 1);
        }

        for (int i = 0; i < popSoundResources.length; i++) {
            mPopSoundIDs[i] = mSoundPool.load(this, popSoundResources[i], 1);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(matchAnimationCountKey, mBlockMatchAnimating);
        outState.putSerializable(gridKey, mGrid);
        outState.putSerializable(blockSwitcherKey, mBlockSwitcher);
        outState.putBoolean(needRowUpdateKey, mNeedNewRowUpdate);
        outState.putSerializable(newBlocksKey, mNewRow);
        outState.putLong(elapsedTimeKey, mElapsedTime);
        outState.putLong(startTimeKey, mStartTime);
        outState.putBoolean(didWinKey, mDidWin);
        outState.putInt(linesToWinKey, mNumOfLinesLeft);
        outState.putInt(currentFrameKey, mCurrentFrameCount);
        outState.putSerializable(statusKey, mStatus);
        outState.putSerializable(matchingBlocksKey, mBlockMatch);
        outState.putInt(trackPositionKey, mTrackPosition);
        outState.putInt(gameSpeedLevelKey, mGameSpeedLevel);
        outState.putInt(linesToSpeedIncreaseKey, mLinesToNewLevel);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        mBoardView.setBoardListener(new BoardListener() {
            @Override
            public void switchBlock(Point switcherLeftBlock) {
                swapBlocks(switcherLeftBlock.x, switcherLeftBlock.y, switcherLeftBlock.x + 1, switcherLeftBlock.y);

                if (mLoadedSoundPool && mSwitchSoundID != 0) {
                    mSoundPool.play(mSwitchSoundID, 1, 1, SWITCH_SOUND_PRIORITY, 0, 1);
                }
            }

            @Override
            public void addNewRow() {
                if (mGameLoop != null && mBlockMatchAnimating == 0) {
                    mGameLoop.addNewRow();
                }
            }

            @Override
            public void blockFinishedMatchAnimation(int row, int column) {
                int rowToUpdate = row - 1;
                while (rowToUpdate >= 0 && !mGrid[rowToUpdate][column].isBlockEmpty()) {
                    if (!mGrid[rowToUpdate][column].hasMatched && !mGrid[rowToUpdate][column].isAnimatingDown && !mGrid[rowToUpdate][column].isBeingSwitched) {
                        mGrid[rowToUpdate][column].canCombo = true;
                    }
                    rowToUpdate--;
                }

                mBlockMatchAnimating--;

                if (mBlockMatchAnimating == 0) {
                    mBoardView.startAnimatingUp();
                }
            }

            @Override
            public void needsBlockSwap(Block b1, Block b2) {
                Point b1Point = b1.getCoords();
                Point b2Point = b2.getCoords();
                swapBlocks(b1Point.x, b1Point.y, b2Point.x, b2Point.y);
            }

            @Override
            public void switchBlockMoved() {
                if (mLoadedSoundPool && mMoveSoundID != 0) {
                    mSoundPool.play(mMoveSoundID, 1, 1, MOVE_SOUND_PRIORITY, 0, 1);
                }
            }

            @Override
            public void blockIsPopping(int position, int total) {
                int soundID = getPopSoundID(position, total);
                if (mLoadedSoundPool && soundID != 0) {
                    mSoundPool.play(soundID, 1, 1, POP_SOUND_PRIORITY, 0, 1);
                }
            }
        });

        bindService(new Intent(this, MusicService.class), mConnection, Context.BIND_AUTO_CREATE);

        startGame();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGameLoop != null) {
            mGameLoop.cancel(true);
        }

        if (mIsMusicServiceBound) {
            mTrackPosition = mMusicService.stopMusic();
            unbindService(mConnection);
            mIsMusicServiceBound = false;
        }
    }

    private Block[][] startNewGrid() {
        Block[][] grid = new Block[NUM_OF_ROWS][NUM_OF_COLS];
        int[] columnCounter = new int[NUM_OF_COLS];

        int numberNumberOfBLocksLeft = NUM_OF_COLS * 5;
        Random rand = new Random();

        // Populate first 3 rows
        for (int i = NUM_OF_ROWS - 1; i > NUM_OF_ROWS - 4; i--) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                grid[i][j] = new Block(rand.nextInt(7) + 1, j, i);
                columnCounter[j]++;
                numberNumberOfBLocksLeft--;
            }
        }

        int x = 0;
        while(x < numberNumberOfBLocksLeft) {
            int position = rand.nextInt(NUM_OF_COLS);
            if (columnCounter[position] < NUM_OF_ROWS - 1) {
                grid[NUM_OF_ROWS - 1 - columnCounter[position]][position] = new Block(rand.nextInt(7) + 1, position, NUM_OF_ROWS - 1 - columnCounter[position]);
                columnCounter[position]++;
                x++;
            }
        }

        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                if (grid[i][j] == null) {
                    grid[i][j] = new Block(0, j, i);
                }
            }
        }

        return grid;
    }

    // Gets called on every frame
    private void applyGravity() {
        for (int y = NUM_OF_ROWS - 2; y >= 0; y--) {
            for (int x = 0; x < NUM_OF_COLS; x++) {
                if (!mGrid[y][x].isBlockEmpty() && !mGrid[y][x].isBeingSwitched && !mGrid[y][x].isAnimatingDown && !mGrid[y][x].hasMatched) {
                    if (((mGrid[y + 1][x].isBlockEmpty()) || (mGrid[y + 1][x].isAnimatingDown)) && !mGrid[y + 1][x].isBeingSwitched && !mGrid[y + 1][x].hasMatched) {
                        mGrid[y][x].startFaillingAnimation();
                        swapBlocks(x, y, x, y + 1);
                    }
                }
            }
        }
    }

    private void checkToResetCombo() {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                if (mGrid[i][j].canCombo || mGrid[i][j].removeComboFlagOnNextFrame) {
                    return;
                }
            }
        }

        comboCount = 0;
    }

    private void checkForMatches() {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                if (!mGrid[i][j].hasMatched && !mGrid[i][j].isBlockEmpty() && !mGrid[i][j].isAnimatingDown && !mGrid[i][j].isBeingSwitched) {
                    mBlockMatch.addAll(checkForMatchWithDirection(i, j, 0));
                    mBlockMatch.addAll(checkForMatchWithDirection(i, j, 1));
                }
            }
        }

        if (!mBlockMatch.isEmpty()) {
            Collections.sort(mBlockMatch, new Comparator<Block>() {
                @Override
                public int compare(Block block, Block t1) {
                    Point p1 = block.getCoords();
                    Point p2 = t1.getCoords();

                    if (p1.y == p2.y) {
                        return p1.x - p2.x;
                    } else {
                        return p1.y - p2.y;
                    }
                }
            });

            removeDuplicateBlocks();
            startMatchAnimation();

            boolean playPokemonSound = shouldPlayPokemonSound();
            if (playPokemonSound) {
                int pokemonSoundID = getPokemonSoundID();
                if (mLoadedSoundPool && pokemonSoundID != 0) {
                    mSoundPool.play(pokemonSoundID, 1, 1, POKEMON_SOUND_PRIORITY, 0, 1);
                }
            }


            if (mBlockMatch.size() > 3 && !playPokemonSound) {
                if (mLoadedSoundPool && mTrainerSoundID != 0) {
                    mSoundPool.play(mTrainerSoundID, 1, 1, TRAINER_SOUND_PRIORITY, 0, 1);
                }
            }

            mBlockMatch.clear();
        }
    }

    private void removeDuplicateBlocks() {
        ArrayList<Block> list = new ArrayList<>();

        Block prevBlock = null;

        for (Block b : mBlockMatch) {
            if (prevBlock != b) {
                list.add(b);
            }

            prevBlock = b;
        }

        mBlockMatch.clear();
        mBlockMatch.addAll(list);
    }

    private boolean shouldPlayPokemonSound() {
        for (Block b : mBlockMatch) {
            if (b.canCombo) {
                comboCount++;
                return true;
            }
        }

        return false;
    }

    private int getPokemonSoundID() {
        if (comboCount <= 0) {
            return 0;
        } else if (comboCount <= 2) {
            return mPokemonSoundIDs[0];
        } else if (comboCount == 3) {
            return mPokemonSoundIDs[1];
        } else if (comboCount == 4) {
            return mPokemonSoundIDs[2];
        } else {
            return mPokemonSoundIDs[3];
        }
    }

    private void startMatchAnimation() {
        int matchSize =  mBlockMatch.size();
        for (int i = 0; i < matchSize; i++) {
            Block b = mBlockMatch.get(i);
            b.hasMatched = true;
            b.delayMatchAnimationCount = i * ANIMATION_MATCH_POP_FRAMES_NEEDED;
            b.matchInvertedAnimationCount = ANIMATION_MATCH_INVERT_FRAMES_NEEDED;
            b.matchPopAnimationCount = 0;
            b.clearMatchCount = (mBlockMatch.size() - 1) * ANIMATION_MATCH_POP_FRAMES_NEEDED - b.delayMatchAnimationCount + ANIMATION_MATCH_INVERT_FRAMES_NEEDED;
            b.popPosition = i;
            b.matchTotalCount = matchSize;
            mBlockMatchAnimating++;
            mBoardView.stopAnimatingUp();
        }
    }

    // 0 - Up/Down
    // 1 - Left/Right

    private ArrayList<Block> checkForMatchWithDirection(int i, int j, int direction) {
        ArrayList<Block> tempList = new ArrayList<>();
        tempList.add(mGrid[i][j]);
        switch(direction) {
            case 0:
                int pos = i - 1;
                // Check down
                while (pos >= 0 && (mGrid[i][j].getBlockType() == mGrid[pos][j].getBlockType()) && !mGrid[pos][j].isAnimatingDown && !mGrid[pos][j].hasMatched && !mGrid[pos][j].isBeingSwitched) {
                    tempList.add(mGrid[pos][j]);
                    pos--;
                }

                pos = i + 1;
                // Check up
                while (pos < NUM_OF_ROWS && (mGrid[i][j].getBlockType() == mGrid[pos][j].getBlockType()) && !mGrid[pos][j].isAnimatingDown  && !mGrid[pos][j].hasMatched  && !mGrid[pos][j].isBeingSwitched) {
                    tempList.add(mGrid[pos][j]);
                    pos++;
                }

                break;
            case 1:
                pos = j - 1;
                // Check right
                while (pos >= 0 && (mGrid[i][j].getBlockType() == mGrid[i][pos].getBlockType()) && !mGrid[i][pos].isAnimatingDown  && !mGrid[i][pos].hasMatched && !mGrid[i][pos].isBeingSwitched) {
                    tempList.add(mGrid[i][pos]);
                    pos--;
                }

                pos = j + 1;
                // Check left
                while (pos < NUM_OF_COLS && (mGrid[i][j].getBlockType() == mGrid[i][pos].getBlockType()) && !mGrid[i][pos].isAnimatingDown  && !mGrid[i][pos].hasMatched  && !mGrid[i][pos].isBeingSwitched) {
                    tempList.add(mGrid[i][pos]);
                    pos++;
                }

                break;
        }

        if (tempList.size() < 3) {
            tempList.clear();
        }

        return tempList;
    }

    private void swapBlocks(int x1, int y1, int x2, int y2) {
        Block blockHolder = mGrid[y1][x1];
        mGrid[y1][x1] = mGrid[y2][x2];
        mGrid[y2][x2] = blockHolder;

        mGrid[y1][x1].changeCoords(x1,y1);
        mGrid[y2][x2].changeCoords(x2,y2);
    }

    private boolean doesRowContainBlock(int row) {
        for (int i = 0; i < NUM_OF_COLS; i++) {
            if (!mGrid[row][i].isBlockEmpty()) {
                return true;
            }
        }

        return false;
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

    private class GameLoop extends AsyncTask<Void, Void, Void> {
        private Random rand;

        @Override
        protected Void doInBackground(Void... voids) {
            while(mStatus != GameStatus.Stopped && !mGameLoop.isCancelled()) {
                mElapsedTime = (System.nanoTime() / 1000000) - mStartTime;

                try {
                    Thread.sleep(FRAME_PERIOD);
                } catch (InterruptedException e) {}

                if (mBlockMatchAnimating <= 0) {
                    mCurrentFrameCount++;
                }

                if (mNeedNewRowUpdate) {
                    updateNewRow();
                    mNeedNewRowUpdate = false;
                }

                applyGravity();
                checkForMatches();
                checkToResetCombo();

                GameStatus prevStatus = mStatus;

                if (doesRowContainBlock(0)) {
                    changeGameStatus(GameStatus.Warning);
                } else if (doesRowContainBlock(3)) {
                    changeGameStatus(GameStatus.Panic);

                    if (prevStatus == GameStatus.Running) {
                        changeSong(GameStatus.Panic);
                    }
                } else {
                    changeGameStatus(GameStatus.Running);

                    if (prevStatus != GameStatus.Running) {
                        changeSong(GameStatus.Running);
                    }
                }

                if (prevStatus == GameStatus.Warning && (mStatus == GameStatus.Running || mStatus == GameStatus.Panic) && mBlockSwitcher.isAtTop()) {
                    mBlockSwitcher.moveDown();
                }

                if ((mGameSpeedFrames < mCurrentFrameCount) && mBlockMatchAnimating == 0) {
                    addNewRow();
                }

                checkIfGameWon();

                publishProgress();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            rand = new Random();
            changeGameStatus(mStatus);
            if (mNumOfLinesLeft <= NUM_OF_ROWS) {
                mBoardView.winLineAt(mNumOfLinesLeft);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mTimeView.setText(String.format(Locale.US, "%04d", (int)(mElapsedTime / 1000)));
            mSpeedView.setText(String.format(Locale.US, "%02d", mGameSpeedLevel));
            mBoardView.invalidate();
        }

        @Override
        protected void onPostExecute(Void result) {
            DialogFragment newFragment = GameDialogFragment.newInstance(mDidWin);
            newFragment.show(getSupportFragmentManager(), "postDialog");
        }

        private void updateNewRow() {
            for (int i = 0; i < NUM_OF_COLS; i++) {
                mNewRow[i] = new Block(rand.nextInt(7) + 1, i, NUM_OF_ROWS - 1);
            }
            mBoardView.setNewRow(mNewRow);
        }

        private void addNewRow() {
            if (doesRowContainBlock(0)) {
                changeGameStatus(GameStatus.Stopped);
                return;
            }

            for (int i = 1; i < NUM_OF_ROWS; i++) {
                for (int j = 0; j < NUM_OF_COLS; j++) {
                    swapBlocks(j, i, j, i-1);
                }
            }

            for (int i = 0; i < NUM_OF_COLS; i++) {
                mGrid[NUM_OF_ROWS - 1][i] = mNewRow[i];
            }

            if (!mBlockSwitcher.isAtTop() || doesRowContainBlock(0)) {
                mBlockSwitcher.moveUp();
            }

            mNeedNewRowUpdate = true;
            mBoardView.newRowAdded();
            mCurrentFrameCount = 0;

            mNumOfLinesLeft--;
            if (mNumOfLinesLeft <= NUM_OF_ROWS) {
                mBoardView.winLineAt(mNumOfLinesLeft);
            }

            if (mGameSpeedLevel < 50) {
                if (mLinesToNewLevel <= 0) {
                    mGameSpeedLevel++;
                    mBoardView.setGameSpeed(normalizeSpeedToFrames(mGameSpeedLevel));
                    mLinesToNewLevel = getNumOfLinesForLevel();
                } else {
                    mLinesToNewLevel--;
                }
            }
        }

        private void changeGameStatus(GameStatus newStatus) {
            mStatus = newStatus;
            mBoardView.statusChanged(newStatus);
        }

        private void checkIfGameWon() {
            if (mNumOfLinesLeft > 11) {
                return;
            }

            for (int i = 0; i < mNumOfLinesLeft; i++) {
                for (int j = 0; j < NUM_OF_COLS; j++) {
                    if (!mGrid[i][j].isBlockEmpty()) {
                        return;
                    }
                }
            }

            mDidWin = true;
            changeGameStatus(GameStatus.Stopped);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mMusicService = ((MusicService.ServiceBinder) binder).getService();
            boolean isPanicMode = mStatus != GameStatus.Running;
            mMusicService.startMusic(mTrackPosition, isPanicMode);
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

    private int getPopSoundID(int pos, int total) {
        int index = (int)(((float)pos / total) * 4);
        return mPopSoundIDs[index];
    }

    private void startGame() {
        mGameLoop = new GameLoop();
        mGameLoop.execute();
    }

    @Override
    public void onGameEndingDialogResponse() {
        finish();
    }

    enum GameStatus {
        Stopped,
        Running,
        Warning,
        Panic
    }
}
