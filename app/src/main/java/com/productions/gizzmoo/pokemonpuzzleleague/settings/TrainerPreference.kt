package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import androidx.preference.DialogPreference
import android.util.AttributeSet
import com.productions.gizzmoo.pokemonpuzzleleague.TrainerResources

class TrainerPreference(context: Context, attrs: AttributeSet? = null) : DialogPreference(context, attrs) {
    val bitmaps: Array<Bitmap>
    var trainerID = DEFAULT_ID
        private set
    private var trainerNames: Array<String>

    init {
        val imageResources = TrainerResources.getAllTrainerPortraits()
        bitmaps = Array(imageResources.size) { i -> BitmapFactory.decodeResource(context.resources, imageResources[i])}
        trainerNames = TrainerResources.getTrainerNames(context)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            trainerID = getPersistedInt(DEFAULT_ID)
        } else {
            persistInt(trainerID)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.value = trainerID
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        val myState = state as SavedState?
        super.onRestoreInstanceState(myState?.superState)

        trainerID = myState?.value ?: DEFAULT_ID
    }

    override fun getSummary(): CharSequence {
        return trainerNames[trainerID]
    }

    fun persistTrainerID(newTrainerID: Int) {
        trainerID = newTrainerID
        persistInt(newTrainerID)
        summary = trainerNames[trainerID]
    }

    companion object {
        const val DEFAULT_ID = 0 // Ash
    }
}