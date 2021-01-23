package com.incplaylist.incoriginalmedia

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONObject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
class ThemeSettings {
    val storage = Storage()
    @SuppressLint("NewApi")
    fun setTheme(context: Context){
        val mcontext = (context as Home)

        mcontext.imageView3.setColorFilter(Color.parseColor("#2a2a2a"))
        mcontext.ivOverlay.setColorFilter(Color.parseColor("#2a2a2a"))

        if (storage.getTheme(context) != ""){
            val color = JSONObject(storage.getTheme(context))

            val iconsColorStates = ColorStateList(
                    arrayOf(
                            intArrayOf(-R.attr.state_checked),
                            intArrayOf(R.attr.state_checked)
                    ), intArrayOf(
                    Color.parseColor("#aaaaaa"),
                    Color.parseColor(color.getString("colorAccent"))
            )
            )


            mcontext.imageView2.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
            val jowable = mcontext.seekBar2.progressDrawable as LayerDrawable
            val jowable2 = mcontext.seekBar3.progressDrawable as LayerDrawable
            val sbProgressJowable2 = jowable2.getDrawable(1)
            val sbProgressJowable = jowable.getDrawable(1)
            val jowable3 = mcontext.progressBar.progressDrawable as LayerDrawable
            val sbProgressJowable3 = jowable3.getDrawable(2)

            sbProgressJowable3.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
            sbProgressJowable.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
            sbProgressJowable2.setColorFilter(Color.parseColor(color.getString("colorAccent")), PorterDuff.Mode.SRC_IN)
            mcontext.playProgress.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(color.getString("colorAccent")))
            mcontext.expandedProgressBar.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(color.getString("colorAccent")))
            mcontext.ivPlayButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            mcontext.ibShuffleButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            mcontext.ibPreviousButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            mcontext.ibRepeatButton.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            mcontext.ivPlayPauseBurron.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            mcontext.ivSkipBurron.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            mcontext.ivSkipButton2.setColorFilter(Color.parseColor(color.getString("colorAccent")))
            mcontext.bottomNavigationView.itemTextColor = iconsColorStates
            mcontext.bottomNavigationView.itemIconTintList = iconsColorStates
            mcontext.bottomNavigationView.setBackgroundColor(Color.parseColor(color.getString("backgroundColor")))
        }
    }
}