package com.mredrock.cyxbs.discover.map.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.R

class CollectPlaceAdapter(val collectPlaceList: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_COLLECT = 1
        const val TYPE_FOOT = 0
    }

    class CollectPlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val map_tv_list_item = itemView.findViewById<TextView>(R.id.map_tv_list_item)
    }

    class CollectPlaceFootViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val map_tv_list_foot_item = itemView.findViewById<TextView>(R.id.map_tv_list_foot_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_COLLECT) {
            val view = LayoutInflater.from(BaseApp.context).inflate(R.layout.map_item_collect_place_list, parent, false)
            return CollectPlaceViewHolder(view)
        } else {
            val view = LayoutInflater.from(BaseApp.context).inflate(R.layout.map_item_collect_place_list_foot, parent, false)
            return CollectPlaceFootViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_COLLECT -> {
                val holder = holder as CollectPlaceViewHolder
                holder.map_tv_list_item.text = collectPlaceList[position]
            }
            TYPE_FOOT -> {
                val holder = holder as CollectPlaceFootViewHolder
                holder.map_tv_list_foot_item.text = "暂无收场哦"
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (collectPlaceList.size == 0)
            return TYPE_FOOT
        else return TYPE_COLLECT
    }

    override fun getItemCount(): Int {
        return collectPlaceList.size
    }

}
