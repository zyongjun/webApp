package com.joe.frame.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.joe.base.Navigator;
import com.joe.frame.R;
import org.jetbrains.annotations.NotNull;

public class WebFragment extends BasePermissionFragment{
    private WebView mWebView;
    private static final int REQUEST_FILE_CHOOSER = 300;
    private ValueCallback<Uri[]> mUploadMessage;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            if (requestCode == REQUEST_FILE_CHOOSER && this.mUploadMessage != null) {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.mUploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                } else {
                    Uri result = data != null && resultCode == -1 ? data.getData() : null;
                    this.mUploadMessage.onReceiveValue(new Uri[]{result});
                }
            }
        }else{

        }
    }

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
                if(url.contains("superacebannerurl")){
//                    superacebannerurl: //http://www.superace.com/appweb/ace-token
                    String redirectUrl = url.substring(url.indexOf("http"),url.length());
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
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
               mUploadMessage = filePathCallback;
                checkPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
                return true;
//                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });
        mWebView.loadUrl(url);
    }


    @Override
    protected void onGranted() {
        super.onGranted();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择操作"), REQUEST_FILE_CHOOSER);
    }
}
