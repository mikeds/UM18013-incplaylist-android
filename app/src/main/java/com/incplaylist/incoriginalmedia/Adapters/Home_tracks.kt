package com.incplaylist.incoriginalmedia.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.incplaylist.incoriginalmedia.ConnectionManager
import com.incplaylist.incoriginalmedia.Home
import com.incplaylist.incoriginalmedia.R
import com.incplaylist.incoriginalmedia.Storage
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class Home_tracks(
        private val context: Context,
        private val list: JSONArray
        ): RecyclerView.Adapter<Home_tracks.ViewHolder>() {

    val storage = Storage()
    val conn = ConnectionManager()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tvHomeSongs)
        val image = itemView.findViewById<ImageView>(R.id.ivHomeSongAlbum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.home_song_item, parent, false)

        view.setOnClickListener{

        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.length()
    }

    override fun onBindViewHolder(rowView: ViewHolder, position: Int) {
        var dataitem = list.getJSONObject(position)
        rowView.title.text = dataitem.getString("title")
        Picasso.get()
                .load(dataitem.getString("thumb"))
                .resize(150, 150)
                .centerCrop()
                .into(rowView.image)


        rowView.itemView.setOnClickListener(View.OnClickListener {
            if (conn.isConnected(context)){
                val home = Home()
                val nowPlayingData = JSONObject()
                nowPlayingData.put("title", dataitem.getString("title"))
                nowPlayingData.put("id", dataitem.getString("id"))
                nowPlayingData.put("from", "All")
                storage.nowPlaying(context, nowPlayingData.toString())
                home.onMediaPlay(context)
            }else{
                Toast.makeText(context, "Internet connection required", Toast.LENGTH_SHORT).show()
            }

        })
    }


}