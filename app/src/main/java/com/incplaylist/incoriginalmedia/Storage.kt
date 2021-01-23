package com.incplaylist.incoriginalmedia

import android.content.Context

class Storage {

    fun setTheme(context: Context, data: String){
        val sharedPreference =  context.getSharedPreferences("Data", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("Theme",data)
        editor.apply()
    }

    fun getTheme(context: Context):String?{
        val sharedPreference =  context.getSharedPreferences("Data", Context.MODE_PRIVATE)
        return sharedPreference.getString("Theme","")
    }

    fun storePlaylistTracks(context:Context, data: String) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("PlaylistTracks",data)
        editor.apply()
    }

    fun getPlaylistTracks(context:Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getString("PlaylistTracks","")
    }

    fun track_count(context:Context, data: String) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("track_count",data)
        editor.apply()
    }

    fun getTrack_count(context:Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getString("track_count","")
    }

    fun nowPlaying(context:Context, data: String) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("nowPlayingData",data)
        editor.apply()
    }

    fun getNowPlaying(context:Context): String? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getString("nowPlayingData","")
    }

    fun setPlaying(context:Context, data: Boolean) {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean("isPlaying",data)
        editor.apply()
    }

    fun isPlaying(context:Context): Boolean? {
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("isPlaying",false)
    }

    fun firstUser(context: Context, unangbeses:Boolean){
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean("firstTimer",unangbeses)
        editor.apply()
    }

    fun isFirstTimeUser(context: Context):Boolean?{
        val sharedPreference =  context.getSharedPreferences("Data",Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("firstTimer", true)
    }
}