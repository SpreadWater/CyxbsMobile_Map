package com.mredrock.cyxbs.discover.map.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.SearchPlace
import com.mredrock.cyxbs.discover.map.model.dao.SearchHistory
import com.mredrock.cyxbs.discover.map.view.activity.MapActivity
import com.mredrock.cyxbs.discover.map.view.activity.SearchActivity
import com.mredrock.cyxbs.discover.map.viewmodel.SearchViewModel
import java.lang.IllegalArgumentException

/**
 *@date 2020-8-7
 *@author zhangsan
 *@description
 */
class HistoryAdapter(val placeList: ArrayList<SearchPlace>, val viewModel: SearchViewModel, val activity: SearchActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var SearchNum: Int = 0

    var isRepetive = false


    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val historyplace: TextView = view.findViewById(R.id.tv_map_history_place)
        val deleteplace: ImageView = view.findViewById(R.id.iv_map_history_delete)
    }

    inner class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resultplace: TextView = view.findViewById(R.id.map_tv_result_search_place)
    }

    override fun getItemViewType(position: Int): Int {
        val place = placeList[position]
        return place.placetype
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == 0) {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.map_item_rv_history_search_place, parent, false)
        val viewHolder = HistoryViewHolder(view)
        //删除历史地点按钮的点击事件
        viewHolder.deleteplace.setOnClickListener {
            val position = viewHolder.adapterPosition
            val place = placeList[position]
            placeList.remove(place)
            //删除数据库中对应的数据
            SearchHistory.deletePlace(place.placeItem.placeId)
            SearchNum = SearchHistory.getSavedNum()//获取上一次保存的记录个数
            SearchHistory.savePlace(place.placeItem, SearchNum)
            SearchNum--//点击一次搜索记录个数-1
            SearchHistory.savePlaceNum(SearchNum)//保存新的搜索个数
            notifyDataSetChanged()
        }
        //历史搜索item的点击事件
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val place = placeList[position]
            viewModel.SearchPlace(place.placeItem.placeId)
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra("placeId", place.placeItem.placeId.toString())
            activity.startActivity(intent)
            activity.finish()
        }
        viewHolder
    } else {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.map_item_rv_result_search_place, parent, false)
        val viewHolder = ResultViewHolder(view)
        viewHolder.resultplace.setOnClickListener {
            val position = viewHolder.adapterPosition
            val place = placeList[position]
            SearchNum = SearchHistory.getSavedNum()//获取上一次保存的记录个数
            //判断一下是否历史记录重复的情况
            for (i in 0..SearchNum - 1) {
                //获取placeid
                var placeid = SearchHistory.getSavedPlaceId(i)
                //如果发现重复
                if (SearchHistory.getSavedPlace(placeid).placeName.equals(place.placeItem.placeName)) {
                    isRepetive = true
                }
            }
            if (!isRepetive) {
                SearchHistory.savePlace(place.placeItem, SearchNum)
                SearchNum++//点击一次搜索记录个数加1
                SearchHistory.savePlaceNum(SearchNum)//保存新的搜索个数
            }
            viewModel.SearchPlace(place.placeItem.placeId)
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra("placeId", place.placeItem.placeId.toString())
            activity.startActivity(intent)
            activity.finish()
        }
        viewHolder
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val place = placeList[position]
        when (holder) {
            is HistoryViewHolder -> holder.historyplace.text = place.placeItem.placeName
            is ResultViewHolder -> holder.resultplace.text = place.placeItem.placeName
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount() = placeList.size
}