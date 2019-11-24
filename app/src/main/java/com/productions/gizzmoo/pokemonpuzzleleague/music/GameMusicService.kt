package com.productions.gizzmoo.pokemonpuzzleleague.music

import android.preference.PreferenceManager
import com.productions.gizzmoo.pokemonpuzzleleague.TrainerResources
import com.productions.gizzmoo.pokemonpuzzleleague.Trainer
import com.productions.gizzmoo.pokemonpuzzleleague.settings.TrainerPreference

class GameMusicService : MusicService() {
    private lateinit var resourceId: IntArray
    private var resourceIndex = 0
    private var isMusicEnabled = true

    override fun onCreate() {
        super.onCreate()
        val settings = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val currentTrainer = Trainer.getTypeByID(settings.getInt("pref_trainer_key", TrainerPreference.DEFAULT_ID))
        isMusicEnabled = settings.getBoolean("pref_music", true)
        resourceId = TrainerResources.getTrainerSong(currentTrainer)
    }

    fun startMusic(position: Int, isPanic: Boolean) {
        if (isMusicEnabled) {
            resourceIndex = if (isPanic) 1 else 0
            super.startMusic(position)
        }
    }

    fun changeSong(isPanic: Boolean) {
        if (isMusicEnabled) {
            stopMusic()
            startMusic(0, isPanic)
        }
    }

    override fun getResource(): Int {
        return resourceId[resourceIndex]
    }
}