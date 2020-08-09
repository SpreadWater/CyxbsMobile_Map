package com.mredrock.cyxbs.discover.map.view.activity

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.View
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import com.mredrock.cyxbs.discover.map.R
import kotlinx.android.synthetic.main.map_collect_dialog.*

/**
 *@date 2020-8-8
 *@author zhangsan
 *@description
 */
class CollectDialog(context: Context):Dialog (context),View.OnClickListener{

    private var mListener: OnClickListener? = null

    interface OnClickListener {
        fun onCancel()
        fun onConfirm()
    }
    override fun onClick(v: View?) {
            if (mListener==null)
                return
        when(v){
            map_btn_dialog_cancel->mListener!!.onCancel()
            map_btn_dialog_confirm->mListener!!.onConfirm()
        }
    }
    init {
        val window = window
        val windowParams = window!!.attributes
        window.setGravity(Gravity.CENTER)
        val decorView = window.decorView
        decorView.getWindowVisibleDisplayFrame(Rect())
        windowParams.width = context.getScreenWidth() - context.dp2px(46f)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.attributes = windowParams
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_collect_dialog)
        map_btn_dialog_cancel.setOnClickListener(this)
        map_btn_dialog_confirm.setOnClickListener(this)
    }
    fun setListener(listener:OnClickListener){
        mListener=listener
    }
}