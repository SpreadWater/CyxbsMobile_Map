package com.mredrock.cyxbs.discover.map.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetWorkUtils
{
    fun isNetWorkAvailable(context: Context): Boolean {
        var isAvailable = false
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo = cm.activeNetworkInfo
        if (networkInfo != null && networkInfo.isAvailable()) {
            isAvailable = true
        }
        return isAvailable
    }
}