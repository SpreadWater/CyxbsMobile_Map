package com.mredrock.cyxbs.discover.map.bean

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @Author: 徐国林
 * @ClassName: BuildingConver
 * @Description:
 * @Date: 2020/8/20 15:16
 */

class BuildingRectConverter {
    @TypeConverter
    fun getBuildingRectFromString(value: String): List<PlaceItem.BuildingRect>? {
        return Gson().fromJson<List<PlaceItem.BuildingRect>>(value, object : TypeToken<List<PlaceItem.BuildingRect>>() {
        }.type)
    }

    @TypeConverter
    fun storeBuildingRectToString(tasks: List<PlaceItem.BuildingRect>): String {
        return Gson().toJson(tasks)
    }
}