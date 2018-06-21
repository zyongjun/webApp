package com.joe.frame.fragment

import android.os.Bundle
import android.view.View
import com.joe.base.BaseFragment
import com.joe.base.extension.loge
import com.joe.frame.R

class TestFragment: BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.content_main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loge("=================onViewCreated")
    }
}