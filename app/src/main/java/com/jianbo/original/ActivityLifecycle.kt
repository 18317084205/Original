package com.jianbo.original

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.jianbo.toolkit.rxjava.RxUtils

/**
 * Created by Jianbo on 2018/5/3.
 */
object ActivityLifecycle : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        RxUtils.removeDisposable(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity?, p1: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, p1: Bundle?) {
        if (activity!!.findViewById<ViewGroup>(R.id.toolbar) != null) {
            activity?.findViewById<TextView>(R.id.toolbar_title).text = activity.title
            activity!!.findViewById<Button>(R.id.toolbar_back).setOnClickListener {
                activity.onBackPressed()
            }
        }
    }
}