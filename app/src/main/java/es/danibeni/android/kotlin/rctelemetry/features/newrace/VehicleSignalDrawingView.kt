package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class VehicleSignalDrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // setup initial color
    private val paintColor = Color.BLACK
    // defines paint and canvas
    private var drawPaint = Paint()
    private var drawPath = Path()

    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        setupPaint()
    }

    // Setup paint with color and stroke styles
    private fun setupPaint() {
        drawPaint = Paint()
        drawPaint!!.setColor(paintColor)
        drawPaint!!.setAntiAlias(true)
        drawPaint!!.setStrokeWidth(5F)
        drawPaint!!.setStyle(Paint.Style.STROKE)
        drawPaint!!.setStrokeJoin(Paint.Join.ROUND)
        drawPaint!!.setStrokeCap(Paint.Cap.ROUND)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}