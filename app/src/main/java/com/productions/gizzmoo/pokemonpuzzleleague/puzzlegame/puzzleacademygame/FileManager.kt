package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.content.Context
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

class FileManager(private val context: Context, stage: Int, level: Int = -1) {
    private lateinit var jsonFile: JSONObject
    private lateinit var jsonStage: JSONArray
    private lateinit var jsonLevel: JSONObject

    init {
        try {
            jsonFile = getJSONObjectFromFile()
            jsonStage = getStageObject(jsonFile, stage)
            if (level > -1) {
                jsonLevel = getLevelJSONObject(jsonStage, level)
            }
        } catch (ex: IOException) {}
    }

    fun getGridFromLevel(): Array<Array<Block>> {
        val gridJson = getLevelOrCurrentJSONObject().getJSONArray(JSON_GRID_KEY)
        // TODO: Check board sanity here
        return Array(gridJson.length())
        { i -> Array((gridJson[i] as JSONArray).length())
        { j -> Block((gridJson[i] as JSONArray).getInt(j), j, i) }}
    }

    fun getMovesFromLevel(): Int {
        // TODO: Check moves sanity here
        return getLevelOrCurrentJSONObject().getInt(JSON_MOVES_KEY)
    }

    fun getHistoryFromLevel(): Stack<Array<Array<Int>>> {
        val stack: Stack<Array<Array<Int>>> = Stack()
        val currentJSON = getLevelOrCurrentJSONObject()
        if (currentJSON.has(JSON_HISTORY_KEY)) {
            val boardHistoryJSON = currentJSON.getJSONArray(JSON_HISTORY_KEY)
            for (i in (boardHistoryJSON.length() - 1) downTo 0) {
                val boardHistoryJSONGrid = boardHistoryJSON[i] as JSONArray
                // TODO: Check board sanity here
                stack.push(jsonToIntGrid(boardHistoryJSONGrid))
            }
        }

        return stack
    }

    fun writeToFile(grid: Array<Array<Block>>, moves: Int, boardHistory: Stack<Array<Array<Int>>>) {
        val currentJSONObject: JSONObject = getCurrentJSONObject()
        currentJSONObject.put(JSON_GRID_KEY, blockGridToJSON(grid))
        currentJSONObject.put(JSON_MOVES_KEY, moves)
        currentJSONObject.put(JSON_HISTORY_KEY, boardHistoryToJSON(boardHistory))
        jsonLevel.put(JSON_CURRENT_OBJECT_KEY, currentJSONObject)
        writeJSONObjectToFile()
    }

    fun clearCurrentJSONObject() {
        if (jsonLevel.has(JSON_CURRENT_OBJECT_KEY)) {
            jsonLevel.remove(JSON_CURRENT_OBJECT_KEY)
            writeJSONObjectToFile()
        }
    }

    fun getNumOfLevels(): Int {
        return jsonStage.length()
    }

    private fun getJSONObjectFromFile(): JSONObject {
        var jsonString = ""
        try {
            val inStream = getInputStream()
            val size = inStream.available()
            val buffer = ByteArray(size)
            inStream.read(buffer)
            inStream.close()
            jsonString = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            CannotReadFileException("Couldn't read from internal or asset file. Error with message ${ex.message}")
        }

        return JSONObject(jsonString)
    }

    private fun writeJSONObjectToFile() {
        try {
            val fileOutputStream = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)
            val jsonString = jsonFile.toString()
            fileOutputStream.write(jsonString.toByteArray())
            fileOutputStream.close()
        } catch (fileNotFound: FileNotFoundException) {
            throw IOException(fileNotFound.message)
        } catch (ioException: IOException) {
            CannotWriteFileException("couldn't write to file. Error with message ${ioException.message}")
        }
    }

    private fun getInputStream(): InputStream {
        var inputStream: InputStream
        try {
            inputStream = context.openFileInput(FILENAME)
        } catch (ex: FileNotFoundException) {
            inputStream = context.assets.open(FILENAME)
        }

        return inputStream
    }

    private fun getStageObject(jsonObject: JSONObject, stage: Int): JSONArray =
        jsonObject.getJSONArray(STAGE_KEY + stage)

    private fun getLevelJSONObject(jsonObject: JSONArray, level: Int): JSONObject =
            jsonObject[level] as JSONObject

    private fun getCurrentJSONObject(): JSONObject =
            if (jsonLevel.has(JSON_CURRENT_OBJECT_KEY)) jsonLevel.getJSONObject(JSON_CURRENT_OBJECT_KEY) else JSONObject()

    private fun getLevelOrCurrentJSONObject(): JSONObject =
            if (jsonLevel.has(JSON_CURRENT_OBJECT_KEY)) jsonLevel.getJSONObject(JSON_CURRENT_OBJECT_KEY) else jsonLevel

    private fun jsonToIntGrid(jsonArray: JSONArray): Array<Array<Int>> {
        return Array(jsonArray.length())
        { i -> Array((jsonArray[i] as JSONArray).length())
        { j -> (jsonArray[i] as JSONArray).getInt(j) }}
    }

    private fun intGridToJSON(currentGrid: Array<Array<Int>>): JSONArray {
        val jsonGrid = JSONArray()
        for (row in currentGrid) {
            jsonGrid.put(JSONArray(row))
        }
        return jsonGrid
    }

    private fun blockGridToJSON(currentGrid: Array<Array<Block>>): JSONArray {
        val jsonGrid = JSONArray()
        for (row in currentGrid) {
            val jsonRow = JSONArray()
            for (block in row) {
                jsonRow.put(block.blockType.value)
            }
            jsonGrid.put(jsonRow)
        }
        return jsonGrid
    }

    private fun boardHistoryToJSON(boardHistory: Stack<Array<Array<Int>>>): JSONArray {
        val jsonArray = JSONArray()
        while (!boardHistory.empty()) {
            jsonArray.put(intGridToJSON(boardHistory.pop()))
        }
        return jsonArray
    }

    class CannotReadFileException(message: String) : Exception(message)
    class CannotWriteFileException(message: String) : Exception(message)

    companion object {
        private const val FILENAME = "puzzle_academy_stages.json"
        private const val STAGE_KEY = "stage"
        const val JSON_GRID_KEY = "grid"
        const val JSON_MOVES_KEY = "moves"
        const val JSON_CURRENT_OBJECT_KEY = "current"
        const val JSON_DID_COMPLETE_KEY = "did_complete"
        const val JSON_HISTORY_KEY = "history"
    }
}

