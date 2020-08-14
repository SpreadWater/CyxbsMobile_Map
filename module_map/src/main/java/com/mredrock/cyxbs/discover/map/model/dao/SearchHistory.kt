package com.mredrock.cyxbs.discover.map.model.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.PlaceItem

/**
 *@date 2020-8-14
 *@author zhangsan
 *@description  用来储存搜索记录。
 */
object SearchHistory {

    fun savePlace(place: PlaceItem){
        sharedPreferences().edit{
            putString(place.placeId.toString(), Gson().toJson(place))
        }
    }
    fun getSavedPlace(placeid:Int): PlaceItem {
        val placeJson= sharedPreferences().getString(placeid.toString(),"")
        return Gson().fromJson(placeJson, PlaceItem::class.java)
    }
    fun isPlaceSaved(placeid: Int)= sharedPreferences().contains(placeid.toString())

    private fun  sharedPreferences()= BaseApp.context.getSharedPreferences("search_place", Context.MODE_PRIVATE)
}