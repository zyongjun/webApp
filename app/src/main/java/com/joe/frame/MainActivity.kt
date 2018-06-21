package com.joe.frame


import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import com.joe.base.BaseActivity
import com.joe.base.extension.loge
import com.joe.frame.fragment.TestFragment

class MainActivity : BaseActivity() {
    var mainFragment:Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        loge("-----mainActivity------start")
        if(mainFragment == null) {
            mainFragment = TestFragment()
            replaceFragment(mainFragment!!)
        }
    }
}
