package com.mredrock.cyxbs.discover.map.bean

import com.bumptech.glide.load.model.FileLoader
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @Author: 徐国林
 * @ClassName: IconBean
 * @Description:
 * @Date: 2020/8/23 14:55
 */
data class IconBean(
        val placeId: Int,
        val placeX: Float,
        val placeY: Float,
        var tagLeft: Float,
        var tagRight: Float,
        var tagTop: Float,
        var tagBottom: Float,
        var buildingLeft: Float,
        var buildingRight: Float,
        var buildingTop: Float,
        var buildingBottom: Float
) : Serializable