package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CollectPlace:Serializable{
    @SerializedName("place_nickname")
    val placeNickName:String?=null
    @SerializedName("place_id")
    val placeId:Int?=null
}