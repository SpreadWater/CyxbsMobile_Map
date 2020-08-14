package com.mredrock.cyxbs.discover.map.view.activity

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.model.dao.HistoryPlaceDao
import com.mredrock.cyxbs.discover.map.view.adapter.HistoryAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.map_activity_search_place.*

class SearchActivity : BaseViewModelActivity<SearchViewModel>() {

    override val viewModelClass = SearchViewModel::class.java

    override val isFragmentActivity = false

    val mlist = ArrayList<Place>()

    val TYPE_HISTORYPLACE = 0

    val TYPE_RESULTPLACE = 1

    var CONTENT_TYPE=false

    var place_num:Int=0

    var isSearch=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_search_place)
         place_num=intent.getIntExtra("place_num",0)
        inithistoryplace()
        initEvent()
    }
    //加载历史记录
    fun inithistoryplace() {
        repeat(3) {
            mlist.add(Place("大西北", TYPE_HISTORYPLACE))
            mlist.add(Place("红岩网校", TYPE_HISTORYPLACE))
            mlist.add(Place("风雨操场", TYPE_HISTORYPLACE))
        }
    }
    //加载搜索结果
    fun initresultplace() {
        repeat(2) {
            mlist.add(Place("四海苑1舍", TYPE_RESULTPLACE))
            mlist.add(Place("四海苑2舍", TYPE_RESULTPLACE))
            mlist.add(Place("四海苑3舍", TYPE_RESULTPLACE))
        }
    }
    //展示搜索结果的ui
    fun showresultplace() {
        map_tv_search_history.visibility = View.GONE
        map_tv_search_deleteall.visibility = View.GONE
        map_iv_search_content_cancel.visibility = View.VISIBLE
        map_iv_search_icon.visibility = View.GONE
        mlist.clear()
        initresultplace()
    }
    //展示历史搜索的ui
    fun showhistoryplace() {
        map_tv_search_history.visibility = View.VISIBLE
        map_tv_search_deleteall.visibility = View.VISIBLE
        map_iv_search_content_cancel.visibility = View.GONE
        map_iv_search_icon.visibility = View.VISIBLE
        mlist.clear()
        inithistoryplace()
    }
    //为事件添加点击事件
    fun initEvent() {
        //为rv构造适配器
        map_rv_search.layoutManager = LinearLayoutManager(this)
        val adapter = HistoryAdapter(mlist, map_et_search_place, this)
        map_rv_search.adapter = adapter

        map_iv_search_back.setOnClickListener {
            finish()
        }

        map_iv_search_content_cancel.setOnClickListener {
            map_et_search_place.setText("")//清空输入框的内容
        }

        //用于监听输入框的变化。
        map_et_search_place.addTextChangedListener { editable ->
            val content = editable.toString()//获取输入的名字
            for(i in 1..place_num){
                if (HistoryPlaceDao.isPlaceSaved(i)){
                    //如果content等于placeName
                    if(content.equals(HistoryPlaceDao.getSavedPlace(i).placeName)){
                       Toast.makeText(this, "查找成功", Toast.LENGTH_SHORT).show()
                        isSearch=true
                        break
                    }
                }
            }
            if(!isSearch){
                Toast.makeText(this, "查找失败", Toast.LENGTH_SHORT).show()
            }
            //监听到输入框不为空就显示另外一个rv布局
//            根据网络请求拿下来的数据判断一下再显示第二个rv布局
            if (content.isNotEmpty()) {
                CONTENT_TYPE=true
                showresultplace()
                adapter.notifyDataSetChanged()
            } else {
                CONTENT_TYPE=false
                showhistoryplace()
                adapter.notifyDataSetChanged()
            }
            if (CONTENT_TYPE){
                map_et_search_place.setPadding(10,0,0,0)
                map_et_search_place.setTypeface(Typeface.DEFAULT_BOLD)
            }else{
                map_et_search_place.setPadding(95,0,0,0)
                map_et_search_place.setTypeface(Typeface.DEFAULT)
            }
        }
        //清除全部的监听事件
        map_tv_search_deleteall.setOnClickListener {
            mlist.clear()
            adapter.notifyDataSetChanged()
        }
    }
}