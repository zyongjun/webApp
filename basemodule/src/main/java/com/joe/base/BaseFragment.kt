package com.joe.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

open  class BaseFragment:Fragment(){
    open var mRootView:View? = null
    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(),container,false)
        }else{
            val parent = mRootView!!.parent as ViewGroup?;
            parent?.removeAllViews()
        }
        return mRootView
    }

    open fun Fragment.onBackPressed():Boolean{
        activity?.finish()
        return true
    }

    open fun getLayoutId():Int{
        return -1
    }

}

fun Fragment.onBackPressed():Boolean{
    activity?.finish()
    return true
}