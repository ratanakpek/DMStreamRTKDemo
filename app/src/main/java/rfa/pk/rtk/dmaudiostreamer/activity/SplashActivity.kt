/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */
package rfa.pk.rtk.dmaudiostreamer.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import rfa.pk.rtk.dmaudiostreamer.R

class SplashActivity : AppCompatActivity() {
    internal var handler = Handler()
    private var context: Context? = null

    internal var postTask: Runnable = Runnable {
        startActivity(Intent(context, MusicActivity::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        this.context = this@SplashActivity
        handler.postDelayed(postTask, delayTime)
    }

    override fun onDestroy() {
        handler.removeCallbacks(postTask)
        super.onDestroy()
    }

    companion object {

        private val delayTime: Long = 1000
    }

}
