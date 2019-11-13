package com.productions.gizzmoo.pokemonpuzzleleague

import android.os.AsyncTask

class PanningLoop : AsyncTask<Void, Void, Void>() {
    var listener: PanningLoopListener? = null

    override fun doInBackground(vararg voids: Void): Void? {
        while (!this.isCancelled) {
            try {
                Thread.sleep(FRAME_PERIOD.toLong())
            } catch (e: InterruptedException) {
            }

            publishProgress()
        }

        return null
    }

    override fun onProgressUpdate(vararg values: Void) {
        listener?.updateImageView()
    }

    companion object {
        const val MAX_FPS = 30
        private const val FRAME_PERIOD = 1000 / MAX_FPS
        const val NUM_OF_SECS_TO_PAN_VIEW = 10
    }
}