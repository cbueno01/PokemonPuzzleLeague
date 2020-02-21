package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoopListener
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingPuzzleBoardView

class MarathonGameFragment : RisingGameFragment<RisingGameLoopListener, MarathonGameLoop, RisingPuzzleBoardView>() {
    override fun createGameLoop(): MarathonGameLoop {
        return MarathonGameLoop(getGameBoard(tempGameSpeed), tempGameSpeed)
    }

    override fun createPuzzleBoardView(): RisingPuzzleBoardView =
            RisingPuzzleBoardView(activity!!)

    private fun getGameBoard(gameSpeedLevel: Int): Array<Array<Block>> {
        val grid = Array(GameLoop.NUM_OF_ROWS) { i -> Array(GameLoop.NUM_OF_COLS) { j -> Block(0, j, i) } }
        val columnCounter = IntArray(GameLoop.NUM_OF_COLS)

        var numberNumberOfBLocksLeft = GameLoop.NUM_OF_COLS * NUMBER_OF_BLOCKS_MULTIPLIER

        // Populate first 3 rows
        for (i in GameLoop.NUM_OF_ROWS - 1 downTo GameLoop.NUM_OF_ROWS - 4 + 1) {
            for (j in 0 until GameLoop.NUM_OF_COLS) {
                grid[i][j] = RisingGameLoop.getRandomBlock(rand, j, i, RisingGameLoop.shouldShowDiamonds(gameSpeedLevel))
                columnCounter[j]++
                numberNumberOfBLocksLeft--
            }
        }

        var x = 0
        while (x < numberNumberOfBLocksLeft) {
            val position = rand.nextInt(GameLoop.NUM_OF_COLS)
            if (columnCounter[position] < GameLoop.NUM_OF_ROWS - 1) {
                grid[GameLoop.NUM_OF_ROWS - 1 - columnCounter[position]][position] = RisingGameLoop.getRandomBlock(rand, position, GameLoop.NUM_OF_ROWS - 1 - columnCounter[position], RisingGameLoop.shouldShowDiamonds(gameSpeedLevel))
                columnCounter[position]++
                x++
            }
        }

        return grid
    }

    companion object {
        private const val NUMBER_OF_BLOCKS_MULTIPLIER = 5
    }
}