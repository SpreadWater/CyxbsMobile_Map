package com.mredrock.cyxbs.discover.map.model.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.PlaceItem

/**
 *@date 2020-8-10
 *@author zhangsan
 *@description
 */
object HistoryPlaceDao {
    /*
    本地开启数据库，以place的id为索引存储
     */
    const val code=0
    fun saveStatus(status: Boolean) {
        sharedPreferences().edit {
            putBoolean(code.toString(), status)
        }
    }

    fun getStatus() = sharedPreferences().getBoolean(code.toString(), false)

    fun savePlace(place: PlaceItem) {
        sharedPreferences().edit {
            putString(place.placeId.toString(), Gson().toJson(place))
        }
    }

    fun getSavedPlace(placeid: Int): PlaceItem? {
        if (sharedPreferences().getString(placeid.toString(), "") != null) {
            val placeJson = sharedPreferences().getString(placeid.toString(), "")
            return Gson().fromJson(placeJson, PlaceItem::class.java)
        } else
            return null
    }

    fun getAllSavePlace(): Int {
        if (sharedPreferences().all.isNotEmpty())
            return sharedPreferences().all.size
        else
            return 0
    }

    fun isPlaceSaved(placeid: Int) = sharedPreferences().contains(placeid.toString())

    private fun sharedPreferences() = BaseApp.context.getSharedPreferences("history_place", Context.MODE_PRIVATE)
}