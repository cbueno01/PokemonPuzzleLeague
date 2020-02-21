package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame

interface RisingFragmentInterface {
    fun changeSong(isPanic: Boolean)
    fun updateGameTimeAndSpeed(timeInMilli: Long, gameSpeed: Int, delayInSeconds: Int)
    fun onGameStarted()
    fun onGameFinished()
}