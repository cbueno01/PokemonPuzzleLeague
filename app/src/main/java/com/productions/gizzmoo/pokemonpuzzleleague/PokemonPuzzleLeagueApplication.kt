package com.productions.gizzmoo.pokemonpuzzleleague

import android.app.Application
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.BoardResources

class PokemonPuzzleLeagueApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeBoardResources()
        initializeTrainerResources()
        initializePokemonResources()
    }

    private fun initializeBoardResources() {
        BoardResources.createImageBitmaps(this)
    }

    private fun initializeTrainerResources() {
        TrainerResources.createImageBitmaps(this)
    }

    private fun initializePokemonResources() {
        PokemonResources.createImageBitmaps(this)
    }
}