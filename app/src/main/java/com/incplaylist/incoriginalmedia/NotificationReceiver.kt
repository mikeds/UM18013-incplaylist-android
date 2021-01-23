package com.incplaylist.incoriginalmedia

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.sendBroadcast(Intent("Service")
                .putExtra("Action", intent.action))

        //context.startService(Intent(context, MusicServices::class.java))
    }
}