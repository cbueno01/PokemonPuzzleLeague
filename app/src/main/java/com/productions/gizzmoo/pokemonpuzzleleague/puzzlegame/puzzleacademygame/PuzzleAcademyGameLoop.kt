package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus

class PuzzleAcademyGameLoop(grid: Array<Array<Block>>, var numOfSwapsLeft: Int) : GameLoop(grid) {
    override fun onProgressUpdate(vararg values: Void?) {
        super.onProgressUpdate(*values)

        if (mListener != null) {
            (mListener as PuzzleAcademyGameLoopListener).updateGameTime(mElapsedTime)
        }
    }

    override fun checkIfGameEnded() {
        checkIfGameIsFinished()
        checkIfWon()
    }

    private fun checkIfGameIsFinished() {
        if (isBoardAnimating) {
            return
        }

        if (numOfSwapsLeft <= 0) {
            changeGameStatus(GameStatus.Stopped)
        }
    }

    private fun checkIfWon() {
        for (row in mGrid) {
            for (block in row) {
                if (!block.isBlockEmpty) {
                    return
                }
            }
        }
        mDidWin = true
        changeGameStatus(GameStatus.Stopped)
    }

    fun setGameGrid(newGrid: Array<Array<Block>>) {
        lock.lock()
        mGrid = newGrid
        comboCount = 0
        mBlockMatch.clear()
        lock.unlock()
    }

    override fun postGameMechanicHook() {}

    interface PuzzleAcademyGameLoopListener : GameLoopListener {
        fun updateGameTime(timeInMilli: Long)
    }
}