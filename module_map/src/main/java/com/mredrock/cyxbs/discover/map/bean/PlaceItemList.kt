package com.mredrock.cyxbs.discover.map.bean

import java.io.Serializable

data class PlaceItemList(val hot_word: String,
                         val place_list: List<PlaceItem>,
                         val map_url: String,
                         val map_width: Float,
                         val map_height: Float,
                         val map_background_color: String):Serializable{
data class PlaceItem(val place_name: String,
                     val place_id: String,
                     val building_list: BulidList,
                     val place_center_x: Float,
                     val place_center_y: Float,
                     val tag_left: Float,
                     val tag_right: Float,
                     val tag_top: Float,
                     val tag_bottom: Float)

data class BulidList(val building_left: Float,
                     val building_right: Float,
                     val building_top: Float,
                     val building_bottom: Float)
}