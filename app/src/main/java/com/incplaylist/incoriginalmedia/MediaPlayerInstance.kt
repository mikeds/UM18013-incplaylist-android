package com.incplaylist.incoriginalmedia

import android.media.MediaPlayer

class MediaPlayerInstance {
    companion object{
        private var instance = MediaPlayer()
        fun getInstance(): MediaPlayer {
            if (instance == null){
                instance = MediaPlayer()
            }
            return instance
        }
    }
}