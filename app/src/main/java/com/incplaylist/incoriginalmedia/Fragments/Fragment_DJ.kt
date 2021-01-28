package com.incplaylist.incoriginalmedia.Fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.incplaylist.incoriginalmedia.*
import com.incplaylist.incoriginalmedia.customwave.DrawView
import com.incplaylist.incoriginalmedia.customwave.UpdaterThread
import kotlinx.android.synthetic.main.fragment_dj.view.*
import org.json.JSONObject
import java.lang.RuntimeException

class Fragment_DJ : Fragment(R.layout.fragment_dj) {

    val home = Home()
    val storage = Storage()
    val mi = MediaPlayerInstance.getInstance()
    val mediaPlayer:MediaPlayer = mi
    val conn = ConnectionManager()
    var up : UpdaterThread? = null
    var drawView : DrawView? = null
    private var bytes: ByteArray = byteArrayOf(1)
    private var keepGoing = false
    var REFRESH_INTERVAL_MS: Long = 200
    private var visualizer: Visualizer? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  : View = inflater.inflate(R.layout.fragment_dj, container, false)

        bytes[0] = -128


        drawView = view.findViewById(R.id.root_adjust)
        up = UpdaterThread(drawView!!, activity!!)
        up!!.start()


        if (storage.getNowPlaying(activity!!)!!.contains("DJ") && mediaPlayer.isPlaying) {
            view.textView.text = "Playing"
            view.ivDjPlayPause.visibility = View.GONE
        }else{
            visualizer?.enabled = false
        }
        requestMicPermission()

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
                if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    setPlayer(mediaPlayer.audioSessionId)
                }

                keepGoing = true

            }else{
                Toast.makeText(activity!!, "Internet connection required", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }





    fun setPlayer(audioSessionId: Int) {
        visualizer = Visualizer(audioSessionId)
        visualizer!!.enabled = false
        visualizer!!.captureSize = Visualizer.getCaptureSizeRange()[0]
        visualizer!!.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(
                visualizer: Visualizer, bytes: ByteArray,
                samplingRate: Int
            ) {
                this@Fragment_DJ.bytes = bytes
                up!!.setPRog((bytes[0] + 128) * 20f)
            }

            override fun onFftDataCapture(
                visualizer: Visualizer, bytes: ByteArray,
                samplingRate: Int
            ) {
            }
        }, Visualizer.getMaxCaptureRate(), true, false)
        visualizer!!.setEnabled(true)

    }


    private fun requestMicPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.RECORD_AUDIO)){
            val dialogBuilder = AlertDialog.Builder(activity!!)

            // set message of alert dialog
                dialogBuilder.setMessage("Do you want to close this application ?")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                            dialog, id -> ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.RECORD_AUDIO) , 1)
                    })
                    // negative button text and action
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })

                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("AlertDialogExample")
                // show alert dialog
                alert.show()
        }else{
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.RECORD_AUDIO) , 1)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                Toast.makeText(activity!!, "Permission Granted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(activity!!, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}