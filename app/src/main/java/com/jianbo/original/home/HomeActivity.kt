package com.jianbo.original.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.BounceInterpolator
import com.jianbo.original.R
import com.jianbo.toolkit.widget.Menu
import com.jianbo.toolkit.widget.NavigationMenu
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        navigationView.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_camera).setTitle("0001"))
        navigationView.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_share).setTitle("0002"))
        navigationView.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_call).setTitle("0003"))
        navigationView.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_agenda).setTitle("0004"))

        navigationView_top.setScroller(true, BounceInterpolator())
        navigationView_top.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_camera).setTitle("0001").setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_share).setTitle("0002").setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_call).setTitle("0003").setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_agenda).setTitle("0004").setMode(Menu.MODE_HORIZONTAL))

        navigationView_top.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_share).setTitle("0002").setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_call).setTitle("0003").setMode(Menu.MODE_HORIZONTAL))
        navigationView_top.addMenu(NavigationMenu(this).setIcon(android.R.drawable.ic_menu_agenda).setTitle("0004").setMode(Menu.MODE_HORIZONTAL))
    }
}
