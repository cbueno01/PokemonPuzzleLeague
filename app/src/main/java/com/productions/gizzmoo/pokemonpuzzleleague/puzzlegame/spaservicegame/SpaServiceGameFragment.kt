package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.spaservicegame

import android.os.Bundle
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.Companion.NUM_OF_ROWS
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoopListener

class SpaServiceGameFragment : RisingGameFragment<RisingGameLoopListener, SpaServiceGameLoop, SpaServicePuzzleBoardView>() {
    private var tempNumOfLinesLeft: Int = 0

    override fun createGameLoop(): SpaServiceGameLoop {
        val gameProperties = (activity as SpaServiceGameActivity).currentGameProperties
        return SpaServiceGameLoop(gameProperties.gameSpeed, tempNumOfLinesLeft)
    }

    override fun createNewGrid(): Array<Array<Block>> {
        val gameProperties = (activity as SpaServiceGameActivity).currentGameProperties
        return getGameBoard(rand, gameProperties.gameSpeed, gameProperties.numOfBlocks, gameProperties.completeRows)
    }

    override fun createPuzzleBoardView(): SpaServicePuzzleBoardView =
            SpaServicePuzzleBoardView(activity!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tempNumOfLinesLeft = savedInstanceState?.getInt(linesToWinKey) ?: (activity as SpaServiceGameActivity).currentGameProperties.linesToWin + NUM_OF_ROWS
    }

    override fun onStop() {
        super.onStop()
        tempNumOfLinesLeft = gameLoop.numOfLinesLeft
    }

    override fun onStart() {
        super.onStart()
        drawLineIfNeeded(tempNumOfLinesLeft)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(linesToWinKey, gameLoop.numOfLinesLeft)
    }

    override fun newBlockWasAdded() {
        super.newBlockWasAdded()
        drawLineIfNeeded(gameLoop.numOfLinesLeft)
    }

    override fun getGameSpeed(): Int =
        (activity as SpaServiceGameActivity).currentGameProperties.gameSpeed

    private fun drawLineIfNeeded(numOfLines: Int) {
        if (numOfLines <= NUM_OF_ROWS) {
            boardView.winLineAt(numOfLines)
        }
    }

    companion object {
        private const val linesToWinKey = "LINES_TO_WIN_KEY"
    }
}