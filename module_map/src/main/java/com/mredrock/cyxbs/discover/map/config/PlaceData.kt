package com.mredrock.cyxbs.discover.map.config

import com.mredrock.cyxbs.discover.map.bean.MapData
import com.mredrock.cyxbs.discover.map.bean.PlaceItem

/**
 * @Author: 徐国林
 * @ClassName: PlaceData
 * @Description:
 * @Date: 2020/8/20 15:07
 */
object PlaceData {
    var placeBasicData = ArrayList<PlaceItem>()
    var collectPlace = ArrayList<PlaceItem>()
    var mapData = MapData()
    const val BaseUrl = "https://cyxbsmobile.redrock.team/wxapi/magipoke-stumap/"
}