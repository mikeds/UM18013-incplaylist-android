package com.incplaylist.incoriginalmedia

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.StrictMode
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class MusicServices : Service() , MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    val mi = MediaPlayerInstance.getInstance()
    val mediaPlayer : MediaPlayer = mi
    val storage = Storage()
    val tracksData = TracksData()
    var requestID = System.currentTimeMillis().toInt()
    val CHANNEL_ID = "incom"
    var notificationManager : NotificationManager? = null


    override fun onBind(p0: Intent): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        when (intent.extras!!.get("action")){
            "nextSong"->{
                nextSong()
            }
            "prevSong"->{
                prevSong()
            }
            "repeatSong"->{
                if (mediaPlayer.isPlaying){
                    mediaPlayer.seekTo(0)
                }
            }
            "shuffleSong"->{

            }
            "PlayPause"->{
                PlayPause()
            }
            else->{
                play()
            }
        }



        return START_STICKY

    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        registerReceiver(receiver, IntentFilter("Service"))


        if (Build.VERSION.SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

    //@Throws(ProtocolException::class)
    fun play(){

        val url =tracksData.getTrackData(application.applicationContext)!!.getString("stream_url")

        try {
            val headers: HashMap<String, String> = HashMap()
            headers["Status"] = "206"
            headers["Content-Type"] = "audio/mp3" // change content type if necessary
            headers["Accept-Ranges"] = "bytes"
            headers["Accept-Encoding"] = "identity"
            headers["Connection"] = "keep-alive"
            headers["Cache-control"] = "no-cache"

            //var okHttpClient = OkHttpClient()
            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder.connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                    .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                    .readTimeout(5, TimeUnit.MINUTES) // read timeout
                    .build()



            //okHttpClient = builder.build()

            val uri: Uri = Uri.parse(url)
            if (mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }


            mediaPlayer.reset()

            //added headers to 01182021 build
            mediaPlayer.setDataSource(this@MusicServices,uri)
            mediaPlayer.apply {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                setOnPreparedListener(this@MusicServices)
                setOnErrorListener(this@MusicServices)
                setOnCompletionListener(this@MusicServices)
                prepareAsync()
            }

        }catch (e:IOException){

        }catch (e:ProtocolException){

        }
        storage.setPlaying(application.applicationContext, true)
        val intent = Intent("home").putExtra("UI","preparing")
        application.applicationContext.sendBroadcast(intent)
        creatNotification(JSONObject(storage.getNowPlaying(application.applicationContext)!!).getString("title"), R.drawable.ic_baseline_pause_24_d1a538, true)

    }




    override fun onError(p0: MediaPlayer, p1: Int, p2: Int): Boolean {
        play()

        return false
    }

    override fun onCompletion(p0: MediaPlayer) {
        //if dj cue
        val from = JSONObject(storage.getNowPlaying(application.applicationContext)!!).getString("from")
        if (from == "DJ"){
            play()
        }else{
            nextSong()
        }

    }

    override fun onPrepared(p0: MediaPlayer) {


        try {
            p0.start()
        }catch (e:IOException){

        }catch (e:ProtocolException){

        }

        val intent = Intent("home").putExtra("UI","prepared")
        application.applicationContext.sendBroadcast(intent)
    }

    fun nextSong(){
        val context = application.applicationContext
        var trackData = JSONObject()
        val nowPlayingData = JSONObject()


        val from = JSONObject(storage.getNowPlaying(context)!!).getString("from")
        val title = JSONObject(storage.getNowPlaying(context)!!).getString("title")
        val id = JSONObject(storage.getNowPlaying(context)!!).getString("id")

        if (from == "All"){
            val allTracks = fetchAllTracks()
            for (i in 0 until allTracks.length()){
                if (id == allTracks.getJSONObject(i).getString("id")){
                    if (i >= allTracks.length()-1){
                        trackData = allTracks.getJSONObject(0)
                    }else{
                        trackData = allTracks.getJSONObject(i+1)
                    }

                }
            }
        }else if(from == "Favorites"){
            val allTracks = fetchFavorites()
            for (i in 0 until allTracks.length()){
                if (id == allTracks.getJSONObject(i).getString("id")){
                    if (i >= allTracks.length()-1){
                        trackData = allTracks.getJSONObject(0)
                    }else{
                        trackData = allTracks.getJSONObject(i+1)
                    }


                }
            }
        }
        else{
            val playlists = JSONArray(storage.getPlaylistTracks(application.applicationContext))
            for (i in 0 until playlists.length()){
                val playlistTracks = JSONArray(playlists.getJSONObject(i).getString("tracks"))
                for (j in 0 until playlistTracks.length()){
                    val tracks = playlistTracks.getJSONObject(j)
                    if (id == tracks.getString("id")){
                        if (j >= playlistTracks.length()-1){
                            trackData = playlistTracks.getJSONObject(0)
                        }else{
                            trackData = playlistTracks.getJSONObject(j+1)
                        }


                    }
                }
            }
        }
        nowPlayingData.put("from", from)
        nowPlayingData.put("title", trackData.getString("title"))
        nowPlayingData.put("id", trackData.getString("id"))
        storage.nowPlaying(context,nowPlayingData.toString())
        val intent = Intent("home").putExtra("UI","updatePlayer")
        application.applicationContext.sendBroadcast(intent)
        play()
    }

    fun prevSong(){
        val context = application.applicationContext
        var trackData = JSONObject()
        val nowPlayingData = JSONObject()


        val from = JSONObject(storage.getNowPlaying(context)!!).getString("from")
        val title = JSONObject(storage.getNowPlaying(context)!!).getString("title")
        val id = JSONObject(storage.getNowPlaying(context)!!).getString("id")

        if (from == "All"){
            val allTracks = fetchAllTracks()
            for (i in 0 until allTracks.length()){
                if (id == allTracks.getJSONObject(i).getString("id")){
                    if (i == 0){
                        trackData = allTracks.getJSONObject(allTracks.length()-1)
                    }else{
                        trackData = allTracks.getJSONObject(i-1)
                    }

                }
            }
        }else if(from == "Favorites"){
            val allTracks = fetchFavorites()
            for (i in 0 until allTracks.length()){
                if (id == allTracks.getJSONObject(i).getString("id")){
                    if (i == 0){
                        trackData = allTracks.getJSONObject(allTracks.length()-1)
                    }else{
                        trackData = allTracks.getJSONObject(i-1)
                    }

                }
            }
        }
        else{
            val playlists = JSONArray(storage.getPlaylistTracks(application.applicationContext))
            for (i in 0 until playlists.length()){
                val playlistTracks = JSONArray(playlists.getJSONObject(i).getString("tracks"))
                for (j in 0 until playlistTracks.length()){
                    val tracks = playlistTracks.getJSONObject(j)
                    if (id == tracks.getString("id")){
                        if (j == 0){
                            trackData = playlistTracks.getJSONObject(playlistTracks.length()-1)
                        }else{
                            trackData = playlistTracks.getJSONObject(j-1)
                        }

                    }
                }
            }
        }
        nowPlayingData.put("from", from)
        nowPlayingData.put("title", trackData.getString("title"))
        nowPlayingData.put("id", trackData.getString("id"))
        storage.nowPlaying(context,nowPlayingData.toString())
        val intent = Intent("home").putExtra("UI","updatePlayer")
        application.applicationContext.sendBroadcast(intent)
        play()
    }

    fun suffledSong(){

    }

    fun PlayPause(){
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
            storage.setPlaying(application.applicationContext, false)
            val intent = Intent("home").putExtra("UI","updatePlayPause")
            application.applicationContext.sendBroadcast(intent)
            creatNotification(JSONObject(storage.getNowPlaying(application.applicationContext)!!).getString("title"), R.drawable.ic_baseline_play_arrow_24_d1a538, false)
        }else{
            mediaPlayer.start()
            storage.setPlaying(application.applicationContext, true)
            val intent = Intent("home").putExtra("UI","updatePlayPause")
            application.applicationContext.sendBroadcast(intent)
            creatNotification(JSONObject(storage.getNowPlaying(application.applicationContext)!!).getString("title"), R.drawable.ic_baseline_pause_24_d1a538, true)
        }
    }



    var receiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, _intent: Intent) {
            val action = _intent.extras?.getString("Action")
            //Toast.makeText(this@Home, "yohoooo!!", Toast.LENGTH_LONG).show()
            when (action){
                "pause" ->{
                    PlayPause()
                }
                "previous" ->{
                    prevSong()
                }
                "next" ->{
                    nextSong()
                }
            }
        }
    }



    fun fetchAllTracks(): JSONArray {
        val storage = Storage()
        val AllTracks = JSONArray()
        val playlist = JSONArray(storage.getPlaylistTracks(application.applicationContext))
        for (i in 0 until playlist.length()){
            playlist.getJSONObject(i)
            val tracksArray = JSONArray(playlist.getJSONObject(i).getString("tracks"))
            for (j in 0 until tracksArray.length()){
                AllTracks.put(tracksArray.getJSONObject(j))
            }
        }

        val sortedTracks = sortedJsonArray(AllTracks)
        return sortedTracks
    }

    fun fetchFavorites(): JSONArray {
        val storage = Storage()
        val AllTracks = JSONArray()
        val playlist = JSONArray(storage.getPlaylistTracks(application.applicationContext!!))
        for (i in 0 until playlist.length()){
            playlist.getJSONObject(i)
            val tracksArray = JSONArray(playlist.getJSONObject(i).getString("tracks"))
            for (j in 0 until tracksArray.length()){
                val trackData = tracksArray.getJSONObject(j)
                if (trackData.getString("favorited")=="true"){
                    AllTracks.put(trackData)
                }
            }
        }

        val sortedTracks = sortedJsonArray(AllTracks)
        return sortedTracks
    }

    fun sortedJsonArray(jsonArray: JSONArray):JSONArray{
        val sortJsonArray = JSONArray()
        val list = ArrayList<JSONObject>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getJSONObject(i))
        }
        Collections.sort(list, Comparator { a1, a2 ->
            a1["title"].toString().trim().compareTo(a2["title"].toString().trim())
        })
        for (i in 0 until jsonArray.length()){
            sortJsonArray.put(list[i])
        }



        return sortJsonArray
    }

    fun creatNotification(Title:String, playButton:Int, isOngoing:Boolean){

        val pauseIntent = Intent(application.applicationContext, NotificationReceiver::class.java).setAction("pause")
        val pausePendingIntent: PendingIntent = PendingIntent.getBroadcast(
                application.applicationContext,requestID, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val prevIntent = Intent(application.applicationContext, NotificationReceiver::class.java).setAction("previous")
        val prevPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                application.applicationContext,requestID, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent = Intent(application.applicationContext, NotificationReceiver::class.java).setAction("next")
        val nextPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                application.applicationContext,requestID, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        val intent = Intent(application.applicationContext, Home::class.java)
        val toact: PendingIntent = PendingIntent.getActivity(
                application.applicationContext,requestID, intent, 0)

        val artwork : Bitmap = BitmapFactory.decodeResource(application.applicationContext.resources, R.drawable.isplas)
        val mediaSession : MediaSessionCompat = MediaSessionCompat(application.applicationContext, "tag")





        val builder = NotificationCompat.Builder(application.applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.isplas)
                .setContentTitle(Title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSubText("is Playing")
                .setOngoing(isOngoing)
                .setContentIntent(toact)

        val from = JSONObject(storage.getNowPlaying(application.applicationContext)!!).getString("from")
        if (from == "DJ"){
            builder.setLargeIcon(artwork)
        }else if (from == "Favorites"){
            builder.setLargeIcon(artwork)
        }else{
            builder.setLargeIcon(getBitmapFromURL(tracksData.getTrackData(application.applicationContext)!!.getString("thumb")))
        }



        if (!Title.contains("DJ")){
            builder
                    .addAction(R.drawable.ic_baseline_skip_previous_24_d1a538, "Previous", prevPendingIntent) // #0
                    .addAction(playButton, "Pause", pausePendingIntent) // #1
                    .addAction(R.drawable.ic_baseline_skip_next_24_d1a538, "Next", nextPendingIntent) // #2
                    .setStyle(
                            androidx.media.app.NotificationCompat.MediaStyle()
                                    .setShowActionsInCompactView(0,1,2)
                                    .setMediaSession(mediaSession.sessionToken))
        }else{
            builder.addAction(playButton, "Pause", pausePendingIntent) // #1
                    .setStyle(
                            androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0)
                                    .setMediaSession(mediaSession.sessionToken))
        }

        val notification : Notification = builder.build()
        startForeground(1, notification)
        if (!isOngoing){
            stopForeground(false)
            /*with(NotificationManagerCompat.from(application.applicationContext)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }*/
        }

    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }





}