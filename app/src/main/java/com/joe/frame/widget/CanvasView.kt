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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        ---0--168---1080---1848: draw   1080x1920
        loge("draw","---${left}--${top}---${right}---${bottom}")
        val paint:Paint = Paint()
        paint.strokeWidth=200f
        paint.style = Paint.Style.STROKE
        paint.color = resources.getColor(R.color.colorPrimaryDark)

        val paint2:Paint = Paint()
//        paint2.strokeWidth=100f
//        paint.style = Paint.Style.FILL_AND_STROKE
        paint2.color = resources.getColor(android.R.color.holo_red_light)
        val rect = RectF(0f,0f,right.toFloat(),bottom.toFloat())
//        canvas.drawArc(rect,-45f,90f,true,paint)
//        canvas.drawCircle(left+400f,top+300f,100f,paint)
//        canvas.drawCircle(left+400f,top+300f,100f,paint2)
        val matrix = Matrix()
        matrix.postRotate(45f)
        matrix.preTranslate(600f,300f)
        canvas.drawBitmap(BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher),matrix,paint)
       /* val bitmap = BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher)
        val rectSrc = Rect(100,0,200,200)
        val rectDst = RectF(100f,100f,300f,600f)
        canvas.drawBitmap(bitmap,rectSrc,rectDst,paint2)*/
    }

}

