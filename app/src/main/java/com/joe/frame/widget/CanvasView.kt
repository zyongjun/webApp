package com.joe.frame.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.joe.base.extension.loge
import com.joe.frame.R

class CanvasView : View{
    constructor(context: Context) : super(context){

    }

    constructor(context: Context, attributes: AttributeSet) : super(context,attributes) {

    }

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(context, attributes, defStyleAttr){

    }
    val text = "THIS IS A good text demo"
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        ---0--168---1080---1848: draw   1080x1920
        loge("draw","---${left}--${top}---${right}---${bottom}")
        val paint = Paint()
//        paint.strokeWidth=30f
        paint.style = Paint.Style.STROKE
        paint.strokeCap=Paint.Cap.BUTT
        paint.textSize=resources.getDimension(R.dimen.s_14)
        paint.strokeJoin = Paint.Join.BEVEL
        paint.textAlign = Paint.Align.CENTER
        paint.color = resources.getColor(R.color.colorPrimaryDark)
        val textWidth = paint.measureText(text)
        val startX = (width )/2f

        canvas.drawLine(startX,0f,startX,500f,paint)
        val fontMetrics = paint.fontMetrics
        val y = height/2f+(Math.abs(fontMetrics.ascent)-fontMetrics.descent)/2f
        canvas.drawText(text,(width)/2f,y,paint)
    }

}

