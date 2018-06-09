package com.jianbo.original.base

import com.jianbo.toolkit.base.BasePresenter
import com.jianbo.toolkit.base.BaseView
import com.jianbo.toolkit.base.JBaseActivity

abstract class BaseActivity : JBaseActivity<BasePresenter<*, *>?>(), BaseView {
    override fun onPermissionGranted(permissions: Array<String>) {

    }

    override fun showDialog(msg: String) {

    }

    override fun dismissDialog() {

    }

    override fun error(msg: String) {

    }


}