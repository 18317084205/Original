package com.jianbo.original.base

/**
 * Created by Jianbo on 2018/4/23.
 */
interface BaseView<in P : BasePresenter> {
    fun setPresenter(presenter: P)
}