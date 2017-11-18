package rfa.pk.rtk.dmaudiostreamer.widgets

/*
 * This is the source code of DMAudioStreaming for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry(dibakar.ece@gmail.com), 2017.
 */


import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

import com.nineoldandroids.view.ViewHelper

import rfa.pk.rtk.dmaudiostreamer.R


class Slider(context: Context, attrs: AttributeSet) : CustomView(context, attrs) {

    private var backgroundColor = Color.parseColor("#000000")
    private var backgroundColorLine = Color.parseColor("#000000")
    private var ball: Ball? = null
    private val bitmap: Bitmap? = null
    var max = 100
    var min = 0
    var onValueChangedListener: OnValueChangedListener? = null
    private var placedBall = false
    private var press = false
    private var value = 0

    init {
        setAttributes(attrs)
    }

    // GETERS & SETTERS

    fun getValue(): Int {
        return value
    }

    fun setValue(value: Int) {
        if (placedBall == false)
            post { setValue(value) }
        else {
            this.value = value
            val division = (ball!!.xFin - ball!!.xIni) / max
            ViewHelper.setX(ball!!, value * division + height / 2 - ball!!.width / 2)
            ball!!.changeBackground()
        }

    }

    override fun invalidate() {
        ball!!.invalidate()
        super.invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        isLastTouch = true
        if (isEnabled) {
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {

                if (event.x <= width && event.x >= 0) {
                    press = true
                    // calculate value
                    var newValue = 0
                    val division = (ball!!.xFin - ball!!.xIni) / (max - min)
                    if (event.x > ball!!.xFin) {
                        newValue = max
                    } else if (event.x < ball!!.xIni) {
                        newValue = min
                    } else {
                        newValue = min + ((event.x - ball!!.xIni) / division).toInt()
                    }
                    if (value != newValue) {
                        value = newValue
                        if (onValueChangedListener != null)
                            onValueChangedListener!!.onValueChanged(newValue)
                    }
                    // move ball indicator
                    var x = event.x
                    x = if (x < ball!!.xIni) ball!!.xIni else x
                    x = if (x > ball!!.xFin) ball!!.xFin else x
                    ViewHelper.setX(ball!!, x)
                    ball!!.changeBackground()

                } else {
                    press = false
                    isLastTouch = false
                }

            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                press = false
            }
        }
        return true
    }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
        backgroundColorLine = makeLineDeselectColor()
        if (isEnabled)
            beforeBackground = backgroundColor
    }

    /**
     * Make a dark color to press effect
     *
     * @return
     */
    protected fun makeLineDeselectColor(): Int {
        var r = this.backgroundColor shr 16 and 0xFF
        var g = this.backgroundColor shr 8 and 0xFF
        var b = this.backgroundColor shr 0 and 0xFF
        r = if (r - 30 < 0) 0 else r - 30
        g = if (g - 30 < 0) 0 else g - 30
        b = if (b - 30 < 0) 0 else b - 30
        return Color.argb(128, r, g, b)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!placedBall) {
            placeBall()
        }

        val paint = Paint()
        paint.color = backgroundColorLine
        paint.strokeWidth = dpToPx(3f, resources).toFloat()
        canvas.drawLine((height / 2).toFloat(), (height / 2).toFloat(), (width - height / 2).toFloat(), (height / 2).toFloat(), paint)
        paint.color = backgroundColor
        val division = (ball!!.xFin - ball!!.xIni) / (max - min)
        val value = this.value - min
        canvas.drawLine((height / 2).toFloat(), (height / 2).toFloat(), value * division + height / 2, (height / 2).toFloat(), paint)

        if (press) {
            paint.color = backgroundColor
            paint.isAntiAlias = true
            canvas.drawCircle(ViewHelper.getX(ball!!) + ball!!.width / 2, (height / 2).toFloat(), (height / 4).toFloat(), paint)
        }
        invalidate()
    }

    // Set atributtes of XML to View
    protected fun setAttributes(attrs: AttributeSet) {

        setBackgroundResource(R.drawable.background_transparent)

        // Set size of view
        minimumHeight = dpToPx(48f, resources)
        minimumWidth = dpToPx(80f, resources)

        // Set background Color
        // Color by resource
        val bacgroundColor = attrs.getAttributeResourceValue(CustomView.ANDROIDXML, "background", -1)
        if (bacgroundColor != -1) {
            setBackgroundColor(resources.getColor(bacgroundColor))
        } else {
            // Color by hexadecimal
            val background = attrs.getAttributeIntValue(CustomView.ANDROIDXML, "background", -1)
            if (background != -1)
                setBackgroundColor(background)
        }

        min = attrs.getAttributeIntValue(CustomView.MATERIALDESIGNXML, "min", 0)
        max = attrs.getAttributeIntValue(CustomView.MATERIALDESIGNXML, "max", 0)
        value = attrs.getAttributeIntValue(CustomView.MATERIALDESIGNXML, "value", min)

        ball = Ball(context)
        val params = RelativeLayout.LayoutParams(dpToPx(15f, resources), dpToPx(15f, resources))
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
        ball!!.layoutParams = params
        addView(ball)

    }

    private fun placeBall() {
        ViewHelper.setX(ball!!, (height / 2 - ball!!.width / 2).toFloat())
        ball!!.xIni = ViewHelper.getX(ball!!)
        ball!!.xFin = (width - height / 2 - ball!!.width / 2).toFloat()
        ball!!.xCen = (width / 2 - ball!!.width / 2).toFloat()
        placedBall = true
    }

    // Event when slider change value
    interface OnValueChangedListener {
        fun onValueChanged(value: Int)
    }

    internal inner class Ball(context: Context) : View(context) {

        var xIni: Float = 0.toFloat()
        var xFin: Float = 0.toFloat()
        var xCen: Float = 0.toFloat()

        init {
            changeBackground()
        }

        fun changeBackground() {
            setBackgroundResource(R.drawable.background_checkbox)
            val layer = background as LayerDrawable
            val shape = layer.findDrawableByLayerId(R.id.shape_bacground) as GradientDrawable
            shape.setColor(backgroundColor)
        }

    }

    companion object {

        /**
         * Convert Dp to Pixel
         */
        fun dpToPx(dp: Float, resources: Resources): Int {
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
            return px.toInt()
        }

        fun getRelativeTop(myView: View): Int {
            return if (myView.id == android.R.id.content)
                myView.top
            else
                myView.top + getRelativeTop(myView.parent as View)
        }

        fun getRelativeLeft(myView: View): Int {
            return if (myView.id == android.R.id.content)
                myView.left
            else
                myView.left + getRelativeLeft(myView.parent as View)
        }
    }

}
