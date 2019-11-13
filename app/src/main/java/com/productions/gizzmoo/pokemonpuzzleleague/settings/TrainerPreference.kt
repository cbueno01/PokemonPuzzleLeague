package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.TrainerResources

class TrainerPreference(context: Context, attrs: AttributeSet? = null) : DialogPreference(context, attrs) {
    private var trainerID = DEFAULT_ID

    private lateinit var gridView: GridView
    private lateinit var portraitAdapter: ImagePortraitAdapter

    private var bitmaps: Array<Bitmap>
    private var mTrainerNames: Array<String>

    init {
        dialogLayoutResource = R.layout.trainer_preference
        val imageResources = TrainerResources.getAllTrainerPortraits()
        bitmaps = Array(imageResources.size) { i -> BitmapFactory.decodeResource(context.resources, imageResources[i])}
        mTrainerNames = TrainerResources.getTrainerNames(context)
    }


    override fun onBindDialogView(view: View) {
        gridView = view.findViewById(R.id.grid)
        portraitAdapter = ImagePortraitAdapter(context, bitmaps)
        portraitAdapter.chosenPosition = trainerID
        gridView.adapter = portraitAdapter
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            persistInt(position)
            trainerID = position
            Toast.makeText(context, "" + mTrainerNames[position], Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        super.onBindDialogView(view)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            trainerID = getPersistedInt(DEFAULT_ID)
        } else {
            persistInt(trainerID)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            persistInt(trainerID)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        if (isPersistent) {
            return superState
        }

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

    companion object {
        const val DEFAULT_ID = 0 // Ash
    }
}