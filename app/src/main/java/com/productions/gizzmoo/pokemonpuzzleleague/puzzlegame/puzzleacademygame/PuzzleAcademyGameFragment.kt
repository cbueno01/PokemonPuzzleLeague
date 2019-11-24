package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.content.Context
import android.graphics.Point
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameDialogFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import java.util.*

class PuzzleAcademyGameFragment : GameFragment<PuzzleAcademyGameLoopListener, PuzzleAcademyGameLoop>(), PuzzleAcademyGameLoopListener {
    var listener: PuzzleAcademyFragmentInterface? = null
    var puzzleId = 0
    var gameEnded = false
    private var boardHistory: Stack<Array<Array<Int>>> = Stack()

    override fun gameStatusChanged(newStatus: GameStatus) {}

    override fun createGameLoop(): PuzzleAcademyGameLoop {
        val jsonReaderWriter = FileManager.getJSONReaderWriter(getActivityContext(), 1)
        val grid: Array<Array<Block>> = jsonReaderWriter.getGridFromLevel(puzzleId)
        val moves: Int = jsonReaderWriter.getMovesFromLevel(puzzleId)
        boardHistory = jsonReaderWriter.getHistoryFromLevel(puzzleId)
        listener?.updateNumOfSwaps(moves)
        return PuzzleAcademyGameLoop(grid, moves)
    }

    override fun blocksMatched() {}

    override fun gameFinished(didWin: Boolean) {
        val newFragment = GameDialogFragment.newInstance(didWin)
        newFragment.show(activity?.supportFragmentManager, "postDialog")
    }

    override fun switchBlock(switcherLeftBlock: Point) {
        if (gameLoop.numOfSwapsLeft > 0) {
            addGridToBoardHistory(gameLoop.grid)
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
            gameLoop.grid = intGridToBlockGrid(currentHistoryGrid)
            boardView.setGrid(gameLoop.grid, gameLoop.blockSwitcher)
            gameLoop.numOfSwapsLeft++
            listener?.updateNumOfSwaps(gameLoop.numOfSwapsLeft)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!gameEnded) {
            FileManager.getJSONReaderWriter(getActivityContext(), 1).writeToFile(getActivityContext(), puzzleId, gameLoop.grid, gameLoop.numOfSwapsLeft, boardHistory)
        }
    }

    private fun addGridToBoardHistory(currentGrid: Array<Array<Block>>) {
        boardHistory.push(blockGridToIntGrid(currentGrid))
    }

    private fun blockGridToIntGrid(currentGrid: Array<Array<Block>>): Array<Array<Int>> =
        Array(currentGrid.size)
            { i -> Array(currentGrid[i].size)
                { j -> currentGrid[i][j].type.value }}

    private fun intGridToBlockGrid(currentGrid: Array<Array<Int>>): Array<Array<Block>> =
        Array(currentGrid.size)
            {i -> Array(currentGrid[i].size)
                {j -> Block(currentGrid[i][j], j, i) }}

    private fun getActivityContext(): Context = activity!!.applicationContext


    interface PuzzleAcademyFragmentInterface {
        fun updateGameTime(timeInMilli: Long)
        fun updateNumOfSwaps(swapsLeft: Int)
    }
}