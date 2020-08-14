package com.mredrock.cyxbs.discover.map.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.widget.RectangleView
import kotlinx.android.synthetic.main.map_item_place_detail_image.view.*

class PlaceDetailImageAdapter(val imageList: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class PlaceDetailImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val map_iv_rect_iamge = itemView.findViewById<RectangleView>(R.id.map_iv_rectangle_iamge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(BaseApp.context).inflate(R.layout.map_item_place_detail_image, parent, false)
        return PlaceDetailImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Glide.with(BaseApp.context).load(R.drawable.map_ic_collect).into(holder.itemView.map_iv_rectangle_iamge)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}