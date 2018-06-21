package com.joe.base.extension

import android.util.Log

fun Any.loge(msg: String?,tag:String = javaClass.simpleName) {
    Log.e(tag,msg?:"------------------")
}