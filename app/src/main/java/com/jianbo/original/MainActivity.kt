package com.jianbo.original

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jianbo.original.R.id.*
import com.jianbo.original.base.BaseActivity
import com.jianbo.toolkit.base.BasePresenter
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

class MainActivity : BaseActivity() {

    private external fun stringFromJNI(): String
    val TAG = MainActivity::class.java.simpleName
    private var isOpenedSettings: Boolean = false
    private val parameters = HashMap<String, Any>()

    private val url1 = "https://img01.sogoucdn.com/app/a/100520076/61501ee65a9af10c8980e634404dcb54"
    private val url = "https://dl-sh-ctc-2.pchome.net/04/p8/kwmusic_20180408.zip?key=320f1754144dcf2a2bda15911560de0d&tmp=1523437795810"

    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE)

    override fun getPresenter(): BasePresenter<*, *>? {
        return null
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initViewOrData(savedInstanceState: Bundle?) {
        sample_text.text = stringFromJNI()

        seekBar.max = 10000;

        checkPermissions(*permissions)

        button.setOnClickListener {
            //https://www.baidu.com/s?ie=utf-8&f=3&rsv_bp=1&tn=baidu&wd=%E5%8F%8C%E8%89%B2%E7%90%83
            LogUtils.e("OnClick")

            val parameters = HashMap<String, String>()
            parameters.put("wd", "java")

            HttpRequest.getInstance().download(url, object : FileCallback() {
                override fun onSuccess(code: Int, result: String?, msg: String?) {
                    if (result == null) return
                    LogUtils.e("55555.abc", result)
                }

                override fun onFailure(code: Int, msg: String?) {
                    LogUtils.e("onFailure", msg)
                }

                override fun onProgress(progress: Float, downloaded: Long, total: Long) {
                    sample_text.text = (progress * 100).toInt().toString() + "%"
                    seekBar.progress = (progress * 10000).toInt()
                }

            })
        }
        button2.setOnClickListener {
            HttpRequest.getInstance().bitmap(url1, object : BitmapCallback() {
                override fun onSuccess(code: Int, result: Bitmap?, msg: String?) {
                    if (result == null) return
                    imageView.setImageBitmap(result)
                }
            })
        }

        button3.setOnClickListener{
            HttpRequest.getInstance().cancel(url)
            HttpRequest.getInstance().cancel(url1)
        }
    }

    override fun <D : Any?> showDataFromPresenter(data: D) {

    }

    override fun onResume() {
        super.onResume()
        if (isOpenedSettings) {
            checkPermissions(*permissions)
        }
    }

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HttpRequest.getInstance().cancelAll()
    }

}



