package com.jianbo.original

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import com.jianbo.original.base.BaseActivity
import com.jianbo.toolkit.base.BasePresenter
import com.jianbo.toolkit.prompt.LogUtils
import com.liang.jhttp.JHttp
import com.liang.jhttp.callback.BitmapCallback
import com.liang.jhttp.callback.FileCallback
import kotlinx.android.synthetic.main.activity_main.*
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

            JHttp.getInstance().download(url, object : FileCallback() {
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
            JHttp.getInstance().bitmap(url1, object : BitmapCallback() {
                override fun onSuccess(code: Int, result: Bitmap?, msg: String?) {
                    if (result == null) return
                    imageView.setImageBitmap(result)
                }
            })
        }

        button3.setOnClickListener{
            JHttp.getInstance().cancel(url)
            JHttp.getInstance().cancel(url1)
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
        JHttp.getInstance().cancelAll()
    }

}



