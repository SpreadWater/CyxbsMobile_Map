package com.mredrock.cyxbs.discover.map.view.widget


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.discover.map.R
import kotlinx.android.synthetic.main.map_item_tablayout_label_one.view.*


class MyTabLayout : TabLayout {
    private var titles: List<String>? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        titles = ArrayList()
        this.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                /**
                 * 设置当前选中的Tab为特殊高亮样式。
                 */
                if (tab?.customView != null) {
                    val tab_layout_text: TextView = tab.customView!!.findViewById(R.id.map_tv_label_item)
                    tab_layout_text.setBackgroundResource(R.drawable.map_shape_et_search_place)
                }
            }

            override fun onTabUnselected(tab: Tab) {
                /**
                 * 重置所有未选中的Tab颜色、字体、背景恢复常态(未选中状态)。
                 */
                if (tab?.customView != null) {
                    val tab_layout_text: TextView = tab.customView!!.findViewById(R.id.map_tv_label_item)
                    tab_layout_text.setBackgroundResource(R.drawable.map_shape_no_select_label_item)
                }
            }

            override fun onTabReselected(tab: Tab) {}
        })
    }

    fun setTitle(titles: List<String>?) {
        this.titles = titles
        var count = 0
        /**
         * 开始添加切换的Tab。
         */
        for (title in this.titles!!) {
            count++
            val tab = newTab()
            tab.setCustomView(R.layout.map_item_tablayout_label_one)
            if (tab.customView != null) {
                val map_iv_hot=tab.customView!!.map_iv_hot
                if(count==1)
                {
                    map_iv_hot.visibility=View.VISIBLE
                }
                else
                {
                    map_iv_hot.visibility=View.GONE
                }
                val text: TextView = tab.customView!!.findViewById(R.id.map_tv_label_item)
                text.text = title
            }
            this.addTab(tab)
        }

    }
}