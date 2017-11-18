/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package rfa.pk.rtk.dmaudiostreamer

import android.app.Application
import android.content.Context

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType


class AudioStreamerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //Image Loader
        initImageLoader(applicationContext)
    }

    companion object {

        fun initImageLoader(context: Context) {
            val config = ImageLoaderConfiguration.Builder(context)
            config.threadPriority(Thread.NORM_PRIORITY - 2)
            config.denyCacheImageMultipleSizesInMemory()
            config.diskCacheFileNameGenerator(Md5FileNameGenerator())
            config.diskCacheSize(50 * 1024 * 1024) // 50 MiB
            config.tasksProcessingOrder(QueueProcessingType.LIFO)
            config.writeDebugLogs() // Remove for release app

            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config.build())
        }
    }
}
