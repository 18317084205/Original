package com.jianbo.original.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jianbo.original.R
import com.liang.jtablayout.Indicator
import com.liang.jtablayout.JIndicator
import com.liang.jtablayout.Menu
import com.liang.jtablayout.NavigationMenu
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        navigationView.setIndicator(JIndicator())
        navigationView.addTab(NavigationMenu.newInstance(this, "0001", android.R.drawable.ic_menu_camera))
        navigationView.addTab(NavigationMenu.newInstance(this, "0002", android.R.drawable.ic_menu_share))
        navigationView.addTab(NavigationMenu.newInstance(this, "0003", android.R.drawable.ic_menu_call))
        navigationView.addTab(NavigationMenu.newInstance(this, "0004", android.R.drawable.ic_menu_camera))
        navigationView.addTab(NavigationMenu.newInstance(this, "0005", android.R.drawable.ic_menu_agenda))
        navigationView.addTab(NavigationMenu.newInstance(this, "0006", android.R.drawable.ic_menu_call))

        navigationView_top.setScroller(true)
        var ind = JIndicator()
        ind.width = 50
        ind.setType(Indicator.TYPE_TRIANGLE)
//        ind.setGravity(Gravity.TOP)
        navigationView_top.setIndicator(ind)
        navigationView_top.addTab(NavigationMenu.newInstance(this, "0sasa001", android.R.drawable.ic_menu_camera).setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addTab(NavigationMenu.newInstance(this, "0002", android.R.drawable.ic_menu_share).setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addTab(NavigationMenu.newInstance(this, "00a03", android.R.drawable.ic_menu_call).setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addTab(NavigationMenu.newInstance(this, "0004", android.R.drawable.ic_menu_camera).setMode(Menu.MODE_HORIZONTAL))

        navigationView_top.addTab(NavigationMenu.newInstance(this, "00sa05", android.R.drawable.ic_menu_call).setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addTab(NavigationMenu.newInstance(this, "0006", android.R.drawable.ic_menu_agenda).setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addTab(NavigationMenu.newInstance(this, "00s07", android.R.drawable.ic_menu_camera).setMode(Menu.MODE_HORIZONTAL))
    }
}
