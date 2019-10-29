package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.content.Context
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.Companion.NUM_OF_COLS
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.Companion.NUM_OF_ROWS
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

class JSONReaderWriter(context: Context, stage: Int) {
    private lateinit var jsonFile: JSONObject
    private lateinit var jsonStage: JSONArray

    init {
        try {
            jsonFile = getJSONObjectFromFile(context)
            jsonStage = getStageObject(jsonFile, stage)
        } catch (ex: IOException) {}
    }

    fun getGridFromLevel(level: Int): Array<Array<Block>> {
        val gridJson = getLevelOrCurrentJSONObject(level).getJSONArray(JSON_GRID_KEY)
        // TODO: Check board sanity here
        val grid = Array(gridJson.length())
        { i -> Array((gridJson[i] as JSONArray).length())
        { j -> Block((gridJson[i] as JSONArray).getInt(j), j, i) }}

        if (!isBoardValid(grid)) {
            throw MalformedJSONFileException("The main board is not a valid configuration.")
        }
        return grid
    }

    fun getMovesFromLevel(level: Int): Int {
        val moves = getLevelOrCurrentJSONObject(level).getInt(JSON_MOVES_KEY)
        if (moves < 0) {
            throw MalformedJSONFileException("Number of moves is less than 0.")
        }
        return moves
    }

    fun getHistoryFromLevel(level: Int): Stack<Array<Array<Int>>> {
        val stack: Stack<Array<Array<Int>>> = Stack()
        val currentJSON = getLevelOrCurrentJSONObject(level)
        if (currentJSON.has(JSON_HISTORY_KEY)) {
            val boardHistoryJSON = currentJSON.getJSONArray(JSON_HISTORY_KEY)
            for (i in (boardHistoryJSON.length() - 1) downTo 0) {
                val boardHistoryJSONGrid = boardHistoryJSON[i] as JSONArray
                val boardHistoryGrid = jsonToIntGrid(boardHistoryJSONGrid)
                if (!isBoardValid(boardHistoryGrid)) {
                    throw MalformedJSONFileException("Board history is not a valid configuration.")
                }
                stack.push(boardHistoryGrid)
            }
        }

        return stack
    }

    fun writeToFile(context: Context, level: Int, grid: Array<Array<Block>>, moves: Int, boardHistory: Stack<Array<Array<Int>>>) {
        val currentJSONObject: JSONObject = getCurrentJSONObject(level)
        currentJSONObject.put(JSON_GRID_KEY, blockGridToJSON(grid))
        currentJSONObject.put(JSON_MOVES_KEY, moves)
        currentJSONObject.put(JSON_HISTORY_KEY, boardHistoryToJSON(boardHistory))
        getLevelJSONObject(level).put(JSON_CURRENT_OBJECT_KEY, currentJSONObject)
        writeJSONObjectToFile(context)
    }

    fun clearCurrentJSONObject(context: Context, level: Int, didWin: Boolean) {
        var shouldWriteFile = false
        val levelJson = getLevelJSONObject(level)
        if (levelJson.has(JSON_CURRENT_OBJECT_KEY)) {
            levelJson.remove(JSON_CURRENT_OBJECT_KEY)
            shouldWriteFile = true
        }

        if (didWin) {
            levelJson.put(JSON_DID_COMPLETE_KEY, true)
            shouldWriteFile = true
        }

        if (shouldWriteFile) {
            writeJSONObjectToFile(context)
        }
    }

    fun getNumOfLevels(): Int =
        jsonStage.length()


    fun didWinLevel(level: Int): Boolean =
        getLevelJSONObject(level).getBoolean(JSON_DID_COMPLETE_KEY)

    private fun getJSONObjectFromFile(context: Context): JSONObject {
        var jsonString = ""
        try {
            val inStream = getInputStream(context)
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

    private fun writeJSONObjectToFile(context: Context) {
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

    private fun getInputStream(context: Context): InputStream {
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

    private fun getLevelJSONObject(level: Int): JSONObject =
            jsonStage[level] as JSONObject

    private fun getCurrentJSONObject(level: Int): JSONObject {
        val jsonLevel = getLevelJSONObject(level)
        return if (jsonLevel.has(JSON_CURRENT_OBJECT_KEY)) jsonLevel.getJSONObject(JSON_CURRENT_OBJECT_KEY) else JSONObject()
    }

    private fun getLevelOrCurrentJSONObject(level: Int): JSONObject {
        val jsonLevel = getLevelJSONObject(level)
        return if (jsonLevel.has(JSON_CURRENT_OBJECT_KEY)) jsonLevel.getJSONObject(JSON_CURRENT_OBJECT_KEY) else jsonLevel
    }

    private fun jsonToIntGrid(jsonArray: JSONArray): Array<Array<Int>> =
        Array(jsonArray.length())
            { i -> Array((jsonArray[i] as JSONArray).length())
                { j -> (jsonArray[i] as JSONArray).getInt(j) }}


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

    private fun <T> isBoardValid(board: Array<Array<T>>): Boolean {
        if (board.size != NUM_OF_ROWS) {
            return false
        }

        for (i in 0 until(board.size)) {
            if (board[i].size != NUM_OF_COLS) {
                return false
            }
        }

        return true
    }

    class CannotReadFileException(message: String) : Exception(message)
    class CannotWriteFileException(message: String) : Exception(message)
    class MalformedJSONFileException(message: String) : Exception(message)

    companion object {
        private const val FILENAME = "puzzle_academy_stages.json"
        private const val STAGE_KEY = "stage"
        const val JSON_GRID_KEY = "grid"
        const val JSON_MOVES_KEY = "moves"
        const val JSON_CURRENT_OBJECT_KEY = "current"
        const val JSON_DID_COMPLETE_KEY = "did_complete"
        const val JSON_HISTORY_KEY = "history"

        /*
        stage - level[]

        level - grid
        level - moves
        level - did_complete
        level - current

        grid - Int[][]

        moves - Int

        did_complete - Boolean

        current - grid
        current - moves
        current - history

        history - grid[] - oldest....newest
        */
    }
}

