package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.adapter.HistoryAdapter
import com.mredrock.cyxbs.discover.map.model.network.Place
import com.mredrock.cyxbs.discover.map.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.map_fragment_search_place.*

/**
 *@date 2020-8-7
 *@author zhangsan
 *@description
 */
class SearchFragment : BaseViewModelFragment<SearchViewModel>() {

    override val viewModelClass = SearchViewModel::class.java

    val mlist = ArrayList<Place>()

    val TYPE_HISTORYPLACE = 0

    val TYPE_RESULTPLACE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_search_place, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inithistoryplace()
        initEvent()
    }

    fun inithistoryplace() {
        repeat(3) {
            mlist.add(Place("大西北", TYPE_HISTORYPLACE))
            mlist.add(Place("红岩网校", TYPE_HISTORYPLACE))
            mlist.add(Place("风雨操场", TYPE_HISTORYPLACE))
        }
    }

    fun initresultplace() {
        repeat(2) {
            mlist.add(Place("四海苑1舍", TYPE_RESULTPLACE))
            mlist.add(Place("四海苑2舍", TYPE_RESULTPLACE))
            mlist.add(Place("四海苑3舍", TYPE_RESULTPLACE))
        }
    }

    fun showresultplace() {
        map_tv_search_history.visibility = View.GONE
        map_tv_search_deleteall.visibility = View.GONE
        map_iv_search_content_cancel.visibility = View.VISIBLE
        map_iv_search_icon.visibility = View.GONE
        mlist.clear()
        initresultplace()
    }

    fun showhistoryplace() {
        map_tv_search_history.visibility = View.VISIBLE
        map_tv_search_deleteall.visibility = View.VISIBLE
        map_iv_search_content_cancel.visibility = View.GONE
        map_iv_search_icon.visibility = View.VISIBLE
        mlist.clear()
        inithistoryplace()
    }

    fun initEvent() {
        //为rv构造适配器
        map_rv_search.layoutManager = LinearLayoutManager(this.context)
        val adapter = HistoryAdapter(mlist, map_et_search_place, this)
        map_rv_search.adapter = adapter

        map_iv_search_back.setOnClickListener {
            activity?.finish()
        }

        map_iv_search_content_cancel.setOnClickListener {
            map_et_search_place.setText("")//清空输入框的内容
        }

        //用于监听输入框的变化。
        map_et_search_place.addTextChangedListener { editable ->
            val content = editable.toString()
            //监听到输入框不为空就显示另外一个rv布局

//            根据网络请求拿下来的数据判断一下再显示第二个rv布局
            if (content.isNotEmpty()) {
                showresultplace()
                adapter.notifyDataSetChanged()
            } else {
                showhistoryplace()
                adapter.notifyDataSetChanged()
            }
        }
        //清除全部的监听事件
        map_tv_search_deleteall.setOnClickListener {
            mlist.clear()
            adapter.notifyDataSetChanged()
        }
    }
}