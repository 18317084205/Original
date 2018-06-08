package com.jianbo.original

import android.app.Application
import com.jianbo.original.http.AppRequest
import com.jianbo.toolkit.http.HttpRequest

/**
 * Created by Jianbo on 2018/4/3.
 */
class IApp : Application() {

    override fun onCreate() {
        super.onCreate()
        HttpRequest.init(AppRequest())
        registerActivityLifecycleCallbacks(ActivityLifecycle)
    }

}