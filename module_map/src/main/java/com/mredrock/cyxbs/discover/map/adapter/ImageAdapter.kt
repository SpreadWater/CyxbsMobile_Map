package com.mredrock.cyxbs.discover.map.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.network.Image
import kotlinx.android.synthetic.main.map_fragment_allimage.*
import kotlinx.android.synthetic.main.map_item_rv_allimage.view.*

/**
 *@date 2020-8-9
 *@author zhangsan
 *@description
 */
class ImageAdapter(val imageUrls:ArrayList<Image>,val fragment: Fragment):RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val image:ImageView=view.findViewById(R.id.map_iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.map_item_rv_allimage,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image=imageUrls[position]
        holder.image.setImageResource(image.imageUrl)

    }

    override fun getItemCount()=imageUrls.size

}