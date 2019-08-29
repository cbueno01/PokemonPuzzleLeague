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
    private lateinit var fileManager: FileManager

    override fun gameStatusChanged(newStatus: GameStatus?) {}

    override fun createGameLoop(): PuzzleAcademyGameLoop {
        fileManager = FileManager(activity, 1, puzzleId)
        val grid: Array<Array<Block>> = fileManager.getGridFromLevel()
        val moves: Int = fileManager.getMovesFromLevel()
        boardHistory = fileManager.getHistoryFromLevel()
//        doSanityChecks(grid, moves)
        listener?.updateNumOfSwaps(moves)
        return PuzzleAcademyGameLoop(grid, moves)
    }

    override fun numberOfBlocksMatched() {}

    override fun gameFinished(didWin: Boolean) {
        val newFragment = GameDialogFragment.newInstance(didWin)
        newFragment.show(activity.fragmentManager, "postDialog")
    }

    override fun switchBlock(switcherLeftBlock: Point) {
        addGridToBoardHistory(gameLoop.gameGrid)
        super.switchBlock(switcherLeftBlock)
        gameLoop.numOfSwapsLeft--
        listener?.updateNumOfSwaps(gameLoop.numOfSwapsLeft)
    }

    override fun updateGameTime(timeInMilli: Long) {
        listener?.updateGameTime(timeInMilli)
    }

    override fun boardSwipedUp() {
        if (boardHistory.empty()) {
            return
        }

        val currentHistoryGrid = boardHistory.pop()
        // Do intGridToBlock here
        val newGrid: Array<Array<Block>> = Array(currentHistoryGrid.size)
        {i -> Array(currentHistoryGrid[i].size)
            {j -> Block(currentHistoryGrid[i][j], j, i) }}

        mGameLoop.gameGrid = newGrid
        mBoardView.setGrid(mGameLoop.gameGrid, mGameLoop.blockSwitcher)
        gameLoop.numOfSwapsLeft++
        listener?.updateNumOfSwaps(gameLoop.numOfSwapsLeft)
    }

    override fun onStop() {
        super.onStop()
        if (!gameEnded) {
            fileManager.writeToFile(gameLoop.gameGrid, gameLoop.numOfSwapsLeft, boardHistory)
        }
    }

    override fun refreshBoardFromInstantState() {}

//    private fun doSanityChecks(grid: Array<Array<Block>>, moves: Int) {
//        if (moves < 0) {
//            throw Exception("There are negative moves in the JSON")
//        }
//
//        if (grid.size != NUM_OF_ROWS) {
//            throw Exception("The number of rows in the JSON file need to be $NUM_OF_ROWS")
//        }
//
//        for (i in 0 until(grid.size)) {
//            if (grid[i].size != NUM_OF_COLS) {
//                throw Exception("The number of columns in row ${i + 1} in the JSON file need to be $NUM_OF_COLS")
//            }
//        }
//    }

    private fun addGridToBoardHistory(currentGrid: Array<Array<Block>>) {
        boardHistory.push(blockGridToIntGrid(currentGrid))
    }

    private fun blockGridToIntGrid(currentGrid: Array<Array<Block>>): Array<Array<Int>> =
        Array(currentGrid.size)
            { i -> Array(currentGrid[i].size)
                { j -> currentGrid[i][j].blockType.value }}


    interface PuzzleAcademyFragmentInterface {
        fun updateGameTime(timeInMilli: Long)
        fun updateNumOfSwaps(swapsLeft: Int)
    }

    /*
    stage - level[]

    level - grid
    level - moves
    level - did_complete
    level - current

    grid - Int[][]

    moves - Int

    did_complete - Boolean

    current - grid
    current - moves
    current - history

    history - grid[] - oldest....newest
     */
}