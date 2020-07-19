package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame

import android.preference.PreferenceManager
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoopListener
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingPuzzleBoardView

class MarathonGameFragment : RisingGameFragment<RisingGameLoopListener, MarathonGameLoop, RisingPuzzleBoardView>() {
    override fun createGameLoop(): MarathonGameLoop {
        return MarathonGameLoop(tempGameSpeed)
    }

    override fun createNewGrid(): Array<Array<Block>> =
        getGameBoard(rand, tempGameSpeed, GameLoop.NUM_OF_COLS * NUMBER_OF_BLOCKS_MULTIPLIER, NUMBER_OF_FULL_ROWS)

    override fun createPuzzleBoardView(): RisingPuzzleBoardView =
            RisingPuzzleBoardView(activity!!)

    override fun getGameSpeed(): Int {
        val settings = PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext)
        return settings.getInt("pref_game_speed", 10)
    }

    companion object {
        private const val NUMBER_OF_BLOCKS_MULTIPLIER = 5
        private const val NUMBER_OF_FULL_ROWS = 5
    }
}