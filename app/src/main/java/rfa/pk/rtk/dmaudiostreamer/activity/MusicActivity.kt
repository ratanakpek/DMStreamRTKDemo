/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package rfa.pk.rtk.dmaudiostreamer.activity

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener

import java.util.ArrayList
import java.util.Collections
import java.util.LinkedList

import dm.audiostreamer.AudioStreamingManager
import dm.audiostreamer.CurrentSessionCallback
import dm.audiostreamer.Logger
import dm.audiostreamer.MediaMetaData
import rfa.pk.rtk.dmaudiostreamer.R
import rfa.pk.rtk.dmaudiostreamer.adapter.AdapterMusic
import rfa.pk.rtk.dmaudiostreamer.network.MusicBrowser
import rfa.pk.rtk.dmaudiostreamer.network.MusicLoaderListener
import rfa.pk.rtk.dmaudiostreamer.slidinguppanel.SlidingUpPanelLayout
import rfa.pk.rtk.dmaudiostreamer.widgets.LineProgress
import rfa.pk.rtk.dmaudiostreamer.widgets.PlayPauseView
import rfa.pk.rtk.dmaudiostreamer.widgets.Slider


class MusicActivity : AppCompatActivity(), CurrentSessionCallback, View.OnClickListener, Slider.OnValueChangedListener {
    private var context: Context? = null
    private var musicList: ListView? = null
    private var adapterMusic: AdapterMusic? = null

    private var btn_play: PlayPauseView? = null
    private var image_songAlbumArt: ImageView? = null
    private var img_bottom_albArt: ImageView? = null
    private var image_songAlbumArtBlur: ImageView? = null
    private var time_progress_slide: TextView? = null
    private var time_total_slide: TextView? = null
    private var time_progress_bottom: TextView? = null
    private var time_total_bottom: TextView? = null
    private var pgPlayPauseLayout: RelativeLayout? = null
    private var lineProgress: LineProgress? = null
    // private Slider audioPg;
    private var btn_backward: ImageView? = null
    private var btn_forward: ImageView? = null
    private var text_songName: TextView? = null
    private var text_songAlb: TextView? = null
    private var txt_bottom_SongName: TextView? = null
    private var txt_bottom_SongAlb: TextView? = null

    private var mLayout: SlidingUpPanelLayout? = null
    private var slideBottomView: RelativeLayout? = null
    private var isExpand = false

    private var options: DisplayImageOptions? = null
    private val imageLoader = ImageLoader.getInstance()
    private val animateFirstListener = AnimateFirstDisplayListener()

    //For  Implementation
    private var streamingManager: AudioStreamingManager? = null
    private var currentSong: MediaMetaData? = null
    private var listOfSongs: List<MediaMetaData> = ArrayList()

    private val notificationPendingIntent: PendingIntent
        get() {
            val intent = Intent(context, MusicActivity::class.java)
            intent.action = "openplayer"
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            return PendingIntent.getActivity(context, 0, intent, 0)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        this.context = this@MusicActivity
        configAudioStreamer()
        uiInitialization()
        loadMusicData()
    }

    override fun onBackPressed() {
        if (isExpand) {
            mLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else {
            super.onBackPressed()
            overridePendingTransition(0, 0)
            finish()
        }
    }


    public override fun onStart() {
        super.onStart()
        try {
            if (streamingManager != null) {
                streamingManager!!.subscribesCallBack(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    public override fun onStop() {
        try {
            if (streamingManager != null) {
                streamingManager!!.unSubscribeCallBack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onStop()
    }

    override fun onDestroy() {
        try {
            if (streamingManager != null) {
                streamingManager!!.unSubscribeCallBack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    override fun updatePlaybackState(state: Int) {
        Logger.e("updatePlaybackState: ", "" + state)
        when (state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                pgPlayPauseLayout!!.visibility = View.INVISIBLE
                btn_play!!.Play()
                if (currentSong != null) {
                    currentSong!!.playState = PlaybackStateCompat.STATE_PLAYING
                    notifyAdapter(currentSong!!)
                }
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                pgPlayPauseLayout!!.visibility = View.INVISIBLE
                btn_play!!.Pause()
                if (currentSong != null) {
                    currentSong!!.playState = PlaybackStateCompat.STATE_PAUSED
                    notifyAdapter(currentSong!!)
                }
            }
            PlaybackStateCompat.STATE_NONE -> {
                currentSong!!.playState = PlaybackStateCompat.STATE_NONE
                notifyAdapter(currentSong!!)
            }
            PlaybackStateCompat.STATE_STOPPED -> {
                pgPlayPauseLayout!!.visibility = View.INVISIBLE
                btn_play!!.Pause()
                //  audioPg.setValue(0);
                if (currentSong != null) {
                    currentSong!!.playState = PlaybackStateCompat.STATE_NONE
                    notifyAdapter(currentSong!!)
                }
            }
            PlaybackStateCompat.STATE_BUFFERING -> {
                pgPlayPauseLayout!!.visibility = View.VISIBLE
                if (currentSong != null) {
                    currentSong!!.playState = PlaybackStateCompat.STATE_NONE
                    notifyAdapter(currentSong!!)
                }
            }
        }
    }

    override fun playSongComplete() {
        val timeString = "00.00"
        time_total_bottom!!.text = timeString
        time_total_slide!!.text = timeString
        time_progress_bottom!!.text = timeString
        time_progress_slide!!.text = timeString
        lineProgress!!.setLineProgress(0)
        //audioPg.setValue(0);
    }

    override fun currentSeekBarPosition(progress: Int) {
        //audioPg.setValue(progress);
        setPGTime(progress)
    }

    override fun playCurrent(indexP: Int, currentAudio: MediaMetaData) {
        showMediaInfo(currentAudio)
        notifyAdapter(currentAudio)
    }

    override fun playNext(indexP: Int, CurrentAudio: MediaMetaData) {
        showMediaInfo(CurrentAudio)
    }

    override fun playPrevious(indexP: Int, currentAudio: MediaMetaData) {
        showMediaInfo(currentAudio)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_forward -> streamingManager!!.onSkipToNext()
            R.id.btn_backward -> streamingManager!!.onSkipToPrevious()
            R.id.btn_play -> if (currentSong != null) {
                playPauseEvent(view)
            }
        }
    }

    override fun onValueChanged(value: Int) {
        streamingManager!!.onSeekTo(value.toLong())
        streamingManager!!.scheduleSeekBarUpdate()
    }

    private fun notifyAdapter(media: MediaMetaData) {
        adapterMusic!!.notifyPlayState(media)
    }

    private fun playPauseEvent(v: View) {
        if (streamingManager!!.isPlaying) {
            streamingManager!!.onPause()
            (v as PlayPauseView).Pause()
        } else {
            streamingManager!!.onPlay(currentSong)
            (v as PlayPauseView).Play()
        }
    }

    private fun playSong(media: MediaMetaData) {
        if (streamingManager != null) {
            streamingManager!!.onPlay(media)
            showMediaInfo(media)
        }
    }

    private fun showMediaInfo(media: MediaMetaData) {
        currentSong = media
        // audioPg.setValue(0);
        // audioPg.setMin(0);
        // audioPg.setMax(Integer.valueOf(media.getMediaDuration()) * 1000);
        setPGTime(0)
        setMaxTime()
        loadSongDetails(media)
    }

    private fun configAudioStreamer() {
        streamingManager = AudioStreamingManager.getInstance(context)
        //Set PlayMultiple 'true' if want to playing sequentially one by one songs
        // and provide the list of songs else set it 'false'
        streamingManager!!.isPlayMultiple = true
        streamingManager!!.setMediaList(listOfSongs)
        //If you want to show the Player Notification then set ShowPlayerNotification as true
        //and provide the pending intent so that after click on notification it will redirect to an activity
        streamingManager!!.setShowPlayerNotification(true)
        streamingManager!!.setPendingIntentAct(notificationPendingIntent)
    }

    private fun uiInitialization() {

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.app_name)

        btn_play = findViewById<View>(R.id.btn_play) as PlayPauseView
        image_songAlbumArtBlur = findViewById<View>(R.id.image_songAlbumArtBlur) as ImageView
        image_songAlbumArt = findViewById<View>(R.id.image_songAlbumArt) as ImageView
        img_bottom_albArt = findViewById<View>(R.id.img_bottom_albArt) as ImageView
        btn_backward = findViewById<View>(R.id.btn_backward) as ImageView
        btn_forward = findViewById<View>(R.id.btn_forward) as ImageView
        // audioPg = (Slider) findViewById(R.id.audio_progress_control);
        pgPlayPauseLayout = findViewById<View>(R.id.pgPlayPauseLayout) as RelativeLayout
        lineProgress = findViewById<View>(R.id.lineProgress) as LineProgress
        time_progress_slide = findViewById<View>(R.id.slidepanel_time_progress) as TextView
        time_total_slide = findViewById<View>(R.id.slidepanel_time_total) as TextView
        time_progress_bottom = findViewById<View>(R.id.slidepanel_time_progress_bottom) as TextView
        time_total_bottom = findViewById<View>(R.id.slidepanel_time_total_bottom) as TextView

        btn_backward!!.setOnClickListener(this)
        btn_forward!!.setOnClickListener(this)
        btn_play!!.setOnClickListener(this)
        pgPlayPauseLayout!!.setOnClickListener(View.OnClickListener { return@OnClickListener })

        btn_play!!.Pause()

        changeButtonColor(btn_backward!!)
        changeButtonColor(btn_forward!!)

        text_songName = findViewById<View>(R.id.text_songName) as TextView
        text_songAlb = findViewById<View>(R.id.text_songAlb) as TextView
        txt_bottom_SongName = findViewById<View>(R.id.txt_bottom_SongName) as TextView
        txt_bottom_SongAlb = findViewById<View>(R.id.txt_bottom_SongAlb) as TextView

        mLayout = findViewById<View>(R.id.sliding_layout) as SlidingUpPanelLayout

        slideBottomView = findViewById<View>(R.id.slideBottomView) as RelativeLayout
        slideBottomView!!.visibility = View.VISIBLE
        slideBottomView!!.setOnClickListener { mLayout!!.panelState = SlidingUpPanelLayout.PanelState.EXPANDED }

        //  audioPg.setMax(0);
        //   audioPg.setOnValueChangedListener(this);

        mLayout!!.setPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                if (slideOffset == 0.0f) {
                    isExpand = false
                    slideBottomView!!.visibility = View.VISIBLE
                    //slideBottomView.getBackground().setAlpha(0);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    //slideBottomView.getBackground().setAlpha((int) slideOffset * 255);
                } else {
                    //slideBottomView.getBackground().setAlpha(100);
                    isExpand = true
                    slideBottomView!!.visibility = View.GONE
                }
            }

            override fun onPanelExpanded(panel: View) {
                isExpand = true
            }

            override fun onPanelCollapsed(panel: View) {
                isExpand = false
            }

            override fun onPanelAnchored(panel: View) {}

            override fun onPanelHidden(panel: View) {}
        })

        musicList = findViewById<View>(R.id.musicList) as ListView
        adapterMusic = AdapterMusic(this!!.context!!, ArrayList())
        adapterMusic!!.setListItemListener { media -> playSong(media as MediaMetaData) }
        musicList!!.adapter = adapterMusic

        this.options = DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art)
                .showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build()

    }

    /*   private void loadMusicFromFirebase(){
        listOfSongs = listMusic;
        adapterMusic.refresh(listMusic);

        configAudioStreamer();
        checkAlreadyPlaying();
    }*/

    private fun loadMusicData() {
        MusicBrowser.loadMusic(this!!.context!!, object : MusicLoaderListener {
            override fun onLoadSuccess(listMusic: List<MediaMetaData>) {
                listOfSongs = listMusic
                adapterMusic!!.refresh(listMusic)

                configAudioStreamer()
                checkAlreadyPlaying()
            }

            override fun onLoadFailed() {
                //TODO SHOW FAILED REASON
            }

            override fun onLoadError() {
                //TODO SHOW ERROR
            }
        })
    }

    private fun checkAlreadyPlaying() {
        if (streamingManager!!.isPlaying) {
            currentSong = streamingManager!!.currentAudio
            if (currentSong != null) {
                currentSong!!.playState = streamingManager!!.mLastPlaybackState
                showMediaInfo(currentSong!!)
                notifyAdapter(currentSong!!)
            }
        }
    }

    private fun loadSongDetails(metaData: MediaMetaData) {
        text_songName!!.text = metaData.mediaTitle
        text_songAlb!!.text = metaData.mediaArtist
        txt_bottom_SongName!!.text = metaData.mediaTitle
        txt_bottom_SongAlb!!.text = metaData.mediaArtist

        imageLoader.displayImage(metaData.mediaArt, image_songAlbumArtBlur, options, animateFirstListener)
        imageLoader.displayImage(metaData.mediaArt, image_songAlbumArt, options, animateFirstListener)
        imageLoader.displayImage(metaData.mediaArt, img_bottom_albArt, options, animateFirstListener)
    }

    private class AnimateFirstDisplayListener : SimpleImageLoadingListener() {

        override fun onLoadingStarted(imageUri: String?, view: View?) {
            progressEvent(view, false)
        }

        override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
            progressEvent(view, true)
        }

        override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
            if (loadedImage != null) {
                val imageView = view as ImageView?
                val firstDisplay = !displayedImages.contains(imageUri)
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 1000)
                    displayedImages.add(imageUri!!)
                }
            }
            progressEvent(view, true)
        }

        companion object {

            internal val displayedImages: MutableList<String> = Collections.synchronizedList(LinkedList())
        }

    }

    private fun setPGTime(progress: Int) {
        try {
            var timeString = "00.00"
            var linePG = 0
            currentSong = streamingManager!!.currentAudio
            if (currentSong != null && progress.toLong() != java.lang.Long.parseLong(currentSong!!.mediaDuration)) {
                timeString = DateUtils.formatElapsedTime((progress / 1000).toLong())
                val audioDuration = java.lang.Long.parseLong(currentSong!!.mediaDuration)
                linePG = (progress / 1000 * 100 / audioDuration).toInt()
            }
            time_progress_bottom!!.text = timeString
            time_progress_slide!!.text = timeString
            lineProgress!!.setLineProgress(linePG)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

    }

    private fun setMaxTime() {
        try {
            val timeString = DateUtils.formatElapsedTime(java.lang.Long.parseLong(currentSong!!.mediaDuration))
            time_total_bottom!!.text = timeString
            time_total_slide!!.text = timeString
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

    }

    private fun changeButtonColor(imageView: ImageView) {
        try {
            val color = Color.BLACK
            imageView.setColorFilter(color)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        private val TAG = MusicActivity::class.java!!.getSimpleName()

        private fun progressEvent(v: View?, isShowing: Boolean) {
            try {
                val parent = (v as ImageView).parent as View
                val pg = parent.findViewById<View>(R.id.pg) as ProgressBar
                if (pg != null)
                    pg.visibility = if (isShowing) View.GONE else View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}