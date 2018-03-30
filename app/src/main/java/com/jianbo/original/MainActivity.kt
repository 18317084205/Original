package com.jianbo.original

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jianbo.toolkit.permission.PermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import com.jianbo.toolkit.permission.PermissionUtil
import com.jianbo.toolkit.prompt.APPUtils
import com.jianbo.toolkit.prompt.DialogUtils
import com.jianbo.toolkit.prompt.LogUtils
import com.jianbo.toolkit.widget.DialogBuilder


class MainActivity : AppCompatActivity(), PermissionsListener {

    private var isOpenedSettings: Boolean = false

    private var permissions = arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        sample_text.text = stringFromJNI()

        PermissionUtil.checkPermissions(this, permissions, this)
    }

    override fun onResume() {
        super.onResume()
        if (isOpenedSettings){
            PermissionUtil.checkPermissions(this, permissions, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onPermissionBanned(permissions: Array<out String>?) {
        if (!permissions!!.isEmpty()) {
            DialogUtils.showDialog(this, "系统提示", "未获取相应的权限，无法使用该应用，请前往设置中心区管理权限",
                    "设置", "退出", object : DialogBuilder.DialogListener {
                override fun sure() {
                    isOpenedSettings = true
                    APPUtils.openAppSettings(this@MainActivity)
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
                    PermissionUtil.requestPermissions(this@MainActivity, permissions)
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
