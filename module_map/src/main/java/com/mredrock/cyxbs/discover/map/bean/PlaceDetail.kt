package com.mredrock.cyxbs.discover.map.bean

import java.io.Serializable

data class PlaceDetail(
        val images: Any,
        val place_attribute: List<String>,
        val place_name: String,
        val tags: List<String>):Serializable{

}