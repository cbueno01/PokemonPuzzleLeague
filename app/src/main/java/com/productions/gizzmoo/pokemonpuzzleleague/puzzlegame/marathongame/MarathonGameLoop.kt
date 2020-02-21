package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoopListener

class MarathonGameLoop(grid: Array<Array<Block>>, gameSpeedLevelParam: Int) : RisingGameLoop<RisingGameLoopListener>(grid, gameSpeedLevelParam) {

    override fun checkIfGameEnded() {
        checkIfUserLost()
        checkIfUserWon()
    }

    @Synchronized
    private fun checkIfUserLost() {
        when {
            framesInWarning >= getNumOfFramesForCurrentLevel() -> changeGameStatus(GameStatus.Stopped)
            status === GameStatus.Warning -> if (canAnimateUp()) { framesInWarning++ }
            else -> framesInWarning = 0
        }
    }
    private fun checkIfUserWon() {
//        if (numOfLinesLeft > 11 || isBoardAnimating()) {
//            return
//        }
//
//        for (i in 0 until numOfLinesLeft) {
//            for (j in 0 until NUM_OF_COLS) {
//                if (!grid[i][j].isBlockEmpty) {
//                    return
//                }
//            }
//        }
//
//        didWin = true
//        changeGameStatus(GameStatus.Stopped)
    }

}