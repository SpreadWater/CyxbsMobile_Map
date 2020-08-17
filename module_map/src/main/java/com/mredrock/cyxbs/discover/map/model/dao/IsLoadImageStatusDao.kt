package com.mredrock.cyxbs.discover.map.model.dao

import android.content.Context
import androidx.core.content.edit
import com.mredrock.cyxbs.common.BaseApp

object IsLoadImageStatusDao { const val CODE = 1
    fun saveStatus(status: Boolean) {
        sharedPreferences().edit {
            putBoolean(CODE.toString(), status)
        }
    }

    fun getStatus() = sharedPreferences().getBoolean(CODE.toString(), false)


    private fun sharedPreferences() = BaseApp.context.getSharedPreferences("is_load", Context.MODE_PRIVATE)
}