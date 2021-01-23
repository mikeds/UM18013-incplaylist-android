package com.incplaylist.incoriginalmedia

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() , FetchDataCallback{


    private val SPLASH_TIME_OUT:Long = 1000
    val conn = ConnectionManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        createNotificationChannel()
        Handler().postDelayed({
            if (conn.isConnected(this)){
                FetchData(this,"main").execute()
            }else{
                startActivity(Intent(this, Home::class.java))
                finish()
            }





        }, SPLASH_TIME_OUT)
    }

    override fun onFetchDataSuccess(context: Context) {
        val mcontext = (context as MainActivity)
        context.startActivity(Intent(context, Home::class.java))
        mcontext.finish()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "INC Playlist"
            val descriptionText = ""
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val CHANNEL_ID = "incom"


            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText

            }
            channel.setSound(null,null)
            channel.vibrationPattern = longArrayOf(0)
            channel.setShowBadge(false)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}