package com.mredrock.cyxbs.discover.map.view.widget

import android.app.Activity

object ShowDialog {
    fun showDialog(activity: Activity) {

        val dialog = ShareDialog(activity)
        dialog.setListener(object : ShareDialog.OnClickListener {
            override fun onCancel() {
                dialog.dismiss()
            }

            override fun onConfirm() {
                android.widget.Toast.makeText(activity, "发送取消收藏网络请求…………", android.widget.Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
        dialog.show()
    }
}