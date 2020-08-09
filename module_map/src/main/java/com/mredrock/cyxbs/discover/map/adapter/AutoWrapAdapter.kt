package com.mredrock.cyxbs.discover.map.adapter

import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatTextView
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.discover.map.R

/**
 *@date 2020-8-7
 *@author zhangsan
 *@description
 */
class AutoWrapAdapter(val recommendList:ArrayList<recommend>,private val onItemClickListener:(String)->Unit)
    :RedRockAutoWarpView.Adapter() {

    override fun getItemId(): Int= R.layout.map_item_aw_collect_recommend

    override fun getItemCount()=recommendList.size

    override fun initItem(item: View, position: Int) {
        if (position>=recommendList.size)return
        (item as AppCompatTextView).text=recommendList[position].recommend
        item.setOnClickListener {
            onItemClickListener.invoke(recommendList[position].recommend)
        }
    }

    open  class recommend(val recommend:String)
}