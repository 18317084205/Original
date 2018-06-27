package com.jianbo.original.splash.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Created by Jianbo on 2018/5/3.
 */

class ViewPagerAdapter(private val views: List<View>) : PagerAdapter() {


    override fun getCount(): Int {
        return views.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = views[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence? {
//        if (position % 2 > 0) {
//            return "Tabasdasdasdasd$position"
//        }
        return "Tab$position"
    }


}