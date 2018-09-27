package com.joe.frame.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.joe.base.Navigator;
import com.joe.frame.BuildConfig;
import com.joe.frame.R;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//https://github.com/DRPrincess/DR_WebviewDemo/blob/master/app/src/main/java/com/dr_webviewdemo/WebActivity.java
public class WebFragment extends BasePermissionFragment {
    private WebView mWebView;
    private static final int REQUEST_FILE_CHOOSER = 300;
    private ValueCallback<Uri[]> mUploadMessageAbL;
    private ValueCallback<Uri> mUploadMessage;

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
            if (mUploadMessageAbL != null) {
                this.mUploadMessageAbL.onReceiveValue(null);
                this.mUploadMessageAbL = null;
            }
            if(mUploadMessage != null){
                this.mUploadMessage.onReceiveValue(null);
                this.mUploadMessage = null;
            }
            return;
        }
        if (requestCode == REQUEST_FILE_CHOOSER && this.mUploadMessageAbL != null) {
            Uri localUri = null;
            if (!TextUtils.isEmpty(mCameraFilePath)) {
                File cameraFile = new File(mCameraFilePath);
                localUri = FileProvider.getUriForFile(getActivity(),BuildConfig.APPLICATION_ID+".fileprovider",cameraFile);
                saveImage(mCameraFilePath);
                Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                getActivity().sendBroadcast(new Intent(localIntent));
            }

            if (mUploadMessage != null) {
                this.mUploadMessage.onReceiveValue(localUri);
                this.mUploadMessage = null;
            }
            if(mUploadMessageAbL != null){
                this.mUploadMessageAbL.onReceiveValue(new Uri[]{localUri});
                this.mUploadMessageAbL = null;
            }
        }

    }


    private void saveImage(String uri) {
        if (uri == null)
            return;
        try {
//            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
            Bitmap bitmap = decodeSampledBitmapFromPath(uri, 360, 640);
            saveBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = "FamilyBannerListFragmen";
    private void saveBitmap(Bitmap bitmap) {
        try {
//            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
//            Bitmap bitmap = decodeSampledBitmapFromPath(uri.getPath(), 720, 1280);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            Log.i(TAG, "图片处理开始!" + baos.toByteArray().length / 1024 + "KB");
            while (baos.toByteArray().length / 1024 > 200) {
                baos.reset();
                if(options >5) {
                    options -= 5;
                }else{
                    break;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            File file = new File(mCameraFilePath);
            FileOutputStream out= null;
            try {
                out = new FileOutputStream(file);
                out.write(baos.toByteArray());
                Log.e(TAG, "图片处理完成!" + baos.toByteArray().length / 1024 + "KB");
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                baos.close();
                if (out != null) {
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // https://blog.csdn.net/gh8609123/article/details/55057494
    private static Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = caculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width >= reqWidth || height >= reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(width * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }


    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};            //验证是否许可权限
            for (String str : permissions) {
                if (this.getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

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
                mUploadMessageAbL = filePathCallback;
                checkPermissions(new String[]{Manifest.permission.CAMERA});
                return true;
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                mUploadMessage = valueCallback;
                checkPermissions(new String[]{Manifest.permission.CAMERA});
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
        File cameraDataDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
//        File cameraDataDir = new File(externalDataDir.getAbsolutePath());
        if(!cameraDataDir.exists()) {
            cameraDataDir.mkdirs();
        }
        String cameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                System.currentTimeMillis() + ".jpg";
        File tempFile = new File(cameraFilePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", tempFile);
            cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//增加读写权限
//            cameraIntent.setDataAndType(uri, contentType);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            Uri uri = Uri.fromFile(tempFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }


        mCameraFilePath = cameraFilePath;
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        return cameraIntent;
    }

    private Intent createCamcorderIntent() {
        return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }

    private Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    }

}
