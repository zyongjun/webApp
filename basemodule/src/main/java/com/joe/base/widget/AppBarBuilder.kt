package com.joe.base.widget

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.joe.base.BaseFragment
import com.joe.base.R

open class AppBarBuilder(val root:ViewGroup){
    protected var showLeft:Boolean
    protected var title:String = ""
    protected var textRight:String = ""
    protected var imageResId:Int = 0
    init {
        showLeft = true
    }

    fun showLeft(isVisible:Boolean):AppBarBuilder{
        showLeft = isVisible
        return this
    }

    fun withTitle(title:String):AppBarBuilder{
        this.title = title
        return this
    }

    fun showRightText(text:String):AppBarBuilder{
        textRight = text
        return this;
    }

    fun showRightImage(resId:Int):AppBarBuilder{
        imageResId = resId
        return this
    }

    fun show() {
        val ivBack = root.findViewById<ImageView>(R.id.ivBack)
        val tvTitle = root.findViewById<TextView>(R.id.tvTitle)
        val tvRight = root.findViewById<TextView>(R.id.tvRight)
        val ivRight = root.findViewById<ImageView>(R.id.ivRight)
        ivBack.visibility = if(showLeft) View.VISIBLE else View.INVISIBLE
        tvTitle.text = title
        tvRight.text = textRight
        if(imageResId != 0) {
            ivRight.visibility = View.VISIBLE
            ivRight.setImageResource(imageResId)
        }else{
            ivRight.visibility = View.GONE
        }
    }
}

fun BaseFragment.appBarBuilder():AppBarBuilder{
    return AppBarBuilder(mRootView as ViewGroup)
}