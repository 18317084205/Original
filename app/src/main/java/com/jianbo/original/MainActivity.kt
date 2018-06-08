package com.jianbo.original

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jianbo.original.R.id.*
import com.jianbo.toolkit.http.HttpRequest
import com.jianbo.toolkit.http.callback.BitmapCallback
import com.jianbo.toolkit.http.callback.FileCallback
import com.jianbo.toolkit.http.callback.HttpCallback
import com.jianbo.toolkit.permission.PermissionUtils
import com.jianbo.toolkit.permission.PermissionsListener
import com.jianbo.toolkit.prompt.AppUtils
import com.jianbo.toolkit.prompt.DialogUtils
import com.jianbo.toolkit.prompt.LogUtils
import com.jianbo.toolkit.prompt.ToastUtils
import com.jianbo.toolkit.widget.DialogBuilder
import com.tamic.novate.Novate
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), PermissionsListener {
    val TAG = MainActivity::class.java.simpleName
    private var isOpenedSettings: Boolean = false
    private lateinit var novate: Novate
    private val parameters = HashMap<String, Any>()

    private val url1 = "https://img01.sogoucdn.com/app/a/100520076/61501ee65a9af10c8980e634404dcb54"
    private val url = "https://dl-sh-ctc-2.pchome.net/04/p8/kwmusic_20180408.zip?key=320f1754144dcf2a2bda15911560de0d&tmp=1523437795810"

    private var permissions = arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        sample_text.text = stringFromJNI()

        seekBar.max = 10000;

        PermissionUtils.checkPermissions(this, permissions, this)


        button.setOnClickListener {
            //https://www.baidu.com/s?ie=utf-8&f=3&rsv_bp=1&tn=baidu&wd=%E5%8F%8C%E8%89%B2%E7%90%83
            LogUtils.d("OnClick")

            val parameters = HashMap<String, String>()
            parameters.put("wd", "java")

//            HttpUtils.getInstance().get("btn1", "s", parameters, object : HttpCallback<String>() {
//                override fun onSuccess(code: Int, result: String?, msg: String?) {
//
//                }
//
//                override fun onFailure(e: kotlin.Throwable?) {
//                }
//
//
//            })


            HttpRequest.getInstance().download(url, object : FileCallback() {
                override fun onSuccess(code: Int, result: String?, msg: String?) {
                    if (result == null) return
                    LogUtils.e("55555.abc", result)
                }

                override fun onFailure(code: Int, msg: String?) {

                }

                override fun onProgress(progress: Float, downloaded: Long, total: Long) {
                    sample_text.text = (progress * 100).toInt().toString() + "%"
                    seekBar.progress = (progress * 10000).toInt()
                }

            })


//            HttpUtils.getInstance().tag("btn1").get(url, Environment.getExternalStorageDirectory().toString(), "55555.abc")
//                    .execute(object : HttpCallback<File>() {
//                        override fun onFailure(e: kotlin.Throwable?) {
//                        }
//
//                        override fun onSuccess(result: File?) {
//                            LogUtils.d("55555.abc", result?.absolutePath)
//                        }
//
//                        override fun onProgress(progress: Float, downloaded: Long, total: Long) {
//                            sample_text.text = (progress * 100).toInt().toString() + "%"
//                            seekBar.progress = (progress * 10000).toInt()
//                        }
//                    })
        }

        button2.setOnClickListener {

            HttpRequest.getInstance().bitmap( url1, object : BitmapCallback() {
                override fun onSuccess(code: Int, result: Bitmap?, msg: String?) {
                    imageView.setImageBitmap(result)
                }

                override fun onFailure(code: Int, msg: String?) {
                    ToastUtils.showToast(this@MainActivity,code.toString() + msg)
                }
            })
        }

        button3.setOnClickListener {
            HttpRequest.getInstance().cancel(url)
            HttpRequest.getInstance().cancel(url1)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isOpenedSettings) {
            PermissionUtils.checkPermissions(this, permissions, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onPermissionBanned(permissions: Array<out String>?) {
        if (!permissions!!.isEmpty()) {
            DialogUtils.showDialog(this, "系统提示", "未获取相应的权限，无法使用该应用，请前往设置中心区管理权限",
                    "设置", "退出", object : DialogBuilder.DialogListener {
                override fun sure() {
                    isOpenedSettings = true
                    AppUtils.openAppSettings(this@MainActivity)
                }

                override fun cancel() {
                    finish()
                }
            })
        }
    }

    override fun onPermissionUntreated(permissions: Array<out String>?) {
        LogUtils.d("onPermission_untreated", "permissions:${permissions.toString()}")
        if (permissions!!.isNotEmpty()) {
            DialogUtils.showDialog(this, "系统提示", "该应用需要相应的权限，才能正常使用",
                    "确定", "退出", object : DialogBuilder.DialogListener {
                override fun sure() {
                    PermissionUtils.requestPermissions(this@MainActivity, permissions)
                }

                override fun cancel() {
                    finish()
                }
            });

        }
    }

    override fun onPermissionGranted(permissions: Array<out String>?) {
        LogUtils.d("onPermission_granted", "permissions:$permissions")
    }

    override fun onPermissionDenied(permissions: Array<out String>?) {
        LogUtils.d("onPermission_denied", "permissions:$permissions")
        isOpenedSettings = false
    }

    override fun onDestroy() {
        super.onDestroy()
        DialogUtils.dismiss()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }


}
