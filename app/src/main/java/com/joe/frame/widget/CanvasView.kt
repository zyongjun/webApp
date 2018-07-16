package com.joe.frame.widget

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.joe.base.extension.loge
import com.joe.frame.R

class CanvasView : View{
    lateinit var bitmap:Bitmap
    constructor(context: Context) : super(context){

    }

    constructor(context: Context, attributes: AttributeSet) : super(context,attributes) {

    }

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(context, attributes, defStyleAttr){

    }

    init {
        bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_round)
    }

    val text = "THIS IS A good text demo"
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
            //View从API Level 11才加入setLayerType方法
            //关闭硬件加速
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        ---0--168---1080---1848: draw   1080x1920
        loge("draw","---${left}--${top}---${right}---${bottom}")
        canvas.drawARGB(255, 255, 156, 161);
        val sc = canvas.saveLayer(0f,0f,canvas.width.toFloat(),canvas.height.toFloat(),null,Canvas.ALL_SAVE_FLAG)
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.RED
        canvas.drawRect(60f,20f,200f,200f,paint)
        val paint2 = Paint()
        paint2.style = Paint.Style.FILL
        paint2.color = resources.getColor(R.color.colorPrimaryDark)
        paint2.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
        canvas.drawCircle(260f,150f,130f,paint2)
        canvas.restoreToCount(sc)
    }

}

