package com.incplaylist.incoriginalmedia.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.incplaylist.incoriginalmedia.Fragments.Fragment_Playlist
import com.incplaylist.incoriginalmedia.R
import com.incplaylist.incoriginalmedia.Storage
import com.squareup.picasso.Picasso
import org.json.JSONArray

class Home_Playlist(private val context: Context) : PagerAdapter() {

    val storage = Storage()
    val list = JSONArray(storage.getPlaylistTracks(context))
    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }

    override fun getCount(): Int {
        return list.length()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var dataitem = list.getJSONObject(position)

        val view = LayoutInflater.from(context).inflate(R.layout.home_playlist_item, container, false)

        view.findViewById<TextView>(R.id.tvSetTitle).text = dataitem.getString("title")
        view.findViewById<TextView>(R.id.tvSetSongCount).visibility = View.GONE

        Picasso.get()
                .load(dataitem.getString("thumb"))
                .fit()
                .centerCrop()
                .into(view.findViewById<ImageView>(R.id.ivSetCard))

        view.setOnClickListener{

            val fragment = Fragment_Playlist()


            fragment.playlistData(context, dataitem)

            val activity = context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,R.anim.exit_to_right)
                    .replace(R.id.flFragments, fragment).addToBackStack(null).commit()
        }

        container.addView(view)
        return view


    }


}