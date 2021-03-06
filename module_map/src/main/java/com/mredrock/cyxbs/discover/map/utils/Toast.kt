package com.mredrock.cyxbs.discover.map.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.R


object Toast {
    fun toast(content: Int,position:Int=Gravity.BOTTOM,xOffset:Int=0,yOffset:Int=100) {
        val view = LayoutInflater.from(BaseApp.context).inflate(R.layout.map_item_toast, null)
        val toast = Toast(BaseApp.context)
        val textView = view.findViewById<TextView>(R.id.map_tv_toast)
        textView.setText(content)
        toast.setGravity(position, xOffset, yOffset)
        toast.duration = Toast.LENGTH_LONG
        toast.view = view
        toast.show()
    }
}
