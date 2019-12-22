package com.productions.gizzmoo.pokemonpuzzleleague.music

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import java.lang.IllegalStateException

abstract class MusicService : Service(), MediaPlayer.OnCompletionListener {
    private lateinit var binder: Binder
    private var mediaPlayerIndex: Int = 0
    private val mp = arrayOfNulls<MediaPlayer>(3)

    override fun onCreate() {
        super.onCreate()
        binder = ServiceBinder(this)
        mediaPlayerIndex = 0
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int = START_STICKY

    override fun onCompletion(curmp: MediaPlayer) {
        val mpPlaying: Int
        val mpNext: Int
        when (curmp){
            mp[0] -> {
                mpPlaying = 1
                mpNext = 2
            }
            mp[1] -> {
                mpPlaying = 2
                mpNext = 0
            }
            mp[2] -> {
                mpPlaying = 0
                mpNext = 1
            }
            else -> {
                mpPlaying = 0
                mpNext = 0
            }
        }
        mediaPlayerIndex = mpPlaying

        try {
            mp[mpNext]?.release()
            mp[mpNext] = MediaPlayer.create(this, getResource())
            mp[mpNext]?.setOnCompletionListener(this)
            mp[mpNext]?.setVolume(.7f, .7f)
            mp[mpPlaying]?.setNextMediaPlayer(mp[mpNext])
            mp[mpPlaying]?.setVolume(.7f, .7f)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    abstract fun getResource(): Int

    fun startMusic(position: Int) {
        // initialize and set listener to three media players
        for (i in mp.indices) {
            mp[i] = MediaPlayer.create(this, getResource())
            mp[i]?.setOnCompletionListener(this)
        }

        // set nextMediaPlayers
        mp[0]?.setNextMediaPlayer(mp[1])
        mp[1]?.setNextMediaPlayer(mp[2])
        mp[2]?.setNextMediaPlayer(mp[0])

        mp[mediaPlayerIndex]?.seekTo(position)
        mp[mediaPlayerIndex]?.setVolume(.7f, .7f)
        mp[mediaPlayerIndex]?.start()
    }

    fun stopMusic(): Int {
        var retValue = 0
        for (mediaPlayer in mp) {
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying) {
                        retValue = mediaPlayer.currentPosition
                        mediaPlayer.stop()
                    }
                } catch (ex: IllegalStateException) { retValue = 0 }
                mediaPlayer.release()
            }
        }

        return retValue
    }
}