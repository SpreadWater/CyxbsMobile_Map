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
    //方便数据的拿取，决定将搜索记录顺序存储
    fun savePlace(place: PlaceItem,placeid: Int){
        sharedPreferences().edit{
            //通过id保存place
            putString(place.placeId.toString(), Gson().toJson(place))
            //顺序保存place的id
            putInt(placeid.toString(),place.placeId)
        }
    }
    //通过id获取对应的place
    fun getSavedPlace(placeid:Int): PlaceItem {
        val placeJson= sharedPreferences().getString(placeid.toString(),"")
        return Gson().fromJson(placeJson, PlaceItem::class.java)
    }
    //顺序获取place的id
    fun getSavedPlaceId(id: Int):Int= sharedPreferences().getInt(id.toString(),0)

    //删除对应的 历史记录
    fun deletePlace(placeid: Int){
        sharedPreferences().edit{
            remove(placeid.toString())
        }
    }

    fun isPlaceSaved(placeid: Int)= sharedPreferences().contains(placeid.toString())

    //保存历史记录的总个数
    fun savePlaceNum(placeNum:Int){
        sharedPreferences().edit(){
            putInt("resultNum",placeNum)
        }
    }

    fun getSavedNum()= sharedPreferences().getInt("resultNum",0)

    fun deleteSavedNum(){
        sharedPreferences().edit{
            remove("resultNum")
        }
    }

    private fun  sharedPreferences()= BaseApp.context.getSharedPreferences("search_place", Context.MODE_PRIVATE)
}