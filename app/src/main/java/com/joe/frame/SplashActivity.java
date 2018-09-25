package com.joe.frame;

import android.os.Bundle;

import com.joe.base.BaseActivity;
import com.joe.frame.fragment.SplashFragment;

import org.jetbrains.annotations.Nullable;

public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        replaceFragment(new SplashFragment());
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
