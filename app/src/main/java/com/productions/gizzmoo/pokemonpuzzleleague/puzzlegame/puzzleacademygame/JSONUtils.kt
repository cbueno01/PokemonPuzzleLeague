package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

object JSONUtils {
    fun getJSONStringFromFile(context: Context): String? {
        var json: String? = null
        try {
            val inStream: InputStream = context.assets.open("puzzle_academy_stages.json")
            val size = inStream.available()
            val buffer = ByteArray(size)
            inStream.read(buffer)
            inStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return json
    }

    fun getStageObjectFromKey(jsonObject: JSONObject, stage: Int): JSONArray =
        jsonObject.getJSONArray("stage$stage")
}