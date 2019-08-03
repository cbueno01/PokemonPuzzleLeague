package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView.ANIMATION_MATCH_INVERT_FRAMES_NEEDED;
import static com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView.ANIMATION_MATCH_POP_FRAMES_NEEDED;

public abstract class GameLoop extends AsyncTask<Void, Void, Void> {
    public static final int NUM_OF_COLS = 6;
    public static final int NUM_OF_ROWS = 12;

    private final int MAX_FPS = 30;
    private final int FRAME_PERIOD = 1000 / MAX_FPS;

    private long mStartTime;
    protected long mElapsedTime;

    protected Block[][] mGrid;
    protected Lock lock = new ReentrantLock();
    protected GameLoopListener mListener;
    protected ArrayList<Block> mBlockMatch;
    protected SwitchBlocks mBlockSwitcher;
    private GameStatus mStatus;
    protected boolean mDidWin;

    private int comboCount;


    public GameLoop(Block[][] grid) {
        mDidWin = false;
        mGrid = grid;
        mBlockMatch = new ArrayList<>();
        mBlockSwitcher = new SwitchBlocks(2, 9, 3, 9);
        comboCount = 0;
        mElapsedTime = 0;
        mStartTime = System.nanoTime() / 1000000;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (mStatus != GameStatus.Stopped && !isCancelled()) {
            mElapsedTime = (System.nanoTime() / 1000000) - mStartTime;

            try {
                Thread.sleep(FRAME_PERIOD);
            } catch (InterruptedException e) {
            }

            applyGravity();
            checkForMatches();
            checkToResetCombo();
            postGameMechanicHook();
            getUpdatedGameStatus();
            checkIfGameEnded();
            publishProgress();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        getUpdatedGameStatus();
        mBlockMatch.clear();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        if (mListener != null) {
            mListener.updateBoardView();
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        if (mListener != null) {
            mListener.gameFinished(mDidWin);
        }
    }

    public void startGame() {
        this.execute();
    }

    // Gets called on every frame
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void applyGravity() {
        for (int y = NUM_OF_ROWS - 2; y >= 0; y--) {
            for (int x = 0; x < NUM_OF_COLS; x++) {
                if (mGrid[y][x].canInteract()) {
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
                if (mGrid[i][j].canInteract()) {
                    mBlockMatch.addAll(checkForMatchWithDirection(i, j, 0));
                    mBlockMatch.addAll(checkForMatchWithDirection(i, j, 1));
                }
            }
        }

        if (!mBlockMatch.isEmpty()) {
            Collections.sort(mBlockMatch);
            removeDuplicateBlocks();
            startMatchAnimation();
            notifyBlocksMatched();
            playSoundIfNecessary();

            mBlockMatch.clear();
        }
    }

    private void getUpdatedGameStatus() {
        if (mDidWin) {
            changeGameStatus(GameStatus.Stopped);
        } else if (doesRowContainBlock(0)) {
            if (mStatus != GameStatus.Warning) {
                changeGameStatus(GameStatus.Warning);
            }
        } else if (doesRowContainBlock(3)) {
            if (mStatus != GameStatus.Panic) {
                changeGameStatus(GameStatus.Panic);
            }
        } else {
            if (mStatus != GameStatus.Running) {
                changeGameStatus(GameStatus.Running);
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 0 - Up/Down
    // 1 - Left/Right

    private ArrayList<Block> checkForMatchWithDirection(int i, int j, int direction) {
        ArrayList<Block> tempList = new ArrayList<>();
        tempList.add(mGrid[i][j]);
        switch (direction) {
            case 0:
                int pos = i - 1;
                // Check down
                while (pos >= 0 && (mGrid[i][j].getBlockType() == mGrid[pos][j].getBlockType()) && mGrid[pos][j].canInteract()) {
                    tempList.add(mGrid[pos][j]);
                    pos--;
                }

                pos = i + 1;
                // Check up
                while (pos < NUM_OF_ROWS && (mGrid[i][j].getBlockType() == mGrid[pos][j].getBlockType()) && mGrid[pos][j].canInteract()) {
                    tempList.add(mGrid[pos][j]);
                    pos++;
                }

                break;
            case 1:
                pos = j - 1;
                // Check right
                while (pos >= 0 && (mGrid[i][j].getBlockType() == mGrid[i][pos].getBlockType()) && mGrid[i][pos].canInteract()) {
                    tempList.add(mGrid[i][pos]);
                    pos--;
                }

                pos = j + 1;
                // Check left
                while (pos < NUM_OF_COLS && (mGrid[i][j].getBlockType() == mGrid[i][pos].getBlockType()) && mGrid[i][pos].canInteract()) {
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
        int matchSize = mBlockMatch.size();
        for (int i = 0; i < matchSize; i++) {
            Block b = mBlockMatch.get(i);
            b.hasMatched = true;
            b.delayMatchAnimationCount = i * ANIMATION_MATCH_POP_FRAMES_NEEDED;
            b.matchInvertedAnimationCount = ANIMATION_MATCH_INVERT_FRAMES_NEEDED;
            b.matchPopAnimationCount = 0;
            b.clearMatchCount = (mBlockMatch.size() - 1) * ANIMATION_MATCH_POP_FRAMES_NEEDED - b.delayMatchAnimationCount + ANIMATION_MATCH_INVERT_FRAMES_NEEDED;
            b.popPosition = i;
            b.matchTotalCount = matchSize;
        }
    }

    protected void notifyBlocksMatched() {
        if (mListener != null) {
            mListener.numberOfBlocksMatched();
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

    protected void changeGameStatus(GameStatus newStatus) {
        mStatus = newStatus;
        if (mListener != null) {
            mListener.gameStatusChanged(newStatus);
        }
    }

    protected abstract void checkIfGameEnded();

    protected abstract void postGameMechanicHook();

    protected boolean doesRowContainBlock(int row) {
        for (int i = 0; i < NUM_OF_COLS; i++) {
            if (!mGrid[row][i].isBlockEmpty()) {
                return true;
            }
        }

        return false;
    }

    protected void swapBlocks(int x1, int y1, int x2, int y2) {
        lock.lock();
        Block blockHolder = mGrid[y1][x1];
        mGrid[y1][x1] = mGrid[y2][x2];
        mGrid[y2][x2] = blockHolder;

        mGrid[y1][x1].changeCoords(x1, y1);
        mGrid[y2][x2].changeCoords(x2, y2);
        lock.unlock();
    }

    protected void blockFinishedMatchAnimation(int row, int column) {
        int rowToUpdate = row - 1;
        while (rowToUpdate >= 0 && !mGrid[rowToUpdate][column].isBlockEmpty()) {
            if (mGrid[rowToUpdate][column].canInteract()) {
                mGrid[rowToUpdate][column].canCombo = true;
            }
            rowToUpdate--;
        }
    }

    public void moveBlockSwitcherFromTop() {
        if (mBlockSwitcher.isAtTop()) {
            mBlockSwitcher.moveDown();
        }
    }

    protected boolean isBoardAnimating() {
        for (Block[] row : mGrid) {
            for (Block block : row) {
                if (block.isAnimating()) {
                    return true;
                }
            }
        }

        return false;
    }

    public GameStatus getGameStatus() {
        return mStatus;
    }

    public Block[][] getGameGrid() {
        return mGrid;
    }

    public SwitchBlocks getBlockSwitcher() {
        return mBlockSwitcher;
    }

    public long getGameStartTime() {
        return mStartTime;
    }

    public void setGameProperties(Block[][] grid, SwitchBlocks switcher, long startTime) {
        mGrid = grid;
        mBlockSwitcher = switcher;
        mStartTime = startTime;
    }

    public void setGameLoopListener(GameLoopListener listener) {
        mListener = listener;
    }

    public interface GameLoopListener {
        void gameStatusChanged(GameStatus newStatus);

        void playPokemonSound(int comboNumber);

        void playTrainerSound(boolean isMetallic);

        void updateBoardView();

        void numberOfBlocksMatched();

        void gameFinished(boolean didWin);
    }
}