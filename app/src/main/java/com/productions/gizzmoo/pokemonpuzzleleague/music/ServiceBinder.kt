package com.productions.gizzmoo.pokemonpuzzleleague.music

import android.os.Binder

class ServiceBinder(private val musicService: MusicService) : Binder() {
    val service: MusicService
        get() = musicService
}