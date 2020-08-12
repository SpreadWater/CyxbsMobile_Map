package com.mredrock.cyxbs.discover.map.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.model.network.Place
import com.mredrock.cyxbs.discover.map.view.activity.SearchActivity
import java.lang.IllegalArgumentException

/**
 *@date 2020-8-7
 *@author zhangsan
 *@description
 */
class HistoryAdapter(val placeList:ArrayList<Place>,val editText: EditText,val activity: SearchActivity):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class HistoryViewHolder(view: View):RecyclerView.ViewHolder(view){
        val historyplace:TextView=view.findViewById(R.id.tv_map_history_place)
        val deleteplace:ImageView=view.findViewById(R.id.iv_map_history_delete)
    }
    inner class ResultViewHolder(view:View):RecyclerView.ViewHolder(view){
        val resultplace:TextView=view.findViewById(R.id.tv_map_result_search_place)
    }

    override fun getItemViewType(position: Int): Int {
        val place=placeList[position]
        return place.placetype
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=if (viewType==0){
        val view=LayoutInflater.from(parent.context).inflate(R.layout.map_item_rv_history_search_place,parent,false)
        val viewHolder=HistoryViewHolder(view)
        //删除历史地点按钮的点击事件
        viewHolder.deleteplace.setOnClickListener {
            val position=viewHolder.adapterPosition
            val place=placeList[position]
            placeList.remove(place)
            notifyDataSetChanged()
        }
        viewHolder.itemView.setOnClickListener {
            val position=viewHolder.adapterPosition
            val place=placeList[position]
            Toast.makeText(parent.context, "开始搜索…………${place.historyplace}", Toast.LENGTH_SHORT).show()
            editText.setText(place.historyplace)
            editText.setSelection(editText.text.length)
            activity.showresultplace()
        }
        viewHolder
    }else{
        val view=LayoutInflater.from(parent.context).inflate(R.layout.map_item_rv_result_search_place,parent,false)
        val viewHolder=ResultViewHolder(view)
        viewHolder
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val place=placeList[position]
        when(holder){
            is HistoryViewHolder->holder.historyplace.text=place.historyplace
            is ResultViewHolder->holder.resultplace.text=place.historyplace
            else->throw IllegalArgumentException()
        }
    }

    override fun getItemCount()=placeList.size
}