package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.graphics.Point
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameDialogFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import java.util.*

class PuzzleAcademyFragment : GameFragment<PuzzleAcademyGameLoop>(), PuzzleAcademyGameLoop.PuzzleAcademyGameLoopListener {
    var listener: PuzzleAcademyFragmentInterface? = null
    var puzzleId = 0
    var gameEnded = false
    private var boardHistory: Stack<Array<Array<Int>>> = Stack()

    override fun gameStatusChanged(newStatus: GameStatus?) {}

    override fun createGameLoop(): PuzzleAcademyGameLoop {
        val jsonReaderWriter = FileManager.getJSONReaderWriter(activity, 1)
        val grid: Array<Array<Block>> = jsonReaderWriter.getGridFromLevel(puzzleId)
        val moves: Int = jsonReaderWriter.getMovesFromLevel(puzzleId)
        boardHistory = jsonReaderWriter.getHistoryFromLevel(puzzleId)
        listener?.updateNumOfSwaps(moves)
        return PuzzleAcademyGameLoop(grid, moves)
    }

    override fun numberOfBlocksMatched() {}

    override fun gameFinished(didWin: Boolean) {
        val newFragment = GameDialogFragment.newInstance(didWin)
        newFragment.show(activity.fragmentManager, "postDialog")
    }

    override fun switchBlock(switcherLeftBlock: Point) {
        if (gameLoop.numOfSwapsLeft > 0) {
            addGridToBoardHistory(gameLoop.gameGrid)
            super.switchBlock(switcherLeftBlock)
            gameLoop.numOfSwapsLeft--
            listener?.updateNumOfSwaps(gameLoop.numOfSwapsLeft)
        }
    }

    override fun updateGameTime(timeInMilli: Long) {
        listener?.updateGameTime(timeInMilli)
    }

    override fun boardSwipedUp() {
        if (!boardHistory.empty()) {
            val currentHistoryGrid = boardHistory.pop()
            mGameLoop.gameGrid = intGridToBlockGrid(currentHistoryGrid)
            mBoardView.setGrid(mGameLoop.gameGrid, mGameLoop.blockSwitcher)
            gameLoop.numOfSwapsLeft++
            listener?.updateNumOfSwaps(gameLoop.numOfSwapsLeft)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!gameEnded) {
            FileManager.getJSONReaderWriter(activity, 1).writeToFile(activity, puzzleId, gameLoop.gameGrid, gameLoop.numOfSwapsLeft, boardHistory)
        }
    }

    override fun refreshBoardFromInstantState() {}

    private fun addGridToBoardHistory(currentGrid: Array<Array<Block>>) {
        boardHistory.push(blockGridToIntGrid(currentGrid))
    }

    private fun blockGridToIntGrid(currentGrid: Array<Array<Block>>): Array<Array<Int>> =
        Array(currentGrid.size)
            { i -> Array(currentGrid[i].size)
                { j -> currentGrid[i][j].blockType.value }}

    private fun intGridToBlockGrid(currentGrid: Array<Array<Int>>): Array<Array<Block>> =
        Array(currentGrid.size)
            {i -> Array(currentGrid[i].size)
                {j -> Block(currentGrid[i][j], j, i) }}


    interface PuzzleAcademyFragmentInterface {
        fun updateGameTime(timeInMilli: Long)
        fun updateNumOfSwaps(swapsLeft: Int)
    }
}