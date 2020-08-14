package com.mredrock.cyxbs.discover.map.view.activity

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.bean.SearchPlace
import com.mredrock.cyxbs.discover.map.model.dao.HistoryPlaceDao
import com.mredrock.cyxbs.discover.map.model.dao.SearchHistory
import com.mredrock.cyxbs.discover.map.view.adapter.HistoryAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.map_activity_search_place.*

class SearchActivity : BaseViewModelActivity<SearchViewModel>() {

    override val viewModelClass = SearchViewModel::class.java

    override val isFragmentActivity = false

    val mlist = ArrayList<SearchPlace>()

    val count=0

    val TYPE_HISTORYPLACE = 0

    val TYPE_RESULTPLACE = 1

    var CONTENT_TYPE = false

    var place_num: Int = 0

    var isSearch = false

    val placeItemList = ArrayList<PlaceItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_search_place)
        place_num = intent.getIntExtra("place_num", 0)
        LogUtils.d("zt",place_num.toString())
        initData()
        inithistoryplace()
        initEvent()
    }

    fun initData() {
        //获取之前本地basic的数据
        for (i in 1..place_num) {
            if (HistoryPlaceDao.isPlaceSaved(i)) {
                LogUtils.d("***zt",HistoryPlaceDao.getSavedPlace(i).placeName.toString())
                placeItemList.add(HistoryPlaceDao.getSavedPlace(i))
            }
        }
    }

    //加载历史记录
    fun inithistoryplace() {
      val SearchNum=SearchHistory.getSavedNum()
        if (SearchNum>0){
            //展示历史记录
            for(i in 0..SearchNum-1){
                //顺序获取place的id
               var placeid= SearchHistory.getSavedPlaceId(i)
                //通过placeid获取place
                val place=SearchHistory.getSavedPlace(placeid)
                mlist.add(SearchPlace(place,TYPE_HISTORYPLACE))
            }
        }else{
            Toast.makeText(this, "历史记录为空", Toast.LENGTH_SHORT).show()
        }
    }

    //展示搜索结果的ui
    fun showresultplace() {
        map_tv_search_history.visibility = View.GONE
        map_tv_search_deleteall.visibility = View.GONE
        map_iv_search_content_cancel.visibility = View.VISIBLE
        map_iv_searchplace_icon.visibility = View.GONE
        mlist.clear()
    }

    //展示历史搜索的ui
    fun showhistoryplace() {
        map_tv_search_history.visibility = View.VISIBLE
        map_tv_search_deleteall.visibility = View.VISIBLE
        map_iv_search_content_cancel.visibility = View.GONE
        map_iv_searchplace_icon.visibility = View.VISIBLE
        mlist.clear()
        inithistoryplace()
    }

    //为事件添加点击事件
    fun initEvent() {
        //为rv构造适配器
        map_rv_search.layoutManager = LinearLayoutManager(this)
        val adapter = HistoryAdapter(mlist, map_et_search_place_set, this)
        map_rv_search.adapter = adapter

        map_iv_search_back.setOnClickListener {
            finish()
        }

        map_iv_search_content_cancel.setOnClickListener {
            map_et_search_place_set.setText("")//清空输入框的内容
        }

        //用于监听输入框的变化。
        //监听到输入框不为空就显示另外一个rv布局
//            根据网络请求拿下来的数据判断一下再显示第二个rv布局
        map_et_search_place_set.addTextChangedListener{ editable ->
            val content = editable.toString()//获取输入的名字
            if(content.isNotEmpty()){
                CONTENT_TYPE = true
                val filterPlace=placeItemList.filter { it.placeName!!.contains(content) }
                if(filterPlace.size!=0){
                    //将每一个筛选的place添加进Mlist
                    showresultplace()
                    for (place in filterPlace){
                        mlist.add(SearchPlace(place,TYPE_RESULTPLACE))
                    }
                    LogUtils.d("zt","1111")
                    adapter.notifyDataSetChanged()
                }else{
                    //用于输入搜索不到时的处理
                    showresultplace()
                    adapter.notifyDataSetChanged()
                    com.mredrock.cyxbs.discover.map.utils.Toast.toast("搜索不到！",Gravity.CENTER,0,-800)
                }
            }else{
                LogUtils.d("zt","222")
                CONTENT_TYPE = false
                showhistoryplace()
                adapter.notifyDataSetChanged()
            }
                //监听到输入框不为空就显示另外一个rv布局
//            根据网络请求拿下来的数据判断一下再显示第二个rv布局
                if (CONTENT_TYPE) {
                    map_et_search_place_set.setPadding(10, 0, 0, 0)
                    map_et_search_place_set.setTypeface(Typeface.DEFAULT_BOLD)
                } else {
                    map_et_search_place_set.setPadding(95, 0, 0, 0)
                    map_et_search_place_set.setTypeface(Typeface.DEFAULT)
                }
            }
            //清除全部的监听事件
            map_tv_search_deleteall.setOnClickListener {
                mlist.clear()
                val SearchNum=SearchHistory.getSavedNum()
                for(i in 0..SearchNum-1 ){
                    //顺序获取place的id
                    var placeid= SearchHistory.getSavedPlaceId(i)
                    SearchHistory.deletePlace(placeid)
                }
                SearchHistory.deleteSavedNum()
                adapter.notifyDataSetChanged()
            }
        }
    }