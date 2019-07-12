package com.productions.gizzmoo.pokemonpuzzleleague

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.preference.DialogPreference
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast

class  PokemonPreference(context: Context, attrs: AttributeSet, defStyleAttr: Int) : DialogPreference(context, attrs, defStyleAttr), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val DEFAULT_ID =  0 // First Pokemon
    }

    private val mContext : Context = context
    private var mPokemonID = DEFAULT_ID
    private val mSettings : SharedPreferences
    private var mCurrentTrainer : Trainer
    private lateinit var mGridView : GridView
    private lateinit var mAdapter : ImageAdapter

    private lateinit var mBitmaps: Array<Bitmap>
    private lateinit var mPokemonArr: Array<Pokemon>
    private lateinit var mPokemonNames: Array<String>

    constructor(context: Context, attrs: AttributeSet): this(context, attrs, 0)

    init {
        dialogLayoutResource = R.layout.pokemon_preference
        mSettings = PreferenceManager.getDefaultSharedPreferences(mContext.applicationContext)
        mCurrentTrainer = Trainer.getTypeByID(mSettings.getInt("pref_trainer_key", 0))
        updateResourcesForNewTrainer()
    }

    override fun onBindDialogView(view: View) {
        mGridView = view.findViewById(R.id.grid)
        mAdapter = ImageAdapter(mContext, mBitmaps)
        mAdapter.positionChosen(mPokemonID)
        mGridView.adapter = mAdapter
        mGridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            persistInt(position)
            mPokemonID = position
            Toast.makeText(mContext, "" + mPokemonNames[position], Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        super.onBindDialogView(view)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "pref_trainer_key") {
            mCurrentTrainer = Trainer.getTypeByID(mSettings.getInt("pref_trainer_key", 0))
            mPokemonID = 0
            mSettings.edit().putInt("pref_pokemon_key", mPokemonID).apply()
            updateResourcesForNewTrainer()
        }
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            mPokemonID = getPersistedInt(DEFAULT_ID)
        } else {
            persistInt(mPokemonID)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            persistInt(mPokemonID)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        if (isPersistent) {
            return superState
        }

        val myState = SavedState(superState)
        myState.value = mPokemonID
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state.javaClass != SavedState::class.java) {
            super.onRestoreInstanceState(state)
            return
        }

        val myState = state as SavedState?
        super.onRestoreInstanceState(myState!!.superState)

        mPokemonID = myState.value
    }

    fun updateResourcesForNewTrainer() {
        mPokemonArr = PokemonResources.Companion.getPokemonForTrainer(mCurrentTrainer)

        mBitmaps = Array(mPokemonArr.size, {i ->
            BitmapFactory.decodeResource(mContext.resources, PokemonResources.getPokemonPortrait(mPokemonArr[i]))
        })

        mPokemonNames = Array(mPokemonArr.size, {i ->
            PokemonResources.getPokemonName(mPokemonArr[i], mContext)
        })
    }
}