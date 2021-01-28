package com.incplaylist.incoriginalmedia.Fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.incplaylist.incoriginalmedia.Adapters.Favorites_Tracks
import com.incplaylist.incoriginalmedia.R
import com.incplaylist.incoriginalmedia.Storage
import kotlinx.android.synthetic.main.fragment_dj.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class Fragment_Favorites : Fragment(R.layout.fragment_favorite) {

    val storage = Storage()

    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_favorite, container, false)



        view.rvFav.layoutManager = LinearLayoutManager(activity!!, OrientationHelper.VERTICAL, false)
        view.rvFav.adapter = Favorites_Tracks(activity!!, fetchFavorites())

        view.refreshlayout.setOnRefreshListener {
            view.rvFav.layoutManager = LinearLayoutManager(activity!!, OrientationHelper.VERTICAL, false)
            view.rvFav.adapter = Favorites_Tracks(activity!!, fetchFavorites())
            view.refreshlayout.isRefreshing = false
        }

        return view
    }

    fun fetchFavorites():JSONArray{
        var favorites = JSONArray()
        try {
            favorites = JSONArray(storage.getFavorites(activity!!))
        }catch (e:JSONException){}

        return favorites
    }


    fun fetchAllTracks(): JSONArray {
        val storage = Storage()
        val AllTracks = JSONArray()
        val playlist = JSONArray(storage.getPlaylistTracks(activity!!))
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

    fun sortedJsonArray(jsonArray: JSONArray): JSONArray {
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