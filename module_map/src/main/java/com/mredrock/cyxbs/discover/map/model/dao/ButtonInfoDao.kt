package com.mredrock.cyxbs.discover.map.model.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.bean.TabLayoutTitles

/**
 * @author xgl
 * @date 2020.8.17
 */
object ButtonInfoDao {

    const val BUTTONINFO_BOOLEN_SAVE = 1
    const val BUTTONINFO_SAVE = 0

    //存储buttoninfo
    fun saveButtonInfo(tabLayoutTitles: TabLayoutTitles, isSaved: Boolean) {
        sharedPreferences().edit {
            putString(BUTTONINFO_SAVE.toString(), Gson().toJson(tabLayoutTitles))
        }
        saveStatus(isSaved)
    }

    fun saveStatus(status: Boolean) {
        sharedPreferences().edit {
            putBoolean(BUTTONINFO_BOOLEN_SAVE.toString(), status)
        }
    }

    fun getStatus() = sharedPreferences().getBoolean(BUTTONINFO_BOOLEN_SAVE.toString(), false)

    fun getSavedButtonInfo(): TabLayoutTitles? {
        if (sharedPreferences().getString(BUTTONINFO_SAVE.toString(), "") != null) {
            val buttonInfoJson = sharedPreferences().getString(BUTTONINFO_SAVE.toString(), "")
            return Gson().fromJson(buttonInfoJson, TabLayoutTitles::class.java)
        } else
            return null
    }

    fun isSaved(id: Int) = sharedPreferences().contains(id.toString())

    private fun sharedPreferences() = BaseApp.context.getSharedPreferences("buttonInfo_place", Context.MODE_PRIVATE)
}