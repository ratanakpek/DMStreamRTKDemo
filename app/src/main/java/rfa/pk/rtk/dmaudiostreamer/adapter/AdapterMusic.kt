/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package rfa.pk.rtk.dmaudiostreamer.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener

import java.util.Collections
import java.util.LinkedList

import dm.audiostreamer.MediaMetaData
import rfa.pk.rtk.dmaudiostreamer.R

class AdapterMusic(private val mContext: Context, private val musicList: MutableList<MediaMetaData>?) : BaseAdapter() {
    private val inflate: LayoutInflater

    private val options: DisplayImageOptions
    private val imageLoader = ImageLoader.getInstance()
    private val animateFirstListener = AnimateFirstDisplayListener()

    private val colorPlay: ColorStateList
    private val colorPause: ColorStateList

    var listItemListener: ListItemListener? = null

    init {
        this.inflate = LayoutInflater.from(mContext)
        this.colorPlay = ColorStateList.valueOf(mContext.resources.getColor(R.color.md_green_600))
        this.colorPause = ColorStateList.valueOf(mContext.resources.getColor(R.color.md_green_800))
        this.options = DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art)
                .showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build()
    }

    fun refresh(musicList: List<MediaMetaData>) {
        if (this.musicList != null) {
            this.musicList.clear()
        }
        this.musicList!!.addAll(musicList)
        notifyDataSetChanged()
    }

    fun notifyPlayState(metaData: MediaMetaData?) {
        if (this.musicList != null && metaData != null) {
            var index = this.musicList.indexOf(metaData)
            //TODO SOMETIME INDEX RETURN -1 THOUGH THE OBJECT PRESENT IN THIS LIST
            if (index == -1) {
                for (i in this.musicList.indices) {
                    if (this.musicList[i].mediaId.equals(metaData.mediaId, ignoreCase = true)) {
                        index = i
                        break
                    }
                }
            }
            if (index > 0 && index < this.musicList.size) {
                this.musicList[index] = metaData
            }
        }
        notifyDataSetChanged()
    }


    override fun getCount(): Int {
        return musicList!!.size
    }

    override fun getItem(i: Int): Any {
        return musicList!![i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        var convertView = convertView

        val mViewHolder: ViewHolder
        if (convertView == null) {
            mViewHolder = ViewHolder()
            convertView = inflate.inflate(R.layout.inflate_allsongsitem, null)
            mViewHolder.mediaArt = convertView!!.findViewById<View>(R.id.img_mediaArt) as ImageView
            mViewHolder.playState = convertView.findViewById<View>(R.id.img_playState) as ImageView
            mViewHolder.mediaTitle = convertView.findViewById<View>(R.id.text_mediaTitle) as TextView
            mViewHolder.MediaDesc = convertView.findViewById<View>(R.id.text_mediaDesc) as TextView
            convertView.tag = mViewHolder
        } else {
            mViewHolder = convertView.tag as ViewHolder
        }
        val media = musicList!![position]

        mViewHolder.mediaTitle!!.text = media.mediaTitle
        mViewHolder.MediaDesc!!.text = media.mediaArtist
        mViewHolder.playState!!.setImageDrawable(getDrawableByState(mContext, media.playState))
        val mediaArt = media.mediaArt
        imageLoader.displayImage(mediaArt, mViewHolder.mediaArt, options, animateFirstListener)

        convertView.setOnClickListener {
            if (listItemListener != null) {
                listItemListener!!.onItemClickListener(musicList[position])
            }
        }

        return convertView
    }

    class ViewHolder {
        var mediaArt: ImageView? = null
        var playState: ImageView? = null
        var mediaTitle: TextView? = null
        var MediaDesc: TextView? = null
    }


    private fun getDrawableByState(context: Context, state: Int): Drawable {
        when (state) {
            PlaybackStateCompat.STATE_NONE -> {
                val pauseDrawable = ContextCompat.getDrawable(context, R.drawable.ic_play)
                DrawableCompat.setTintList(pauseDrawable, colorPlay)
                return pauseDrawable
            }
            PlaybackStateCompat.STATE_PLAYING -> {
                val animation = ContextCompat.getDrawable(context, R.drawable.equalizer) as AnimationDrawable
                DrawableCompat.setTintList(animation, colorPlay)
                animation.start()
                return animation
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                val playDrawable = ContextCompat.getDrawable(context, R.drawable.equalizer)
                DrawableCompat.setTintList(playDrawable, colorPause)
                return playDrawable
            }
            else -> {
                val noneDrawable = ContextCompat.getDrawable(context, R.drawable.ic_play)
                DrawableCompat.setTintList(noneDrawable, colorPlay)
                return noneDrawable
            }
        }
    }


    private inner class AnimateFirstDisplayListener : SimpleImageLoadingListener() {

        internal val displayedImages: MutableList<String> = Collections.synchronizedList(LinkedList())

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
                    FadeInBitmapDisplayer.animate(imageView, 200)
                    if (imageUri != null) {
                        displayedImages.add(imageUri)
                    }
                }
            }
            progressEvent(view, true)
        }

    }

    private fun progressEvent(v: View?, isShowing: Boolean) {
        try {
            val rl = (v as ImageView).parent as RelativeLayout
            val pg = rl.findViewById<View>(R.id.pg) as ProgressBar
            pg.visibility = if (isShowing) View.GONE else View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun setListItemListener(listItemListener: (Any) -> Unit) {
        this.listItemListener = listItemListener?
    }

    interface ListItemListener {
        fun onItemClickListener(media: MediaMetaData)
    }
}
