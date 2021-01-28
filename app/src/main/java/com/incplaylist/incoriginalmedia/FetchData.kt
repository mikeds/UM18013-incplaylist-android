package com.incplaylist.incoriginalmedia

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.incplaylist.incoriginalmedia.Fragments.Fragment_Home
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.URL
import kotlin.math.ceil

@Suppress("DEPRECATION")

class FetchData (
        private val context: Context,
        private val from:String

) : AsyncTask<String, Void, String>() {

    val storage = Storage()

    val url1 = "https://api-v2.hearthis.at/incplaylist/"



    override fun onPreExecute() {
        super.onPreExecute()

    }

    override fun doInBackground(vararg params: String?): String? {

        val Playlists = URL(url1).readText(Charsets.UTF_8)
        val PlaylistsData = JSONObject(Playlists)
        val playlist_count = PlaylistsData.getString("playlist_count").toDouble()
        val playlist_pages = ceil(playlist_count / 20)
        val track_count = PlaylistsData.getString("track_count")
        if (storage.getTrack_count(context) != ""){
            if (storage.getTrack_count(context)!!.toInt()  == track_count.toInt()){
                return null
            }
        }

        storage.track_count(context, track_count)

        val playlistArray = JSONArray()

        for (i in 1 until playlist_pages.toInt() + 1) {
            val Playlists = URL("https://api-v2.hearthis.at/incplaylist/?type=playlists&page=$i&count=20").readText(Charsets.UTF_8)
            val PlaylistsArr = JSONArray(Playlists)

            for (j in 0 until PlaylistsArr.length()) {

                val PlaylistObj = PlaylistsArr.getJSONObject(j)

                val PlaylistTracks = URL(PlaylistObj.getString("uri")).readText(Charsets.UTF_8)
                val PlaylistTracksArr = JSONArray(PlaylistTracks)

                val PlaylistData = JSONArray()
                for (k in 0 until PlaylistTracksArr.length()) {
                    val tracks = PlaylistTracksArr.getJSONObject(k)
                    tracks.put("favorited", tracks.getBoolean("favorited").toString())

                    PlaylistData.put(tracks)
                }
                PlaylistObj.put("tracks", PlaylistData)

                playlistArray.put(PlaylistObj)





            }
        }
        storage.storePlaylistTracks(context,  playlistArray.toString())
        return playlistArray.toString()

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (from == "main"){
            val main = MainActivity()
            main.onFetchDataSuccess(context)
        }else{
            val home = Home()
            home.doneLoad(context)
        }

        //saveResult(context, result.toString())
    }

    private fun saveResult(context: Context, content: String){


        val folder = File(context.filesDir, "elpaboritos")
        if (!folder.exists()) {
            folder.mkdir()
        }
        val file = File("$folder/result.txt")

        try {
            val fos = FileOutputStream(file, false)
            fos.write(content.toByteArray())
            fos.close()
            Toast.makeText(context, "SAVED!", Toast.LENGTH_SHORT).show()
        }catch (e:FileNotFoundException){
            e.printStackTrace()
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        }catch (e:IOException){
            e.printStackTrace()
            Toast.makeText(context, "SAVED!", Toast.LENGTH_SHORT).show()
        }
    }

}