package com.joe.frame.fragment

import android.os.Bundle
import android.view.View
import com.joe.base.BaseFragment
import com.joe.base.widget.appBarBuilder
import com.joe.frame.R

class CanvasFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_canvas
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarBuilder().showLeft(true)
                .withTitle("画布")
                .show()
    }
}