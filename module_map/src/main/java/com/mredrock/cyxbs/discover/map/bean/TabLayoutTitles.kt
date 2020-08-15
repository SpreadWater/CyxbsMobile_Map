package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TabLayoutTitles : Serializable {

    @SerializedName("button_info")
    val buttonInfo: List<TabLayoutItem>? = null

    class TabLayoutItem : Serializable {
        @SerializedName("title")
        val title: String? = null

        @SerializedName("code")
        val code: String? = null

        @SerializedName("is_hot")
        val isHot: Boolean? = null
    }
}
