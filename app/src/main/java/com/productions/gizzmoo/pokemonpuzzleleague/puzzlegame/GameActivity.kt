package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.FragmentActivity
import android.view.View
import com.productions.gizzmoo.pokemonpuzzleleague.PokemonPuzzleLeagueActivity
import com.productions.gizzmoo.pokemonpuzzleleague.music.GameMusicService
import com.productions.gizzmoo.pokemonpuzzleleague.music.MusicService
import com.productions.gizzmoo.pokemonpuzzleleague.music.ServiceBinder

abstract class GameActivity : PokemonPuzzleLeagueActivity(), ServiceConnection {
    protected var musicService: GameMusicService? = null
    protected var isMusicServiceBound: Boolean = false
    private var trackPosition: Int = 0

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        musicService = ((binder as ServiceBinder).service as GameMusicService)
        musicService?.startMusic(trackPosition, shouldPlayPanicMusic())
        isMusicServiceBound = true
    }

    override fun onServiceDisconnected(name: ComponentName) {
        musicService = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackPosition = savedInstanceState?.getInt(trackPositionKey) ?: 0
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(trackPositionKey, trackPosition)
        super.onSaveInstanceState(outState)
    }

    public override fun onStart() {
        super.onStart()
        bindService(Intent(this, GameMusicService::class.java), this, Context.BIND_AUTO_CREATE)

    }

    public override fun onStop() {
        super.onStop()

        if (isMusicServiceBound) {
            trackPosition = musicService?.stopMusic() ?: 0
            unbindService(this)
            isMusicServiceBound = false
        }
    }

    abstract fun shouldPlayPanicMusic(): Boolean

    companion object {
        private const val trackPositionKey = "TRACK_POSITION_KEY"
    }
}
