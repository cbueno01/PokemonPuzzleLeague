package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.graphics.Point
import android.media.SoundPool
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.productions.gizzmoo.pokemonpuzzleleague.Direction
import com.productions.gizzmoo.pokemonpuzzleleague.PokemonResources
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.Trainer
import com.productions.gizzmoo.pokemonpuzzleleague.TrainerResources
import com.productions.gizzmoo.pokemonpuzzleleague.settings.PokemonPreference
import com.productions.gizzmoo.pokemonpuzzleleague.settings.TrainerPreference

abstract class GameFragment<U : GameLoopListener, T : GameLoop<U>, V : PuzzleBoardView> : Fragment(), IBoard, GameLoopListener {
    protected lateinit var boardView: V
    lateinit var gameLoop: T
        protected set

    private var tempGrid: Array<Array<Block>>? = null
    private var tempSwitcher: SwitchBlocks? = null
    private var gameStartTime: Long = 0

    private lateinit var soundPool: SoundPool
    private var loadedSoundPool: Boolean = false
    private var switchSoundID: Int = 0
    private var trainerSoundID: Int = 0
    private var moveSoundID: Int = 0
    private val popSoundIDs = IntArray(4)
    private val pokemonSoundIDs = IntArray(4)
    private var isGameSoundEnabled: Boolean = true

    private val popSoundResources = intArrayOf(R.raw.pop_sound_1, R.raw.pop_sound_2, R.raw.pop_sound_3, R.raw.pop_sound_4)
    private lateinit var pokemonSoundResources: Array<Int>

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        val pokemonIndex = settings.getInt("pref_pokemon_key", PokemonPreference.DEFAULT_ID)
        val trainer = getCurrentTrainer()
        isGameSoundEnabled = settings.getBoolean("pref_game_sound", true)
        pokemonSoundResources = PokemonResources.getPokemonComboResources(PokemonResources.getPokemonForTrainer(trainer)[pokemonIndex])

        if (savedInstanceState != null) {
            tempGrid = savedInstanceState.getSerializable(BOARD_KEY) as Array<Array<Block>>
            tempSwitcher = savedInstanceState.getSerializable(BLOCK_SWITCHER_KEY) as SwitchBlocks
            gameStartTime = savedInstanceState.getLong(GAME_START_TIME_KEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.game_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        boardView = createPuzzleBoardView().apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        (view as ViewGroup).addView(boardView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setGameSound()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onStart() {
        super.onStart()

        gameLoop = createGameLoop()
        boardView.listener = this
        gameLoop.listener = this as U

        if (tempSwitcher != null || tempGrid != null) {
            gameLoop.setGameProperties(tempGrid!!, tempSwitcher!!, gameStartTime)
            tempGrid = null
            tempSwitcher = null
        }

        boardView.setGrid(gameLoop.grid, gameLoop.blockSwitcher)
        startGame()
    }

    override fun onStop() {
        super.onStop()

        tempGrid = gameLoop.grid
        tempSwitcher = gameLoop.blockSwitcher
        gameStartTime = gameLoop.startTime
        gameLoop.cancel(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(BOARD_KEY, gameLoop.grid)
        outState.putSerializable(BLOCK_SWITCHER_KEY, gameLoop.blockSwitcher)
        outState.putLong(GAME_START_TIME_KEY, gameLoop.startTime)
    }

    override fun switchBlock(switcherLeftBlock: Point) {
        startSwitchAnimation(switcherLeftBlock, gameLoop.grid)
        gameLoop.swapBlocks(switcherLeftBlock.x, switcherLeftBlock.y, switcherLeftBlock.x + 1, switcherLeftBlock.y)

        if (loadedSoundPool && switchSoundID != 0 && isGameSoundEnabled) {
            soundPool.play(switchSoundID, 1f, 1f, SWITCH_SOUND_PRIORITY, 0, 1f)
        }
    }

    override fun switchBlockMoved() {
        if (loadedSoundPool && moveSoundID != 0 && isGameSoundEnabled) {
            soundPool.play(moveSoundID, 1f, 1f, MOVE_SOUND_PRIORITY, 0, 1f)
        }
    }

    override fun blockIsPopping(position: Int, total: Int) {
        val soundID = getPopSoundID(position, total)
        if (loadedSoundPool && soundID != 0 && isGameSoundEnabled) {
            soundPool.play(soundID, 1f, 1f, POP_SOUND_PRIORITY, 0, 1f)
        }
    }

    override fun playPokemonSound(comboNumber: Int) {
        val pokemonSoundID = getPokemonSoundID(comboNumber)
        if (loadedSoundPool && pokemonSoundID != 0 && isGameSoundEnabled) {
            soundPool.play(pokemonSoundID, 1f, 1f, POKEMON_SOUND_PRIORITY, 0, 1f)
        }
    }

    override fun playTrainerSound(isMetallic: Boolean) {
        if (loadedSoundPool && trainerSoundID != 0 && isGameSoundEnabled) {
            soundPool.play(trainerSoundID, 1f, 1f, TRAINER_SOUND_PRIORITY, 0, 1f)
        }
    }

    override fun updateBoardView() {
        boardView.invalidate()
    }

    protected open fun startGame() {
        gameLoop.startGame()
    }

    private fun setGameSound() {
        val trainer = getCurrentTrainer()

        soundPool = SoundPool.Builder().setMaxStreams(2).build()
        soundPool.setOnLoadCompleteListener { _, _, _ -> loadedSoundPool = true }

        trainerSoundID = soundPool.load(activity?.applicationContext, TrainerResources.getTrainerComboSound(trainer), 1)
        switchSoundID = soundPool.load(activity?.applicationContext, R.raw.switch_sound, 1)
        moveSoundID = soundPool.load(activity?.applicationContext, R.raw.move_sound, 1)

        for (i in pokemonSoundResources.indices) {
            pokemonSoundIDs[i] = soundPool.load(activity?.applicationContext, pokemonSoundResources[i], 1)
        }

        for (i in popSoundResources.indices) {
            popSoundIDs[i] = soundPool.load(activity?.applicationContext, popSoundResources[i], 1)
        }
    }

    private fun getPopSoundID(pos: Int, total: Int): Int {
        val index = (pos.toFloat() / total * 4).toInt()
        return popSoundIDs[index]
    }

    private fun getPokemonSoundID(comboCount: Int): Int {
        return when {
            comboCount <= 0 -> 0
            comboCount <= 2 -> pokemonSoundIDs[0]
            comboCount == 3 -> pokemonSoundIDs[1]
            comboCount == 4 -> pokemonSoundIDs[2]
            else -> pokemonSoundIDs[3]
        }
    }

    private fun getCurrentTrainer(): Trainer {
        val settings = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        return Trainer.getTypeByID(settings.getInt("pref_trainer_key", TrainerPreference.DEFAULT_ID))
    }

    private fun startSwitchAnimation(leftBlockSwitch: Point, blocks: Array<Array<Block>>) {
        blocks[leftBlockSwitch.y][leftBlockSwitch.x].startSwitchAnimation(Direction.Right)
        blocks[leftBlockSwitch.y][leftBlockSwitch.x + 1].startSwitchAnimation(Direction.Left)
    }

    protected abstract fun createGameLoop(): T

    protected abstract fun createPuzzleBoardView(): V

    companion object {
        private const val BOARD_KEY = "BOARD_KEY"
        private const val BLOCK_SWITCHER_KEY = "BLOCK_SWITCHER_KEY"
        private const val GAME_START_TIME_KEY = "GAME_START_TIME_KEY"

        private const val POKEMON_SOUND_PRIORITY = 4
        private const val TRAINER_SOUND_PRIORITY = 3
        private const val POP_SOUND_PRIORITY = 2
        private const val SWITCH_SOUND_PRIORITY = 1
        private const val MOVE_SOUND_PRIORITY = 0
    }
}