package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.content.Context

object FileManager {
    // TODO: manage all the different stages here.
    private var jsonReaderWriter: JSONReaderWriter? = null

    fun getJSONReaderWriter(context: Context, stage: Int): JSONReaderWriter {
        if (jsonReaderWriter == null) {
            jsonReaderWriter = JSONReaderWriter(context, stage)
        }

        return jsonReaderWriter!!
    }
}