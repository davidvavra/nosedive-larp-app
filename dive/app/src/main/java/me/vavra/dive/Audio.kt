package me.vavra.dive

import android.app.Application
import android.media.MediaPlayer

class Audio(private val app: Application) {
    private var currentSound: Int? = null

    fun play(sound: Int) {
        if (currentSound != sound) {
            currentSound = sound
            var mediaPlayer = MediaPlayer.create(app, sound)
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.reset()
                mediaPlayer.release()
                currentSound = null
                mediaPlayer = null
            }
            mediaPlayer.start()
        }
    }
}