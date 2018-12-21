package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Context;

public class TimeZoneGameLoop extends GameLoop {

    private int mNumOfLinesLeft;
    private int mLinesToNewLevel;

    public TimeZoneGameLoop(Block[][] grid) {
        super(grid);
    }

//    @Override
//    public static TimeZoneGameLoop createGame() {
//        return new TimeZoneGameLoop();
//    }

    @Override
    protected void checkIfGameWon() {
//        if (mNumOfLinesLeft > 11) {
//            return;
//        }
//
//        for (int i = 0; i < mNumOfLinesLeft; i++) {
//            for (int j = 0; j < NUM_OF_COLS; j++) {
//                if (!mGrid[i][j].isBlockEmpty()) {
//                    return;
//                }
//            }
//        }
//
//        mDidWin = true;
    }
}
