package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.graphics.Point
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameDialogFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.NUM_OF_COLS
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.NUM_OF_ROWS
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class PuzzleAcademyFragment : GameFragment<PuzzleAcademyGameLoop>(), PuzzleAcademyGameLoop.PuzzleAcademyGameLoopListener {
    var listener: PuzzleAcademyFragmentInterface? = null
    var puzzleId = 0

    override fun gameStatusChanged(newStatus: GameStatus?) {}

    override fun createGameLoop(): PuzzleAcademyGameLoop {
        lateinit var grid: Array<Array<Block>>
        val moves: Int

        try {
            val json = JSONObject(JSONUtils.getJSONStringFromFile(activity))
            val stage = JSONUtils.getStageObjectFromKey(json, 1)
            val level = getLevelObjectFromKey(stage, puzzleId)
            grid = getGridFromLevel(level)
            moves = getMovesFromLevel(level)
            doSanityChecks(grid, moves)
        } catch (ex: org.json.JSONException) {
            throw Exception("There was a parsing error with the JSON file")
        }

        listener?.updateNumOfSwaps(moves)
        return PuzzleAcademyGameLoop(grid, moves)
    }

    override fun numberOfBlocksMatched() {}

    override fun gameFinished(didWin: Boolean) {
        val newFragment = GameDialogFragment.newInstance(didWin)
        newFragment.show(activity.fragmentManager, "postDialog")
    }

    override fun switchBlock(switcherLeftBlock: Point) {
        super.switchBlock(switcherLeftBlock)
        gameLoop.numOfSwapsLeft--
        listener?.updateNumOfSwaps(gameLoop.numOfSwapsLeft)
    }

    override fun updateGameTime(timeInMilli: Long) {
        listener?.updateGameTime(timeInMilli)
    }

    interface PuzzleAcademyFragmentInterface {
        fun updateGameTime(timeInMilli: Long)
        fun updateNumOfSwaps(swapsLeft: Int)
    }

    private fun getLevelObjectFromKey(jsonObject: JSONArray, level: Int): JSONObject =
        jsonObject[level] as JSONObject

    private fun getGridFromLevel(level: JSONObject): Array<Array<Block>> {
        val gridJson = level.getJSONArray("grid")
        return Array(gridJson.length())
                { i -> Array((gridJson[i] as JSONArray).length())
                        { j -> Block((gridJson[i] as JSONArray).getInt(j), j, i) }}
    }

    private fun getMovesFromLevel(level: JSONObject): Int =
        level.getInt("moves")

    private fun doSanityChecks(grid: Array<Array<Block>>, moves: Int) {
        if (moves < 0) {
            throw Exception("There are negative moves in the JSON")
        }

        if (grid.size != NUM_OF_ROWS) {
            throw Exception("The number of rows in the JSON file need to be $NUM_OF_ROWS")
        }

        for (i in 0 until(grid.size)) {
            if (grid[i].size != NUM_OF_COLS) {
                throw Exception("The number of columns in row ${i + 1} in the JSON file need to be $NUM_OF_COLS")
            }
        }
    }

}