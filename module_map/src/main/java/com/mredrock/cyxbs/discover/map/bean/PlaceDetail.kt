package com.mredrock.cyxbs.discover.map.bean

data class PlaceDetail(
        val images: Any,
        val place_attribute: List<String>,
        val place_name: String,
        val tags: List<String>)