package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager.STREAM_MUSIC
import android.media.SoundPool
import android.os.Parcelable
import android.preference.DialogPreference
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import com.productions.gizzmoo.pokemonpuzzleleague.Pokemon
import com.productions.gizzmoo.pokemonpuzzleleague.PokemonResources
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.Trainer

class PokemonPreference(context: Context, attrs: AttributeSet? = null) : DialogPreference(context, attrs), SharedPreferences.OnSharedPreferenceChangeListener {
    private var pokemonID = DEFAULT_ID
    private val settings : SharedPreferences
    private var currentTrainer : Trainer
    private lateinit var gridView : GridView
    private lateinit var portraitAdapter : ImagePortraitAdapter

    private lateinit var bitmaps: Array<Bitmap>
    private lateinit var pokemonArr: Array<Pokemon>
    private lateinit var pokemonNames: Array<String>
    private lateinit var pokemonSounds: Array<Int>

    private var soundPool: SoundPool? = null
    private var loadedSoundPool: Boolean = false

    init {
        dialogLayoutResource = R.layout.pokemon_preference
        settings = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        currentTrainer = Trainer.getTypeByID(settings.getInt("pref_trainer_key", TrainerPreference.DEFAULT_ID))
        soundPool = SoundPool(2, STREAM_MUSIC, 0)
        soundPool?.setOnLoadCompleteListener { _, _, _ -> loadedSoundPool = true }
        updateResourcesForNewTrainer()
    }

    override fun onBindDialogView(view: View) {
        gridView = view.findViewById(R.id.grid)
        portraitAdapter = ImagePortraitAdapter(context, bitmaps)
        portraitAdapter.chosenPosition = pokemonID
        gridView.adapter = portraitAdapter
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            persistInt(position)
            pokemonID = position
            if (loadedSoundPool) {
                soundPool?.play( pokemonSounds[position], 1f, 1f, 1, 0, 1f)
            }
            dialog.dismiss()
        }

        super.onBindDialogView(view)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "pref_trainer_key") {
            currentTrainer = Trainer.getTypeByID(settings.getInt("pref_trainer_key", TrainerPreference.DEFAULT_ID))
            pokemonID = 0
            settings.edit().putInt("pref_pokemon_key", pokemonID).apply()
            updateResourcesForNewTrainer()
        }
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            pokemonID = getPersistedInt(DEFAULT_ID)
        } else {
            persistInt(pokemonID)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            persistInt(pokemonID)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        if (isPersistent) {
            return superState
        }

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