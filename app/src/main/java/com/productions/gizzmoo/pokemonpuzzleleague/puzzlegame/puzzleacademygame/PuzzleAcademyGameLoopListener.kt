package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoopListener

interface PuzzleAcademyGameLoopListener : GameLoopListener {
    fun updateGameTime(timeInMilli: Long)
}