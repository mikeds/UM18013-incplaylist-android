package com.incplaylist.incoriginalmedia

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.incplaylist.incoriginalmedia.Fragments.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.first_user_dialog.*
import kotlinx.android.synthetic.main.fragment_dj.*
import org.json.JSONObject

@Suppress("DEPRECATION")
class Home : AppCompatActivity(),Fragment_Settings.onSelectThemeListener, MediaPlayCallBack {

    val themeSettings = ThemeSettings()
    val storage = Storage()
    val tracksData = TracksData()
    val mi = MediaPlayerInstance.getInstance()
    val mediaPlayer : MediaPlayer = mi
    var firstTimeDialog: Dialog? = null
    var context:Context? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        context = this
        firstTimeDialog = Dialog(this)


        if (storage.isFirstTimeUser(this)!!){
            firstUse()
        }
        //fragments initialization
        val fragment_DJ = Fragment_DJ()
        val fragment_Home = Fragment_Home()
        val fragment_Settings = Fragment_Settings()
        val fragment_Favorites= Fragment_Favorites()
        setCurrentFragment(fragment_Home)

        themeSettings.setTheme(this)

        registerReceiver(receiver, IntentFilter("home"))



        val bottomsheetbehavior = BottomSheetBehavior.from(bottomsheet)

        bottomsheet.visibility = View.GONE

        if (mediaPlayer.isPlaying){
            updatePlayer(this)
        }

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        MediaControllerCollapse.visibility = View.VISIBLE
                        MediaControllerExpanded.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        MediaControllerCollapse.visibility = View.GONE
                        MediaControllerExpanded.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        if (storage.getNowPlaying(this@Home)?.contains("DJ")!!) {
                            bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        }

        bottomsheetbehavior.addBottomSheetCallback(bottomSheetCallback)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHome-> {
                    setCurrentFragment(fragment_Home)
                    bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navDJ-> {
                    setCurrentFragment(fragment_DJ)
                    bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navFavorites-> {
                    setCurrentFragment(fragment_Favorites)
                    bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navSettings-> {
                    setCurrentFragment(fragment_Settings)
                    bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false

        }

        tvMediaTitle.setOnClickListener {
            if (!JSONObject(storage.getNowPlaying(this)!!).getString("title").contains("DJ")){
                bottomsheetbehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

        }

        ivCollapseButton.setOnClickListener {
            bottomsheetbehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        ivTopImg.setOnClickListener {
            val url = "https://iglesianicristo.net/"

            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        //media button declarations

        ivSkipBurron.setOnClickListener {musicAction("nextSong")}
        ivSkipButton2.setOnClickListener{musicAction("nextSong")}
        ibPreviousButton.setOnClickListener{musicAction("prevSong")}
        ibShuffleButton.setOnClickListener {musicAction("shuffleSong")}
        ibRepeatButton.setOnClickListener {musicAction("repeatSong")}
        ivPlayPauseBurron.setOnClickListener {musicAction("PlayPause")}
        ivPlayButton.setOnClickListener{musicAction("PlayPause")}


        seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i)
                    seekBar2.progress = i
                    seekBar3.progress = i
                    progressBar.progress = i
                    durstart.text = millisToTime(i)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })

        runOnUiThread(object : Runnable {
            override fun run() {

                if (mediaPlayer.isPlaying){
                    val message = Message()
                    message.what = mediaPlayer.currentPosition
                    handler.sendMessage(message)
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    fun millisToTime(duration: Int) :String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60

        val formattedSeconds = String.format("%02d", seconds);

        val time = "$minutes:$formattedSeconds"
        return time
    }

    fun fadeout(view: View) {
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        tvDownloadMusic.visibility = View.GONE
        tvDownloadMusic.startAnimation(fadeOut)
    }

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            seekBar2.progress = msg.what
            seekBar3.progress = msg.what
            progressBar.progress = msg.what
            durstart.text = millisToTime(msg.what)
        }
    }


    fun musicAction(action:String){
        val intent = Intent(this@Home, MusicServices::class.java)
        intent.putExtra("action", action)
        startService(intent)
    }




    override fun onResume() {
        super.onResume()
        if (mediaPlayer.isPlaying){
            updatePlayer(this)
        }

        registerReceiver(receiver, IntentFilter("home"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        //storage.setPlaying(this@Home, false)
    }


    private  fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
            replace(R.id.flFragments, fragment)
            commit()
        }
    }

    override fun onSelectTheme(color: JSONObject) {
        themeSettings.setTheme(this)
    }

    override fun onMediaPlay(context: Context) {
        val mcontext = (context as Home)

        updatePlayer(context)

        if (storage.getNowPlaying(context)!!.contains("DJ")){
            mcontext.ivSkipBurron.visibility = View.GONE
        }else{
            mcontext.ivSkipBurron.visibility = View.VISIBLE
        }

        val intent = Intent(mcontext,MusicServices::class.java)
        intent.putExtra("action", "Play")
        mcontext.startService(intent)
    }

    fun updatePlayer(context: Context){
        val mcontext = (context as Home)
        val jsonObject = JSONObject(storage.getNowPlaying(context)!!)
        val title = jsonObject.getString("title")
        val from = jsonObject.getString("from")
        mcontext.tvMediaTitle.text = title
        mcontext.bottomsheet.visibility = View.VISIBLE

        if (from != "DJ"){

            val trackData = tracksData.getTrackData(context)!!

            // collapse player thumb
            Picasso.get()
                    .load(trackData.getString("thumb"))
                    .into(mcontext.ivMediaAlbum)

            // expanded player thumb
            Picasso.get()
                    .load(trackData.getString("thumb"))
                    .into(mcontext.ivMediaControllerHeaderThumb)

            // waveform
            Picasso.get()
                    .load(trackData.getString("waveform_url"))
                    .into(mcontext.imageView3)

            mcontext.tvMediaControllerHeaderTitle.text = title

        }else{
            mcontext.ivMediaAlbum.setImageResource(R.drawable.isplas)
        }

        if (mediaPlayer.isPlaying){
            isPrepared(context)
        }
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, _intent: Intent) {
            val action = _intent.extras?.getString("UI")

            playProgress.visibility = View.GONE
            when (action){
                "updatePlayer" ->{
                    onMediaPlay(this@Home)
                }
                "prepared"->{
                    isPrepared(context)
                }
                "preparing"->{
                    preparing(context)
                }
                "updatePlayPause"->{
                    updatePlayPause(context)
                }
            }
        }
    }

    fun isPrepared(context: Context?){
        val mcontext = (context as Home)
        mcontext.expandedProgressBar.visibility = View.GONE
        mcontext.playProgress.visibility = View.GONE
        mcontext.ivPlayButton.visibility = View.VISIBLE
        mcontext.ivPlayPauseBurron.visibility = View.VISIBLE

        mcontext.seekBar2.max = mediaPlayer.duration
        mcontext.seekBar3.max = mediaPlayer.duration
        mcontext.progressBar.max = mediaPlayer.duration
        mcontext.durend.text = millisToTime(mediaPlayer.duration)

        updatePlayPause(context)
    }

    fun preparing(context: Context?){
        val mcontext = (context as Home)
        mcontext.expandedProgressBar.visibility = View.VISIBLE
        mcontext.playProgress.visibility = View.VISIBLE
        mcontext.ivPlayButton.visibility = View.GONE
        mcontext.ivPlayPauseBurron.visibility = View.GONE
        mcontext.seekBar2.progress = 0
        mcontext.seekBar3.progress = 0
        mcontext.progressBar.progress = 0
        updatePlayPause(context)
    }

    fun updatePlayPause(context: Context?){
        val mcontext = (context as Home)
        if (storage.isPlaying(context)!!){
            mcontext.ivPlayButton.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
            mcontext.ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
        }else{
            mcontext.ivPlayButton.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
            mcontext.ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
        }

    }

    fun firstUse (){
        val mcontext = (context as? Home)
        mcontext?.firstTimeDialog?.setContentView(R.layout.first_user_dialog)
        mcontext?.firstTimeDialog?.setCancelable(false)
        mcontext?.firstTimeDialog?.setCanceledOnTouchOutside(false)
        mcontext?.firstTimeDialog?.cbAgree?.setOnCheckedChangeListener { button, b ->
            mcontext?.firstTimeDialog?.bContinue?.isEnabled = firstTimeDialog?.cbAgree?.isChecked!!
        }

        mcontext?.firstTimeDialog?.bContinue?.setOnClickListener {
            storage.firstUser(this, false)
            mcontext?.firstTimeDialog?.dismiss()
        }
        mcontext?.firstTimeDialog?.show()
    }

    @SuppressLint("SetTextI18n")
    fun favorite_popup (context: Context, data:JSONObject, method:String, id: String, from:String){
        val favorites = Favorites()
        val mcontext = (context as? Home)

        mcontext?.firstTimeDialog?.setContentView(R.layout.favorite_dialog)
        mcontext?.firstTimeDialog?.setCancelable(false)
        mcontext?.firstTimeDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mcontext?.firstTimeDialog?.setCanceledOnTouchOutside(false)


        mcontext?.firstTimeDialog?.findViewById<TextView>(R.id.tvFaveQues)?.text = "Do you want to $method \n\n ${data.getString("title")} \n\n to favorites?"
        mcontext?.firstTimeDialog?.findViewById<Button>(R.id.favNo)?.setOnClickListener {

            mcontext.firstTimeDialog?.dismiss()
        }


        mcontext?.firstTimeDialog?.findViewById<Button>(R.id.favYes)?.setOnClickListener {
            favorites.AddRemoveFavorites(context,data.getString("id"), method, data.getString("stream_url"), data.getString("title"))
            var fragment: Fragment?
            if (from == "favorites"){
                fragment = Fragment_Favorites()

            }else{
                fragment = Fragment_Playlist()
                fragment.playlistData(context, tracksData.getPlaylistData(context,id))
            }



            val activity = context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.flFragments, fragment).commit()
            mcontext.firstTimeDialog?.dismiss()
        }

        mcontext?.firstTimeDialog?.show()
    }


    fun doneLoad(context: Context){
        val fragment = Fragment_Home()
        val activity = context as AppCompatActivity
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.flFragments, fragment).commit()
    }








    @SuppressLint("SetTextI18n")
    fun downloadingMusic(context: Context, title:String, action:String){
        val mcontext = context as? Home
        val fade_in = AnimationUtils.loadAnimation(mcontext, R.anim.fade_in)


        mcontext?.tvDownloadMusic?.visibility = View.VISIBLE
        mcontext?.tvDownloadMusic?.startAnimation(fade_in)

        mcontext?.tvDownloadMusic?.text = "$title is $action \n \n Tap to close"

    }








}