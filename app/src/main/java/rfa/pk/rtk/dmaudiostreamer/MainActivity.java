package rfa.pk.rtk.dmaudiostreamer;

import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import dm.audiostreamer.AudioStreamingManager;
import dm.audiostreamer.CurrentSessionCallback;
import dm.audiostreamer.MediaMetaData;

public class MainActivity extends AppCompatActivity implements CurrentSessionCallback {

    private MainActivity context;
    private AudioStreamingManager streamingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = MainActivity.this;
        streamingManager = AudioStreamingManager.getInstance(context);




        List<MediaMetaData> mediaMetaData= new ArrayList<>();
        MediaMetaData obj = new MediaMetaData();
        obj.setMediaId("1");
        obj.setMediaUrl("http://www.rfa.org/khmer/news/history/history-on-organizing-commune-in-pre-era-12162009050945.html/h1215ov.mp3");
        obj.setMediaTitle("Hello World");


        MediaMetaData obj1 = new MediaMetaData();
        obj.setMediaId("2");
        obj.setMediaUrl("http://www.rfa.org/khmer/news/history/history-on-organizing-commune-in-pre-era-12162009050945.html/h1215ov.mp3");
        obj.setMediaTitle("Hello World");

        MediaMetaData obj2 = new MediaMetaData();
        obj.setMediaId("3");
        obj.setMediaUrl("http://www.rfa.org/khmer/news/history/history-on-organizing-commune-in-pre-era-12162009050945.html/h1215ov.mp3");
        obj.setMediaTitle("Hello World");
        mediaMetaData.add(obj);
        mediaMetaData.add(obj1);
        mediaMetaData.add(obj2);

        streamingManager.setMediaList(mediaMetaData);
        streamingManager.setPlayMultiple(true);


        streamingManager.setShowPlayerNotification(true);
        //streamingManager.setPendingIntentAct(`Create Your Pending Intent And Set Here`);



    }

    @Override
    public void onStart() {
        super.onStart();
        if (streamingManager != null) {
            streamingManager.subscribesCallBack(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (streamingManager != null) {
            streamingManager.unSubscribeCallBack();
        }
    }

    @Override
    public void updatePlaybackState(int state) {
        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                break;
            case PlaybackStateCompat.STATE_NONE:
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                break;
        }
    }

    @Override
    public void playSongComplete() {
    }

    @Override
    public void currentSeekBarPosition(int progress) {
    }

    @Override
    public void playCurrent(int indexP, MediaMetaData currentAudio) {
    }

    @Override
    public void playNext(int indexP, MediaMetaData CurrentAudio) {
    }

    @Override
    public void playPrevious(int indexP, MediaMetaData currentAudio) {
    }

}
