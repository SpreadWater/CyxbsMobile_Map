package com.mredrock.cyxbs.discover.map.bean

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TabLayoutTitles : Serializable {

    @SerializedName("button_info")
    var buttonInfo: List<TabLayoutItem>? = null

    class TabLayoutItem : Serializable {

        @SerializedName("title")
        val title: String? = null

        @SerializedName("code")
        val code: String? = null

        @SerializedName("is_hot")
        val isHot: Boolean? = null
    }

}
