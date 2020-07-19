package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameLoopListener

class MarathonGameLoop(gameSpeedLevelParam: Int) : RisingGameLoop<RisingGameLoopListener>(gameSpeedLevelParam) {

    override fun checkIfGameEnded() {
        checkIfUserLost()
    }
}