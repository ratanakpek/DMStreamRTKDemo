package rfa.pk.rtk.dmaudiostreamer.widgets

/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */


import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Property
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView

import rfa.pk.rtk.dmaudiostreamer.R


@SuppressLint("AppCompatCustomView")
class PlayPauseView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    private val mDrawable: PlayPauseDrawable
    private val mPaint = Paint()
    private val mPauseBackgroundColor: Int
    private val mPlayBackgroundColor: Int
    var isDrawCircle = true

    private var mAnimatorSet: AnimatorSet? = null
    private var mBackgroundColor: Int = 0
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var color: Int
        get() = mBackgroundColor
        set(color) {
            mBackgroundColor = color
            invalidate()
        }

    var isPlay: Boolean = false
        private set

    init {
        setWillNotDraw(false)

        val colorTheme = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, colorTheme, true)

        mBackgroundColor = Color.parseColor("#000000")//colorTheme.data;
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mDrawable = PlayPauseDrawable(context)
        mDrawable.callback = this

        mPauseBackgroundColor = Color.parseColor("#000000")//colorTheme.data;
        mPlayBackgroundColor = Color.parseColor("#000000")//colorTheme.data;

        val a = context.obtainStyledAttributes(attrs, R.styleable.PlayPause)
        isDrawCircle = a.getBoolean(R.styleable.PlayPause_isCircleDraw, isDrawCircle)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // final int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // setMeasuredDimension(size, size);
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawable.setBounds(0, 0, w, h)
        mWidth = w
        mHeight = h

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = object : ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
            clipToOutline = true
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return who === mDrawable || super.verifyDrawable(who)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = mBackgroundColor
        val radius = Math.min(mWidth, mHeight) / 2f
        if (isDrawCircle) {
            canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint)
        }
        mDrawable.draw(canvas)
    }

    // public void toggle() {
    // if (mAnimatorSet != null) {
    // mAnimatorSet.cancel();
    // }
    //
    // mAnimatorSet = new AnimatorSet();
    // final boolean isPlay = mDrawable.isPlay();
    // final ObjectAnimator colorAnim = ObjectAnimator.ofInt(this, COLOR, isPlay
    // ? mPauseBackgroundColor : mPlayBackgroundColor);
    // colorAnim.setEvaluator(new ArgbEvaluator());
    // final Animator pausePlayAnim = mDrawable.getPausePlayAnimator();
    // mAnimatorSet.setInterpolator(new DecelerateInterpolator());
    // mAnimatorSet.setDuration(PLAY_PAUSE_ANIMATION_DURATION);
    // mAnimatorSet.playTogether(colorAnim, pausePlayAnim);
    // mAnimatorSet.start();
    // }

    fun Play() {
        if (mAnimatorSet != null) {
            mAnimatorSet!!.cancel()
        }
        mAnimatorSet = AnimatorSet()
        val colorAnim = ObjectAnimator.ofInt(this, COLOR, mPlayBackgroundColor)
        isPlay = true
        colorAnim.setEvaluator(ArgbEvaluator())
        mDrawable.setmIsPlay(isPlay)
        val pausePlayAnim = mDrawable.pausePlayAnimator
        mAnimatorSet!!.interpolator = DecelerateInterpolator()
        mAnimatorSet!!.duration = PLAY_PAUSE_ANIMATION_DURATION
        mAnimatorSet!!.playTogether(colorAnim, pausePlayAnim)
        mAnimatorSet!!.start()
    }

    fun Pause() {
        if (mAnimatorSet != null) {
            mAnimatorSet!!.cancel()
        }

        mAnimatorSet = AnimatorSet()
        val colorAnim = ObjectAnimator.ofInt(this, COLOR, mPauseBackgroundColor)
        isPlay = false
        colorAnim.setEvaluator(ArgbEvaluator())
        mDrawable.setmIsPlay(isPlay)
        val pausePlayAnim = mDrawable.pausePlayAnimator
        mAnimatorSet!!.interpolator = DecelerateInterpolator()
        mAnimatorSet!!.duration = PLAY_PAUSE_ANIMATION_DURATION
        mAnimatorSet!!.playTogether(colorAnim, pausePlayAnim)
        mAnimatorSet!!.start()
    }

    companion object {

        private val COLOR = object : Property<PlayPauseView, Int>(Int::class.java, "color") {
            override fun get(v: PlayPauseView): Int {
                return v.color
            }

            override fun set(v: PlayPauseView, value: Int?) {
                v.color = value
            }
        }

        private val PLAY_PAUSE_ANIMATION_DURATION: Long = 200
    }

}
