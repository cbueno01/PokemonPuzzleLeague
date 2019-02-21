package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

import static android.media.AudioManager.STREAM_MUSIC;
import static com.productions.gizzmoo.pokemonpuzzleleague.PuzzleBoardView.ANIMATION_MATCH_INVERT_FRAMES_NEEDED;
import static com.productions.gizzmoo.pokemonpuzzleleague.PuzzleBoardView.ANIMATION_MATCH_POP_FRAMES_NEEDED;

public abstract class GameLoop extends AsyncTask<Void, Void, Void> {
    public static final int NUM_OF_COLS = 6;
    public static final int NUM_OF_ROWS = 12;

    private final int MAX_FPS = 30;
    private final int FRAME_PERIOD = 1000 / MAX_FPS;

    private long mStartTime;
    private long mElapsedTime;

    protected Block[][] mGrid;
    protected GameLoopListener mListener;
    private ArrayList<Block> mBlockMatch;
    protected SwitchBlocks mBlockSwitcher;
    private GameStatus mStatus;
    protected boolean mDidWin;
//    private Block[] mNewRow;

    private int comboCount;


    public GameLoop(Block[][] grid) {
        mDidWin = false;
        mGrid = grid;
        mBlockMatch = new ArrayList<>();
        mBlockSwitcher = new SwitchBlocks(2, 9, 3, 9);
        comboCount = 0;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while(mStatus != GameStatus.Stopped && !isCancelled()) {
            mElapsedTime = (System.nanoTime() / 1000000) - mStartTime;

            try {
                Thread.sleep(FRAME_PERIOD);
            } catch (InterruptedException e) {}

//            if (mBlockMatchAnimating <= 0) {
//                mCurrentFrameCount++;
//            }

//            if (mNeedNewRowUpdate) {
//                updateNewRow();
//                mNeedNewRowUpdate = false;
//            }

            applyGravity();
            checkForMatches();
            checkToResetCombo();
            checkIfGameWon();
            updateGameStatusIfNeeded();

//            if (prevStatus == GameStatus.Warning && (mStatus == GameStatus.Running || mStatus == GameStatus.Panic) && mBlockSwitcher.isAtTop()) {
//                mBlockSwitcher.moveDown();
//            }

//            if ((mGameSpeedFrames < mCurrentFrameCount) && mBlockMatchAnimating == 0) {
//                addNewRow();
//            }

            publishProgress();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        mElapsedTime = 0;
        mStartTime = System.nanoTime() / 1000000;
        changeGameStatus(mStatus);
        mBlockMatch.clear();
//        if (mNumOfLinesLeft <= NUM_OF_ROWS) {
//            mBoardView.winLineAt(mNumOfLinesLeft);
//        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        if (mListener != null) {
            mListener.updateBoardView();
        }
//        mTimeView.setText(String.format(Locale.US, "%04d", (int)(mElapsedTime / 1000)));
//        mSpeedView.setText(String.format(Locale.US, "%02d", mGameSpeedLevel));
    }

    @Override
    protected void onPostExecute(Void result) {
//        DialogFragment newFragment = GameDialogFragment.newInstance(mDidWin);
//        newFragment.show(getSupportFragmentManager(), "postDialog");
    }

    public void startGame() {
        this.execute();
    }

    // Gets called on every frame
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
            playSoundIfNecessary();

            mBlockMatch.clear();
        }
    }

    private void updateGameStatusIfNeeded() {
//        GameStatus prevStatus = mStatus;

        if (mDidWin) {
            changeGameStatus(GameStatus.Stopped);
        }
        else if (doesRowContainBlock(0)) {
            if (mStatus != GameStatus.Warning) {
                changeGameStatus(GameStatus.Warning);
            }
        } else if (doesRowContainBlock(3)) {
            if (mStatus != GameStatus.Panic) {
                changeGameStatus(GameStatus.Panic);
            }

//                if (prevStatus == GameStatus.Running) {
//                    changeSong(GameStatus.Panic);
//                }
        } else {
            if (mStatus != GameStatus.Running) {
                changeGameStatus(GameStatus.Running);
            }

//                if (prevStatus != GameStatus.Running) {
//                    changeSong(GameStatus.Running);
//                }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    private void playSoundIfNecessary() {
        boolean playPokemonSound = shouldPlayPokemonSound();
        if (playPokemonSound) {
           if (mListener != null) {
               mListener.playPokemonSound(comboCount);
           }
        }


        if (mBlockMatch.size() > 3 && !playPokemonSound) {
            if (mListener != null) {
                mListener.playTrainerSound(false);
            }
        }
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
//            mBlockMatchAnimating++;
//            mBoardView.stopAnimatingUp();
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


//    private void updateNewRow() {
//        for (int i = 0; i < NUM_OF_COLS; i++) {
//            mNewRow[i] = new Block(rand.nextInt(7) + 1, i, NUM_OF_ROWS - 1);
//        }
//        mBoardView.setNewRow(mNewRow);
//    }

//    private void addNewRow() {
//        if (doesRowContainBlock(0)) {
//            changeGameStatus(GameStatus.Stopped);
//            return;
//        }
//
//        for (int i = 1; i < NUM_OF_ROWS; i++) {
//            for (int j = 0; j < NUM_OF_COLS; j++) {
//                swapBlocks(j, i, j, i-1);
//            }
//        }
//
//        for (int i = 0; i < NUM_OF_COLS; i++) {
//            mGrid[NUM_OF_ROWS - 1][i] = mNewRow[i];
//        }
//
//        if (!mBlockSwitcher.isAtTop() || doesRowContainBlock(0)) {
//            mBlockSwitcher.moveUp();
//        }
//
//        mNeedNewRowUpdate = true;
//        mBoardView.newRowAdded();
//        mCurrentFrameCount = 0;
//
//        mNumOfLinesLeft--;
//        if (mNumOfLinesLeft <= NUM_OF_ROWS) {
//            mBoardView.winLineAt(mNumOfLinesLeft);
//        }
//
//        if (mGameSpeedLevel < 50) {
//            if (mLinesToNewLevel <= 0) {
//                mGameSpeedLevel++;
//                mBoardView.setGameSpeed(normalizeSpeedToFrames(mGameSpeedLevel));
//                mLinesToNewLevel = getNumOfLinesForLevel();
//            } else {
//                mLinesToNewLevel--;
//            }
//        }
//    }

    protected void changeGameStatus(GameStatus newStatus) {
        mStatus = newStatus;
//        mBoardView.statusChanged(newStatus);
    }

    protected abstract void checkIfGameWon();

    private boolean doesRowContainBlock(int row) {
        for (int i = 0; i < NUM_OF_COLS; i++) {
            if (!mGrid[row][i].isBlockEmpty()) {
                return true;
            }
        }

        return false;
    }

    public void swapBlocks(int x1, int y1, int x2, int y2) {
        Block blockHolder = mGrid[y1][x1];
        mGrid[y1][x1] = mGrid[y2][x2];
        mGrid[y2][x2] = blockHolder;

        mGrid[y1][x1].changeCoords(x1,y1);
        mGrid[y2][x2].changeCoords(x2,y2);
    }

    public void blockFinishedMatchAnimation(int row, int column) {
        int rowToUpdate = row - 1;
        while (rowToUpdate >= 0 && !mGrid[rowToUpdate][column].isBlockEmpty()) {
            if (!mGrid[rowToUpdate][column].hasMatched && !mGrid[rowToUpdate][column].isAnimatingDown && !mGrid[rowToUpdate][column].isBeingSwitched) {
                mGrid[rowToUpdate][column].canCombo = true;
            }
            rowToUpdate--;
        }
    }

    public GameStatus getGameStatus() {
        return mStatus;
    }

    public Block[][] getGameGrid() {
        return  mGrid;
    }

    public SwitchBlocks getBlockSwitcher() {
        return mBlockSwitcher;
    }

    public void setGameLoopListener(GameLoopListener listener) {
        mListener = listener;
    }

    public interface GameLoopListener {
        void gameStatusChanged(GameStatus newStatus);
        void playPokemonSound(int comboNumber);
        void playTrainerSound(boolean isMetallic);
        void updateBoardView();
    }
}