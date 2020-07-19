package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.spaservicegame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoopListener

class SpaServiceGameLoop(gameSpeedLevelParam: Int, numOfLines: Int) : RisingGameLoop<RisingGameLoopListener>(gameSpeedLevelParam) {
    var numOfLinesLeft = numOfLines
    private set

    override fun addNewRow() {
        numOfLinesLeft--
        super.addNewRow()
    }

    override fun checkIfGameEnded() {
        checkIfUserLost()
        checkIfUserWon()
    }

    private fun checkIfUserWon() {
        if (numOfLinesLeft > NUM_OF_ROWS - 1 || isBoardAnimating()) {
            return
        }

        for (i in 0 until numOfLinesLeft) {
            for (j in 0 until NUM_OF_COLS) {
                if (!grid[i][j].isBlockEmpty) {
                    return
                }
            }
        }

        didWin = true
        changeGameStatus(GameStatus.Stopped)
    }
}