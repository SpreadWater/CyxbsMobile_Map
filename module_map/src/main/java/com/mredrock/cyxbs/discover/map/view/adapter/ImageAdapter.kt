package com.mredrock.cyxbs.discover.map.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.model.network.Image
import com.mredrock.cyxbs.discover.map.view.activity.ViewImageActivity

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
        val view=LayoutInflater.from(parent.context).inflate(R.layout.map_item_rv_allimage,parent,false)
        val viewHolder=ViewHolder(view)
        viewHolder.image.setOnClickListener {
            val position=viewHolder.adapterPosition
            val intent=Intent(fragment.activity,ViewImageActivity::class.java)
            intent.putExtra("url",imageUrls[position].imageUrl)
            fragment.activity?.startActivity(intent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image=imageUrls[position]
        holder.image.setImageResource(image.imageUrl)

    }

    override fun getItemCount()=imageUrls.size

}