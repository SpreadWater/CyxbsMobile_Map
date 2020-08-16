package com.mredrock.cyxbs.discover.map.view.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import com.mredrock.cyxbs.discover.map.R
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_dialog_collect.*
import kotlinx.android.synthetic.main.map_dialog_collect.map_btn_dialog_cancel
import kotlinx.android.synthetic.main.map_dialog_progress.*
import org.jetbrains.anko.runOnUiThread

/**

 */
class ProgressDialog(context: Context) : Dialog(context), View.OnClickListener {

    private var mListener: OnClickListener? = null

    interface OnClickListener {
        fun onCancel()
    }


    fun setProgress(progress: String = "0") {
        context.runOnUiThread {
            map_tv_load_map.text = "正在加载地图图片$progress%"
        }
    }

    override fun onClick(v: View?) {
        if (mListener == null)
            return
        when (v) {
            map_btn_dialog_cancel -> mListener!!.onCancel()
        }
    }

    init {
        val window = window
        val windowParams = window!!.attributes
        window.setGravity(Gravity.CENTER)
        val decorView = window.decorView
        decorView.getWindowVisibleDisplayFrame(Rect())
        windowParams.width = context.getScreenWidth() - context.dp2px(20f)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.attributes = windowParams
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_dialog_progress)
        map_btn_dialog_cancel.setOnClickListener(this)
    }

    fun setListener(listener: OnClickListener) {
        mListener = listener
    }
}