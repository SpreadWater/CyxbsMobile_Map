package com.mredrock.cyxbs.discover.map.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.utils.ImageSelectutils
import com.mredrock.cyxbs.discover.map.view.activity.CollectActivity
import com.mredrock.cyxbs.discover.map.view.activity.ImageAllActivity
import com.mredrock.cyxbs.discover.map.view.adapter.PlaceDetailImageAdapter
import com.mredrock.cyxbs.discover.map.view.adapter.PlaceLabelAdapter
import com.mredrock.cyxbs.discover.map.view.widget.ShareDialog
import com.mredrock.cyxbs.discover.map.viewmodel.PlaceDetailViewModel
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.map_fragment_place_content.*

class PlaceDetailContentFragment : BaseViewModelFragment<PlaceDetailViewModel>() {
    var placeId: Int? = 29
    private val ATTRIBUTE = 0
    private val LABEL = 1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.map_fragment_place_content, container, false)
        val bundle = this.arguments //得到从Activity传来的数据
        if (bundle != null) {
            placeId = bundle.getString("placeId").toInt()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        placeId?.let { getPlaceDetail(it) }
        initOnClick()

    }

    fun getPlaceDetail(placeId: Int) {
        viewModel.getPlaceDetail(placeId)
        viewModel.placeItemDetail.observe(viewLifecycleOwner, Observer {
            it?.run {
                initImagesRv()
                initLabelRV(placeAttribute as ArrayList<String>, ATTRIBUTE)
                map_tv_place_name.text = placeName
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === ImageSelectutils.REQUEST_CODE_CHOOSE_PHOTO_ALBUM && resultCode === Activity.RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            val pathList: List<Uri> = Matisse.obtainResult(data)
            for (_Uri in pathList) {
//                Glide.with(this).load(_Uri).into(mView)
                Log.e("*****zt", _Uri.path)
                System.out.println(_Uri.path)
            }
        }
    }

    private fun initOnClick() {
        map_iv_place_collect.setOnClickListener {
            changeToActivity(CollectActivity())
        }
        map_tv_share_image.setOnClickListener {
            this.activity?.let { it1 -> showDialog(it1) }
        }
        map_tv_search_more_place_detail.setOnClickListener {
            changeToActivity(ImageAllActivity())
        }
        map_iv_place_collect.setOnClickListener {
            changeToActivity(CollectActivity())
        }
    }

    private fun initLabelRV(placeLabelList: ArrayList<String>, type: Int) {
        when (type) {
            ATTRIBUTE -> {
                val placeAttributeAdapter = PlaceLabelAdapter(placeLabelList)
                map_rrv_place_label.adapter = placeAttributeAdapter
            }
            LABEL -> {
                val placeLabelAdapter = PlaceLabelAdapter(placeLabelList)
                map_rrv_place_detail_label.adapter = placeLabelAdapter
            }
        }
    }

    private fun initImagesRv() {
        val titleList = ArrayList<String>()
        for (title in viewModel.titles)
            titleList.add(title)
        val placeDetailImageAdapter = PlaceDetailImageAdapter(titleList)
        map_rv_place_detail_image_list.apply {
            layoutManager = LinearLayoutManager(BaseApp.context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(DividerItemDecoration(BaseApp.context, DividerItemDecoration.VERTICAL))
            adapter = placeDetailImageAdapter
        }
    }

    private fun changeToActivity(activity: Activity) {
        val intent = Intent(BaseApp.context, activity::class.java)
        startActivity(intent)
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
        ImageSelectutils.selectImageFromAlbum(activity as AppCompatActivity, 9, this)
    }

    override val viewModelClass: Class<PlaceDetailViewModel>
        get() = PlaceDetailViewModel::class.java
}