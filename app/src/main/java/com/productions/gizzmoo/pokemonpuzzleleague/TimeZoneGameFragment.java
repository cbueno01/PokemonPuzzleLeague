package com.productions.gizzmoo.pokemonpuzzleleague;

import android.os.Bundle;
import android.text.format.Time;

import java.util.Random;

import static com.productions.gizzmoo.pokemonpuzzleleague.GameLoop.NUM_OF_COLS;
import static com.productions.gizzmoo.pokemonpuzzleleague.GameLoop.NUM_OF_ROWS;

public class TimeZoneGameFragment extends GameFragment<TimeZoneGameLoop> {

    private final static String didWinKey = "DID_WIN_KEY";
    private final static String linesToWinKey = "LINES_TO_WIN_KEY";
    private final static String linesToSpeedIncreaseKey = "LINES_FOR_SPEED_INCREASE_KEY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
//            mDidWin = savedInstanceState.getBoolean(didWinKey);
//            mNumOfLinesLeft = savedInstanceState.getInt(linesToWinKey);
//            mLinesToNewLevel = savedInstanceState.getInt(linesToSpeedIncreaseKey);
        } else {
//            mDidWIn = false;
//            mNumOfLinesLeft = settings.getInt("pref_lines_key", 15) + NUM_OF_ROWS;
//            mLinesToNewLevel =  getNumOfLinesForLevel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putBoolean(didWinKey, mDidWin);
//        outState.putInt(linesToWinKey, mNumOfLinesLeft);
//        outState.putInt(linesToSpeedIncreaseKey, mLinesToNewLevel);
    }


    @Override
    public void addNewRow() {

    }

    @Override
    public void blockFinishedMatchAnimation(int row, int column) {

    }

    @Override
    protected TimeZoneGameLoop createGameLoop() {
        return new TimeZoneGameLoop(getGameBoard());
    }

    private Block[][] getGameBoard() {
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
}
