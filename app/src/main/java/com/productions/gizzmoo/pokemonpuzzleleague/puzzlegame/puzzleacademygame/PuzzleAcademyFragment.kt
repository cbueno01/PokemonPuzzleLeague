package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.graphics.Point
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameDialogFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.NUM_OF_COLS
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.NUM_OF_ROWS
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus

class PuzzleAcademyFragment : GameFragment<PuzzleAcademyGameLoop>(), PuzzleAcademyGameLoop.PuzzleAcademyGameLoopListener {
    var listener: PuzzleAcademyFragmentInterface? = null

    override fun gameStatusChanged(newStatus: GameStatus?) {}

    override fun createGameLoop(): PuzzleAcademyGameLoop {
        val swapsLeft = getNumOfSwipes()
        listener?.updateNumOfSwaps(swapsLeft)
        return PuzzleAcademyGameLoop(getPuzzleBoard(), swapsLeft)
    }

    override fun numberOfBlocksMatched() {}

    override fun gameFinished(didWin: Boolean) {
        val newFragment = GameDialogFragment.newInstance(didWin)
        newFragment.show(activity.fragmentManager, "postDialog")
    }

    override fun switchBlock(switcherLeftBlock: Point) {
        super.switchBlock(switcherLeftBlock)
        gameLoop.numOfSwapsLeft--
        listener?.updateNumOfSwaps(gameLoop.numOfSwapsLeft)
    }

    override fun updateGameTime(timeInMilli: Long) {
        listener?.updateGameTime(timeInMilli)
    }

    private fun getPuzzleBoard(): Array<Array<Block>> {
        val grid = Array(NUM_OF_ROWS) { arrayOfBlocks(NUM_OF_COLS) }

        // TODO: Get grid scheme from device
        grid[NUM_OF_ROWS - 1][1] = Block(2, 1, NUM_OF_ROWS - 1)
        grid[NUM_OF_ROWS - 1][2] = Block(2, 2, NUM_OF_ROWS - 1)
        grid[NUM_OF_ROWS - 1][4] = Block(2, 4, NUM_OF_ROWS - 1)
        return grid
    }

    private fun getNumOfSwipes(): Int {
        // TODO: Get num of swipes from device
        return 1
    }

    private fun arrayOfBlocks(numOfBlocks: Int): Array<Block> {
        return Array(numOfBlocks) { Block(0, it, numOfBlocks) }
    }

    interface PuzzleAcademyFragmentInterface {
        fun updateGameTime(timeInMilli: Long)
        fun updateNumOfSwaps(swapsLeft: Int)
    }

}