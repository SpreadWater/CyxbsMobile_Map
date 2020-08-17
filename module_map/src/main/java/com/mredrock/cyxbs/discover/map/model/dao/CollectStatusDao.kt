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
object CollectStatusDao {
    /*
        用于保存收藏的id和状态
     */
    //placeid存储Collect的状态
    fun saveCollectStatus(placeid: Int, status: Boolean) {
        sharedPreferences().edit {
            putBoolean(placeid.toString(), status)
        }
    }

    //placeid获取Collect的状态
    fun getCollectStatus(placeid: Int) = sharedPreferences().getBoolean(placeid.toString(), false)


    private fun sharedPreferences() = BaseApp.context.getSharedPreferences("collect_status_place", Context.MODE_PRIVATE)
}