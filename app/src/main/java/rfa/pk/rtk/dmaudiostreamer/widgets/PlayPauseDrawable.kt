package rfa.pk.rtk.dmaudiostreamer.widgets

/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Property

class PlayPauseDrawable(context: Context) : Drawable() {

    private val mLeftPauseBar = Path()
    private val mRightPauseBar = Path()
    private val mPaint = Paint()
    private val mBounds = RectF()
    private var mPauseBarWidth: Float = 0.toFloat()
    private var mPauseBarHeight: Float = 0.toFloat()
    private var mPauseBarDistance: Float = 0.toFloat()

    private var mWidth: Float = 0.toFloat()
    private var mHeight: Float = 0.toFloat()

    private var progress: Float = 0.toFloat()
        set(progress) {
            field = progress
            invalidateSelf()
        }
    var isPlay: Boolean = false
        private set

    val pausePlayAnimator: Animator
        get() {
            val anim = ObjectAnimator.ofFloat<PlayPauseDrawable>(this, PROGRESS, if (isPlay) 1 else 0, if (isPlay) 0 else 1)
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isPlay = !isPlay
                }
            })
            return anim
        }

    fun setmIsPlay(mIsPlay: Boolean) {
        this.isPlay = mIsPlay
    }

    init {
        val res = context.resources
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.WHITE
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mBounds.set(bounds)
        mWidth = mBounds.width()
        mHeight = mBounds.height()

        mPauseBarWidth = mWidth / 10
        mPauseBarHeight = mHeight * 0.30f
        mPauseBarDistance = mPauseBarWidth

    }

    override fun draw(canvas: Canvas) {
        mLeftPauseBar.rewind()
        mRightPauseBar.rewind()

        // The current distance between the two pause bars.
        val barDist = lerp(mPauseBarDistance, 0f, progress)
        // The current width of each pause bar.
        val barWidth = lerp(mPauseBarWidth, mPauseBarHeight / 2f, progress)
        // The current position of the left pause bar's top left coordinate.
        val firstBarTopLeft = lerp(0f, barWidth, progress)
        // The current position of the right pause bar's top right coordinate.
        val secondBarTopRight = lerp(2 * barWidth + barDist, barWidth + barDist, progress)

        // Draw the left pause bar. The left pause bar transforms into the
        // top half of the play button triangle by animating the position of the
        // rectangle's top left coordinate and expanding its bottom width.
        mLeftPauseBar.moveTo(0f, 0f)
        mLeftPauseBar.lineTo(firstBarTopLeft, -mPauseBarHeight)
        mLeftPauseBar.lineTo(barWidth, -mPauseBarHeight)
        mLeftPauseBar.lineTo(barWidth, 0f)
        mLeftPauseBar.close()

        // Draw the right pause bar. The right pause bar transforms into the
        // bottom half of the play button triangle by animating the position of
        // the
        // rectangle's top right coordinate and expanding its bottom width.
        mRightPauseBar.moveTo(barWidth + barDist, 0f)
        mRightPauseBar.lineTo(barWidth + barDist, -mPauseBarHeight)
        mRightPauseBar.lineTo(secondBarTopRight, -mPauseBarHeight)
        mRightPauseBar.lineTo(2 * barWidth + barDist, 0f)
        mRightPauseBar.close()

        canvas.save()

        // Translate the play button a tiny bit to the right so it looks more
        // centered.
        canvas.translate(lerp(0f, mPauseBarHeight / 8f, progress), 0f)

        // (1) Pause --> Play: rotate 0 to 90 degrees clockwise.
        // (2) Play --> Pause: rotate 90 to 180 degrees clockwise.
        val rotationProgress = if (isPlay) 1 - progress else progress
        val startingRotation = (if (isPlay) 90 else 0).toFloat()
        canvas.rotate(lerp(startingRotation, startingRotation + 90, rotationProgress), mWidth / 2f, mHeight / 2f)

        // Position the pause/play button in the center of the drawable's
        // bounds.
        canvas.translate(mWidth / 2f - (2 * barWidth + barDist) / 2f, mHeight / 2f + mPauseBarHeight / 2f)

        // Draw the two bars that form the animated pause/play button.
        canvas.drawPath(mLeftPauseBar, mPaint)
        canvas.drawPath(mRightPauseBar, mPaint)

        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    companion object {

        private val PROGRESS = object : Property<PlayPauseDrawable, Float>(Float::class.java, "progress") {
            override fun get(d: PlayPauseDrawable): Float {
                return d.progress
            }

            override fun set(d: PlayPauseDrawable, value: Float?) {
                d.progress = value
            }
        }

        /**
         * Linear interpolate between a and b with parameter t.
         */
        private fun lerp(a: Float, b: Float, t: Float): Float {
            return a + (b - a) * t
        }
    }
}
