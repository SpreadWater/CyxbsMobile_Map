package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PlaceBasicData : Serializable {
    @SerializedName("hot_word")
    var hotWord: String? = null

    @SerializedName("place_list")
    var placeList: MutableList<PlaceItem>? = null

    @SerializedName("map_url")
    var mapUrl: String? = null

    @SerializedName("map_width")
    var mapWidth: Float = 0f

    @SerializedName("map_height")
    var mapHeight: Float = 0f

    @SerializedName("map_background_color")
    var mapBackgroundColor: String = "#96ECBB"

    @SerializedName("id")
    var zoomInId: Int = 1
}