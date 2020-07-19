package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameActivity
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus

abstract class RisingGameActivity : GameActivity(), RisingFragmentInterface {
    protected lateinit var gameFragment: RisingGameFragment<*, *, *>
    // Game may have started but music is not ready.
    private var gameStarted = false

    override fun onStop() {
        super.onStop()
        gameStarted = false
    }

    override fun changeSong(isPanic: Boolean) {
        if (isMusicServiceBound) {
            musicService?.changeSong(isPanic)
        }
    }

    override fun shouldPlayPanicMusic(): Boolean =
            gameFragment.gameLoop.status != GameStatus.Running

    override fun onGameStarted() {
        if (isMusicServiceBound) {
            startMusic()
        }
        gameStarted = true
    }

    override fun playMusicOnStartUp() {
        if (gameStarted) {
            super.playMusicOnStartUp()
        }
    }

    override fun onGameFinished() {
        musicService?.stopMusic()
    }
}