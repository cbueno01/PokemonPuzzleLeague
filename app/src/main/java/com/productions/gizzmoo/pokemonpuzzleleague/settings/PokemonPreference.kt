package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.SoundPool
import android.os.Parcelable
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceManager
import com.productions.gizzmoo.pokemonpuzzleleague.Pokemon
import com.productions.gizzmoo.pokemonpuzzleleague.PokemonResources
import com.productions.gizzmoo.pokemonpuzzleleague.Trainer

class PokemonPreference(context: Context, attrs: AttributeSet? = null) : DialogPreference(context, attrs), SharedPreferences.OnSharedPreferenceChangeListener {
    var pokemonID = DEFAULT_ID
        private set
    private val settings : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private var currentTrainer : Trainer

    lateinit var bitmaps: Array<Bitmap>
        private set
    private lateinit var pokemonArr: Array<Pokemon>
    private lateinit var pokemonNames: Array<String>
    private lateinit var pokemonSounds: Array<Int>

    private var soundPool: SoundPool? = null
    private var loadedSoundPool: Boolean = false

    init {
        currentTrainer = Trainer.getTypeByID(settings.getInt("pref_trainer_key", TrainerPreference.DEFAULT_ID))
        soundPool = SoundPool.Builder().setMaxStreams(2).build()
        soundPool?.setOnLoadCompleteListener { _, _, _ -> loadedSoundPool = true }
        updateResourcesForNewTrainer()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "pref_trainer_key") {
            currentTrainer = Trainer.getTypeByID(settings.getInt("pref_trainer_key", TrainerPreference.DEFAULT_ID))
            pokemonID = 0
            settings.edit().putInt("pref_pokemon_key", pokemonID).apply()
            updateResourcesForNewTrainer()
            summary = pokemonNames[pokemonID]
        }
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            pokemonID = getPersistedInt(DEFAULT_ID)
        } else {
            persistInt(pokemonID)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.value = pokemonID
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        val myState = state as SavedState?
        super.onRestoreInstanceState(myState?.superState)

        pokemonID = myState?.value ?: DEFAULT_ID
    }

    override fun getSummary(): CharSequence {
        return pokemonNames[pokemonID]
    }

    fun persistPokemonID(newPokemonID: Int) {
        pokemonID = newPokemonID
        summary = pokemonNames[newPokemonID]
        persistInt(newPokemonID)
        if (loadedSoundPool) {
            soundPool?.play( pokemonSounds[newPokemonID], 1f, 1f, 1, 0, 1f)
        }
    }

    private fun updateResourcesForNewTrainer() {
        pokemonArr = PokemonResources.getPokemonForTrainer(currentTrainer)

        bitmaps = Array(pokemonArr.size) {
            i -> BitmapFactory.decodeResource(context.resources, PokemonResources.getPokemonPortrait(pokemonArr[i]))
        }

        pokemonNames = Array(pokemonArr.size) {
            i -> PokemonResources.getPokemonName(pokemonArr[i], context)
        }

        pokemonSounds = Array(pokemonArr.size) {
            i -> soundPool?.load(context, PokemonResources.getPokemonSelectionSound(pokemonArr[i]), 1)!!
        }
    }

    companion object {
        const val DEFAULT_ID = 0 // First Pokemon
    }
}