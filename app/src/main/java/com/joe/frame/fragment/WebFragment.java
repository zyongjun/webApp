package com.joe.frame.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;

public class WebFragment extends BasePermissionFragment {
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
        if (resultCode == Activity.RESULT_CANCELED) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.mUploadMessage.onReceiveValue(null);
                this.mUploadMessage = null;
            } else {
                this.mUploadMessage.onReceiveValue(null);
                this.mUploadMessage = null;
            }
            return;
        }
        if (requestCode == REQUEST_FILE_CHOOSER && this.mUploadMessage != null) {
            Uri result = data == null? null : data.getData();
            if (result == null && data == null && resultCode == Activity.RESULT_OK) {
                File cameraFile = new File(mCameraFilePath);
                if (cameraFile.exists()) {
                    result = Uri.fromFile(cameraFile);
                    // Broadcast to the media scanner that we have a new photo
                    // so it will be added into the gallery for the user.
                    getActivity().sendBroadcast(
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
                }
            }

            if (Build.VERSION.SDK_INT >= 21) {
                this.mUploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                this.mUploadMessage = null;
            } else {
//                Uri result = data != null && resultCode == -1 ? data.getData() : null;
                this.mUploadMessage.onReceiveValue(new Uri[]{result});
                this.mUploadMessage = null;
            }
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String url = getArguments().getString("url");
        String title = getArguments().getString("title", "");
        View appBar = getMRootView().findViewById(R.id.appBar);
        if (TextUtils.isEmpty(title)) {
            appBar.setVisibility(View.GONE);
        } else {
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

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("superacebannerurl")) {
//                    superacebannerurl: //http://www.superace.com/appweb/ace-token
                    String redirectUrl = url.substring(url.indexOf("http"), url.length());
                    if (!TextUtils.isEmpty(redirectUrl)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("url", redirectUrl);
                        bundle.putString("title", "详情");
                        Navigator.INSTANCE.startCommon(getActivity(), WebFragment.class.getName(), bundle, -1);
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadMessage = filePathCallback;
                checkPermissions(new String[]{Manifest.permission.CAMERA});
                return true;
//                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });
        mWebView.loadUrl(url);
    }


    @Override
    protected void onGranted() {
        super.onGranted();
        startActivityForResult(Intent.createChooser(createDefaultOpenableIntent(), "选择操作"), REQUEST_FILE_CHOOSER);
    }

    private Intent createDefaultOpenableIntent() {
        // Create and return a chooser with the default OPENABLE
        // actions including the camera, camcorder and sound
        // recorder where available.
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        Intent chooser = createChooserIntent(createCameraIntent(), createCamcorderIntent(),
                createSoundRecorderIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, i);
        return chooser;
    }

    private Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE, "选择操作");
        return chooser;
    }

    private String mCameraFilePath;
    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File externalDataDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
                File.separator + "browser-photos");
        cameraDataDir.mkdirs();
        String cameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                System.currentTimeMillis() + ".jpg";
        mCameraFilePath = cameraFilePath;
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cameraFilePath)));
        return cameraIntent;
    }

    private Intent createCamcorderIntent() {
        return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }

    private Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    }

}
