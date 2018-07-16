package com.joe.frame.fragment

import android.os.Bundle
import android.view.VelocityTracker
import android.view.View
import com.joe.base.BaseFragment
import com.joe.base.widget.appBarBuilder
import com.joe.frame.R

class SpringScrollFragment:BaseFragment(){
    override fun getLayoutId(): Int {
        return R.layout.fragment_spring
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarBuilder().showLeft(true)
                .withTitle("阻尼滚动")
                .show()
    }
}