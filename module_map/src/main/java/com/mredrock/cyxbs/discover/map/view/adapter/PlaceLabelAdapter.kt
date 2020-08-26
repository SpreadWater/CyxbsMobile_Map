package com.mredrock.cyxbs.discover.map.view.adapter

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.View
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import com.mredrock.cyxbs.discover.map.R
import kotlinx.android.synthetic.main.map_item_place_label.view.*

class PlaceLabelAdapter(val labelList: ArrayList<String>) : RedRockAutoWarpView.Adapter() {
    override fun getItemCount(): Int {
        return labelList.size
    }

    override fun getItemId(position: Int): Int? {
        return R.layout.map_item_place_label
    }

    override fun initItem(item: View, position: Int) {
        item.map_tv_label_item.apply {
            text = labelList[position]
        }
        //点击动画效果
        item.map_tv_label_item.setOnClickListener {
            val animScale=ValueAnimator.ofFloat(1f,0.8f,1f)
            animScale.duration = 500
            animScale.addUpdateListener {
                item.map_tv_label_item.scaleX=it.animatedValue as Float
                item.map_tv_label_item.scaleY=it.animatedValue as Float
            }
            animScale.start()
        }
    }

}