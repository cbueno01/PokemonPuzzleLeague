package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.music.GameMusicService
import com.productions.gizzmoo.pokemonpuzzleleague.music.ServiceBinder

abstract class GameActivity : AppCompatActivity(), ServiceConnection {
    protected var musicService: GameMusicService? = null
    protected var isMusicServiceBound: Boolean = false
    private var trackPosition: Int = 0

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        musicService = ((binder as ServiceBinder).service as GameMusicService)
        isMusicServiceBound = true
        playMusicOnStartUp()
    }

    override fun onServiceDisconnected(name: ComponentName) {
        musicService = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackPosition = savedInstanceState?.getInt(trackPositionKey) ?: 0
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        removeActionBar()
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    protected open fun playMusicOnStartUp() {
        startMusic()
    }

    protected fun startMusic() {
        musicService?.startMusic(trackPosition, shouldPlayPanicMusic())
    }

    @SuppressLint("RestrictedApi")
    private fun removeActionBar() {
        supportActionBar?.setShowHideAnimationEnabled(false)
        supportActionBar?.hide()
    }

    abstract fun shouldPlayPanicMusic(): Boolean

    companion object {
        private const val trackPositionKey = "TRACK_POSITION_KEY"
    }
}
