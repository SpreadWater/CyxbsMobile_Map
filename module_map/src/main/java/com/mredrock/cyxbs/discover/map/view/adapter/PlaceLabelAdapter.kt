package com.mredrock.cyxbs.discover.map.view.adapter

import android.graphics.PointF
import android.view.View
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
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
            text = labelList.get(position).toString()
        }
    }

}