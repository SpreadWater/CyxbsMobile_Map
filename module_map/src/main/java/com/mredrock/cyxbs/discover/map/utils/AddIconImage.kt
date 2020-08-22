package com.mredrock.cyxbs.discover.map.utils

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.mredrock.cyxbs.common.BaseApp

object AddIconImage {
    private val LEFT = 0
    private val RIGHT = 1
    private val TOP = 2
    private val BOTTOM = 3
    fun setImageViewToButton(drawable: Int, view: Button, place: Int) {
        val drawable: Drawable =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    BaseApp.context.resources.getDrawable(drawable, null)
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")
                }
        drawable.setBounds(0, 0, 80, 65)
        when (place) {
            TOP -> view.setCompoundDrawables(null, drawable, null, null)
            RIGHT -> view.setCompoundDrawables(null, null, drawable, null)
            LEFT -> view.setCompoundDrawables(drawable, null, null, null)
            BOTTOM -> view.setCompoundDrawables(null, null, drawable, drawable)
        }
    }

    fun setImageViewToButton(drawable: Int, view: EditText, place: Int) {
        val drawable: Drawable =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    BaseApp.context.resources.getDrawable(drawable, null)
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")
                }
        drawable.setBounds(0, 0, 45, 45)
        when (place) {
            TOP -> view.setCompoundDrawables(null, drawable, null, null)
            RIGHT -> view.setCompoundDrawables(null, null, drawable, null)
            LEFT -> view.setCompoundDrawables(drawable, null, null, null)
            BOTTOM -> view.setCompoundDrawables(null, null, drawable, drawable)
        }
    }
}