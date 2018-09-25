package com.joe.frame.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.joe.base.BaseFragment;
import com.joe.base.Navigator;
import com.joe.frame.R;

import org.jetbrains.annotations.NotNull;

public class WebFragment extends BaseFragment{
    private WebView mWebView;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_web;
    }

    @Override
    public boolean onBackPressed(@NotNull Fragment $receiver) {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return super.onBackPressed($receiver);
        }
    }

    private static final String TAG = "WebFragment";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String url = getArguments().getString("url");
        String title = getArguments().getString("title","");
        View appBar = getMRootView().findViewById(R.id.appBar);
        if(TextUtils.isEmpty(title)){
            appBar.setVisibility(View.GONE);
        }else{
            TextView tvTitle = getMRootView().findViewById(R.id.tvTitle);
            tvTitle.setText(title);
            appBar.setVisibility(View.VISIBLE);
            getMRootView().findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed(WebFragment.this);
                }
            });
        }
        mWebView = getMRootView().findViewById(R.id.web);
        WebSettings webSettings = mWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上版本不允许https和http同时存在
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(TAG, "shouldOverrideUrlLoading: ========="+url );
                if(url.contains("superacebannerurl")){
                    Log.e(TAG, "shouldOverrideUrlLoading: "+url);
//                    superacebannerurl: //http://www.superace.com/appweb/ace-token
                    String redirectUrl = url.substring(url.indexOf("http"),url.length());
                    Log.e(TAG, "shouldOverrideUrlLoading: ======================ssss=="+redirectUrl );
                    if(!TextUtils.isEmpty(redirectUrl)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("url", redirectUrl);
                        bundle.putString("title","详情");
                        Navigator.INSTANCE.startCommon(getActivity(), WebFragment.class.getName(), bundle, -1);
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        mWebView.loadUrl(url);
    }
}
