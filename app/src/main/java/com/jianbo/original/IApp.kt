package com.jianbo.original

import android.app.Application
import com.jianbo.original.http.HttpRequest
import com.jianbo.toolkit.http.HttpUtils

/**
 * Created by Jianbo on 2018/4/3.
 */
class IApp : Application() {

    override fun onCreate() {
        super.onCreate()
        HttpUtils.init(this,HttpRequest())
        registerActivityLifecycleCallbacks(ActivityLifecycle)
    }

}