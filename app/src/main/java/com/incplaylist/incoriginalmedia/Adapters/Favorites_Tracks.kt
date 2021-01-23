package com.incplaylist.incoriginalmedia.Adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.incplaylist.incoriginalmedia.Home
import com.incplaylist.incoriginalmedia.R
import com.incplaylist.incoriginalmedia.Storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.playlist_items.view.*
import org.json.JSONArray
import org.json.JSONObject

class Favorites_Tracks (
        private val context: Context,
        private val list: JSONArray
): RecyclerView.Adapter<Favorites_Tracks.ViewHolder>() {

    val storage = Storage()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tvSetTracksTitle)
        val image = itemView.findViewById<ImageView>(R.id.ivSetTracksThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.playlist_items, parent, false)
        if (storage.getTheme(context) != ""){
            val color = JSONObject(storage.getTheme(context)!!)
            view.findViewById<ImageView>(R.id.ibPlayPause).setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
            view.findViewById<ImageView>(R.id.ibMore).setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
        }
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


        if (dataitem.getString("downloadable")=="1"){
            rowView.itemView.tvDownloading.visibility = View.VISIBLE
            rowView.itemView.llitem.setBackgroundColor(Color.parseColor("#dddddd"))
        }else{
            rowView.itemView.ibMore.setImageResource(R.drawable.ic_baseline_favorite_24_000000)
            rowView.itemView.setOnClickListener(View.OnClickListener {
                val home = Home()
                val nowPlayingData = JSONObject()
                nowPlayingData.put("title", dataitem.getString("title"))
                nowPlayingData.put("id", dataitem.getString("id"))
                nowPlayingData.put("from", "Favorites")
                storage.nowPlaying(context, nowPlayingData.toString())
                home.onMediaPlay(context)
            })
        }

        rowView.itemView.ibMore.setOnClickListener {
            val home=Home()
            home.favorite_popup(context,dataitem, "remove","","favorites")
        }


        if (position == list.length() - 1){
            val params = rowView.itemView.llitem.layoutParams
            params.height = convertDpToPixel(200)
            rowView.itemView.llitem.layoutParams = params
        }


    }

    fun convertDpToPixel(dp: Int): Int {
        return (dp.toFloat() * (Resources.getSystem().displayMetrics.density)).toInt()
    }


}