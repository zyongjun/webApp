package com.joe.frame.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.joe.base.BaseFragment
import com.joe.base.widget.appBarBuilder
import com.joe.frame.R
import kotlinx.android.synthetic.main.fragment_animator.*

class AnimatorFragment:BaseFragment(){
    override fun getLayoutId(): Int {
        return R.layout.fragment_animator
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarBuilder().showLeft(true)
                .withTitle("动画")
                .show()

    }

    fun showAnimator() {
//        PropertyValuesHolder.of
//        AnimatorSet.Builder
//        val objectAnimator = ObjectAnimator.ofFloat(tvAnimator!,"translationX",0.500f)
//        val valuAnimator = ValueAnimator.ofFloat(0)
        AnimationUtils.currentAnimationTimeMillis()
    }
}