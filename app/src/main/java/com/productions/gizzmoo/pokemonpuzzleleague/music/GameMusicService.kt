package com.productions.gizzmoo.pokemonpuzzleleague.music

import android.preference.PreferenceManager
import com.productions.gizzmoo.pokemonpuzzleleague.TrainerResources
import com.productions.gizzmoo.pokemonpuzzleleague.Trainer

class GameMusicService : MusicService() {
    private lateinit var resourceId: IntArray
    private var resourceIndex = 0

    override fun onCreate() {
        super.onCreate()
        val settings = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val currentTrainer = Trainer.getTypeByID(settings.getInt("pref_trainer_key", 0))
        resourceId = TrainerResources.getTrainerSong(currentTrainer)
    }

    fun startMusic(position: Int, isPanic: Boolean) {
        resourceIndex = if (isPanic) 1 else 0
        super.startMusic(position)
    }

    fun changeSong(isPanic: Boolean) {
        stopMusic()
        startMusic(0, isPanic)
    }

    override fun getResource(): Int {
        return resourceId[resourceIndex]
    }
}