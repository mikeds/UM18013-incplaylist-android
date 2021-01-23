package com.incplaylist.incoriginalmedia.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.incplaylist.incoriginalmedia.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.playlist_items.view.*
import org.json.JSONArray
import org.json.JSONObject

class Playlist_Tracks (
        private val context: Context,
        val intent: JSONObject
) : RecyclerView.Adapter<BaseViewHolder<*>>() {
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEMS = 1
        private const val TYPE_EMPTY = 2
    }


    var home = Home()
    val storage = Storage()
    val favorites = Favorites()
    val list = JSONArray(intent.getString("tracks"))
    val conn = ConnectionManager()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BaseViewHolder<*> {
        return when (position) {
            TYPE_HEADER -> {


                val view = LayoutInflater.from(context).inflate(R.layout.playlist_header, parent, false)
                if (storage.getTheme(context) != ""){
                    val color = JSONObject(storage.getTheme(context)!!)
                    view.findViewById<ImageView>(R.id.imageView2).setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
                }else{
                    view.findViewById<ImageView>(R.id.imageView2).setColorFilter(Color.parseColor("#03DAC5"))
                }

                view.findViewById<TextView>(R.id.tvSetTracksHeaderTitle).text = intent.getString("title")
                Picasso.get()
                        .load(intent.getString("thumb"))
                        .fit()
                        .centerCrop()
                        .into(view.findViewById<ImageView>(R.id.ivSetTracksHeaderThumb))


                HeaderViewHolder(view)
            }

            TYPE_ITEMS -> {
                val view = LayoutInflater.from(context).inflate(R.layout.playlist_items, parent, false)
                if (storage.getTheme(context) != ""){
                    val color = JSONObject(storage.getTheme(context)!!)
                    view.findViewById<ImageView>(R.id.ibPlayPause).setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
                    view.findViewById<ImageView>(R.id.ibMore).setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
                }

                ItemViewHolder(view)

            }

            TYPE_EMPTY -> {
                val view = LayoutInflater.from(context).inflate(R.layout.empty_playlist_items, parent, false)

                EmptyViewHolder(view)

            }

            else -> throw IllegalArgumentException("INvalid view type")
        }
    }

    override fun getItemCount(): Int {
        return list.length() + 2
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {


        if (position.equals(0)){

        }else if(position <= list.length()){
            val nowPlayingData = JSONObject()
            val dataitem = list.getJSONObject(position-1)
            holder.itemView.findViewById<TextView>(R.id.tvSetTracksTitle).text = dataitem.getString("title")

            Picasso.get()
                    .load(dataitem.getString("thumb"))
                    .resize(300, 300)
                    .centerCrop()
                    .into(holder.itemView.findViewById<ImageView>(R.id.ivSetTracksThumb))


            holder.itemView.ibPlayPause.setOnClickListener {
                if (conn.isConnected(context)){
                    nowPlayingData.put("title", dataitem.getString("title"))
                    nowPlayingData.put("id", dataitem.getString("id"))
                    nowPlayingData.put("from", "Playlist")
                    storage.nowPlaying(context, nowPlayingData.toString())
                    home.onMediaPlay(context)
                }else{
                    Toast.makeText(context, "Internet connection required", Toast.LENGTH_SHORT).show()
                }

            }

            holder.itemView.setOnClickListener {
                if (conn.isConnected(context)){
                    nowPlayingData.put("title", dataitem.getString("title"))
                    nowPlayingData.put("id", dataitem.getString("id"))
                    nowPlayingData.put("from", "Playlist")
                    storage.nowPlaying(context, nowPlayingData.toString())
                    home.onMediaPlay(context)
                }else{
                    Toast.makeText(context, "Internet connection required", Toast.LENGTH_SHORT).show()
                }
            }



            //!!!!!!!!!!!!!!!!!!!!!!!CHANGE POP UP DATA FLOW ON FUTURE UPDATES !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 01222021 !!!!!!!!!!!!! UPDATE DATE IF STILL PENDING

            /*val popup = PopupMenu(context, holder.itemView)
            popup.inflate(R.menu.popup_menu)*/

            if (dataitem.getString("favorited") == "true"){
                holder.itemView.ibMore.setImageResource(R.drawable.ic_baseline_favorite_24_000000)
                /*popup.menu.findItem(R.id.action_popup_removetofavorites).isVisible = true*/
            }

            holder.itemView.ibMore.setOnClickListener {

                if (dataitem.getString("favorited") == "false"){
                    home.favorite_popup(context, dataitem, "add", intent.getString("id"),"playlist")
                }else{
                    home.favorite_popup(context,dataitem, "remove", intent.getString("id"),"playlist")
                }


                /*popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_popup_addtofavorites ->{

                            dataitem.put("favorited","true")
                            favorites.AddRemoveFavorites(
                                    context,
                                    dataitem.getString("id"),
                                    "add",
                                    dataitem.getString("stream_url"),
                                    dataitem.getString("title"))

                            notifyItemChanged(holder.adapterPosition)

                            true
                        }
                        R.id.action_popup_removetofavorites->{
                            dataitem.put("favorited","false")
                            //Log.e("from", intent.getString("playlistID"))
                            favorites.AddRemoveFavorites(context,
                                    dataitem.getString("id"),
                                    "remove",
                                    dataitem.getString("stream_url"),
                                    dataitem.getString("title"))

                            notifyItemChanged(holder.adapterPosition)
                            true
                        }
                        else -> {false}

                    }

                }
                popup.show()*/
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> {
                TYPE_HEADER
            }
            position <= list.length() -> {
                TYPE_ITEMS
            }
            else -> {
                TYPE_EMPTY
            }
        }


    }


    inner class HeaderViewHolder(itemView: View): BaseViewHolder<View>(itemView) {
        override fun bind(item: View) {

        }
    }

    inner class ItemViewHolder(itemView: View): BaseViewHolder<View>(itemView) {
        override fun bind(item: View) {

        }
    }

    inner class EmptyViewHolder(itemView: View): BaseViewHolder<View>(itemView) {
        override fun bind(item: View) {

        }
    }








}