package com.mredrock.cyxbs.discover.map.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.utils.ImageSelectutils
import com.mredrock.cyxbs.discover.map.view.activity.ImageAllActivity
import com.mredrock.cyxbs.discover.map.view.activity.MapActivity
import com.mredrock.cyxbs.discover.map.view.activity.ViewImageActivity
import com.mredrock.cyxbs.discover.map.view.adapter.PlaceDetailImageAdapter
import com.mredrock.cyxbs.discover.map.view.adapter.PlaceLabelAdapter
import com.mredrock.cyxbs.discover.map.view.widget.ShareDialog
import com.mredrock.cyxbs.discover.map.viewmodel.PlaceDetailViewModel
import kotlinx.android.synthetic.main.map_fragment_place_content.*

class PlaceDetailContentFragment : BaseViewModelFragment<PlaceDetailViewModel>() {
    private val titles = listOf<String>("入校报到点", "运动场", "教学楼", "图书", "快递")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.map_fragment_place_content, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initImagesRv()
        initLabelRV()
        initOnClick()
    }

    private fun initOnClick() {
        map_tv_share_image.setOnClickListener {
            this.activity?.let { it1 -> showDialog(it1) }
        }
        map_tv_search_more_place_detail.setOnClickListener {
            changeToActivity(ImageAllActivity())
        }
    }

    private fun initLabelRV() {
        val titleList = ArrayList<String>()
        for (title in titles)
            titleList.add(title)
        val placeLabelAdapter = PlaceLabelAdapter(titleList)
        map_rrv_place_detail_label.adapter = placeLabelAdapter
        map_rrv_place_label.adapter = placeLabelAdapter
    }

    private fun initImagesRv() {
        val titleList = ArrayList<String>()
        for (title in titles)
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