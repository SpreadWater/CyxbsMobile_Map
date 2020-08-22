package com.mredrock.cyxbs.discover.map.model.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.MapData
import com.mredrock.cyxbs.discover.map.bean.PlaceItem

object MapDataDao {
    /*
    存储地图基础数据
     */
    fun saveMap(map: MapData) {
        sharedPreferences().edit {
            putString(map.mapVersion.toString(), Gson().toJson(map))
        }
    }

    fun getSavedMap(mapVersion: Int): MapData? {
        if (sharedPreferences().getString(mapVersion.toString(), "") != null) {
            val mapJson = sharedPreferences().getString(mapVersion.toString(), "")
            return Gson().fromJson(mapJson, MapData::class.java)
        } else
            return null
    }

    fun isMapSaved(mapVersion: Int) = MapDataDao.sharedPreferences().contains(mapVersion.toString())
    private fun sharedPreferences() = BaseApp.context.getSharedPreferences("map_data", Context.MODE_PRIVATE)
}