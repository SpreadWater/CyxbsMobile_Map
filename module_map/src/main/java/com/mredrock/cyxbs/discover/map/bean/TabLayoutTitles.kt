package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TabLayoutTitles : Serializable {

    @SerializedName("button_info")
    var buttonInfo: List<TabLayoutItem>? = null

    class TabLayoutItem : Serializable {

        @SerializedName("title")
        val title: String? = null

        @SerializedName("place_id")
        val placeId: List<Int>? = null

        @SerializedName("is_hot")
        val isHot: Boolean? = null
    }

}
