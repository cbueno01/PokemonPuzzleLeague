package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoopListener

interface RisingGameLoopListener : GameLoopListener {
    fun newBlockWasAdded()
    fun updateGameTimeAndSpeed(timeInMilli: Long, gameSpeed: Int, delayInSeconds: Int)
    fun gameIsPrepared()
    fun tryToStartAnimatingUp()
    fun stopAnimatingUp()
}