package rfa.pk.rtk.dmaudiostreamer

import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import java.util.ArrayList

import dm.audiostreamer.AudioStreamingManager
import dm.audiostreamer.CurrentSessionCallback
import dm.audiostreamer.MediaMetaData

class MainActivity : AppCompatActivity(), CurrentSessionCallback {

    private var context: MainActivity? = null
    private var streamingManager: AudioStreamingManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.context = this@MainActivity
        streamingManager = AudioStreamingManager.getInstance(context)


        val mediaMetaData = ArrayList<MediaMetaData>()
        val obj = MediaMetaData()
        obj.mediaId = "1"
        obj.mediaUrl = "http://www.rfa.org/khmer/news/history/history-on-organizing-commune-in-pre-era-12162009050945.html/h1215ov.mp3"
        obj.mediaTitle = "Hello World"


        val obj1 = MediaMetaData()
        obj.mediaId = "2"
        obj.mediaUrl = "http://www.rfa.org/khmer/news/history/history-on-organizing-commune-in-pre-era-12162009050945.html/h1215ov.mp3"
        obj.mediaTitle = "Hello World"

        val obj2 = MediaMetaData()
        obj.mediaId = "3"
        obj.mediaUrl = "http://www.rfa.org/khmer/news/history/history-on-organizing-commune-in-pre-era-12162009050945.html/h1215ov.mp3"
        obj.mediaTitle = "Hello World"
        mediaMetaData.add(obj)
        mediaMetaData.add(obj1)
        mediaMetaData.add(obj2)

        streamingManager!!.setMediaList(mediaMetaData)
        streamingManager!!.isPlayMultiple = true


        streamingManager!!.setShowPlayerNotification(true)
        //streamingManager.setPendingIntentAct(`Create Your Pending Intent And Set Here`);


    }

    public override fun onStart() {
        super.onStart()
        if (streamingManager != null) {
            streamingManager!!.subscribesCallBack(this)
        }
    }

    public override fun onStop() {
        super.onStop()
        if (streamingManager != null) {
            streamingManager!!.unSubscribeCallBack()
        }
    }

    override fun updatePlaybackState(state: Int) {
        when (state) {
            PlaybackStateCompat.STATE_PLAYING -> {
            }
            PlaybackStateCompat.STATE_PAUSED -> {
            }
            PlaybackStateCompat.STATE_NONE -> {
            }
            PlaybackStateCompat.STATE_STOPPED -> {
            }
            PlaybackStateCompat.STATE_BUFFERING -> {
            }
        }
    }

    override fun playSongComplete() {}

    override fun currentSeekBarPosition(progress: Int) {}

    override fun playCurrent(indexP: Int, currentAudio: MediaMetaData) {}

    override fun playNext(indexP: Int, CurrentAudio: MediaMetaData) {}

    override fun playPrevious(indexP: Int, currentAudio: MediaMetaData) {}

}
