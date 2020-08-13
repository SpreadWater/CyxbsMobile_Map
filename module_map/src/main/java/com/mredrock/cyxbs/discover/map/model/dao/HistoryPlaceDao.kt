package com.mredrock.cyxbs.discover.map.model.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.Place

/**
 *@date 2020-8-10
 *@author zhangsan
 *@description
 */
object HistoryPlaceDao {
    fun savePlace(place: Place){
        sharedPreferences().edit{
            putString("place",Gson().toJson(place))
        }
    }
    fun getSavedPlace(): Place {
        val placeJson= sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson, Place::class.java)
    }
    fun isPlaceSaved()= sharedPreferences().contains("place")

    private fun  sharedPreferences()=BaseApp.context.getSharedPreferences("history_place",Context.MODE_PRIVATE)
}