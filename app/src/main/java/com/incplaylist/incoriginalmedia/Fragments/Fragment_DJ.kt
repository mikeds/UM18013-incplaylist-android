package com.incplaylist.incoriginalmedia.Fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.incplaylist.incoriginalmedia.*
import kotlinx.android.synthetic.main.fragment_dj.view.*
import org.json.JSONObject

class Fragment_DJ : Fragment(R.layout.fragment_dj) {

    val home = Home()
    val storage = Storage()
    val mi = MediaPlayerInstance.getInstance()
    val mediaPlayer:MediaPlayer = mi
    val conn = ConnectionManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_dj, container, false)


        if (storage.getNowPlaying(activity!!)!!.contains("DJ") && mediaPlayer.isPlaying) {
            view.textView.text = "Playing"
            view.ivDjPlayPause.visibility = View.GONE
        }
        view.ivDjPlayPause.setOnClickListener {
            if (conn.isConnected(activity!!)){
                view.ivDjPlayPause.visibility = View.GONE
                view.textView.text = "Playing"
                val data = JSONObject()
                data.put("title", "DJ's Cue Live Streaming")
                data.put("from", "DJ")
                data.put("id", "00")
                storage.nowPlaying(activity!!, data.toString())
                home.onMediaPlay(activity!!)
            }else{
                Toast.makeText(activity!!, "Internet connection required", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}