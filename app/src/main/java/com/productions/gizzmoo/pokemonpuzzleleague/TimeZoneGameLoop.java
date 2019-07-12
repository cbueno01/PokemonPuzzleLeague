package com.productions.gizzmoo.pokemonpuzzleleague;

import java.util.Random;

public class TimeZoneGameLoop extends GameLoop {

    private Block[] mNewRow;
    private int mNumOfLinesLeft;
    private int mGameSpeedLevel;
    private int mLinesToNewLevel;
    private int mCurrentFrameCount;
    private int mBlockMatchAnimating;
    private int mFramesInWarning;
    private Random rand;

    TimeZoneGameLoop(Block[][] grid, int numOfLines, int gameSpeedLevel) {
        super(grid);
        mNumOfLinesLeft = numOfLines;
        mGameSpeedLevel = gameSpeedLevel;
        rand = new Random();
    }

    @Override
    protected void checkIfGameEnded() {
        checkIfUserLost();
        checkIfUserWon();
    }

    @Override
    protected void postGameMechanicHook() {
        if (mBlockMatchAnimating == 0) {
            if (mCurrentFrameCount >= getNumOfFramesForCurrentLevel()) {
                addNewRow();
            } else {
                mCurrentFrameCount++;
            }
        }
    }

    @Override
    protected void notifyBlocksMatched() {
        mBlockMatchAnimating += mBlockMatch.size();
        super.notifyBlocksMatched();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

        if (mListener != null) {
            ((TimeZoneGameLoopListener)mListener).updateGameTimeAndSpeed(mElapsedTime, mGameSpeedLevel);
        }
    }


    public synchronized void addNewRow() {
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

        mNumOfLinesLeft--;
        mCurrentFrameCount = 0;

        if (!mBlockSwitcher.isAtTop() || doesRowContainBlock(0)) {
            mBlockSwitcher.moveUp();
        }

        increaseGameSpeedIfNeeded();
        createNewRowBlocks(mNewRow, rand);

        if (mListener != null) {
            ((TimeZoneGameLoopListener)mListener).newBlockWasAdded(mNumOfLinesLeft);
        }
    }

    public int getNumOfFramesForCurrentLevel() {
        float maxSpeed = 50;
        float minSpeed = 1;

        if (mGameSpeedLevel > 50) {
            mGameSpeedLevel = 50;
        } else if (mGameSpeedLevel < 1) {
            mGameSpeedLevel = 1;
        }

        float normalizedSpeed = (mGameSpeedLevel - minSpeed) / (maxSpeed - minSpeed);
        float invertNormSpeed = 1 - normalizedSpeed;
        return (int)(((invertNormSpeed * 9) + 1) * 30);
    }

    private void checkIfUserLost() {
        if (mFramesInWarning >= getNumOfFramesForCurrentLevel()) {
            changeGameStatus(GameStatus.Stopped);
        }
        else if (getGameStatus() == GameStatus.Warning) {
            mFramesInWarning++;
        } else {
            mFramesInWarning = 0;
        }
    }

    private void checkIfUserWon() {
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

    public static void createNewRowBlocks(Block[] blockRowArr, Random rng) {
        for (int i = 0; i < blockRowArr.length; i++) {
            blockRowArr[i] = new Block(rng.nextInt(7) + 1, i, NUM_OF_ROWS - 1);
        }
    }

    private void increaseGameSpeedIfNeeded() {
        if (mGameSpeedLevel < 50) {
            if (mLinesToNewLevel <= 0) {
                mGameSpeedLevel++;
                mLinesToNewLevel = getNumOfRowsForLevel();
            } else {
                mLinesToNewLevel--;
            }
        }
    }

    public void aBlockFinishedAnimating() {
        mBlockMatchAnimating--;
    }

    public int getNumOfLinesLeft() {
        return mNumOfLinesLeft;
    }

    public int getGameSpeed() {
        return mGameSpeedLevel;
    }

    private int getNumOfRowsForLevel() {
        return ((int)(mGameSpeedLevel * 1.25)) + 3;
    }

    public Block[] getNewRow() {
        return mNewRow;
    }

    public boolean isABlockAnimating() {
        return mBlockMatchAnimating > 0;
    }

    public int getNumOfAnimatingBlocks() {
        return mBlockMatchAnimating;
    }

    public int getLinesUntilSpeedIncrease() {
        return mLinesToNewLevel;
    }

    public int getCurrentFrameCount() {
        return mCurrentFrameCount;
    }

    public int getFrameCountInWarning() {
        return mFramesInWarning;
    }

    public void setTimeZoneGameProperties(Block[] newRow, int numOfBlocksAnimating, int linesUntilSpeedIncrease, int frameCount, int frameCountInWarning) {
        mNewRow = newRow;
        mBlockMatchAnimating = numOfBlocksAnimating;
        mLinesToNewLevel = (linesUntilSpeedIncrease < 0) ? getNumOfRowsForLevel() : linesUntilSpeedIncrease;
        mCurrentFrameCount = frameCount;
        mFramesInWarning = frameCountInWarning;
    }

    public interface TimeZoneGameLoopListener extends GameLoopListener {
        void newBlockWasAdded(int numOfLinesLeft);
        void updateGameTimeAndSpeed(long timeInMilli, int gameSpeed);
    }
}
