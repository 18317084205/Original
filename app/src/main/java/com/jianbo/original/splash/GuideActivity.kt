package com.jianbo.original.splash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.jianbo.original.R
import com.jianbo.original.base.BaseActivity
import com.jianbo.original.home.HomeActivity
import com.jianbo.original.splash.adapter.ViewPagerAdapter
import com.jianbo.toolkit.base.BasePresenter
import com.jianbo.toolkit.prompt.DensityUtils
import com.jianbo.toolkit.prompt.StatusBarUtils
import java.util.*


class GuideActivity : AppCompatActivity(), ViewTreeObserver.OnGlobalLayoutListener, ViewPager.OnPageChangeListener {

    private var viewpager: ViewPager? = null
    private var relatively: RelativeLayout? = null
    private var linearly: LinearLayout? = null
    private var point_blue: View? = null
    private var pointParams: RelativeLayout.LayoutParams? = null
    private var leftMax: Int = 0
    private var winthrop: Int = 0

    private var list = ArrayList<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        StatusBarUtils.with(this)
                .setThemeColor(Color.GRAY)
                .useDarkTextColor(true).init()
        viewpager = findViewById(R.id.viewPager)
        relatively = findViewById(R.id.relativeLayout)
        linearly = findViewById(R.id.linearLayout)

        winthrop = DensityUtils.dip2px(this, 10F);
        for (i in 0 until 5) {
            val view = View(this)
            view.setBackgroundColor(Color.WHITE)
            list.add(view)
            //加载下边的正常状态的圆点
            initNormalPoint(i)
        }
        //加载下边的选中状态的圆点
        initBluepoint()
        viewpager!!.adapter = ViewPagerAdapter(list)
        viewpager!!.addOnPageChangeListener(this)

        findViewById<Button>(R.id.guide_start).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }


    override fun onGlobalLayout() {
        point_blue!!.viewTreeObserver.removeGlobalOnLayoutListener(this);
        leftMax = linearly!!.getChildAt(1).left - linearly!!.getChildAt(0).left;
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val leftMagi = position * leftMax + positionOffset * leftMax
        pointParams = point_blue!!.layoutParams as RelativeLayout.LayoutParams
        pointParams!!.leftMargin = leftMagi.toInt()
        point_blue!!.layoutParams = pointParams

    }

    override fun onPageSelected(position: Int) {
        if (position == list.size - 1) {
            findViewById<Button>(R.id.guide_start).visibility = View.VISIBLE;
        } else {
            findViewById<Button>(R.id.guide_start).visibility = View.GONE;
        }
    }

    private fun initNormalPoint(i: Int) {
        val point = View(this)
        point.setBackgroundResource(R.color.black_overlay)
        val params = LinearLayout.LayoutParams(winthrop, winthrop)
        if (i != 0) {
            params.leftMargin = winthrop
        }
        point.layoutParams = params
        linearly!!.addView(point)
    }


    private fun initBluepoint() {
        point_blue = View(this)
        point_blue!!.setBackgroundResource(R.color.colorPrimary)
        pointParams = RelativeLayout.LayoutParams(winthrop, winthrop)
        point_blue!!.layoutParams = pointParams
        relatively!!.addView(point_blue)
        point_blue!!.viewTreeObserver.addOnGlobalLayoutListener(this)
    }
}
