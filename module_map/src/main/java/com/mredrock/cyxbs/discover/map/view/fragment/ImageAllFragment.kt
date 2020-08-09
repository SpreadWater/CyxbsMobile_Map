package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.adapter.ImageAdapter
import com.mredrock.cyxbs.discover.map.network.Image
import com.mredrock.cyxbs.discover.map.viewmodel.ImageLoaderViewModel
import kotlinx.android.synthetic.main.map_fragment_allimage.*

/**
 *@date 2020-8-9
 *@author zhangsan
 *@description
 */
class ImageAllFragment : BaseViewModelFragment<ImageLoaderViewModel>() {

    override val viewModelClass=ImageLoaderViewModel::class.java

    val imageUrls=ArrayList<Image>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_allimage,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
        val layoutManager=GridLayoutManager(activity,2)
        layoutManager.isAutoMeasureEnabled
        map_rv_allimage.adapter=ImageAdapter(imageUrls,this)
        map_rv_allimage.layoutManager=layoutManager
        if (isBottom(map_rv_allimage)){
            map_tv_nomore.visibility=View.VISIBLE
        }else{
            map_tv_nomore.visibility=View.GONE
        }
    }
    fun initData(){
        repeat(7){
            imageUrls.add(Image(R.drawable.umeng_socialize_sina))
            imageUrls.add(Image(R.drawable.umeng_socialize_wxcircle))
        }
    }

    fun isBottom(rv:RecyclerView):Boolean{
        if (rv==null)return false
        if (rv.computeVerticalScrollExtent()+rv.computeVerticalScrollOffset()>=
                rv.computeVerticalScrollRange())return true
        return false
    }
}