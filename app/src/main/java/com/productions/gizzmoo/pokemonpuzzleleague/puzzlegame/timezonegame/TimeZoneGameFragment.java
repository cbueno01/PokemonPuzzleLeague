package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.timezonegame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.app.DialogFragment;

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block;
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameDialogFragment;
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameFragment;
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus;

import java.util.Random;

import static com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.NUM_OF_COLS;
import static com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.NUM_OF_ROWS;

public class TimeZoneGameFragment extends GameFragment<TimeZoneGameLoop> implements TimeZoneGameLoop.TimeZoneGameLoopListener {

    private final static String linesToWinKey = "LINES_TO_WIN_KEY";
    private final static String linesUntilSpeedIncreaseKey = "LINES_UNTIL_SPEED_INCREASE_KEY";
    private final static String matchAnimationCountKey = "MATCH_ANIMATION_COUNT_KEY";
    private final static String gameSpeedKey = "GAME_SPEED_KEY";
    private final static String frameCountKey = "FRAME_COUNT_KEY";
    private final static String frameCountInWarningKey = "FRAME_COUNT_IN_WARNING_KEY";
    private final static String newRowKey = "NEW_BLOCK_ROW_KEY";

    private final static int NUMBER_OF_BLOCKS_MULTIPLIER = 5;

    private Random rand;

    private TimeZoneFragmentInterface mListener;
    private int mTempNumOfLinesLeft;
    private int mTempGameSpeed;
    private int mTempBlockMatchAnimating;
    private int mTempLinesUntilSpeedIncrease;
    private int mTempCurrentFrameCount;
    private int mTempFrameCountInWarning;
    private Block[] mTempNewBlockRow = new Block[NUM_OF_COLS];
    private GameStatus mPrevGameStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rand = new Random();

        if (savedInstanceState != null) {
            mTempBlockMatchAnimating = savedInstanceState.getInt(matchAnimationCountKey);
            mTempNumOfLinesLeft = savedInstanceState.getInt(linesToWinKey);
            mTempGameSpeed = savedInstanceState.getInt(gameSpeedKey);
            mTempLinesUntilSpeedIncrease = savedInstanceState.getInt(linesUntilSpeedIncreaseKey);
            mTempCurrentFrameCount = savedInstanceState.getInt(frameCountKey);
            mTempFrameCountInWarning = savedInstanceState.getInt(frameCountInWarningKey);
            mTempNewBlockRow = (Block[])savedInstanceState.getSerializable(newRowKey);
        } else {
            mTempBlockMatchAnimating = 0;
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            mTempNumOfLinesLeft = settings.getInt("pref_lines_key", 15) + NUM_OF_ROWS;
            mTempGameSpeed = settings.getInt("pref_speed_key", 10);
            mTempLinesUntilSpeedIncrease = -1;      // -1 so game loop knows to get the full amount of rows needed
            mTempCurrentFrameCount = 0;
            mTempFrameCountInWarning = 0;
            TimeZoneGameLoop.createNewRowBlocks(mTempNewBlockRow, new Random());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mTempNumOfLinesLeft = mGameLoop.getNumOfLinesLeft();
        mTempGameSpeed = mGameLoop.getGameSpeed();
        mTempBlockMatchAnimating = mGameLoop.getNumOfAnimatingBlocks();
        mTempLinesUntilSpeedIncrease = mGameLoop.getLinesUntilSpeedIncrease();
        mTempCurrentFrameCount = mGameLoop.getCurrentFrameCount();
        mTempFrameCountInWarning = mGameLoop.getFrameCountInWarning();
        mTempNewBlockRow = mGameLoop.getNewRow();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPrevGameStatus = mGameLoop.getGameStatus();
        mGameLoop.setTimeZoneGameProperties(mTempNewBlockRow, mTempBlockMatchAnimating, mTempLinesUntilSpeedIncrease, mTempCurrentFrameCount, mTempFrameCountInWarning);
        drawLineIfNeeded(mTempNumOfLinesLeft);
        mBoardView.setGameSpeed(mGameLoop.getNumOfFramesForCurrentLevel());
        mBoardView.startAnimatingUp();
        mBoardView.setNewRow(mGameLoop.getNewRow());
        mBoardView.setRisingAnimationCount(mTempCurrentFrameCount);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(matchAnimationCountKey, mGameLoop.getNumOfAnimatingBlocks());
        outState.putInt(linesToWinKey, mGameLoop.getNumOfLinesLeft());
        outState.putInt(gameSpeedKey, mGameLoop.getGameSpeed());
        outState.putInt(linesUntilSpeedIncreaseKey, mGameLoop.getLinesUntilSpeedIncrease());
        outState.putInt(frameCountKey, mGameLoop.getCurrentFrameCount());
        outState.putInt(frameCountInWarningKey, mGameLoop.getFrameCountInWarning());
        outState.putSerializable(newRowKey, mGameLoop.getNewRow());
    }


    @Override
    public void boardSwipedUp() {
        if (mGameLoop != null &&  !mGameLoop.isABlockAnimating()) {
            mGameLoop.addNewRow();
        }
    }

    @Override
    public void blockFinishedMatchAnimation(int row, int column) {
        super.blockFinishedMatchAnimation(row, column);
        mGameLoop.aBlockFinishedAnimating();

        if (!mGameLoop.isABlockAnimating()) {
            mBoardView.startAnimatingUp();
        }
    }

    @Override
    protected TimeZoneGameLoop createGameLoop() {
        return new TimeZoneGameLoop(getGameBoard(), mTempNumOfLinesLeft, mTempGameSpeed);
    }

    @Override
    public void gameStatusChanged(GameStatus newStatus) {
        // Check if game status actually changed
        if (mPrevGameStatus == newStatus || newStatus == GameStatus.Stopped || mPrevGameStatus == GameStatus.Stopped) {
            return;
        }

        mBoardView.statusChanged(newStatus);

        if (mPrevGameStatus == GameStatus.Warning && (newStatus == GameStatus.Running || newStatus == GameStatus.Panic)) {
            mGameLoop.moveBlockSwitcherFromTop();
        }

        if (mListener != null) {
            if (newStatus == GameStatus.Running && mPrevGameStatus != GameStatus.Running) {
                mListener.changeSong(false);
            } else if ((newStatus == GameStatus.Warning || newStatus == GameStatus.Panic) && (mPrevGameStatus != GameStatus.Warning && mPrevGameStatus != GameStatus.Panic)) {
                mListener.changeSong(true);
            }
        }

        mPrevGameStatus = newStatus;
    }

    @Override
    public void numberOfBlocksMatched() {
        mBoardView.stopAnimatingUp();
    }

    @Override
    public void gameFinished(boolean didWin) {
        DialogFragment newFragment = GameDialogFragment.newInstance(didWin);
        newFragment.show(getActivity().getFragmentManager(), "postDialog");
    }

    @Override
    public void newBlockWasAdded(int numOfLinesLeft) {
        drawLineIfNeeded(numOfLinesLeft);
        mBoardView.resetRisingAnimationCount();
        mBoardView.setNewRow(mGameLoop.getNewRow());
        mBoardView.setGameSpeed(mGameLoop.getNumOfFramesForCurrentLevel());
    }

    @Override
    public void updateGameTimeAndSpeed(long timeInMilli, int gameSpeed) {
        if (mListener != null) {
            mListener.updateGameTimeAndSpeed(timeInMilli, gameSpeed);
        }
    }

    private Block[][] getGameBoard() {
        Block[][] grid = new Block[NUM_OF_ROWS][NUM_OF_COLS];
        int[] columnCounter = new int[NUM_OF_COLS];

        int numberNumberOfBLocksLeft = NUM_OF_COLS * NUMBER_OF_BLOCKS_MULTIPLIER;

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

    public void drawLineIfNeeded(int numOfLines) {
        if (numOfLines <= NUM_OF_ROWS) {
            mBoardView.winLineAt(numOfLines);
        }
    }

    public void setFragmentListener (TimeZoneFragmentInterface listener) {
        mListener = listener;
    }

    public interface TimeZoneFragmentInterface {
        void changeSong(boolean isPanic);
        void updateGameTimeAndSpeed(long timeInMilli, int gameSpeed);
    }
}
