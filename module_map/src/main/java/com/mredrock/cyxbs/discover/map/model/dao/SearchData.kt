package com.mredrock.cyxbs.discover.map.model.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.PlaceItem

/**
 *@date 2020-8-15
 *@author zhangsan
 *@description
 */
object SearchData {
    /*
    用于存储搜索页面的传入的值，防止因加载不完时搜索页面的崩溃
     */
    fun saveHotword(hotword: String) {
        sharedPreferences().edit {
            putString("hotword", hotword)
        }
    }

    fun saveItemNum(ItemNum: Int) {
        sharedPreferences().edit {
            putInt("ItemNum", ItemNum)
        }
    }

    fun getSavedHotword(): String = sharedPreferences().getString("hotword", "")

    fun getSavedItemNum(): Int = sharedPreferences().getInt("ItemNum", 125)

    fun isHotwordSaved() = sharedPreferences().contains("hotword")

    fun isHotItemNum() = sharedPreferences().contains("ItemNum")

    private fun sharedPreferences() = BaseApp.context.getSharedPreferences("search_data", Context.MODE_PRIVATE)
}