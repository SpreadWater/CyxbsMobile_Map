package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.adapter.PlaceDetailImageAdapter
import com.mredrock.cyxbs.discover.map.view.adapter.PlaceLabelAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.PlaceDetailViewModel
import kotlinx.android.synthetic.main.map_fragment_place_content.*

class PlaceDetailContentFragment : BaseViewModelFragment<PlaceDetailViewModel>(), View.OnClickListener {
    private val titles = listOf<String>("入校报到点", "运动场", "教学楼", "图书馆", "食堂", "快递")
    private val imageList = ArrayList<String>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.map_fragment_place_content, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initImagesRv()
        initLabelRV()
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

    override val viewModelClass: Class<PlaceDetailViewModel>
        get() = PlaceDetailViewModel::class.java

    override fun onClick(view: View?) {
        when (view?.id) {
        }
    }
}