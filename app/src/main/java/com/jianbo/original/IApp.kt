package com.jianbo.original

import android.app.Application
import com.jianbo.original.http.AppRequest
import com.liang.jhttp.JHttp

/**
 * Created by Jianbo on 2018/4/3.
 */
class IApp : Application() {

    override fun onCreate() {
        super.onCreate()
        JHttp.init(AppRequest())
        registerActivityLifecycleCallbacks(ActivityLifecycle)
    }

}