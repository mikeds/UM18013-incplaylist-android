package com.incplaylist.incoriginalmedia

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class TracksData {
    val storage = Storage()
    fun getTrackData(context: Context) :JSONObject?{
        val from = JSONObject(storage.getNowPlaying(context)!!).getString("from")
        val title = JSONObject(storage.getNowPlaying(context)!!).getString("title")
        val id = JSONObject(storage.getNowPlaying(context)!!).getString("id")
        var trackData = JSONObject()
        when (from){
            "DJ"->{
                trackData.put("stream_url", "https://edge.mixlr.com/channel/wycvw")
            }
            "Favorites"->{
                val playlists = JSONArray(storage.getPlaylistTracks(context))
                for (i in 0 until playlists.length()){
                    val playlistTracks = JSONArray(playlists.getJSONObject(i).getString("tracks"))
                    for (j in 0 until playlistTracks.length()){
                        val tracks = playlistTracks.getJSONObject(j)
                        if (id == tracks.getString("id")){
                            val trackID = tracks.getString("id")
                            val dir = File(context.filesDir, "elpaboritos")
                            val path = "$dir/$trackID.mp3"
                            tracks.put("stream_url", path)
                            Log.e("plath", path)
                            trackData = tracks
                            break
                        }
                    }
                }
            }
            else->{
                val playlists = JSONArray(storage.getPlaylistTracks(context))
                for (i in 0 until playlists.length()){
                    val playlistTracks = JSONArray(playlists.getJSONObject(i).getString("tracks"))
                    for (j in 0 until playlistTracks.length()){
                        val tracks = playlistTracks.getJSONObject(j)
                        if (id == tracks.getString("id")){
                            trackData = tracks
                            break
                        }
                    }
                }
            }
        }

        return trackData
    }

    fun getPlaylistData(context: Context, id:String):JSONObject{
        var PlaylistData = JSONObject()
        val Playlists = JSONArray(storage.getPlaylistTracks(context))
        for (i in 0 until Playlists.length()){
            if (Playlists.getJSONObject(i).getString("id") == id){
                PlaylistData = Playlists.getJSONObject(i)

                break
            }
        }


        return PlaylistData
    }



}