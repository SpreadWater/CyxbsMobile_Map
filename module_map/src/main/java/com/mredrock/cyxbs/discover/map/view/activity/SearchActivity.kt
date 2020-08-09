package com.mredrock.cyxbs.discover.map.view.activity

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.utils.selectImageFromAlbum
import com.mredrock.cyxbs.discover.map.viewmodel.SearchViewModel

class SearchActivity : BaseViewModelActivity<SearchViewModel>() {

    override val viewModelClass = SearchViewModel::class.java

    override val isFragmentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_search_place)

    }
}