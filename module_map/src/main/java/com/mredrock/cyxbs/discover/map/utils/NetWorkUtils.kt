package com.mredrock.cyxbs.discover.map.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.mredrock.cyxbs.common.BaseApp

object NetWorkUtils
{
    fun isNetWorkAvailable(context: Context): Boolean {
        val manager = BaseApp.context
                .applicationContext.getSystemService(
                        Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return !(networkInfo == null || !networkInfo.isAvailable)
    }
}