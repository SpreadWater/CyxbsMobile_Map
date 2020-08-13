package com.mredrock.cyxbs.discover.map.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.Image
import com.mredrock.cyxbs.discover.map.view.activity.ViewImageActivity

/**
 *@date 2020-8-9
 *@author zhangsan
 *@description
 */
class ImageAdapter(val imageUrls:ArrayList<Image>, val activity: AppCompatActivity):RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val image:ImageView=view.findViewById(R.id.map_iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.map_item_rv_allimage,parent,false)
        val viewHolder=ViewHolder(view)
        viewHolder.image.setOnClickListener {
            val position=viewHolder.adapterPosition
            val intent=Intent(activity,ViewImageActivity::class.java)
            intent.putExtra("url",imageUrls[position].imageUrl)
            activity.startActivity(intent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image=imageUrls[position]
        holder.image.setImageResource(image.imageUrl)

    }

    override fun getItemCount()=imageUrls.size

}