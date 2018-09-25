package com.joe.frame.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.joe.base.BaseFragment;
import com.joe.base.Navigator;
import com.joe.frame.BuildConfig;
import com.joe.frame.R;

public class SplashFragment extends BaseFragment{
    private Handler mHandler = new Handler();

    private void handleNormalSplashAction() {
        mHandler.postDelayed(mNormalSplashRunnable, 3000);
    }

    private Runnable mNormalSplashRunnable = new Runnable() {
        @Override
        public void run() {
            navigateToLoginPage();

        }
    };

    private void navigateToLoginPage() {
        Bundle bundle = new Bundle();
        bundle.putString("url","http://www.superace.net");
        Navigator.INSTANCE.startCommon(getActivity(),WebFragment.class.getName(),bundle,-1);
        getActivity().finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_splash;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleNormalSplashAction();
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mNormalSplashRunnable);
        }
//        ApplicationDelegate.unbindDrawables(mBaseRootView);
        super.onDestroy();
        Runtime.getRuntime().gc();
    }


}
