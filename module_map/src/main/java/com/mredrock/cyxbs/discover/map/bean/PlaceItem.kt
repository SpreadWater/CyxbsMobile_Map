package com.mredrock.cyxbs.discover.map.bean

import androidx.room.Entity
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Entity(primaryKeys = ["placeId"], tableName = "places")
class PlaceItem : Serializable {
    @SerializedName("place_name")
    var placeName: String? = null

    @SerializedName("place_id")
    var placeId: Int = 0

    @SerializedName("place_center_x")
    var placeCenterX: Float = 0f

    @SerializedName("place_center_y")
    var placeCenterY: Float = 0f

    @TypeConverters(BuildingRectConverter::class)
    @SerializedName("building_list")
    var buildingRectList: MutableList<BuildingRect>? = null

    @SerializedName("tag_left")
    var tagLeft: Float = 0f

    @SerializedName("tag_right")
    var tagRight: Float = 0f

    @SerializedName("tag_top")
    var tagTop: Float = 0f

    @SerializedName("tag_bottom")
    var tagBottom: Float = 0f


    class BuildingRect : Serializable {
        @SerializedName("building_left")
        var buildingLeft: Float = 0f

        @SerializedName("building_right")
        var buildingRight: Float = 0f

        @SerializedName("building_top")
        var buildingTop: Float = 0f

        @SerializedName("building_bottom")
        var buildingBottom: Float = 0f
    }
}

class PlaceDetail : Serializable {
    @SerializedName("place_name")
    var placeName: String? = null

    @SerializedName("place_attribute")
    var placeAttribute: List<String>? = null

    @SerializedName("tags")
    var tags: List<String>? = null

    @SerializedName("images")
    var images: List<String>? = null
}