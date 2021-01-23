package com.incplaylist.incoriginalmedia

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.json.JSONArray
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class Favorites {
    val storage = Storage()
    val home = Home()
    fun AddRemoveFavorites(context: Context, id:String, method:String, url:String, title:String){

        if (method == "add"){

            home.downloadingMusic(context,title,"Downloading")

            refreshData(context, id, "true", "1")

            val task = DownloadTask(
                    context,
                    url,
                    "$id.mp3",
                    id.toInt(), //if you don't have id then you can pass any value here
                    object : DownloadTask.DownloadListener {
                        override fun onDownloadComplete(download: Boolean, pos: Int) {
                            if (download) {

                                refreshData(context, id, "true", "0")


                                //saveResult(context,newPlaylistArray.toString())
                                //home.downloadingMusic(context, title, "Added to Favorites")
                                //data.favorites(context,newfavorites.toString())
                            }
                        }

                        override fun downloadProgress(status: Int) {

                        }
                    })
            task.execute()

        }else{
            val folder = File(context.filesDir, "elpaboritos")
            val documentFile = File("$folder/$id.mp3")
            documentFile.delete()
            refreshData(context, id, "false", "0")
        }


    }

    fun refreshData(context: Context, id:String, isFavorited:String, downloadable: String){
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_MONTH, 15)
        val expireTime = SimpleDateFormat("yyyyMMdd").format(date.time)
        var title = ""
        val PlaylistArray = JSONArray(storage.getPlaylistTracks(context))
        val newPlaylistArray = JSONArray()
        for (i in 0 until  PlaylistArray.length()){
            val PlaylistData = PlaylistArray.getJSONObject(i)

            val PlaylistTracksArray = JSONArray(PlaylistData.getString("tracks"))



            val newPlaylistTracksData = JSONArray()
            for (j in 0 until PlaylistTracksArray.length()){
                val PlaylistTracksData = PlaylistTracksArray.getJSONObject(j)
                if (PlaylistTracksData.getString("id") == id){
                    title = PlaylistTracksData.getString("title")
                    PlaylistTracksData.put("favorited", isFavorited)
                    PlaylistTracksData.put("downloadable", downloadable)
                    if (isFavorited == "true"){
                        PlaylistTracksData.put("expiration", expireTime)
                    }
                }
                newPlaylistTracksData.put(PlaylistTracksData)
            }

            val newPlaylistData = PlaylistData.put("tracks", newPlaylistTracksData)
            newPlaylistArray.put(newPlaylistData)
        }


        if (isFavorited == "false"){
            home.downloadingMusic(context, title, "Removed from Favorites")
        }else{
            home.downloadingMusic(context, title, "Added to Favorites")
        }
        storage.storePlaylistTracks(context, newPlaylistArray.toString())

    }

    private fun saveResult(context: Context, content: String){


        val folder = File(context.filesDir, "elpaboritos")
        if (!folder.exists()) {
            folder.mkdir()
        }
        val file = File("$folder/result3.txt")
        Log.e("filedir", file.toString())
        try {
            val fos = FileOutputStream(file, false)
            fos.write(content.toByteArray())
            fos.close()
            Toast.makeText(context, "SAVED!", Toast.LENGTH_SHORT).show()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        }catch (e: IOException){
            e.printStackTrace()
            Toast.makeText(context, "SAVED!", Toast.LENGTH_SHORT).show()
        }
    }
}