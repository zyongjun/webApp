package com.joe.frame.widget

import android.content.Context
import android.support.animation.SpringAnimation
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.ScrollView
import android.support.v4.view.VelocityTrackerCompat.getXVelocity
import android.view.View.TRANSLATION_X
import android.support.v4.view.ViewCompat.getTranslationX



class SpringScrollView:ScrollView{
    var velocityTracker: VelocityTracker
    var downY = 0f

    constructor(context: Context) : super(context){

    }

    constructor(context:Context,attributeSet: AttributeSet):super(context,attributeSet){

    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle){

    }

    init {
        velocityTracker = VelocityTracker.obtain()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN ->{
                downY = ev.getY()
                velocityTracker.addMovement(ev)
                return true
            }
            MotionEvent.ACTION_MOVE->{
                translationY = ev.y-downY
                velocityTracker.addMovement(ev)
                return true
            }
            MotionEvent.ACTION_UP->{
                velocityTracker.computeCurrentVelocity(1000)
                if (translationY !== 0f) {
                    val animX = SpringAnimation(this, SpringAnimation.TRANSLATION_Y, 0f)
                    animX.getSpring().setStiffness(1500f)
                    animX.getSpring().setDampingRatio(0.5f)
                    animX.setStartVelocity(velocityTracker.xVelocity)
                    animX.start()
                }
                velocityTracker.clear()
                return true
            }
        }
        return super.onTouchEvent(ev)
    }
}