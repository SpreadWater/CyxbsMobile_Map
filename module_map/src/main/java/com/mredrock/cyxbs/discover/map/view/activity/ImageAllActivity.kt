package com.mredrock.cyxbs.discover.map.view.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.model.network.Image
import com.mredrock.cyxbs.discover.map.utils.ImageSelectutils
import com.mredrock.cyxbs.discover.map.utils.ImageSelectutils.REQUEST_CODE_CHOOSE_PHOTO_ALBUM
import com.mredrock.cyxbs.discover.map.view.adapter.ImageAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.ImageLoaderViewModel
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.map_activity_allimage.*


/**
 *@date 2020-8-9
 *@author zhangsan
 *@description
 */
class ImageAllActivity : BaseViewModelActivity<ImageLoaderViewModel>() {
    override val isFragmentActivity: Boolean
        get() = false
    override val viewModelClass = ImageLoaderViewModel::class.java

    val imageUrls = ArrayList<Image>()

    val MAX_SELECTABLE_IMAGE_COUNT = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_allimage)
        initData()

        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.isAutoMeasureEnabled
        map_rv_allimage.adapter = ImageAdapter(imageUrls, this)
        map_rv_allimage.layoutManager = layoutManager
        //返回false表示已经到达底部

        map_tv_allimage_share.setOnClickListener {
            ImageSelectutils.selectImageFromAlbum(MAX_SELECTABLE_IMAGE_COUNT,this)
        }
        map_iv_allimage_back.setOnClickListener {
            finish()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_CODE_CHOOSE_PHOTO_ALBUM && resultCode === Activity.RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            val pathList: List<Uri> = Matisse.obtainResult(data)
            for (_Uri in pathList) {
//                Glide.with(this).load(_Uri).into(mView)
                Log.e("*****zt",_Uri.path)
                System.out.println(_Uri.path)
            }
        }
    }

    private fun initData() {
        repeat(7) {
            imageUrls.add(Image(R.drawable.umeng_socialize_qq))
            imageUrls.add(Image(R.drawable.umeng_socialize_qq))
        }
    }


}