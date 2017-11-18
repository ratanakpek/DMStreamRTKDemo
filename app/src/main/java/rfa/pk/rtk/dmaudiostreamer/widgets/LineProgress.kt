package rfa.pk.rtk.dmaudiostreamer.widgets

/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class LineProgress : View {

    var progress: Int = 0
    var paint = Paint()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint = Paint()
        paint.color = Color.TRANSPARENT
        canvas.drawRect(0f, 0f, this.width.toFloat(), this.height.toFloat(), paint)

        paint = Paint()
        paint.color = resources.getColor(dm.audiostreamer.R.color.md_green_800)
        canvas.drawRect(0f, 0f, this.progress.toFloat(), this.height.toFloat(), paint)
    }

    fun setLineProgress(pg: Int) {
        val wdt = this.width
        this.progress = pg * (wdt / 100)
        invalidate()
    }
}


