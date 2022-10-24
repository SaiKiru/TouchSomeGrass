package com.grassyass.touchsomegrass.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.utils.pxToDp
import kotlin.math.roundToInt

/**
 * TODO: document your custom view class.
 */
class BarGraph : View {

    private var _barColor: Int = Color.GRAY
    private var _textColor: Int = Color.GRAY
    private var _boundsColor: Int = Color.GRAY
    private var _guideLineColor: Int = Color.GRAY
    private var _values: ArrayList<Float> = arrayListOf(0F)
    private var _labels: ArrayList<String> = arrayListOf("")

    private lateinit var barPaint: Paint
    private lateinit var textPaint: TextPaint
    private lateinit var boundsPaint: Paint
    private lateinit var guideLinePaint: Paint

    var barColor: Int
        get() = _barColor
        set(value) { _barColor = value }

    var textColor: Int
        get() = _textColor
        set(value) { _textColor = value }

    var boundsColor: Int
        get() = _boundsColor
        set(value) { _boundsColor = value }

    var guideLineColor: Int
        get() = _guideLineColor
        set(value) { _guideLineColor = value}

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.BarGraph, defStyle, 0
        )

        _barColor = a.getColor(
            R.styleable.BarGraph_barColor,
            barColor
        )

        _textColor = a.getColor(
            R.styleable.BarGraph_textColor,
            textColor
        )

        _boundsColor = a.getColor(
            R.styleable.BarGraph_boundsColor,
            boundsColor
        )

        _guideLineColor = a.getColor(
            R.styleable.BarGraph_guideLineColor,
            guideLineColor
        )

        a.recycle()

        barPaint = Paint().apply {
            color = barColor
            style = Paint.Style.FILL
        }

        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = textColor
            textAlign = Paint.Align.CENTER
            textSize = pxToDp(context, 16F)
            typeface = Typeface.DEFAULT_BOLD
        }

        boundsPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = boundsColor
            strokeWidth = 2F
            style = Paint.Style.STROKE
        }

        guideLinePaint = Paint().apply {
            color = guideLineColor
            strokeWidth = 2F
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val max = _values.max()
        val skyline = contentHeight * 0.8
        val containerWidth = 1.0 * contentWidth / _values.size
        val halfWidth = containerWidth / 4.0

        canvas.drawLine(
            0F,
            (contentHeight - (0.70 * skyline)).toFloat(),
            contentWidth.toFloat(),
            (contentHeight - (0.70 * skyline)).toFloat(),
            guideLinePaint
        )

        canvas.drawLine(
            0F,
            (contentHeight - (0.30 * skyline)).toFloat(),
            contentWidth.toFloat(),
            (contentHeight - (0.30 * skyline)).toFloat(),
            guideLinePaint
        )

        canvas.drawText(
            "${(max * 0.70).roundToInt()}",
            contentWidth + pxToDp(context, 16F),
            (contentHeight - (0.70 * skyline) - (textPaint.descent() + textPaint.ascent()) / 2).toFloat(),
            textPaint
        )

        canvas.drawText(
            "${(max * 0.30).roundToInt()}",
            contentWidth + pxToDp(context, 16F),
            (contentHeight - (0.30 * skyline) - (textPaint.descent() + textPaint.ascent()) / 2).toFloat(),
            textPaint
        )

        for (i in 0 until _values.size) {
            val seqHelper = 2 * i + 1
            val anchor = (containerWidth / 2.0) * seqHelper

            val barLeft = anchor - halfWidth
            val barRight = anchor + halfWidth
            val barTop = contentHeight - ((_values[i] / max) * skyline)

            canvas.drawRect(
                barLeft.toFloat(),
                barTop.toFloat(),
                barRight.toFloat(),
                contentHeight.toFloat(),
                barPaint
            )

            canvas.drawText(
                _labels[i],
                anchor.toFloat(),
                contentHeight + pxToDp(context, 16F + 4F),
                textPaint
            )
        }

        canvas.drawRoundRect(
            1F,
            1F,
            contentWidth.toFloat() - 1F,
            contentHeight.toFloat(),
            16F,
            16F,
            boundsPaint
        )
    }

    fun setData(values: ArrayList<Any>, labels: ArrayList<String>) {
        _values = values as ArrayList<Float>
        _labels = labels

        invalidate()
    }
}