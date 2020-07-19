package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.spaservicegame

import android.content.Context
import androidx.preference.PreferenceManager

object SpaServiceUtils {
    data class StageAndRoundProperties(val completeRows: Int, val numOfBlocks: Int, val gameSpeed: Int, val linesToWin: Int)

    fun getCurrentStage(context: Context): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPreferences.getInt(STAGE_KEY, DEFAULT_STAGE)
    }

    fun getCurrentRound(context: Context): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPreferences.getInt(ROUND_KEY, DEFAULT_ROUND)
    }

    fun completedRoundInStage(context: Context, stage: Int, round: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        when {
            stage >= MAX_NUM_OF_STAGES && round >= MAX_NUM_OF_ROUNDS -> {
                sharedPreferences.edit().putInt(STAGE_KEY, DEFAULT_STAGE).apply()
                sharedPreferences.edit().putInt(ROUND_KEY, DEFAULT_ROUND).apply()
            }
            round >= MAX_NUM_OF_ROUNDS -> {
                sharedPreferences.edit().putInt(STAGE_KEY, stage + 1).apply()
                sharedPreferences.edit().putInt(ROUND_KEY, DEFAULT_ROUND).apply()
            }
            else -> {
                sharedPreferences.edit().putInt(ROUND_KEY, round + 1).apply()
            }
        }
    }

    fun getStageAndRoundProperties(stage: Int, round: Int): StageAndRoundProperties {
        val rowDefault = 3
        val numOfBlocksDefault = 30
        val gameSpeedDefault = 1
        val numOfLinesDefault = 5

        if (stage < STAGE_ROUND_PROPERTY_MAPPINGS.size) {
            if (round < STAGE_ROUND_PROPERTY_MAPPINGS[stage].size) {
                val currentRound = STAGE_ROUND_PROPERTY_MAPPINGS[stage][round]
                return StageAndRoundProperties(currentRound[ROWS_KEY] ?: rowDefault, currentRound[NUM_OF_BLOCKS_KEY] ?: numOfBlocksDefault, currentRound[GAME_SPEED_KEY] ?: gameSpeedDefault, currentRound[NUM_OF_LINES_TO_WIN_KEY] ?: numOfLinesDefault)
            }
        }

        return StageAndRoundProperties(rowDefault, numOfBlocksDefault, gameSpeedDefault, numOfLinesDefault)
    }

    private const val MAX_NUM_OF_STAGES = 3
    private const val MAX_NUM_OF_ROUNDS = 5
    private const val ROWS_KEY = "rows"
    private const val NUM_OF_BLOCKS_KEY = "num_of_blocks"
    private const val GAME_SPEED_KEY = "game_speed"
    private const val NUM_OF_LINES_TO_WIN_KEY = "lines_to_win"
    private val STAGE_ROUND_PROPERTY_MAPPINGS = arrayOf(
            arrayOf(        // Stage 1
                    hashMapOf(
                            ROWS_KEY to 2,
                            NUM_OF_BLOCKS_KEY to 18,
                            GAME_SPEED_KEY to 1,
                            NUM_OF_LINES_TO_WIN_KEY to 5
                    ),
                    hashMapOf(
                            ROWS_KEY to 3,
                            NUM_OF_BLOCKS_KEY to 24,
                            GAME_SPEED_KEY to 2,
                            NUM_OF_LINES_TO_WIN_KEY to 5
                    ),
                    hashMapOf(
                            ROWS_KEY to 3,
                            NUM_OF_BLOCKS_KEY to 30,
                            GAME_SPEED_KEY to 3,
                            NUM_OF_LINES_TO_WIN_KEY to 5
                    ),
                    hashMapOf(
                            ROWS_KEY to 2,
                            NUM_OF_BLOCKS_KEY to 36,
                            GAME_SPEED_KEY to 4,
                            NUM_OF_LINES_TO_WIN_KEY to 5
                    ),
                    hashMapOf(
                            ROWS_KEY to 5,
                            NUM_OF_BLOCKS_KEY to 42,
                            GAME_SPEED_KEY to 5,
                            NUM_OF_LINES_TO_WIN_KEY to 5
                    )
            ),
            arrayOf(        // Stage 2
                    hashMapOf(
                            ROWS_KEY to 3,
                            NUM_OF_BLOCKS_KEY to 24,
                            GAME_SPEED_KEY to 9,
                            NUM_OF_LINES_TO_WIN_KEY to 7
                    ),
                    hashMapOf(
                            ROWS_KEY to 4,
                            NUM_OF_BLOCKS_KEY to 30,
                            GAME_SPEED_KEY to 10,
                            NUM_OF_LINES_TO_WIN_KEY to 6
                    ),
                    hashMapOf(
                            ROWS_KEY to 4,
                            NUM_OF_BLOCKS_KEY to 36,
                            GAME_SPEED_KEY to 11,
                            NUM_OF_LINES_TO_WIN_KEY to 5
                    ),
                    hashMapOf(
                            ROWS_KEY to 4,
                            NUM_OF_BLOCKS_KEY to 42,
                            GAME_SPEED_KEY to 12,
                            NUM_OF_LINES_TO_WIN_KEY to 5
                    ),
                    hashMapOf(
                            ROWS_KEY to 7,
                            NUM_OF_BLOCKS_KEY to 48,
                            GAME_SPEED_KEY to 13,
                            NUM_OF_LINES_TO_WIN_KEY to 6
                    )
            ),
            arrayOf(        // Stage 3
                    hashMapOf(
                            ROWS_KEY to 4,
                            NUM_OF_BLOCKS_KEY to 30,
                            GAME_SPEED_KEY to 16,
                            NUM_OF_LINES_TO_WIN_KEY to 8
                    ),
                    hashMapOf(
                            ROWS_KEY to 4,
                            NUM_OF_BLOCKS_KEY to 36,
                            GAME_SPEED_KEY to 17,
                            NUM_OF_LINES_TO_WIN_KEY to 7
                    ),
                    hashMapOf(
                            ROWS_KEY to 6,
                            NUM_OF_BLOCKS_KEY to 42,
                            GAME_SPEED_KEY to 18,
                            NUM_OF_LINES_TO_WIN_KEY to 6
                    ),
                    hashMapOf(
                            ROWS_KEY to 7,
                            NUM_OF_BLOCKS_KEY to 48,
                            GAME_SPEED_KEY to 19,
                            NUM_OF_LINES_TO_WIN_KEY to 6
                    ),
                    hashMapOf(
                            ROWS_KEY to 8,
                            NUM_OF_BLOCKS_KEY to 54,
                            GAME_SPEED_KEY to 20,
                            NUM_OF_LINES_TO_WIN_KEY to 7
                    )
            )
    )
    const val STAGE_KEY = "stage"
    const val ROUND_KEY = "round"
    const val DEFAULT_STAGE = 0
    const val DEFAULT_ROUND = 0
}