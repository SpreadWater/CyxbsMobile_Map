package com.mredrock.cyxbs.discover.map.view.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.Image
import com.mredrock.cyxbs.discover.map.utils.ImageSelectutils
import com.mredrock.cyxbs.discover.map.utils.ImageSelectutils.REQUEST_CODE_CHOOSE_PHOTO_ALBUM
import com.mredrock.cyxbs.discover.map.view.adapter.ImageAdapter
import com.mredrock.cyxbs.discover.map.view.widget.ShareDialog
import com.mredrock.cyxbs.discover.map.viewmodel.ImageLoaderViewModel
import com.umeng.analytics.MobclickAgent
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

    val imageUrls = ArrayList<Uri>()

    val MAX_SELECTABLE_IMAGE_COUNT = 6

    var adapter:ImageAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_allimage)
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.isAutoMeasureEnabled
        adapter= ImageAdapter(imageUrls,this)
        map_rv_allimage.adapter = adapter
        map_rv_allimage.layoutManager = layoutManager
        //返回false表示已经到达底部

        map_tv_allimage_share.setOnClickListener {
            showDialog(this)
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

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_CODE_CHOOSE_PHOTO_ALBUM && resultCode === Activity.RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            val pathList: List<Uri> = Matisse.obtainResult(data)
            for (_Uri in pathList) {
//                Glide.with(this).load(_Uri).into(mView)
                imageUrls.add(_Uri)
                Log.e("*****zt",_Uri.path)
                System.out.println(_Uri.path)
            }
            adapter?.notifyDataSetChanged()
        }
    }


    private fun showDialog(activity: Activity) {

        val dialog = ShareDialog(activity)
        dialog.setListener(object : ShareDialog.OnClickListener {
            override fun onCancel() {
                dialog.dismiss()
            }
            override fun onConfirm() {
                dialog.dismiss()
                openAlbum()
            }
        })
        dialog.show()
    }

    private fun openAlbum() {
        ImageSelectutils.selectImageFromAlbum(9,this)
    }
}