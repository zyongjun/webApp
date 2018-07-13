package com.joe.frame.fragment

import android.os.Bundle
import android.view.View
import com.joe.base.BaseFragment
import com.joe.base.Navigator
import com.joe.base.widget.appBarBuilder
import com.joe.frame.R
import kotlinx.android.synthetic.main.content_main.*

class MainFragment: BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.content_main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarBuilder()
                .showLeft(true)
                .withTitle("主页")
                .show()
        tvTestStageAndroid.setOnClickListener {
            Navigator.startCommon(it.context,TestStageAndroidFragment::class.java.name,Bundle())
        }
        tvCanvas.setOnClickListener {
            Navigator.startCommon(it.context,CanvasFragment::class.java.name,Bundle())
        }

    }
}