package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.timezonegame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoopListener

interface TimeZoneGameLoopListener : GameLoopListener {
    fun newBlockWasAdded(numOfLinesLeft: Int)
    fun updateGameTimeAndSpeed(timeInMilli: Long, gameSpeed: Int, delayInSeconds: Int)
    fun startAnimatingUp()
    fun stopAnimatingUp()
}