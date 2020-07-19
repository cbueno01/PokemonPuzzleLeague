package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

interface GameLoopListener {
    fun gameStatusChanged(newStatus: GameStatus)
    fun playPokemonSound(comboNumber: Int)
    fun playTrainerSound(isMetallic: Boolean)
    fun updateBoardView()
    fun gameFinished(didWin: Boolean)
    fun blockIsPopping(position: Int, total: Int)
}