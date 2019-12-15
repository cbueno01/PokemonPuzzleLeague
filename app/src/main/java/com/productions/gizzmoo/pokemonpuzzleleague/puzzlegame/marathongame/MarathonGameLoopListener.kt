package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoopListener

interface MarathonGameLoopListener : GameLoopListener {
    fun newBlockWasAdded()
    fun updateGameTimeAndSpeed(timeInMilli: Long, gameSpeed: Int, delayInSeconds: Int)
    fun gameIsPrepared()
    fun tryToStartAnimatingUp()
    fun stopAnimatingUp()
}