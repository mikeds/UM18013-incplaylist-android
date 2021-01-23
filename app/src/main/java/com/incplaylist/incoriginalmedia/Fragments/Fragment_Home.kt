package com.incplaylist.incoriginalmedia.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.incplaylist.incoriginalmedia.Adapters.Home_Playlist
import com.incplaylist.incoriginalmedia.Adapters.Home_tracks
import com.incplaylist.incoriginalmedia.FetchData
import com.incplaylist.incoriginalmedia.R
import com.incplaylist.incoriginalmedia.Storage
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class Fragment_Home : Fragment(R.layout.fragment_home) {


    val data = Storage()

    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_home, container, false)

        if (data.getTheme(activity!!) != ""){
            val color = JSONObject(data.getTheme(activity!!)!!)
            view.view_pager.setBackgroundColor(Color.parseColor(color.getString("colorAccent")))
            view.llAccent.setBackgroundColor(Color.parseColor(color.getString("colorAccent")))
            view.llTab.setBackgroundColor(Color.parseColor(color.getString("backgroundColor")))
            view.textView2.setTextColor(Color.parseColor(color.getString("colorAccent")))
            view.svContent.setBackgroundColor(Color.parseColor(color.getString("backgroundColor")))
        }

        view.view_pager.adapter = Home_Playlist(activity!!)
        view.rvHome.layoutManager = LinearLayoutManager(activity!!, OrientationHelper.HORIZONTAL, false)
        view.rvHome.adapter = Home_tracks(activity!!, fetchAllTracks())

        view.refreshlayout.setOnRefreshListener {
            FetchData(activity!!, "home").execute()
        }
        return view
    }



    fun fetchAllTracks(): JSONArray {
        val storage = Storage()
        val AllTracks = JSONArray()
        val playlist = JSONArray(storage.getPlaylistTracks(activity!!))
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



}