package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.adapter.CollectPlaceAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.CollectPlaceVIewModel
import kotlinx.android.synthetic.main.map_fragment_collect_place.*

class CollectPlaceFragment : BaseViewModelFragment<CollectPlaceVIewModel>() {
    private val titles = listOf("入校报到点", "教学楼", "图书馆", "食堂", "快递", " ")
    override val viewModelClass: Class<CollectPlaceVIewModel>
        get() = CollectPlaceVIewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.map_fragment_place_content, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRV()
    }

    private fun initRV() {
        val collectPlaceAdapter = CollectPlaceAdapter(titles as ArrayList<String>)
        map_rv_collect_place.apply {
            layoutManager = LinearLayoutManager(BaseApp.context)
            addItemDecoration(DividerItemDecoration(BaseApp.context, DividerItemDecoration.VERTICAL))
            adapter = collectPlaceAdapter
        }
    }

}