package com.jianbo.original.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.jianbo.toolkit.prompt.WindowUtils

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {

    private val handler = Handler()

    private val runnable = Runnable {
        startActivity(Intent(this, GuideActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowUtils.hideBottomUIMenu(this)
        super.onCreate(savedInstanceState)

        handler.postDelayed(runnable, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        window.setBackgroundDrawable(null)
    }
}
