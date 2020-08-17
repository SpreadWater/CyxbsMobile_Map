package com.mredrock.cyxbs.discover.map.view.activity


import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.bean.SearchPlace
import com.mredrock.cyxbs.discover.map.model.dao.HistoryPlaceDao
import com.mredrock.cyxbs.discover.map.model.dao.SearchData
import com.mredrock.cyxbs.discover.map.model.dao.SearchHistory
import com.mredrock.cyxbs.discover.map.view.adapter.HistoryAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.map_activity_search_place.*

class SearchActivity : BaseViewModelActivity<SearchViewModel>() {

    override val viewModelClass = SearchViewModel::class.java

    override val isFragmentActivity = false

    val mlist = ArrayList<SearchPlace>()

    val TYPE_HISTORYPLACE = 0

    val TYPE_RESULTPLACE = 1

    var CONTENT_TYPE = false

    var place_num: Int = 0

    var isSearch = false

    val placeItemList = ArrayList<PlaceItem>()

    var hot_word = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_search_place)
        place_num = SearchData.getSavedItemNum()
        hot_word = SearchData.getSavedHotword()
        initData()
        inithistoryplace()
        initEvent()
    }

    fun initData() {
        if (!hot_word.equals("")) {
            map_et_search_place_set.setText("大家都在搜：$hot_word")
            map_et_search_place_set.setTypeface(Typeface.DEFAULT)
        } else {
            map_et_search_place_set.setText("大家都在搜：红岩网校")
            map_et_search_place_set.setTypeface(Typeface.DEFAULT)
        }
        //获取之前本地basic的数据
        for (i in 1..place_num) {
            if (HistoryPlaceDao.isPlaceSaved(i)) {
                HistoryPlaceDao.getSavedPlace(i)?.let { placeItemList.add(it) }
            }
        }
    }

    //加载历史记录
    fun inithistoryplace() {
        val SearchNum = SearchHistory.getSavedNum()
        if (SearchNum > 0) {
            //展示历史记录
            for (i in 0..SearchNum - 1) {
                //顺序获取place的id
                var placeid = SearchHistory.getSavedPlaceId(i)
                //通过placeid获取place
                val place = SearchHistory.getSavedPlace(placeid)
                mlist.add(SearchPlace(place, TYPE_HISTORYPLACE))
            }
        } else {
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
        val adapter = HistoryAdapter(mlist, viewModel, this)
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

        //把属性变化一下
        map_et_search_place_set.setOnClickListener {
            map_et_search_place_set.setText("")
            map_et_search_place_set.isCursorVisible = true
            map_et_search_place_set.isFocusable = true
        }
        map_et_search_place_set.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val content = editable.toString()//获取输入的名字
                if(content.isNotEmpty()){
                    CONTENT_TYPE = true
                    val filterPlace=placeItemList.filter { it.placeName!!.contains(content) }
                    showresultplace()
                    if(filterPlace.size!=0){
                        //将每一个筛选的place添加进Mlist
                        isSearch=false
                        for (place in filterPlace){
                            mlist.add(SearchPlace(place,TYPE_RESULTPLACE))
                        }
                        adapter.notifyDataSetChanged()
                    }else{
                        //用于搜素不到的处理
                        if(!isSearch){
                            isSearch=true
                            com.mredrock.cyxbs.discover.map.utils.Toast.toast("搜索不到该地点",Gravity.CENTER,0,-800)
                        }
                    }
                }else{
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

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                map_et_search_place_set.setTextColor(resources.getColor(R.color.levelOneFontColor))
            }
        })

        //清除全部的监听事件
        map_tv_search_deleteall.setOnClickListener {
            mlist.clear()
            val SearchNum = SearchHistory.getSavedNum()
            for (i in 0..SearchNum - 1) {
                //顺序获取place的id
                var placeid = SearchHistory.getSavedPlaceId(i)
                SearchHistory.deletePlace(placeid)
            }
            SearchHistory.deleteSavedNum()
            adapter.notifyDataSetChanged()
        }
    }

}