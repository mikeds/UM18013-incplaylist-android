package com.incplaylist.incoriginalmedia.customwave

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.Nullable
import com.gigamole.infinitecycleviewpager.BuildConfig
import com.incplaylist.incoriginalmedia.R
import java.util.*
@SuppressLint("NewApi")
class DrawView : LinearLayout {
    private var ViewHeight = 0
    private var ViewMid = 0.0f
    private var ViewWidth = 0
    var amplitude = 0.5f
    var density = 1.0f
    private var drawLock = false
    var frequency = 1.2f
    private var maxAmplitude = 0.0f
    private var numberOfWaves = 0
    private val paintsArray =
        ArrayList<Paint>()
    private var path2: Path? = null
    var phase = 0.0f
    var phaseShift = -0.25f
    var refreshInterval = 30
    private var waveColor = 0

    constructor(
        context: Context,
        @Nullable attrs: AttributeSet?
    ) : super(context, attrs) {
        initialize(context, attrs, 0)
    }

    constructor(
        context: Context,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs, defStyleAttr)
    }


    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initialize(context, attrs, defStyleAttr)
    }

    fun initShit() {
        val res = resources
        for (i in 0 until numberOfWaves) {
            val multiplier = Math.min(
                1.0f,
                (1.0f - i.toFloat() / numberOfWaves.toFloat()) / 3.0f * 2.0f + 0.33333334f
            )
            var p: Paint
            if (i == 0) {
                p = Paint(1)
                p.color = waveColor
                p.strokeWidth = res.getDimension(R.dimen.waver_width)
                p.style = Paint.Style.STROKE
                paintsArray.add(p)
            } else {
                p = Paint(1)
                Log.v(
                    "Color",
                    BuildConfig.FLAVOR + ((1.0f * multiplier).toDouble() * 0.7 * 255.0).toInt()
                )
                p.color = waveColor
                p.alpha = ((1.0f * multiplier).toDouble() * 0.8 * 255.0).toInt()
                p.strokeWidth = res.getDimension(R.dimen.waver_width_min)
                p.style = Paint.Style.STROKE
                paintsArray.add(p)
            }
        }
        path2 = Path()
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        ViewWidth = xNew
        ViewHeight = yNew
        ViewMid = ViewWidth.toFloat() / 2.0f
        maxAmplitude = ViewHeight.toFloat() / 2.0f - 4.0f
        Log.v("Waver", "width=" + ViewWidth)
    }

    fun setMaxAmplitude(mAmpli: Float) {
        phase += phaseShift
        amplitude = (amplitude + Math.max(mAmpli / 5590.5337f, 0.01f)) / 2.0f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!drawLock) {
            drawLock = true
            for (i in 0 until numberOfWaves) {
                val normedAmplitude =
                    (1.5f * (1.0f - i.toFloat() / numberOfWaves.toFloat()) - 0.5f) * amplitude
                path2!!.reset()
                var x = 0.0f
                while (x < ViewWidth.toFloat() + density) {
                    val y =
                        maxAmplitude.toDouble() * (-Math.pow(
                            (x / ViewMid - 1.0f).toDouble(),
                            2.0
                        ) + 1.0) * normedAmplitude.toDouble() * Math.sin(6.282 * (x / ViewWidth.toFloat()).toDouble() * frequency.toDouble() + phase.toDouble()) + ViewHeight.toDouble() / 2.0
                    if (x == 0.0f) {
                        path2!!.moveTo(x, y.toFloat())
                    } else {
                        path2!!.lineTo(x, y.toFloat())
                    }
                    x += density
                }
                val p = paintsArray[i]
                canvas.drawPath(path2!!, p)
            }
            drawLock = false
        }
    }

    @SuppressLint("CustomViewStyleable")
    private fun initialize(
        c: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) {
        val a =
            c.obtainStyledAttributes(attrs, R.styleable.SiriWaveView, defStyleAttr, 0)
        waveColor = a.getColor(R.styleable.SiriWaveView_wave_color, -1)
        numberOfWaves = a.getInteger(R.styleable.SiriWaveView_wave_count, 5)
        refreshInterval = a.getInteger(R.styleable.SiriWaveView_waveRefreshInterval, 30)
        a.recycle()
        initShit()
    }

    fun setWaveColor(wCol: Int) {
        waveColor = wCol
    }

    fun setNumberOfWaves(nWaves: Int) {
        numberOfWaves = nWaves
    }
}
