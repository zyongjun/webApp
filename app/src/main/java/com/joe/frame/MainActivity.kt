package com.joe.frame

import android.os.Bundle
import android.support.v4.app.Fragment
import com.joe.base.BaseActivity
import com.joe.frame.fragment.MainFragment

class MainActivity : BaseActivity() {
    var mainFragment:Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        if(mainFragment == null) {
            mainFragment = MainFragment()
            replaceFragment(mainFragment!!)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_fragment_container
    }
}
