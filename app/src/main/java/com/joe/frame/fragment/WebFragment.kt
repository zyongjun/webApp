package com.joe.frame.fragment

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.joe.base.Navigator
import com.joe.frame.BuildConfig
import com.joe.frame.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

//https://github.com/DRPrincess/DR_WebviewDemo/blob/master/app/src/main/java/com/dr_webviewdemo/WebActivity.java
class WebFragment : BasePermissionFragment() {
    private var mWebView: WebView? = null
    private var mUploadMessageAbL: ValueCallback<Array<Uri>>? = null
    private var mUploadMessage: ValueCallback<Uri>? = null

    private var mCameraFilePath: String? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_web
    }

    override fun onBackPressed(): Boolean {
        if (mWebView!!.canGoBack()) {
            mWebView!!.goBack()
            return true
        } else {
            return super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            if (mUploadMessageAbL != null) {
                this.mUploadMessageAbL!!.onReceiveValue(null)
                this.mUploadMessageAbL = null
            }
            if (mUploadMessage != null) {
                this.mUploadMessage!!.onReceiveValue(null)
                this.mUploadMessage = null
            }
            return
        }
        if (requestCode == REQUEST_FILE_CHOOSER && this.mUploadMessageAbL != null) {
            var localUri: Uri? = null

            if (data != null) {
                localUri = data.data
                try {
                    val path = getRealFilePath(activity, localUri)
                    saveImage(path)
                } catch (e: Exception) {

                }

            } else if (!TextUtils.isEmpty(mCameraFilePath)) {
                val cameraFile = File(mCameraFilePath!!)
                localUri = FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".fileprovider", cameraFile)
                saveImage(mCameraFilePath)
                val localIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri)
                activity!!.sendBroadcast(Intent(localIntent))
            }

            if (mUploadMessage != null) {
                this.mUploadMessage!!.onReceiveValue(localUri)
                this.mUploadMessage = null
            }
            if (mUploadMessageAbL != null) {
                this.mUploadMessageAbL!!.onReceiveValue(arrayOf<Uri>(localUri!!))
                this.mUploadMessageAbL = null
            }
        }

    }


    private fun saveImage(uri: String?) {
        if (uri == null)
            return
        try {
            //            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
            val bitmap = decodeSampledBitmapFromPath(uri, 360, 640)
            saveBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun saveBitmap(bitmap: Bitmap) {
        try {
            //            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
            val baos = ByteArrayOutputStream()
            var options = 80
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
            Log.i(TAG, "图片处理开始!" + baos.toByteArray().size / 1024 + "KB")
            while (baos.toByteArray().size / 1024 > 200) {
                baos.reset()
                if (options > 5) {
                    options -= 5
                } else {
                    break
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
            }
            val file = File(mCameraFilePath!!)
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(file)
                out.write(baos.toByteArray())
                Log.e(TAG, "图片处理完成!" + baos.toByteArray().size / 1024 + "KB")
                out.flush()
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                baos.close()
                out?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //        if (Build.VERSION.SDK_INT >= 23) {
        //            int REQUEST_CODE_CONTACT = 101;
        //            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};            //验证是否许可权限
        //            for (String str : permissions) {
        //                if (this.getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
        //                    //申请权限
        //                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
        //                    return;
        //                }
        //            }
        //        }

        val url = arguments!!.getString("url")
        val title = arguments!!.getString("title", "")
        val appBar = mRootView!!.findViewById<View>(R.id.appBar)
        if (TextUtils.isEmpty(title)) {
            appBar.visibility = View.GONE
        } else {
            val tvTitle = mRootView!!.findViewById<TextView>(R.id.tvTitle)
            tvTitle.text = title
            appBar.visibility = View.VISIBLE
            mRootView!!.findViewById<View>(R.id.ivBack).setOnClickListener { onBackPressed() }
        }
        mWebView = mRootView!!.findViewById(R.id.web)
        val webSettings = mWebView!!.settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上版本不允许https和http同时存在
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        mWebView!!.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("superacebannerurl")) {
                    // superacebannerurl: //http://www.superace.com/appweb/ace-token
                    val redirectUrl = url.substring(url.indexOf("http"), url.length)
                    if (!TextUtils.isEmpty(redirectUrl)) {
                        val bundle = Bundle()
                        bundle.putString("url", redirectUrl)
                        bundle.putString("title", "详情")
                        Navigator.startCommon(activity!!, WebFragment::class.java.name, bundle, -1)
                    }
                    return true
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
        mWebView!!.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
                mUploadMessageAbL = filePathCallback
                checkPermissions(arrayOf(Manifest.permission.CAMERA))
                return true
            }

            //For Android  >= 4.1
            fun openFileChooser(valueCallback: ValueCallback<Uri>, acceptType: String, capture: String) {
                mUploadMessage = valueCallback
                checkPermissions(arrayOf(Manifest.permission.CAMERA))
            }
        }
        mWebView!!.loadUrl(url)
    }


    override fun onGranted() {
        super.onGranted()
        startActivityForResult(Intent.createChooser(createDefaultOpenableIntent(), "选择操作"), REQUEST_FILE_CHOOSER)
    }

    private fun createDefaultOpenableIntent(): Intent {
        // Create and return a chooser with the default OPENABLE
        // actions including the camera, camcorder and sound
        // recorder where available.
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "*/*"
        val chooser = createChooserIntent(createCameraIntent(), createCamcorderIntent(),
                createSoundRecorderIntent())
        chooser.putExtra(Intent.EXTRA_INTENT, i)
        return chooser
    }

    private fun createChooserIntent(vararg intents: Intent): Intent {
        val chooser = Intent(Intent.ACTION_CHOOSER)
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents)
        chooser.putExtra(Intent.EXTRA_TITLE, "选择操作")
        return chooser
    }

    private fun createCameraIntent(): Intent {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraDataDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM)
        //        File cameraDataDir = new File(externalDataDir.getAbsolutePath());
        if (!cameraDataDir.exists()) {
            cameraDataDir.mkdirs()
        }
        val cameraFilePath = cameraDataDir.absolutePath + File.separator +
                System.currentTimeMillis() + ".jpg"
        val tempFile = File(cameraFilePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val uri = FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".fileprovider", tempFile)
            cameraIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            cameraIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION//增加读写权限
            //            cameraIntent.setDataAndType(uri, contentType);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        } else {
            val uri = Uri.fromFile(tempFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }


        mCameraFilePath = cameraFilePath
        //        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        return cameraIntent
    }

    private fun createCamcorderIntent(): Intent {
        return Intent(MediaStore.ACTION_VIDEO_CAPTURE)
    }

    private fun createSoundRecorderIntent(): Intent {
        return Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
    }

    companion object {
        private val REQUEST_FILE_CHOOSER = 300

        private val TAG = "FamilyBannerListFragmen"

        // https://blog.csdn.net/gh8609123/article/details/55057494
        private fun decodeSampledBitmapFromPath(path: String, width: Int, height: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)
            options.inSampleSize = caculateInSampleSize(options, width, height)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(path, options)
        }

        private fun caculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val width = options.outWidth
            val height = options.outHeight
            var inSampleSize = 1
            if (width >= reqWidth || height >= reqHeight) {
                val widthRadio = Math.round(width * 1.0f / reqWidth)
                val heightRadio = Math.round(width * 1.0f / reqHeight)
                inSampleSize = Math.max(widthRadio, heightRadio)
            }
            return inSampleSize
        }


        fun getRealFilePath(context: Context?, uri: Uri?): String? {
            if (null == uri) return null
            val scheme = uri.scheme
            var data: String? = null
            if (scheme == null)
                data = uri.path
            else if (ContentResolver.SCHEME_FILE == scheme) {
                data = uri.path
            } else if (ContentResolver.SCHEME_CONTENT == scheme) {
                val cursor = context!!.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                        if (index > -1) {
                            data = cursor.getString(index)
                        }
                    }
                    cursor.close()
                }
            }
            return data
        }
    }

}
