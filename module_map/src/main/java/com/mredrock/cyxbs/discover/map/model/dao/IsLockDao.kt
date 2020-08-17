package com.mredrock.cyxbs.discover.map.model.dao

import android.content.Context
import androidx.core.content.edit
import com.mredrock.cyxbs.common.BaseApp

object IsLockDao {
    /*
       用于保存是否锁住状态
    */

    const val CODE = 1
    fun saveStatus(status: Boolean) {
        sharedPreferences().edit {
            putBoolean(CODE.toString(), status)
        }
    }

    fun getStatus() = sharedPreferences().getBoolean(CODE.toString(), true)


    private fun sharedPreferences() = BaseApp.context.getSharedPreferences("is_collect", Context.MODE_PRIVATE)
}