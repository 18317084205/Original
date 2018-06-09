package com.jianbo.original.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jianbo.toolkit.prompt.WindowUtils
import com.jianbo.toolkit.prompt.rxjava.RxUtils
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowUtils.hideBottomUIMenu(this)
        super.onCreate(savedInstanceState)

        disposable = Flowable.timer(2000, TimeUnit.MILLISECONDS)
                .compose(RxUtils.flyableTransformer<Long>())
                .subscribe {
                    startActivity(Intent(this, GuideActivity::class.java))
                    finish()
                }

        RxUtils.addDisposable(this, disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        window.setBackgroundDrawable(null)
    }
}
