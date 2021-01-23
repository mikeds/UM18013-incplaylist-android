package com.incplaylist.incoriginalmedia.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.incplaylist.incoriginalmedia.Adapters.Playlist_Tracks
import com.incplaylist.incoriginalmedia.R
import com.incplaylist.incoriginalmedia.Storage
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import org.json.JSONObject

class Fragment_Playlist : Fragment(R.layout.fragment_playlist) {
    var intent = JSONObject()
    val storage = Storage()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_playlist, container, false)




        val layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.rvSetTracks.layoutManager = layoutManager
        view.rvSetTracks.adapter = Playlist_Tracks(activity!!, intent)

        return view
    }

    fun playlistData(context:Context, jsonObject: JSONObject){
        intent = jsonObject
    }
}