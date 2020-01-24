package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus

class PuzzleAcademyGameLoop(grid: Array<Array<Block>>, var numOfSwapsLeft: Int) : GameLoop<PuzzleAcademyGameLoopListener>(grid) {
    override fun onProgressUpdate(vararg values: Void?) {
        super.onProgressUpdate()
        listener?.updateGameTime(elapsedTime)
    }

    override fun checkIfGameEnded() {
        checkIfGameIsFinished()
        checkIfWon()
    }

    override fun getUpdatedGameStatus() {}

    private fun checkIfGameIsFinished() {
        if (isBoardAnimating()) {
            return
        }

        if (numOfSwapsLeft <= 0) {
            changeGameStatus(GameStatus.Stopped)
        }
    }

    private fun checkIfWon() {
        for (row in grid) {
            for (block in row) {
                if (!block.isBlockEmpty) {
                    return
                }
            }
        }
        didWin = true
        changeGameStatus(GameStatus.Stopped)
    }

    override fun postGameMechanicHook() {}
}