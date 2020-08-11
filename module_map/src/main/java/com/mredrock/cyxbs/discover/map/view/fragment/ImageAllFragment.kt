package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.adapter.ImageAdapter
import com.mredrock.cyxbs.discover.map.model.network.Image
import com.mredrock.cyxbs.discover.map.utils.selectImageFromAlbum
import com.mredrock.cyxbs.discover.map.view.activity.SearchActivity
import com.mredrock.cyxbs.discover.map.viewmodel.ImageLoaderViewModel
import kotlinx.android.synthetic.main.map_fragment_allimage.*

/**
 *@date 2020-8-9
 *@author zhangsan
 *@description
 */
class ImageAllFragment : BaseViewModelFragment<ImageLoaderViewModel>() {

    override val viewModelClass = ImageLoaderViewModel::class.java

    val imageUrls = ArrayList<Image>()

    val MAX_SELECTABLE_IMAGE_COUNT = 6

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_allimage, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()

        val layoutManager = GridLayoutManager(activity, 2)
        layoutManager.isAutoMeasureEnabled
        map_rv_allimage.adapter = ImageAdapter(imageUrls, this)
        map_rv_allimage.layoutManager = layoutManager
        //返回false表示已经到达底部
        map_tv_allimage_share.setOnClickListener {
            val activity = activity as SearchActivity
            activity.selectImageFromAlbum(MAX_SELECTABLE_IMAGE_COUNT, null)
        }
        map_rv_allimage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //1代表底部,返回true表示没到底部,还可以滑
                val b = map_rv_allimage.canScrollVertically(1)
                if (b) {
                    map_tv_nomore.visibility = View.GONE
                } else {
                    map_tv_nomore.visibility = View.VISIBLE
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    fun initData() {
        repeat(7) {
            imageUrls.add(Image(R.drawable.umeng_socialize_qq))
            imageUrls.add(Image(R.drawable.umeng_socialize_qq))
        }
    }
}