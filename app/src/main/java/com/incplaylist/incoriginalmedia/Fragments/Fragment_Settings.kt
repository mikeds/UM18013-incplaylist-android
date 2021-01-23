package com.incplaylist.incoriginalmedia.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.incplaylist.incoriginalmedia.R
import com.incplaylist.incoriginalmedia.Storage
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.json.JSONObject

class Fragment_Settings:Fragment(R.layout.fragment_settings) {
    val data = Storage()
    private var mListener: onSelectThemeListener? = null
    val colorData = JSONObject()

    @SuppressLint("WrongConstant", "NewApi")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_settings, container, false)

        view.rg1.setOnCheckedChangeListener(listener1);
        view.rg2.setOnCheckedChangeListener(listener2);


        return view
    }
    private val listener1: RadioGroup.OnCheckedChangeListener =
            RadioGroup.OnCheckedChangeListener { group, checkedId ->

                if (checkedId != -1) {
                    view?.rg2?.setOnCheckedChangeListener(null) // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                    view?.rg2?.clearCheck() // clear the second RadioGroup!
                    view?.rg2?.setOnCheckedChangeListener(listener2) //reset the listener
                    Log.e("XXX2", "do the work")

                    when(checkedId){
                        R.id.rb1 ->{
                            colorData.put("backgroundColor", "#ffffff")
                            colorData.put("textColor", "#cda73a")
                            colorData.put("colorAccent", "#cda73a")
                            setcolor(colorData)
                        }
                        R.id.rb2 ->{
                            colorData.put("backgroundColor", "#ffffff")
                            colorData.put("textColor", "#050505")
                            colorData.put("colorAccent", "#050505")
                            setcolor(colorData)
                        }
                        R.id.rb3 ->{
                            colorData.put("backgroundColor", "#262626")
                            colorData.put("textColor", "#3ffc86")
                            colorData.put("colorAccent", "#3ffc86")
                            setcolor(colorData)
                        }
                        R.id.rb4 ->{
                            colorData.put("backgroundColor", "#ffffff")
                            colorData.put("textColor", "#fc3f45")
                            colorData.put("colorAccent", "#fc3f45")
                            setcolor(colorData)
                        }
                    }
                }
            }

    private val listener2: RadioGroup.OnCheckedChangeListener =
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                if (checkedId != -1) {
                    view?.rg1?.setOnCheckedChangeListener(null) // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                    view?.rg1?.clearCheck() // clear the second RadioGroup!
                    view?.rg1?.setOnCheckedChangeListener(listener1) //reset the listener
                    Log.e("XXX2", "do the work")
                    when(checkedId){
                        R.id.rb5 ->{
                            colorData.put("backgroundColor", "#ffffff")
                            colorData.put("textColor", "#569ded")
                            colorData.put("colorAccent", "#569ded")
                            setcolor(colorData)
                        }
                        R.id.rb6 ->{
                            colorData.put("backgroundColor", "#262626")
                            colorData.put("textColor", "#4b4b4b")
                            colorData.put("colorAccent", "#4b4b4b")
                            setcolor(colorData)
                        }
                        R.id.rb7 ->{
                            colorData.put("backgroundColor", "#262626")
                            colorData.put("textColor", "#f5af2b")
                            colorData.put("colorAccent", "#f5af2b")
                            setcolor(colorData)
                        }
                        R.id.rb8 ->{
                            colorData.put("backgroundColor", "#ffffff")
                            colorData.put("textColor", "#10427f")
                            colorData.put("colorAccent", "#10427f")
                            setcolor(colorData)
                        }
                    }
                }
            }

    fun setcolor(color: JSONObject){

        view?.llSettingsLayout?.setBackgroundColor(Color.parseColor(color.getString("backgroundColor")))
        view?.tvSelectTheme?.setTextColor(Color.parseColor(color.getString("colorAccent")))
        data.setTheme(activity!!, color.toString())
        mListener?.onSelectTheme(color)
    }

    interface onSelectThemeListener{
        fun onSelectTheme(color: JSONObject)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is onSelectThemeListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }


}